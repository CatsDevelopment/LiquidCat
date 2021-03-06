/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.script

import jdk.internal.dynalink.beans.StaticClass
import jdk.nashorn.api.scripting.JSObject
import jdk.nashorn.api.scripting.ScriptUtils
import lol.liquidcat.LiquidCat
import lol.liquidcat.features.command.Command
import lol.liquidcat.features.command.CommandManager
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleManager
import net.ccbluex.liquidbounce.script.api.*
import net.ccbluex.liquidbounce.script.api.global.Chat
import net.ccbluex.liquidbounce.script.api.global.Setting
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import java.io.File
import java.util.function.Function
import javax.script.ScriptEngineManager

class Script(val scriptFile: File) : MinecraftInstance() {

    private var scriptEngine = ScriptEngineManager().getEngineByName("nashorn")
    private val scriptText = scriptFile.readText()

    // Script information
    lateinit var scriptName: String
    lateinit var scriptVersion: String
    lateinit var scriptAuthors: Array<String>

    private var state = false
    private val events = HashMap<String, JSObject>()
    private val registeredModules = mutableListOf<Module>()
    private val registeredCommands = mutableListOf<Command>()

    init {
        // Global classes
        scriptEngine.put("Chat", StaticClass.forClass(Chat::class.java))
        scriptEngine.put("Setting", StaticClass.forClass(Setting::class.java))

        // Global instances
        scriptEngine.put("mc", mc)
        scriptEngine.put("moduleManager", ModuleManager)
        scriptEngine.put("commandManager", CommandManager)
        scriptEngine.put("scriptManager", ScriptManager)

        // Global functions
        scriptEngine.put("registerScript", RegisterScript())

        supportLegacyScripts()

        scriptEngine.eval(scriptText)

        callEvent("load")
    }

    @Suppress("UNCHECKED_CAST")
    inner class RegisterScript : Function<JSObject, Script> {
        /**
         * Global function 'registerScript' which is called to register a script.
         * @param scriptObject JavaScript object containing information about the script.
         * @return The instance of this script.
         */
        override fun apply(scriptObject: JSObject): Script {
            scriptName = scriptObject.getMember("name") as String
            scriptVersion = scriptObject.getMember("version") as String
            scriptAuthors = ScriptUtils.convert(scriptObject.getMember("authors"), Array<String>::class.java) as Array<String>

            return this@Script
        }
    }

    /**
     * Registers a new script module.
     * @param moduleObject JavaScript object containing information about the module.
     * @param callback JavaScript function to which the corresponding instance of [ScriptModule] is passed.
     * @see ScriptModule
     */
    @Suppress("unused")
    fun registerModule(moduleObject: JSObject, callback: JSObject) {
        val module = ScriptModule(moduleObject)
        ModuleManager.registerModule(module)
        registeredModules += module
        callback.call(moduleObject, module)
    }

    /**
     * Registers a new script command.
     * @param commandObject JavaScript object containing information about the command.
     * @param callback JavaScript function to which the corresponding instance of [ScriptCommand] is passed.
     * @see ScriptCommand
     */
    @Suppress("unused")
    fun registerCommand(commandObject: JSObject, callback: JSObject) {
        val command = ScriptCommand(commandObject)
        CommandManager.registerCommand(command)
        registeredCommands += command
        callback.call(commandObject, command)
    }

    /**
     * Registers a new creative inventory tab.
     * @param tabObject JavaScript object containing information about the tab.
     * @see ScriptTab
     */
    @Suppress("unused")
    fun registerTab(tabObject: JSObject) {
        ScriptTab(tabObject)
    }

    fun supportLegacyScripts() {
        if (!scriptText.lines().first().contains("api_version=2")) {
            LiquidCat.logger.info("[ScriptAPI] Running script '${scriptFile.name}' with legacy support.")
            val legacyScript = LiquidCat::class.java.getResource("/assets/minecraft/liquidcat/scriptapi/legacy.js").readText()
            scriptEngine.eval(legacyScript)
        }
    }

    /**
     * Called from inside the script to register a new event handler.
     * @param eventName Name of the event.
     * @param handler JavaScript function used to handle the event.
     */
    fun on(eventName: String, handler: JSObject) {
        events[eventName] = handler
    }

    /**
     * Called when the client enables the script.
     */
    fun onEnable() {
        if (state) return

        callEvent("enable")
        state = true
    }

    /**
     * Called when the client disables the script. Handles unregistering all modules and commands
     * created with this script.
     */
    fun onDisable() {
        if (!state) return

        registeredModules.forEach { ModuleManager.unregisterModule(it) }
        registeredCommands.forEach { CommandManager.unregisterCommand(it) }

        callEvent("disable")
        state = false
    }

    /**
     * Imports another JavaScript file inro the context of this script.
     * @param scriptFile Path to the file to be imported.
     */
    fun import(scriptFile: String) {
        scriptEngine.eval(File(ScriptManager.scriptsFolder, scriptFile).readText())
    }

    /**
     * Calls the handler of a registered event.
     * @param eventName Name of the event to be called.
     */
    private fun callEvent(eventName: String) {
        try {
            events[eventName]?.call(null)
        } catch (throwable: Throwable) {
            LiquidCat.logger.error("[ScriptAPI] Exception in script '$scriptName'!", throwable)
        }
    }
}
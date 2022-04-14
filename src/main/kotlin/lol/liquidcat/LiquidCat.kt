/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat

import lol.liquidcat.discord.ClientRichPresence
import lol.liquidcat.event.ClientShutdownEvent
import lol.liquidcat.event.EventManager
import lol.liquidcat.features.command.CommandManager
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.file.FileManager
import lol.liquidcat.ui.client.hud.HUD
import lol.liquidcat.ui.client.hud.HUD.Companion.createDefault
import lol.liquidcat.utils.ClientUtils.disableFastRender
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.RotationUtils
import org.apache.logging.log4j.LogManager

object LiquidCat {

    /**
     * Client name
     */
    const val CLIENT_NAME = "LiquidCat"

    /**
     * Current client version
     *
     * @TODO Latest client version check (API)
     */
    const val CLIENT_VERSION = "1.0.0"

    /**
     * Client creators
     */
    const val CLIENT_CREATOR = "CCBlueX & CatsDevelopment"

    /**
     * Cloud with client resources
     *
     * @TODO Make a new LiquidCat cloud
     */
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    /**
     * Client load status
     */
    var loading = false

    /**
     * Client logger used to output info in console
     */
    val logger = LogManager.getLogger(CLIENT_NAME)!!

    // HUD & ClickGUI
    lateinit var hud: HUD
    lateinit var clickGui: ClickGui

    // Discord RPC
    private lateinit var clientRichPresence: ClientRichPresence

    /**
     * Called when client starts
     */
    fun startClient() {
        loading = true

        logger.info("Launching $CLIENT_NAME $CLIENT_VERSION, by $CLIENT_CREATOR")

        // Register listeners
        EventManager.registerListener(RotationUtils())

        // Load client fonts
        Fonts.loadFonts()

        ModuleManager.registerModules()
        CommandManager.registerCommands()

        // Remapper
        try {
            loadSrg()

            // ScriptManager
            ScriptManager.loadScripts()
            ScriptManager.enableScripts()
        } catch (throwable: Throwable) {
            logger.error("Failed to load scripts.", throwable)
        }

        clickGui = ClickGui()
        hud = createDefault()

        // Load configs
        FileManager.loadConfigs()

        // Setup Discord RPC
        try {
            clientRichPresence = ClientRichPresence()
            clientRichPresence.setup()
        } catch (throwable: Throwable) {
            logger.error("Failed to setup Discord RPC.", throwable)
        }

        // Disable optifine fastrender
        disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()

        loading = false
    }

    /**
     * Called when client stops
     */
    fun stopClient() {
        logger.info("Shutting down $CLIENT_NAME...")

        // Call client shutdown
        EventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        FileManager.saveConfigs()

        // Shutdown discord rpc
        clientRichPresence.shutdown()
    }
}
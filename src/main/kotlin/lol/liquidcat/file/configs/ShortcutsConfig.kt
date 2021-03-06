/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import lol.liquidcat.features.command.Command
import lol.liquidcat.features.command.CommandManager
import lol.liquidcat.features.command.shortcuts.Shortcut
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import java.io.File
import java.io.IOException

object ShortcutsConfig : FileConfig(File(FileManager.mainDir, "shortcuts.json")) {

    /**
     * Load config from file
     *
     * @throws IOException
     */
    override fun load() {
        val jsonElement = JsonParser().parse(file.readText())

        if (jsonElement !is JsonArray)
            return

        for (shortcutJson in jsonElement) {
            if (shortcutJson !is JsonObject)
                continue

            val name = shortcutJson.get("name")?.asString ?: continue
            val scriptJson = shortcutJson.get("script")?.asJsonArray ?: continue

            val script = mutableListOf<Pair<Command, Array<String>>>()

            for (scriptCommand in scriptJson) {
                if (scriptCommand !is JsonObject)
                    continue

                val commandName = scriptCommand.get("name")?.asString ?: continue
                val arguments = scriptCommand.get("arguments")?.asJsonArray ?: continue

                val command = CommandManager.getCommand(commandName) ?: continue

                script.add(Pair(command, arguments.map { it.asString }.toTypedArray()))
            }

            CommandManager.registerCommand(Shortcut(name, script))
        }
    }

    /**
     * Save config to file
     *
     * @throws IOException
     */
    override fun save() {
        val jsonArray = JsonArray()

        for (command in CommandManager.commands) {
            if (command !is Shortcut)
                continue

            val jsonCommand = JsonObject()
            jsonCommand.addProperty("name", command.command)

            val scriptArray = JsonArray()

            for (pair in command.script) {
                val pairObject = JsonObject()

                pairObject.addProperty("name", pair.first.command)

                val argumentsObject = JsonArray()
                for (argument in pair.second) {
                    argumentsObject.add(argument)
                }

                pairObject.add("arguments", argumentsObject)

                scriptArray.add(pairObject)
            }

            jsonCommand.add("script", scriptArray)

            jsonArray.add(jsonCommand)
        }

        file.writeText(FileManager.gson.toJson(jsonArray))
    }

}
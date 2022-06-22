/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import lol.liquidcat.features.command.CommandManager
import lol.liquidcat.features.misc.AntiForge
import lol.liquidcat.features.misc.AutoReconnect.delay
import lol.liquidcat.features.misc.BungeeCordSpoof
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.features.module.modules.misc.LiquidChat.jwtToken
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.value.Value
import net.ccbluex.liquidbounce.ui.client.altmanager.sub.altgenerator.GuiTheAltening.Companion.apiKey
import java.io.*
import java.util.function.Consumer

object ValuesConfig : FileConfig(File(FileManager.mainDir, "values.json")) {

    override fun load() {
        val jsonElement = JsonParser().parse(BufferedReader(FileReader(file)))
        if (jsonElement is JsonNull) return
        val jsonObject = jsonElement as JsonObject
        val iterator: Iterator<Map.Entry<String, JsonElement>> = jsonObject.entrySet().iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            if (key.equals("CommandPrefix", ignoreCase = true)) {
                CommandManager.prefix = value.asCharacter
            } else if (key.equals("targets", ignoreCase = true)) {
                val jsonValue = value as JsonObject
                if (jsonValue.has("TargetPlayer")) EntityUtils.targetPlayer = jsonValue["TargetPlayer"].asBoolean
                if (jsonValue.has("TargetMobs")) EntityUtils.targetMobs = jsonValue["TargetMobs"].asBoolean
                if (jsonValue.has("TargetAnimals")) EntityUtils.targetAnimals = jsonValue["TargetAnimals"].asBoolean
                if (jsonValue.has("TargetInvisible")) EntityUtils.targetInvisible =
                    jsonValue["TargetInvisible"].asBoolean
                if (jsonValue.has("TargetDead")) EntityUtils.targetDead = jsonValue["TargetDead"].asBoolean
            } else if (key.equals("features", ignoreCase = true)) {
                val jsonValue = value as JsonObject
                if (jsonValue.has("AntiForge")) AntiForge.enabled = jsonValue["AntiForge"].asBoolean
                if (jsonValue.has("AntiForgeFML")) AntiForge.blockFML = jsonValue["AntiForgeFML"].asBoolean
                if (jsonValue.has("AntiForgeProxy")) AntiForge.blockProxyPacket = jsonValue["AntiForgeProxy"].asBoolean
                if (jsonValue.has("AntiForgePayloads")) AntiForge.blockPayloadPackets =
                    jsonValue["AntiForgePayloads"].asBoolean
                if (jsonValue.has("BungeeSpoof")) BungeeCordSpoof.enabled = jsonValue["BungeeSpoof"].asBoolean
                if (jsonValue.has("AutoReconnectDelay")) delay = jsonValue["AutoReconnectDelay"].asInt
            } else if (key.equals("thealtening", ignoreCase = true)) {
                val jsonValue = value as JsonObject
                if (jsonValue.has("API-Key")) apiKey = jsonValue["API-Key"].asString
            } else if (key.equals("liquidchat", ignoreCase = true)) {
                val jsonValue = value as JsonObject
                if (jsonValue.has("token")) jwtToken = jsonValue["token"].asString
            } else {
                val module = ModuleManager.getModule(key)
                if (module != null) {
                    val jsonModule = value as JsonObject
                    for (moduleValue in module.values) {
                        val element = jsonModule[moduleValue.name]
                        if (element != null) moduleValue.fromJson(element)
                    }
                }
            }
        }
    }

    override fun save() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("CommandPrefix", CommandManager.prefix)
        val jsonTargets = JsonObject()
        jsonTargets.addProperty("TargetPlayer", EntityUtils.targetPlayer)
        jsonTargets.addProperty("TargetMobs", EntityUtils.targetMobs)
        jsonTargets.addProperty("TargetAnimals", EntityUtils.targetAnimals)
        jsonTargets.addProperty("TargetInvisible", EntityUtils.targetInvisible)
        jsonTargets.addProperty("TargetDead", EntityUtils.targetDead)
        jsonObject.add("targets", jsonTargets)
        val jsonFeatures = JsonObject()
        jsonFeatures.addProperty("AntiForge", AntiForge.enabled)
        jsonFeatures.addProperty("AntiForgeFML", AntiForge.blockFML)
        jsonFeatures.addProperty("AntiForgeProxy", AntiForge.blockProxyPacket)
        jsonFeatures.addProperty("AntiForgePayloads", AntiForge.blockPayloadPackets)
        jsonFeatures.addProperty("BungeeSpoof", BungeeCordSpoof.enabled)
        jsonFeatures.addProperty("AutoReconnectDelay", delay)
        jsonObject.add("features", jsonFeatures)
        val theAlteningObject = JsonObject()
        theAlteningObject.addProperty("API-Key", apiKey)
        jsonObject.add("thealtening", theAlteningObject)
        val liquidChatObject = JsonObject()
        liquidChatObject.addProperty("token", jwtToken)
        jsonObject.add("liquidchat", liquidChatObject)
        ModuleManager.modules.stream().filter { module: Module -> !module.values.isEmpty() }
            .forEach { module: Module ->
                val jsonModule = JsonObject()
                module.values.forEach(Consumer { value: Value<*> -> jsonModule.add(value.name, value.toJson()) })
                jsonObject.add(module.name, jsonModule)
            }
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject))
        printWriter.close()
    }
}
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
import lol.liquidcat.LiquidCat
import lol.liquidcat.features.module.Module
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.value.Value
import lol.liquidcat.features.module.modules.misc.LiquidChat.Companion.jwtToken
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.AutoReconnect.delay
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.ui.client.GuiBackground.Companion.enabled
import net.ccbluex.liquidbounce.ui.client.GuiBackground.Companion.particles
import net.ccbluex.liquidbounce.ui.client.altmanager.sub.GuiDonatorCape.Companion.capeEnabled
import net.ccbluex.liquidbounce.ui.client.altmanager.sub.GuiDonatorCape.Companion.transferCode
import net.ccbluex.liquidbounce.ui.client.altmanager.sub.altgenerator.GuiTheAltening.Companion.apiKey
import java.io.*
import java.util.function.Consumer

class ValuesConfig(file: File?) : FileConfig(file!!) {
    /**
     * Load config from file
     */
    override fun loadConfig() {
        val jsonElement = JsonParser().parse(BufferedReader(FileReader(file)))
        if (jsonElement is JsonNull) return
        val jsonObject = jsonElement as JsonObject
        val iterator: Iterator<Map.Entry<String, JsonElement>> = jsonObject.entrySet().iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            if (key.equals("CommandPrefix", ignoreCase = true)) {
                LiquidCat.commandManager.prefix = value.asCharacter
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
            } else if (key.equals("DonatorCape", ignoreCase = true)) {
                val jsonValue = value as JsonObject
                if (jsonValue.has("TransferCode")) transferCode = jsonValue["TransferCode"].asString
                if (jsonValue.has("CapeEnabled")) capeEnabled = jsonValue["CapeEnabled"].asBoolean
            } else if (key.equals("Background", ignoreCase = true)) {
                val jsonValue = value as JsonObject
                if (jsonValue.has("Enabled")) enabled = jsonValue["Enabled"].asBoolean
                if (jsonValue.has("Particles")) particles = jsonValue["Particles"].asBoolean
            } else {
                val module = LiquidCat.moduleManager.getModule(key)
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

    /**
     * Save config to file
     */
    override fun saveConfig() {
        val jsonObject = JsonObject()
        jsonObject.addProperty("CommandPrefix", LiquidCat.commandManager.prefix)
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
        val capeObject = JsonObject()
        capeObject.addProperty("TransferCode", transferCode)
        capeObject.addProperty("CapeEnabled", capeEnabled)
        jsonObject.add("DonatorCape", capeObject)
        val backgroundObject = JsonObject()
        backgroundObject.addProperty("Enabled", enabled)
        backgroundObject.addProperty("Particles", particles)
        jsonObject.add("Background", backgroundObject)
        LiquidCat.moduleManager.modules.stream().filter { module: Module -> !module.values.isEmpty() }
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
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import lol.liquidcat.features.friend.FriendManager
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import java.io.*

class FriendsConfig(file: File?) : FileConfig(file!!) {

    /**
     * Load config from file
     */
    override fun load() {
        val jsonElement = JsonParser().parse(BufferedReader(FileReader(file)))
        val jsonObject = jsonElement as JsonObject

        val iterator: Iterator<Map.Entry<String, JsonElement>> = jsonObject.entrySet().iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()

            FriendManager.addFriend(key, value.asString)
        }
    }

    /**
     * Save config to file
     */
    override fun save() {
        val jsonObject = JsonObject()
        FriendManager.friends.forEach { jsonObject.addProperty(it.name, it.alias) }

        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(jsonObject))
        printWriter.close()
    }
}
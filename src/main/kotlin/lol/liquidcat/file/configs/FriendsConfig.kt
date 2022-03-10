/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import lol.liquidcat.file.FileConfig
import java.io.*

class FriendsConfig(file: File?) : FileConfig(file!!) {
    val friends = mutableListOf<Friend>()

    /**
     * Load config from file
     */
    override fun loadConfig() {
        clearFriends()
        val bufferedReader = BufferedReader(FileReader(file))
        var line: String
        while (bufferedReader.readLine().also { line = it } != null) {
            if (!line.contains("{") && !line.contains("}")) {
                line = line.replace(" ", "").replace("\"", "").replace(",", "")
                if (line.contains(":")) {
                    val data = line.split(":".toRegex()).toTypedArray()
                    addFriend(data[0], data[1])
                } else addFriend(line)
            }
        }
        bufferedReader.close()
    }

    /**
     * Save config to file
     */
    override fun saveConfig() {
        val printWriter = PrintWriter(FileWriter(file))
        for (friend in friends) printWriter.append(friend.playerName).append(":").append(friend.alias).append("\n")
        printWriter.close()
    }

    /**
     * Add friend to config
     *
     * @param playerName of friend
     * @param alias      of friend
     * @return of successfully added friend
     */
    @JvmOverloads
    fun addFriend(playerName: String, alias: String? = playerName): Boolean {
        if (isFriend(playerName)) return false
        friends.add(Friend(playerName, alias))
        return true
    }

    /**
     * Remove friend from config
     *
     * @param playerName of friend
     */
    fun removeFriend(playerName: String): Boolean {
        if (!isFriend(playerName)) return false
        friends.removeIf { friend: Friend -> friend.playerName == playerName }
        return true
    }

    /**
     * Check is friend
     *
     * @param playerName of friend
     * @return is friend
     */
    fun isFriend(playerName: String): Boolean {
        for (friend in friends) if (friend.playerName == playerName) return true
        return false
    }

    /**
     * Clear all friends from config
     */
    fun clearFriends() {
        friends.clear()
    }

    inner class Friend
    /**
     * @param playerName of friend
     * @param alias      of friend
     */ internal constructor(
        /**
         * @return name of friend
         */
        val playerName: String,
        /**
         * @return alias of friend
         */
        val alias: String?
    )
}
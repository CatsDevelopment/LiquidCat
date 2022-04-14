/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */

package lol.liquidcat.features.friend

object FriendManager {

    /**
     * List with all friends
     */
    val friends = mutableListOf<Friend>()

    /**
     * Adds a new friend
     *
     * @param name Friend name
     * @param alias Friend alias
     */
    fun addFriend(name: String, alias: String = name) {
        friends.add(Friend(name, alias))
    }

    /**
     * Removes a friend
     *
     * @param name Friend name
     */
    fun removeFriend(name: String) {
        friends.removeAll { it.name == name }
    }

    /**
     * Clears all friends
     */
    fun clearFriends() = friends.clear()

    /**
     * Checks if friend with [name] already exists
     */
    fun isFriend(name: String) = friends.any { it.name == name }

    class Friend(val name: String, val alias: String = name)
}
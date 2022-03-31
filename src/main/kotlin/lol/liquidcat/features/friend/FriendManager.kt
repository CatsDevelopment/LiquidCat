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
     * Adds a friend
     *
     * @param name Friend's name
     * @param alias Friend's alias
     * @return Add friend status
     */
    fun addFriend(name: String, alias: String? = name): Boolean {
        if (isFriend(name)) return false
        friends.add(Friend(name, alias))
        return true
    }

    /**
     * Removes a friend
     *
     * @param name Friend's name
     * @return Remove friend status
     */
    fun removeFriend(name: String): Boolean {
        if (!isFriend(name)) return false
        friends.removeIf { it.name == name }
        return true
    }

    /**
     * Clears all friends
     */
    fun clearFriends() = friends.clear()

    fun isFriend(name: String) = friends.contains(Friend(name))

    class Friend(val name: String, val alias: String? = null)
}
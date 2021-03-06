/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.features.command.Command
import lol.liquidcat.features.friend.FriendManager
import lol.liquidcat.file.FileManager
import lol.liquidcat.file.configs.FriendsConfig
import lol.liquidcat.utils.StringUtils

object FriendCommand : Command("friend", arrayOf("friends")) {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when {
                args[1].equals("add", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val name = args[2]

                        if (name.isEmpty()) {
                            chat("The name is empty.")
                            return
                        }

                        if (!FriendManager.isFriend(name)) {
                            FriendManager.addFriend(name, if (args.size > 3) StringUtils.toCompleteString(args, 3) else name)
                            FileManager.saveConfig(FriendsConfig)
                            chat("§a§l$name§3 was added to your friend list.")
                            playEdit()
                        } else
                            chat("The name is already in the list.")

                        return
                    }
                    chatSyntax("friend add <name> [alias]")
                    return
                }

                args[1].equals("remove", ignoreCase = true) -> {
                    if (args.size > 2) {
                        val name = args[2]

                        if (FriendManager.isFriend(name)) {
                            FriendManager.removeFriend(name)
                            FileManager.saveConfig(FriendsConfig)
                            chat("§a§l$name§3 was removed from your friend list.")
                            playEdit()
                        } else
                            chat("This name is not in the list.")

                        return
                    }
                    chatSyntax("friend remove <name>")
                    return
                }

                args[1].equals("clear", ignoreCase = true) -> {
                    val friends = FriendManager.friends.size
                    FriendManager.clearFriends()
                    FileManager.saveConfig(FriendsConfig)
                    chat("Removed $friends friend(s).")
                    return
                }

                args[1].equals("list", ignoreCase = true) -> {
                    chat("Your Friends:")

                    for (friend in FriendManager.friends)
                        chat("§7> §a§l${friend.name} §c(§7§l${friend.alias}§c)")

                    chat("You have §c${FriendManager.friends.size}§3 friends.")
                    return
                }
            }
        }

        chatSyntax("friend <add/remove/list/clear>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("add", "remove", "list", "clear").filter { it.startsWith(args[0], true) }
            2 -> {
                when (args[0].lowercase()) {
                    "add" -> {
                        return mc.theWorld.playerEntities
                            .map { it.name }
                            .filter { it.startsWith(args[1], true) }
                    }
                    "remove" -> {
                        return FriendManager.friends
                                .map { it.name }
                                .filter { it.startsWith(args[1], true) }
                    }
                }
                return emptyList()
            }
            else -> emptyList()
        }
    }
}
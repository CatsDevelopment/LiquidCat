/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render2DEvent
import lol.liquidcat.features.friend.FriendManager
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.file.FileManager
import lol.liquidcat.file.configs.FriendsConfig
import lol.liquidcat.utils.msg
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.input.Mouse

object MidClick : Module("MidClick", "Allows you to add a player as a friend by right clicking him.", ModuleCategory.MISC) {

    private var wasDown = false

    @EventTarget
    fun onRender(event: Render2DEvent) {
        if (mc.currentScreen != null) return

        if (!wasDown && Mouse.isButtonDown(2)) {
            val entity = mc.objectMouseOver.entityHit

            if (entity is EntityPlayer) {
                val playerName = entity.gameProfile.name

                if (FriendManager.isFriend(playerName)) {
                    FriendManager.removeFriend(playerName)
                    FileManager.saveConfig(FriendsConfig)
                    msg("§a§l$playerName§c was removed from your friends.")
                } else {
                    FriendManager.addFriend(playerName)
                    FileManager.saveConfig(FriendsConfig)
                    msg("§a§l$playerName§c was added to your friends.")
                }
            } else msg("§c§lError: §aYou need to select a player.")
        }
        wasDown = Mouse.isButtonDown(2)
    }
}
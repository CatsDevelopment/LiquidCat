/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.TextEvent
import lol.liquidcat.features.friend.FriendManager
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.render.color.ColorUtils.translateAlternateColorCodes
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.TextValue

object NameProtect : Module("NameProtect", "Hides player names.", ModuleCategory.MISC) {

    private val fakeName by TextValue("FakeName", "&cMe")
    val allPlayers by BoolValue("AllPlayers", false)
    val skinProtect by BoolValue("SkinProtect", true)

    @EventTarget(ignoreCondition = true)
    fun onText(event: TextEvent) {
        val text = event.text ?: return

        if (mc.thePlayer == null || event.text!!.contains("§8[§9§l" + LiquidCat.CLIENT_NAME + "§8] §3"))
            return

        for (friend in FriendManager.friends)
            event.text = text.replace(friend.name, translateAlternateColorCodes(friend.alias) + "§f")

        if (!state)
            return

        event.text = text.replace(mc.thePlayer.name, translateAlternateColorCodes(fakeName) + "§f")

        if (allPlayers)
            for (playerInfo in mc.netHandler.playerInfoMap)
                event.text = text.replace(playerInfo.gameProfile.name, "Protected User")
    }
}
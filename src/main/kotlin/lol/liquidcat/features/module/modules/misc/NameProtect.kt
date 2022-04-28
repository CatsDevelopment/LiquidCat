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
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.TextValue
import net.ccbluex.liquidbounce.utils.misc.StringUtils
import lol.liquidcat.utils.render.ColorUtils.translateAlternateColorCodes

object NameProtect : Module("NameProtect", "Changes playernames clientside.", ModuleCategory.MISC) {

    private val fakeName by TextValue("FakeName", "&cMe")
    val allPlayers by BoolValue("AllPlayers", false)
    val skinProtect by BoolValue("SkinProtect", true)

    @EventTarget(ignoreCondition = true)
    fun onText(event: TextEvent) {
        if (mc.thePlayer == null || event.text!!.contains("§8[§9§l" + LiquidCat.CLIENT_NAME + "§8] §3")) return
        for (friend in FriendManager.friends)
            event.text = StringUtils.replace(event.text, friend.name, translateAlternateColorCodes(friend.alias!!) + "§f")

        if (!state) return

        event.text =
            StringUtils.replace(event.text, mc.thePlayer.name, translateAlternateColorCodes(fakeName) + "§f")
        if (allPlayers) for (playerInfo in mc.netHandler.playerInfoMap) event.text =
            StringUtils.replace(event.text, playerInfo.gameProfile.name, "Protected User")
    }
}
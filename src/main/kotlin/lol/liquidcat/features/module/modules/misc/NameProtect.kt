/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.TextEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.TextValue
import net.ccbluex.liquidbounce.utils.misc.StringUtils
import net.ccbluex.liquidbounce.utils.render.ColorUtils.translateAlternateColorCodes

class NameProtect : Module("NameProtect", "Changes playernames clientside.", ModuleCategory.MISC) {

    private val fakeNameValue = TextValue("FakeName", "&cMe")
    val allPlayersValue = BoolValue("AllPlayers", false)
    val skinProtectValue = BoolValue("SkinProtect", true)

    //@EventTarget(ignoreCondition = true)
    fun onText(event: TextEvent) {
        if (mc.thePlayer == null || event.text!!.contains("§8[§9§l" + LiquidCat.CLIENT_NAME + "§8] §3")) return
        for (friend in LiquidCat.fileManager.friendsConfig.friends) event.text = StringUtils.replace(
            event.text, friend.playerName, translateAlternateColorCodes(
                friend.alias!!
            ) + "§f"
        )
        if (!state) return
        event.text =
            StringUtils.replace(event.text, mc.thePlayer.name, translateAlternateColorCodes(fakeNameValue.get()) + "§f")
        if (allPlayersValue.get()) for (playerInfo in mc.netHandler.playerInfoMap) event.text =
            StringUtils.replace(event.text, playerInfo.gameProfile.name, "Protected User")
    }
}
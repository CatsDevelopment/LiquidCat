/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue
import net.minecraft.entity.player.EntityPlayer

object Teams : Module("Teams", "Prevents Killaura from attacking teammates.", ModuleCategory.MISC) {

    private val scoreboard by BoolValue("Scoreboard", true)
    private val color by BoolValue("Color", true)

    /**
     * Checks if [player] is your teammate
     */
    fun isYourTeammate(player: EntityPlayer): Boolean {
        if (!state) return false

        if (color) {
            val eName = player.displayName?.unformattedText
            val pName = mc.thePlayer.displayName?.unformattedText

            if (eName != null && pName != null && eName.startsWith(pName.substring(0..1)))
                return true
        }

        if (scoreboard) {
            val eTeam = player.team
            val pTeam = mc.thePlayer.team

            if (eTeam != null && pTeam != null && eTeam == pTeam)
                return true
        }

        return false
    }
}
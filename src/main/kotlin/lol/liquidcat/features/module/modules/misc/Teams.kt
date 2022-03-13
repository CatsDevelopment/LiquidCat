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

object Teams : Module("Teams", "Prevents Killaura from attacking team mates.", ModuleCategory.MISC) {

    private val scoreboard = BoolValue("ScoreboardTeam", true)
    private val color = BoolValue("Color", true)

    /**
     * Check if [entity] is in your own team using scoreboard, name color or team prefix
     */
    fun isInYourTeam(entity: EntityPlayer): Boolean {
        if (!state) return false

        if (color.get()) {
            val eName = entity.displayName?.unformattedText
            val pName = mc.thePlayer.displayName?.unformattedText

            if (eName != null && pName != null && eName.startsWith(pName.substring(0..1))) {
                return true
            }
        }

        if (scoreboard.get()) {
            val eTeam = entity.team
            val pTeam = mc.thePlayer.team

            if (eTeam != null && pTeam != null && pTeam.isSameTeam(eTeam)) {
                return true
            }
        }

        return false
    }
}
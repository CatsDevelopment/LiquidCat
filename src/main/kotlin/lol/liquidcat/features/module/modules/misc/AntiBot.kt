/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.ping
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import net.minecraft.entity.player.EntityPlayer

//TODO Add check for invalid UUID using Mojang profiles API

object AntiBot : Module("AntiBot", "Prevents KillAura from attacking AntiCheat bots.", ModuleCategory.MISC) {

    private val invalidName by BoolValue("InvalidName", true)
    private val invalidPitch by BoolValue("InvalidPitch", true)
    private val invalidHealth by BoolValue("InvalidHealth", true)

    private val livingTime by BoolValue("LivingTime", false)
    private val livingTimeTicks by IntValue("LivingTimeTicks", 40, 1..200)

    private val entityId by BoolValue("EntityID", true)

    private val ping by BoolValue("Ping", false)

    private val duplicateInWorld by BoolValue("DuplicateInWorld", false)
    private val duplicateInTab by BoolValue("DuplicateInTab", true)

    fun isBot(player: EntityPlayer): Boolean {
        if (!state) return false

        // Checks if player name contains illegal characters or the length of the name is not valid
        if (invalidName && (!player.name.contains(Regex("^[a-zA-Z0-9_]+$")) || player.name.length !in 3..16))
            return true

        // Checks if player's health is higher than its limit
        if (invalidHealth && (player.health > player.maxHealth || player.health.isNaN()))
            return true

        // Checks if player's head pitch is not within its limit
        if (invalidPitch && (player.rotationPitch > 90f || player.rotationPitch < -90f))
            return true

        if (livingTime && player.ticksExisted < livingTimeTicks)
            return true

        // Checks if player's entity ID is not valid
        if (entityId && (player.entityId >= 1000000000 || player.entityId <= -1))
            return true

        // Checks if player's ping is 0
        if (ping && player.ping == 0)
            return true

        // Checks if player name is duplicated in the world several times
        if (duplicateInWorld)
            if (mc.theWorld.loadedEntityList.count { it is EntityPlayer && player.name == it.gameProfile.name } > 1)
                return true

        // Checks if player name is duplicated in tab several times
        if (duplicateInTab)
            if (mc.netHandler.playerInfoMap.count { player.name == it.gameProfile.name } > 1)
                return true

        // Returns false if all checks are passed
        return false
    }
}
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
import lol.liquidcat.value.IntegerValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

//TODO Add InvalidUUID Check

object AntiBot : Module("AntiBot", "Prevents KillAura from attacking AntiCheat bots.", ModuleCategory.MISC) {

    private val invalidName = BoolValue("InvalidName", true)
    private val invalidPitch = BoolValue("InvalidPitch", true)
    private val health = BoolValue("Health", true)
    private val livingTime = BoolValue("LivingTime", false)
    private val livingTimeTicks = IntegerValue("LivingTimeTicks", 40, 1, 200)
    private val entityId = BoolValue("EntityID", true)

    private val ping = BoolValue("Ping", false)
    private val duplicateInWorld = BoolValue("DuplicateInWorld", false)
    private val duplicateInTab = BoolValue("DuplicateInTab", true)

    fun isBot(entity: EntityLivingBase): Boolean {
        if (entity !is EntityPlayer)
            return false

        if (!state)
            return false

        if (invalidName.get() && (!entity.name.contains(Regex("^[a-zA-Z0-9_]+$")) || entity.name.length !in 3..16))
            return true

        if (health.get() && (entity.health > entity.maxHealth || entity.health.isNaN()))
            return true

        if (invalidPitch.get() && (entity.rotationPitch > 90f || entity.rotationPitch < -90f))
            return true

        if (livingTime.get() && entity.ticksExisted < livingTimeTicks.get())
            return true

        if (entityId.get() && (entity.entityId >= 1000000000 || entity.entityId <= -1))
            return true

        if (ping.get() && entity.ping == 0)
            return true

        if (duplicateInWorld.get()) {
            if (mc.theWorld.loadedEntityList
                    .count { ent -> ent is EntityPlayer && entity.gameProfile.name == ent.gameProfile.name } > 1
            ) return true
        }

        if (duplicateInTab.get()) {
            if (mc.netHandler.playerInfoMap
                    .count { plr -> entity.gameProfile.name == plr.gameProfile.name } > 1
            ) return true
        }

        return false
    }
}
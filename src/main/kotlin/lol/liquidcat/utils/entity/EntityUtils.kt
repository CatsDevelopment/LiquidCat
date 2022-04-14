/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.entity

import lol.liquidcat.features.module.modules.combat.NoFriends
import lol.liquidcat.features.module.modules.misc.AntiBot
import lol.liquidcat.features.module.modules.misc.Teams
import lol.liquidcat.utils.mc
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer

object EntityUtils {
    @JvmField
    var targetInvisible = false
    @JvmField
    var targetPlayer = true
    @JvmField
    var targetMobs = true
    @JvmField
    var targetAnimals = false
    @JvmField
    var targetDead = false

    @JvmStatic
    fun isSelected(entity: Entity, canAttackCheck: Boolean): Boolean {
        if (entity is EntityLivingBase && (targetDead || entity.isEntityAlive()) && entity != mc.thePlayer) {
            if (targetInvisible || !entity.isInvisible()) {
                if (targetPlayer && entity is EntityPlayer) {
                    if (canAttackCheck) {
                        if (AntiBot.isBot(entity)) return false
                        if (entity.isFriend() && !NoFriends.state) return false
                        if (entity.isSpectator) return false
                        return !Teams.isInYourTeam(entity)
                    }
                    return true
                }
                return targetMobs && entity.isMob() || targetAnimals && entity.isAnimal()
            }
        }
        return false
    }
}
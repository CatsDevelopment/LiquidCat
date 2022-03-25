/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.entity

import lol.liquidcat.LiquidCat
import lol.liquidcat.features.module.modules.combat.NoFriends
import lol.liquidcat.features.module.modules.misc.AntiBot
import lol.liquidcat.features.module.modules.misc.Teams
import lol.liquidcat.utils.mc
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.boss.EntityDragon
import net.minecraft.entity.monster.EntityGhast
import net.minecraft.entity.monster.EntityGolem
import net.minecraft.entity.monster.EntityMob
import net.minecraft.entity.monster.EntitySlime
import net.minecraft.entity.passive.EntityAnimal
import net.minecraft.entity.passive.EntityBat
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.entity.passive.EntityVillager
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.scoreboard.ScorePlayerTeam

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
                        if (isFriend(entity) && !NoFriends.state) return false
                        if (entity.isSpectator) return false
                        return !Teams.isInYourTeam(entity)
                    }
                    return true
                }
                return targetMobs && isMob(entity) || targetAnimals && isAnimal(entity)
            }
        }
        return false
    }

    fun isFriend(entity: Entity): Boolean {
        return entity is EntityPlayer && entity.getName() != null &&
                LiquidCat.fileManager.friendsConfig.isFriend(entity.getName())
    }

    fun isAnimal(entity: Entity?): Boolean {
        return entity is EntityAnimal || entity is EntitySquid || entity is EntityGolem ||
                entity is EntityBat
    }

    fun isMob(entity: Entity?): Boolean {
        return entity is EntityMob || entity is EntityVillager || entity is EntitySlime ||
                entity is EntityGhast || entity is EntityDragon
    }

    fun getName(networkPlayerInfoIn: NetworkPlayerInfo): String {
        return if (networkPlayerInfoIn.displayName != null) networkPlayerInfoIn.displayName.formattedText else ScorePlayerTeam.formatPlayerName(
            networkPlayerInfoIn.playerTeam,
            networkPlayerInfoIn.gameProfile.name
        )
    }
}
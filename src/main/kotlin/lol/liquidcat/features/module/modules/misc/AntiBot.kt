/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.AttackEvent
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.event.WorldEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.EntityUtils.getName
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntegerValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.utils.render.ColorUtils.stripColor
import net.minecraft.client.network.NetworkPlayerInfo
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.S0BPacketAnimation
import net.minecraft.network.play.server.S14PacketEntity

object AntiBot : Module("AntiBot", "Prevents KillAura from attacking AntiCheat bots.", ModuleCategory.MISC) {

    private val tabValue = BoolValue("Tab", true)
    private val tabModeValue = ListValue("TabMode", arrayOf("Equals", "Contains"), "Contains")
    private val entityIDValue = BoolValue("EntityID", true)
    private val colorValue = BoolValue("Color", false)
    private val livingTimeValue = BoolValue("LivingTime", false)
    private val livingTimeTicksValue = IntegerValue("LivingTimeTicks", 40, 1, 200)
    private val groundValue = BoolValue("Ground", true)
    private val airValue = BoolValue("Air", false)
    private val invalidGroundValue = BoolValue("InvalidGround", true)
    private val swingValue = BoolValue("Swing", false)
    private val healthValue = BoolValue("Health", false)
    private val derpValue = BoolValue("Derp", true)
    private val wasInvisibleValue = BoolValue("WasInvisible", false)
    private val armorValue = BoolValue("Armor", false)
    private val pingValue = BoolValue("Ping", false)
    private val needHitValue = BoolValue("NeedHit", false)
    private val duplicateInWorldValue = BoolValue("DuplicateInWorld", false)
    private val duplicateInTabValue = BoolValue("DuplicateInTab", false)
    private val ground: MutableList<Int> = ArrayList()
    private val air: MutableList<Int> = ArrayList()
    private val invalidGround: MutableMap<Int, Int> = HashMap()
    private val swing: MutableList<Int> = ArrayList()
    private val invisible: MutableList<Int> = ArrayList()
    private val hitted: MutableList<Int> = ArrayList()

    override fun onDisable() {
        clearAll()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return
        val packet = event.packet
        if (packet is S14PacketEntity) {
            val packetEntity = event.packet as S14PacketEntity
            val entity = packetEntity.getEntity(mc.theWorld)
            if (entity is EntityPlayer) {
                if (packetEntity.onGround && !ground.contains(entity.getEntityId())) ground.add(entity.getEntityId())
                if (!packetEntity.onGround && !air.contains(entity.getEntityId())) air.add(entity.getEntityId())
                if (packetEntity.onGround) {
                    if (entity.prevPosY != entity.posY) invalidGround[entity.getEntityId()] =
                        invalidGround.getOrDefault(entity.getEntityId(), 0) + 1
                } else {
                    val currentVL = invalidGround.getOrDefault(entity.getEntityId(), 0) / 2
                    if (currentVL <= 0) invalidGround.remove(entity.getEntityId()) else invalidGround[entity.getEntityId()] =
                        currentVL
                }
                if (entity.isInvisible() && !invisible.contains(entity.getEntityId())) invisible.add(entity.getEntityId())
            }
        }
        if (packet is S0BPacketAnimation) {
            val packetAnimation = event.packet as S0BPacketAnimation
            val entity = mc.theWorld.getEntityByID(packetAnimation.entityID)
            if (entity is EntityLivingBase && packetAnimation.animationType == 0 && !swing.contains(entity.getEntityId())) swing.add(
                entity.getEntityId()
            )
        }
    }

    @EventTarget
    fun onAttack(e: AttackEvent) {
        val entity = e.targetEntity
        if (entity is EntityLivingBase && !hitted.contains(entity.getEntityId())) hitted.add(entity.getEntityId())
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        clearAll()
    }

    private fun clearAll() {
        hitted.clear()
        swing.clear()
        ground.clear()
        invalidGround.clear()
        invisible.clear()
    }

    fun isBot(entity: EntityLivingBase): Boolean {
        if (entity !is EntityPlayer) return false
        val antiBot = LiquidCat.moduleManager.getModule(AntiBot::class.java) as AntiBot?
        if (antiBot == null || !antiBot.state) return false
        if (antiBot.colorValue.get() && !entity.getDisplayName().formattedText
                .replace("§r", "").contains("§")
        ) return true
        if (antiBot.livingTimeValue.get() && entity.ticksExisted < antiBot.livingTimeTicksValue.get()) return true
        if (antiBot.groundValue.get() && !antiBot.ground.contains(entity.getEntityId())) return true
        if (antiBot.airValue.get() && !antiBot.air.contains(entity.getEntityId())) return true
        if (antiBot.swingValue.get() && !antiBot.swing.contains(entity.getEntityId())) return true
        if (antiBot.healthValue.get() && entity.getHealth() > 20f) return true
        if (antiBot.entityIDValue.get() && (entity.getEntityId() >= 1000000000 || entity.getEntityId() <= -1)) return true
        if (antiBot.derpValue.get() && (entity.rotationPitch > 90f || entity.rotationPitch < -90f)) return true
        if (antiBot.wasInvisibleValue.get() && antiBot.invisible.contains(entity.getEntityId())) return true
        if (antiBot.armorValue.get()) {
            if (entity.inventory.armorInventory[0] == null && entity.inventory.armorInventory[1] == null && entity.inventory.armorInventory[2] == null && entity.inventory.armorInventory[3] == null) return true
        }
        if (antiBot.pingValue.get()) {
            if (mc.getNetHandler().getPlayerInfo(entity.uniqueID).getResponseTime() == 0) return true
        }
        if (antiBot.needHitValue.get() && !antiBot.hitted.contains(entity.getEntityId())) return true
        if (antiBot.invalidGroundValue.get() && antiBot.invalidGround.getOrDefault(
                entity.getEntityId(),
                0
            ) >= 10
        ) return true
        if (antiBot.tabValue.get()) {
            val equals = antiBot.tabModeValue.get().equals("Equals", ignoreCase = true)
            val targetName = stripColor(entity.getDisplayName().formattedText)
            if (targetName != null) {
                for (networkPlayerInfo in mc.getNetHandler().getPlayerInfoMap()) {
                    val networkName = stripColor(
                        getName(
                            networkPlayerInfo!!
                        )
                    ) ?: continue
                    if (if (equals) targetName == networkName else targetName.contains(networkName)) return false
                }
                return true
            }
        }
        if (antiBot.duplicateInWorldValue.get()) {
            if (mc.theWorld.loadedEntityList.stream()
                    .filter { currEntity: Entity? ->
                        currEntity is EntityPlayer && currEntity
                            .displayNameString == currEntity.displayNameString
                    }
                    .count() > 1
            ) return true
        }
        if (antiBot.duplicateInTabValue.get()) {
            if (mc.netHandler.playerInfoMap.stream()
                    .filter { networkPlayer: NetworkPlayerInfo? ->
                        entity.getName() == stripColor(
                            getName(
                                networkPlayer!!
                            )
                        )
                    }
                    .count() > 1
            ) return true
        }
        return entity.getName().isEmpty() || entity.getName() == mc.thePlayer.getName()
    }
}
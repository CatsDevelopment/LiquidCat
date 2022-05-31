/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.AttackEvent
import lol.liquidcat.event.EventTarget
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

object Criticals : Module("Criticals", "Automatically deals critical hits.", ModuleCategory.COMBAT) {

    val mode by ListValue("Mode", arrayOf("NCP", "Lowest", "Phase", "Visual"), "NCP")
    val delay by IntValue("Delay", 0, 0..500)

    val delayTimer = MSTimer()

    override val tag
        get() = mode

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (canCrit(event.targetEntity ?: return)) {

            val x = mc.thePlayer.posX
            val y = mc.thePlayer.posY
            val z = mc.thePlayer.posZ

            when (mode) {
                "NCP" -> {
                    sendPacket(C04PacketPlayerPosition(x, y + 0.06251000240445849, z, false))
                    sendPacket(C04PacketPlayerPosition(x, y, z, false))
                }
                "Lowest" -> {
                    sendPacket(C04PacketPlayerPosition(x, y + 0.00000000000000356, z, false))
                    sendPacket(C04PacketPlayerPosition(x, y, z, false))
                }
                "Phase" -> sendPacket(C04PacketPlayerPosition(x, y - 0.00000000000000356, z, false))
            }

            mc.thePlayer.onCriticalHit(event.targetEntity)
            delayTimer.reset()
        }
    }

    private fun canCrit(entity: Entity): Boolean {
        return entity is EntityLivingBase && delayTimer.hasTimePassed(delay.toLong()) &&
                mc.thePlayer.onGround && !mc.thePlayer.isOnLadder &&
                !mc.thePlayer.isInWeb && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava &&
                !mc.thePlayer.isRiding && !mc.gameSettings.keyBindJump.isKeyDown
    }
}
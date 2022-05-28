/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.strafe
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.world.Explosion

object Velocity : Module("Velocity", "Allows you to modify the amount of knockback you take.", ModuleCategory.COMBAT) {

    private val mode by ListValue("Mode", arrayOf("Normal", "Strafe", "Reset"), "Normal")
    private val horizontal by FloatValue("Horizontal", 0f, 0f..1f)
    private val vertical by FloatValue("Vertical", 0f, 0f..1f)
    private val time by IntValue("ResetTime", 5, 1..10)

    override val tag
        get() = if (mode == "Normal") "${horizontal}% ${vertical}%" else mode

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (mode == "Normal") {
            if (mc.theWorld == null || mc.thePlayer == null)
                return

            if (packet is S12PacketEntityVelocity) {

                val entity = mc.theWorld.getEntityByID(packet.entityID) ?: return

                if (entity != mc.thePlayer) {

                    if (horizontal == 0f && vertical == 0f)
                        event.cancelEvent()

                    packet.motionX = (packet.getMotionX() * horizontal).toInt()
                    packet.motionY = (packet.getMotionY() * vertical).toInt()
                    packet.motionZ = (packet.getMotionZ() * horizontal).toInt()
                }
            }

            if (packet is S27PacketExplosion) {
                event.cancelEvent()

                Explosion(mc.theWorld, null, packet.x, packet.y, packet.z, packet.strength, packet.affectedBlockPositions).doExplosionB(true)
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (mode) {
            "Strafe" -> if (mc.thePlayer.hurtTime > 0) mc.thePlayer.strafe()

            "Reset" -> if (mc.thePlayer.hurtTime == time) {
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
            }
        }
    }
}
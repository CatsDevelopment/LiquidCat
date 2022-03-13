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
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion

class Velocity : Module("Velocity", "Allows you to modify the amount of knockback you take.", ModuleCategory.COMBAT) {

    private val mode = ListValue("Mode", arrayOf("Normal", "Strafe"), "Normal")
    private val horizontal = FloatValue("Horizontal", 0F, 0F, 1F)
    private val vertical = FloatValue("Vertical", 0F, 0F, 1F)

    override val tag: String
        get() = if (mode.get() == "Normal") "${horizontal.get()}% ${vertical.get()}%" else mode.get()

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S12PacketEntityVelocity) {
            if ((mc.theWorld?.getEntityByID(packet.entityID) ?: return) == (mc.thePlayer ?: return)) {
                if (mode.get() == "Normal") {
                    val horizontal = horizontal.get()
                    val vertical = vertical.get()

                    if (horizontal == 0f && vertical == 0f) event.cancelEvent()

                    packet.motionX = (packet.getMotionX() * horizontal).toInt()
                    packet.motionY = (packet.getMotionY() * vertical).toInt()
                    packet.motionZ = (packet.getMotionZ() * horizontal).toInt()
                }
            }
        }

        if (packet is S27PacketExplosion) event.cancelEvent()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mode.get() == "Strafe" && mc.thePlayer.hurtTime > 0) mc.thePlayer.strafe()
    }
}
/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.network.play.server.S27PacketExplosion

@ModuleInfo(name = "Velocity", description = "Allows you to modify the amount of knockback you take.", category = ModuleCategory.COMBAT)
class Velocity : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Normal", "Strafe"), "Normal")
    private val horizontalValue = FloatValue("Horizontal", 0F, 0F, 1F)
    private val verticalValue = FloatValue("Vertical", 0F, 0F, 1F)

    override val tag: String
        get() = if (modeValue.get() == "Normal") "${horizontalValue.get()}% ${verticalValue.get()}%" else modeValue.get()

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S12PacketEntityVelocity) {
            if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer)
                return

            //TODO: Add more modes
            if (modeValue.get() == "Normal") {
                val horizontal = horizontalValue.get()
                val vertical = verticalValue.get()

                if (horizontal == 0F && vertical == 0F) event.cancelEvent()

                packet.motionX = (packet.getMotionX() * horizontal).toInt()
                packet.motionY = (packet.getMotionY() * vertical).toInt()
                packet.motionZ = (packet.getMotionZ() * horizontal).toInt()
            }
        }

        if (packet is S27PacketExplosion) {
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (modeValue.get() == "Strafe" && mc.thePlayer.hurtTime > 0) MovementUtils.strafe()
    }
}
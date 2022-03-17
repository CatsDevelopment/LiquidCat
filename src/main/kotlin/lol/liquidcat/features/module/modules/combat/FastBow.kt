/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.aiming
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.network.play.client.C03PacketPlayer.C05PacketPlayerLook
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

class FastBow : Module("FastBow", "Turns your bow into a machine gun.", ModuleCategory.COMBAT) {

    private val packetsValue = IntValue("Packets", 20, 3..20)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.aiming) {
            sendPacket(C08PacketPlayerBlockPlacement(BlockPos.ORIGIN, 255, mc.thePlayer.currentEquippedItem, 0f, 0f, 0f))

            val yaw = if (RotationUtils.targetRotation != null)
                RotationUtils.targetRotation.yaw
            else
                mc.thePlayer.rotationYaw

            val pitch = if (RotationUtils.targetRotation != null)
                RotationUtils.targetRotation.pitch
            else
                mc.thePlayer.rotationPitch

            repeat(packetsValue.get()) {
                sendPacket(C05PacketPlayerLook(yaw, pitch, mc.thePlayer.onGround))
            }

            sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            mc.thePlayer.itemInUseCount = mc.thePlayer.inventory.getCurrentItem().maxItemUseDuration - 1
        }
    }
}
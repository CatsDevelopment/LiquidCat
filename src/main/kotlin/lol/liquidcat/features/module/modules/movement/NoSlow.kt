/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventState
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.MotionEvent
import lol.liquidcat.event.SlowDownEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.combat.KillAura
import lol.liquidcat.utils.entity.moving
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

//TODO Add more modes

object NoSlow : Module("NoSlow", "Cancels slowness effects.", ModuleCategory.MOVEMENT) {

    private val mode by ListValue("Mode", arrayOf("Vanilla", "NCP"), "Vanilla")
    private val forwardMultiplier by FloatValue("ForwardMultiplier", 1f, 0.2f..1f)
    private val strafeMultiplier by FloatValue("StrafeMultiplier", 1f, 0.2f..1f)
    val soulsand by BoolValue("Soulsand", true)
    val web by BoolValue("Web", true)

    override val tag
        get() = mode

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (mode == "NCP")
            if (mc.thePlayer.moving && (mc.thePlayer.isBlocking || KillAura.blockingStatus))
                when (event.eventState) {
                    EventState.PRE -> sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                    EventState.POST -> sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
                }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        event.forward = forwardMultiplier
        event.strafe = strafeMultiplier
    }
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventState
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.MotionEvent
import lol.liquidcat.event.SlowDownEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.combat.KillAura
import lol.liquidcat.utils.entity.moving
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing

//TODO Add more modes

class NoSlow : Module("NoSlow", "Cancels slowness effects caused by soulsand and using items.", ModuleCategory.MOVEMENT) {

    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "NCP"), "Vanilla")

    private val forwardMultiplier = FloatValue("ForwardMultiplier", 1f, 0.2f, 1f)
    private val strafeMultiplier = FloatValue("StrafeMultiplier", 1f, 0.2f, 1f)

    val soulsandValue = BoolValue("Soulsand", true)
    val webValue = BoolValue("web", true)

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val killAura = LiquidCat.moduleManager[KillAura::class.java] as KillAura

        if (modeValue.get() == "NCP")
            if (mc.thePlayer.moving && (mc.thePlayer.isBlocking || killAura.blockingStatus))
                when (event.eventState) {
                    EventState.PRE -> mc.netHandler.addToSendQueue(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
                    EventState.POST -> mc.netHandler.addToSendQueue(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
                }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        event.forward = forwardMultiplier.get()
        event.strafe = strafeMultiplier.get()
    }
}
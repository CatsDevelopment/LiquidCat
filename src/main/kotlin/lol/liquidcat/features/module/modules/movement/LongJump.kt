/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.MotionEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.moving
import lol.liquidcat.utils.entity.speed
import lol.liquidcat.utils.entity.strafe
import lol.liquidcat.value.FloatValue

//TODO Add modes

class LongJump : Module("LongJump", "Allows you to jump further.", ModuleCategory.MOVEMENT) {

    private val boostValue = FloatValue("Boost", 5f, 1.01f..10f)

    private var jumpTicks = 0

    override fun onEnable() {
        jumpTicks = 0

        if (mc.thePlayer.onGround && mc.thePlayer.moving) {
            mc.thePlayer.jump()
            mc.thePlayer.strafe(mc.thePlayer.speed * boostValue.get().toDouble())
        }
    }

    @EventTarget
    fun onUpdate(event: MotionEvent) {
        jumpTicks++

        mc.thePlayer.strafe(speed = 1.0)

        if (jumpTicks >= 3 && mc.thePlayer.onGround) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0

            state = false
        }
    }
}
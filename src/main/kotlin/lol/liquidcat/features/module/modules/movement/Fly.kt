/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.strafe
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue

//TODO Add more modes

object Fly : Module("Fly", "Allows you to fly.", ModuleCategory.MOVEMENT) {

    private val mode by ListValue("Mode", arrayOf("Vanilla"), "Vanilla")
    private val speed by FloatValue("Speed", 1f, 0.1f..5f)

    override val tag
        get() = mode

    override fun onDisable() {
        mc.thePlayer.setVelocity(0.0, 0.0, 0.0)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (mode) {
            "Vanilla" -> {
                val vanillaSpeed = speed.toDouble()

                mc.thePlayer.capabilities.isFlying = false

                mc.thePlayer.setVelocity(0.0, 0.0, 0.0)

                if (mc.gameSettings.keyBindJump.isKeyDown)
                    mc.thePlayer.motionY += vanillaSpeed

                if (mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY -= vanillaSpeed

                mc.thePlayer.strafe(vanillaSpeed)
            }
        }
    }
}
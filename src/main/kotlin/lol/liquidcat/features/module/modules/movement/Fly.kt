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

class Fly : Module("Fly", "Allows you to fly in survival mode.", ModuleCategory.MOVEMENT) {

    private val modeValue = ListValue("Mode", arrayOf("Vanilla"), "Vanilla")
    private val speedValue = FloatValue("Speed", 2f, 0f..5f)

    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        println(mc.framebuffer.framebufferTexture)
        when (modeValue.get()) {
            "Vanilla" -> {
                val vanillaSpeed = speedValue.get().toDouble()

                mc.thePlayer.capabilities.isFlying = false

                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionZ = 0.0

                if (mc.gameSettings.keyBindJump.isKeyDown)
                    mc.thePlayer.motionY += vanillaSpeed

                if (mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY -= vanillaSpeed

                mc.thePlayer.strafe(speed = vanillaSpeed)
            }
        }
    }
}
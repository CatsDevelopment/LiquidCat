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
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.entity.strafe
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue

@ModuleInfo(name = "Fly", description = "Allows you to fly in survival mode.", category = ModuleCategory.MOVEMENT)
class Fly : Module() {

    val modeValue = ListValue("Mode", arrayOf("Vanilla"), "Vanilla")
    private val vanillaSpeedValue = FloatValue("VanillaSpeed", 2f, 0f, 5f)
    
    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0

        super.onDisable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (modeValue.get()) {
            "Vanilla" -> {
                val vanillaSpeed = vanillaSpeedValue.get()

                mc.thePlayer.capabilities.isFlying = false

                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.motionZ = 0.0

                if (mc.gameSettings.keyBindJump.isKeyDown)
                    mc.thePlayer.motionY += vanillaSpeed.toDouble()

                if (mc.gameSettings.keyBindSneak.isKeyDown)
                    mc.thePlayer.motionY -= vanillaSpeed.toDouble()

                mc.thePlayer.strafe(speed = vanillaSpeed.toDouble())
            }

            //TODO: Add more modes
        }
    }
}
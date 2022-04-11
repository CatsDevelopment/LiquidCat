/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.`fun`

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import net.minecraft.entity.player.EnumPlayerModelParts
import kotlin.random.Random

object SkinDerp : Module("SkinDerp", "Makes your skin blink (Requires multi-layer skin).", ModuleCategory.FUN) {

    private val hat by BoolValue("Hat", true)
    private val jacket by BoolValue("Jacket", true)
    private val leftPants by BoolValue("LeftPants", true)
    private val rightPants by BoolValue("RightPants", true)
    private val leftSleeve by BoolValue("LeftSleeve", true)
    private val rightSleeve by BoolValue("RightSleeve", true)

    private val delay by IntValue("Delay", 0, 0..1000)

    private var prevModelParts = emptySet<EnumPlayerModelParts>()

    private val delayTimer = MSTimer()

    override fun onEnable() {
        prevModelParts = mc.gameSettings.modelParts
    }

    override fun onDisable() {

        // Disable all current model parts
        mc.gameSettings.modelParts.forEach { mc.gameSettings.setModelPartEnabled(it, false) }

        // Enable all old model parts
        prevModelParts.forEach { mc.gameSettings.setModelPartEnabled(it, true) }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (delayTimer.hasTimePassed(delay.toLong())) {
            if (hat)
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.HAT, Random.nextBoolean())
            if (jacket)
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.JACKET, Random.nextBoolean())
            if (leftPants)
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_PANTS_LEG, Random.nextBoolean())
            if (rightPants)
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_PANTS_LEG, Random.nextBoolean())
            if (leftSleeve)
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.LEFT_SLEEVE, Random.nextBoolean())
            if (rightSleeve)
                mc.gameSettings.setModelPartEnabled(EnumPlayerModelParts.RIGHT_SLEEVE, Random.nextBoolean())

            delayTimer.reset()
        }
    }
}
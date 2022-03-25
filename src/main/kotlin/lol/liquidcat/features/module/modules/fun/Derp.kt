/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.`fun`

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue

//TODO Rewrite and add more options

object Derp : Module("Derp", "Makes it look like you were derping around.", ModuleCategory.FUN) {

    private val headless by BoolValue("Headless", false)
    private val spinny by BoolValue("Spinny", false)
    private val increment by FloatValue("Increment", 1f, 0f..50f)

    private var currentSpin = 0F

    val rotation: FloatArray
        get() {
            val derpRotations = floatArrayOf(mc.thePlayer.rotationYaw + (Math.random() * 360 - 180).toFloat(), (Math.random() * 180 - 90).toFloat())

            if (headless)
                derpRotations[1] = 180F

            if (spinny) {
                derpRotations[0] = currentSpin + increment
                currentSpin = derpRotations[0]
            }

            return derpRotations
        }

}
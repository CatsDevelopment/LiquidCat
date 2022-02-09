/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.JumpEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.FloatValue

@ModuleInfo(name = "HighJump", description = "Allows you to jump higher.", category = ModuleCategory.MOVEMENT)
class HighJump : Module() {

    private val heightValue = FloatValue("Height", 2f, 1.01f, 5f)

    override val tag: String
        get() = heightValue.get().toString()

    @EventTarget
    fun onJump(event: JumpEvent) {
        event.motion *= heightValue.get()
    }
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.FloatValue

@ModuleInfo("FOV", "Disables FOV changes caused by speed effect, etc.", ModuleCategory.RENDER)
class FOV : Module() {
    val fovValue = FloatValue("FOV", 1f, 0f, 1.5f)
}
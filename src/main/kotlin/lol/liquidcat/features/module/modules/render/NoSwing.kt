/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue

class NoSwing : Module("NoSwing", "Disabled swing effect when hitting an entity/mining a block.", ModuleCategory.RENDER) {
    val serverSide by BoolValue("ServerSide", true)
}
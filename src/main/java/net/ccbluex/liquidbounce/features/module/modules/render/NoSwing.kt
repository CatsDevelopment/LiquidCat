/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.BoolValue

@ModuleInfo(name = "NoSwing", description = "Disabled swing effect when hitting an entity/mining a block.", category = ModuleCategory.RENDER)
class NoSwing : Module() {
    val serverSideValue = BoolValue("ServerSide", true)
}
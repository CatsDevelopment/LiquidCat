/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import net.ccbluex.liquidbounce.utils.RotationUtils

class HeadRotations : Module("HeadRotations", "Allows you to see server-sided head rotations.", ModuleCategory.RENDER) {

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (RotationUtils.serverRotation != null)
            mc.thePlayer.rotationYawHead = RotationUtils.serverRotation.yaw
    }
}
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
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.color.ColorUtils.rainbow
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import java.awt.Color

object ESP : Module("ESP", "Allows you to see targets through walls.", ModuleCategory.RENDER) {

    val mode by ListValue(
        "Mode",
        arrayOf("Box", "WireFrame", "Outline"),
        "Box"
    )

    private val red by IntValue("Red", 255, 0..255)
    private val green by IntValue("Green", 255, 0..255)
    private val blue by IntValue("Blue", 255, 0..255)
    private val rainbow by BoolValue("Rainbow", false)

    val outlineWidth by FloatValue("Outline-Width", 3f, 0.5f..5f)
    val wireframeWidth by FloatValue("WireFrame-Width", 2f, 0.5f..5f)

    override val tag
        get() = mode

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val color = Color(red, green, blue)

        if (mode == "Box")
            for (entity in mc.theWorld.loadedEntityList)
                if (EntityUtils.isSelected(entity, false))
                    GLUtils.drawEntityBox(entity, color, true, true)
    }

    fun getColor(): Color {
        return if (rainbow) rainbow() else Color(red, green, blue)
    }
}
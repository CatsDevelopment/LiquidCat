/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.KeyEvent
import lol.liquidcat.event.Render2DEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.ui.client.hud.designer.GuiHudDesigner
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.BoolValue
import net.minecraft.client.gui.GuiChat

object HUD : Module("HUD", "Toggles visibility of the HUD.", ModuleCategory.RENDER, hide = true) {

    val blackHotbar by BoolValue("BlackHotbar", true)
    private val blur by BoolValue("Blur", false)
    val fontChat by BoolValue("FontChat", false)
    val cleanChat by BoolValue("CleanChat", true)

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (blur && mc.currentScreen != null && !(mc.currentScreen is GuiChat || mc.currentScreen is GuiHudDesigner))
            GLUtils.blur(10) {
                GLUtils.drawRect(0f, 0f, mc.displayWidth.toFloat(), mc.displayHeight.toFloat(), -1)
            }

        if (mc.currentScreen !is GuiHudDesigner)
            LiquidCat.hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        LiquidCat.hud.update()
    }

    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidCat.hud.handleKey('a', event.key)
    }

    init {
        state = true
    }
}
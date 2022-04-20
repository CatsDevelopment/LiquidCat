/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.*
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.ui.client.hud.designer.GuiHudDesigner
import lol.liquidcat.value.BoolValue
import net.minecraft.client.gui.GuiChat
import net.minecraft.util.ResourceLocation

object HUD : Module("HUD", "Toggles visibility of the HUD.", ModuleCategory.RENDER, hide = true) {

    val blackHotbar by BoolValue("BlackHotbar", true)
    val inventoryParticle by BoolValue("InventoryParticle", false)
    private val blur by BoolValue("Blur", false)
    val fontChat by BoolValue("FontChat", false)
    val cleanChat by BoolValue("CleanChat", true)

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
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

    @EventTarget
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return

        val shaderName = "${LiquidCat.CLIENT_NAME.toLowerCase()}/blur.json"

        if (blur && !mc.entityRenderer.isShaderActive && event.guiScreen != null && !(event.guiScreen is GuiChat || event.guiScreen is GuiHudDesigner))
            mc.entityRenderer.loadShader(ResourceLocation(shaderName))
        else if (mc.entityRenderer.shaderGroup != null && mc.entityRenderer.shaderGroup.shaderGroupName.contains(shaderName))
            mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }
}
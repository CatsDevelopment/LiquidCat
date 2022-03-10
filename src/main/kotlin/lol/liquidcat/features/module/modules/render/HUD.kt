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
import lol.liquidcat.value.BoolValue
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.minecraft.client.gui.GuiChat
import net.minecraft.util.ResourceLocation

class HUD : Module("HUD", "Toggles visibility of the HUD.", ModuleCategory.RENDER, array = false) {

    val blackHotbarValue = BoolValue("BlackHotbar", true)
    val inventoryParticle = BoolValue("InventoryParticle", false)
    private val blurValue = BoolValue("Blur", false)
    val fontChatValue = BoolValue("FontChat", false)

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

        if (blurValue.get() && !mc.entityRenderer.isShaderActive && event.guiScreen != null && !(event.guiScreen is GuiChat || event.guiScreen is GuiHudDesigner))
            mc.entityRenderer.loadShader(ResourceLocation(shaderName))
        else if (mc.entityRenderer.shaderGroup != null && mc.entityRenderer.shaderGroup.shaderGroupName.contains(shaderName))
            mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }
}
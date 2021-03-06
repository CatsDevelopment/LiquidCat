/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element.elements

import lol.liquidcat.ui.client.hud.element.Border
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.ui.client.hud.element.Align
import lol.liquidcat.utils.mc
import lol.liquidcat.value.ListValue
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11

/**
 * CustomHUD Armor element
 *
 * Shows a horizontal display of current armor
 */
@ElementInfo("Armor")
class Armor : Element(-8.0, 57.0, align = Align(Align.Horizontal.MIDDLE, Align.Vertical.DOWN)) {

    private val mode by ListValue("Alignment", arrayOf("Horizontal", "Vertical"), "Horizontal")

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        if (mc.playerController.isNotCreative) {
            GL11.glPushMatrix()

            val renderItem = mc.renderItem
            val isInsideWater = mc.thePlayer.isInsideOfMaterial(Material.water)

            var x = 1
            var y = if (isInsideWater) 10 else 0

            for (index in 3 downTo 0) {
                val stack = mc.thePlayer.inventory.armorInventory[index] ?: continue

                renderItem.renderItemIntoGUI(stack, x, y)
                renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
                if (mode.equals("Horizontal", true))
                    x += 18
                else if (mode.equals("Vertical", true))
                    y += 18
            }

            GlStateManager.enableAlpha()
            GlStateManager.disableBlend()
            GlStateManager.disableLighting()
            GlStateManager.disableCull()
            GL11.glPopMatrix()
        }

        return if (mode.equals("Horizontal", true))
            Border(0F, 0F, 72F, 17F)
        else
            Border(0F, 0F, 18F, 72F)
    }
}
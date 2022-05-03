/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element.elements

import lol.liquidcat.ui.client.hud.element.Border
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.utils.mc
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import java.awt.Color

@ElementInfo(name = "Inventory", single = true)
class Inventory : Element() {

    private val title by ListValue("Title", arrayOf("Right", "Center", "Left"), "Right")
    private val space by IntValue("Space", 5, 1..10)

    private val red by IntValue("Red", 0, 0..255)
    private val green by IntValue("Green", 0, 0..255)
    private val blue by IntValue("Blue", 0, 0..255)
    private val alpha by IntValue("Alpha", 50, 0..255)

    private val blur by BoolValue("Blur", true)

    override fun drawElement(): Border {

        val textWidth = Fonts.nunitoBold40.getStringWidth("Inventory")
        val size = 16 + space
        val halfSpace = space / 2
        val invWidth = 9f * size
        val invHeight = 3f * size

        val titleX = when (title) {
            "Right" -> 5f
            "Center" -> invWidth / 2 - textWidth / 2 + 2.5f
            "Left" -> invWidth - textWidth - 5f

            else -> 5f
        }

        if (blur)
            GLUtils.blur(10) {
                GLUtils.drawRoundedRect(0f, -15f, invWidth, invHeight, 3f, Color.WHITE)
            }

        GLUtils.drawRoundedRect(0f, -15f, invWidth, invHeight, 3f, Color(red, green, blue, alpha))
        Fonts.nunitoBold40.drawString("Inventory", titleX, -10f, Color.WHITE.rgb)

        for (i in 9..35) {

            val x = (i % 9) * size + halfSpace
            val y = (i / 9 - 1) * size + halfSpace
            val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack ?: continue

            renderItemStack(stack, x, y)
        }

        return Border(0f, -15f, invWidth, invHeight)
    }

    private fun renderItemStack(stack: ItemStack, x: Int, y: Int) {
        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)

        RenderHelper.enableGUIStandardItemLighting()

        mc.renderItem.renderItemAndEffectIntoGUI(stack, x, y)
        mc.renderItem.renderItemOverlays(Fonts.nunito40, stack, x, y)

        RenderHelper.disableStandardItemLighting()

        GlStateManager.disableRescaleNormal()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }
}
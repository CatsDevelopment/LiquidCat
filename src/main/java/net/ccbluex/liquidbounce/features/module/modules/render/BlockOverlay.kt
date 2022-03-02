/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.utils.block.isClickable
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntegerValue
import net.ccbluex.liquidbounce.utils.render.ColorUtils.rainbow
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11
import java.awt.Color

@ModuleInfo("BlockOverlay", "Allows you to change the design of the block overlay.", ModuleCategory.RENDER)
class BlockOverlay : Module() {

    private val colorRedValue = IntegerValue("R", 68, 0, 255)
    private val colorGreenValue = IntegerValue("G", 117, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val colorRainbow = BoolValue("Rainbow", false)

    val currentBlock: BlockPos?
        get() {
            val blockPos = mc.objectMouseOver?.blockPos ?: return null

            if (blockPos.isClickable() && mc.theWorld.worldBorder.contains(blockPos))
                return blockPos

            return null
        }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val blockPos = currentBlock ?: return
        val block = mc.theWorld.getBlockState(blockPos).block ?: return
        val partialTicks = event.partialTicks
        val color = if (colorRainbow.get()) rainbow(0.4F) else Color(colorRedValue.get(),
                colorGreenValue.get(), colorBlueValue.get(), (0.4F * 255).toInt())

        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GLUtils.glColor(color)
        GL11.glLineWidth(2F)
        GlStateManager.disableTexture2D()
        GlStateManager.depthMask(false)

        block.setBlockBoundsBasedOnState(mc.theWorld, blockPos)

        val x = mc.thePlayer.lastTickPosX + (mc.thePlayer.posX - mc.thePlayer.lastTickPosX) * partialTicks
        val y = mc.thePlayer.lastTickPosY + (mc.thePlayer.posY - mc.thePlayer.lastTickPosY) * partialTicks
        val z = mc.thePlayer.lastTickPosZ + (mc.thePlayer.posZ - mc.thePlayer.lastTickPosZ) * partialTicks

        val axisAlignedBB = block.getSelectedBoundingBox(mc.theWorld, blockPos)
                .expand(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026)
                .offset(-x, -y, -z)

        GLUtils.drawOutlinedBB(axisAlignedBB)
        GLUtils.drawFilledBB(axisAlignedBB)
        GlStateManager.depthMask(true)
        GlStateManager.enableTexture2D()
        GlStateManager.disableBlend()
        GlStateManager.resetColor()
    }
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import co.uk.hexeption.utils.OutlineUtils
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.world.ChestAura.clickedBlocks
import lol.liquidcat.utils.ClientUtils.disableFastRender
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntityChest
import org.lwjgl.opengl.GL11
import java.awt.Color

object StorageESP : Module("StorageESP", "Allows you to see chests, dispensers, etc. through walls.", ModuleCategory.RENDER) {

    private val mode by ListValue(
        "Mode",
        arrayOf("Box", "Outline", "WireFrame"),
        "Outline"
    )

    private val red by IntValue("Red", 255, 0..255)
    private val green by IntValue("Green", 255, 0..255)
    private val blue by IntValue("Blue", 255, 0..255)

    private val outlineWidth by FloatValue("Outline-Width", 3f, 0.5f..5f)
    private val wireframeWidth by FloatValue("WireFrame-Width", 2f, 0.5f..5f)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        try {
            if (mode == "Outline") {
                disableFastRender()
                OutlineUtils.checkSetupFBO()
            }

            val gamma = mc.gameSettings.gammaSetting
            mc.gameSettings.gammaSetting = 100000f

            for (tileEntity in mc.theWorld.loadedTileEntityList) {
                if (tileEntity is TileEntityChest && !clickedBlocks.contains(tileEntity.getPos())) {
                    when (mode) {
                        "Box" -> GLUtils.drawBlockBox(
                            tileEntity.pos,
                            getColor(),
                            true,
                            true
                        )

                        "Outline" -> {
                            GLUtils.glColor(getColor())
                            OutlineUtils.renderOne(outlineWidth)
                            TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                            OutlineUtils.renderTwo()
                            TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                            OutlineUtils.renderThree()
                            TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                            OutlineUtils.renderFour(getColor())
                            TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                            OutlineUtils.renderFive()
                            OutlineUtils.setColor(Color.WHITE)
                        }

                        "WireFrame" -> {
                            GL11.glPushMatrix()
                            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS)
                            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE)
                            GL11.glDisable(GL11.GL_TEXTURE_2D)
                            GL11.glDisable(GL11.GL_LIGHTING)
                            GL11.glDisable(GL11.GL_DEPTH_TEST)
                            GL11.glEnable(GL11.GL_LINE_SMOOTH)
                            GL11.glEnable(GL11.GL_BLEND)
                            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                            TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                            GLUtils.glColor(getColor())
                            GL11.glLineWidth(wireframeWidth)
                            TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, event.partialTicks, -1)
                            GL11.glPopAttrib()
                            GL11.glPopMatrix()
                        }
                    }
                }
            }

            GLUtils.glColor(Color(255, 255, 255))
            mc.gameSettings.gammaSetting = gamma
        } catch (ignored: Exception) {}
    }

    private fun getColor() = Color(red, green, blue)
}
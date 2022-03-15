/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import co.uk.hexeption.utils.OutlineUtils
import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render2DEvent
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.world.ChestAura.clickedBlocks
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntegerValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.utils.ClientUtils
import lol.liquidcat.utils.render.shader.shaders.GlowShader
import lol.liquidcat.utils.render.shader.shaders.OutlineShader
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntityChest
import org.lwjgl.opengl.GL11
import java.awt.Color

class StorageESP : Module("StorageESP", "Allows you to see chests, dispensers, etc. through walls.", ModuleCategory.RENDER) {

    private val modeValue = ListValue(
        "Mode",
        arrayOf("Box", "Outline", "ShaderOutline", "ShaderGlow", "WireFrame"),
        "Outline"
    )

    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)

    private val outlineWidth = FloatValue("Outline-Width", 3f, 0.5f, 5f)
    private val wireframeWidth = FloatValue("WireFrame-Width", 2f, 0.5f, 5f)

    private val shaderOutlineRadius = FloatValue("ShaderOutline-Radius", 1.35f, 1f, 2f)
    private val shaderGlowRadius = FloatValue("ShaderGlow-Radius", 2.3f, 2f, 3f)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        try {
            val mode = modeValue.get()
            if (mode == "Outline") {
                ClientUtils.disableFastRender()
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
                            OutlineUtils.renderOne(outlineWidth.get())
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
                            GL11.glLineWidth(wireframeWidth.get())
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

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val mode = modeValue.get()
        val shader = when(mode) {
            "ShaderOutline" -> OutlineShader
            "ShaderGlow" -> GlowShader

            else -> return
        }

        shader.startDraw(event.partialTicks)

        try {
            val renderManager = mc.renderManager
            for (entity in mc.theWorld.loadedTileEntityList)
                if (entity is TileEntityChest)
                    TileEntityRendererDispatcher.instance.renderTileEntityAt(
                        entity,
                        entity.pos.x - renderManager.renderPosX,
                        entity.pos.y - renderManager.renderPosY,
                        entity.pos.z - renderManager.renderPosZ,
                        event.partialTicks
                    )
        } catch (e: Exception) {
            LiquidCat.logger.error("An error occurred while rendering all storages for shader esp", e)
        }

        val radius = when(mode) {
            "ShaderOutline" -> shaderOutlineRadius.get()
            "ShaderGlow" -> shaderGlowRadius.get()

            else -> 1f
        }

        shader.stopDraw(getColor(), radius, 1f)
    }

    private fun getColor() = Color(redValue.get(), greenValue.get(), blueValue.get())
}
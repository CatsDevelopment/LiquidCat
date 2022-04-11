/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render2DEvent
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.WorldToScreen
import lol.liquidcat.utils.render.shader.shaders.GlowShader
import lol.liquidcat.utils.render.shader.shaders.OutlineShader
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.utils.render.ColorUtils.rainbow
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector3f
import java.awt.Color

object ESP : Module("ESP", "Allows you to see targets through walls.", ModuleCategory.RENDER) {

    val mode by ListValue(
        "Mode",
        arrayOf("ShaderOutline", "ShaderGlow", "Box", "WireFrame", "2D", "Outline"),
        "Box"
    )

    private val shaderMode by ListValue("ShaderMode", arrayOf("Outline", "Box"), "Outline")

    private val red by IntValue("Red", 255, 0..255)
    private val green by IntValue("Green", 255, 0..255)
    private val blue by IntValue("Blue", 255, 0..255)
    private val rainbow by BoolValue("Rainbow", false)

    val outlineWidth by FloatValue("Outline-Width", 3f, 0.5f..5f)
    val wireframeWidth by FloatValue("WireFrame-Width", 2f, 0.5f..5f)

    private val shaderOutlineRadius by FloatValue("ShaderOutline-Radius", 1.35f, 1f..2f)
    private val shaderGlowRadius by FloatValue("ShaderGlow-Radius", 2.3f, 2f..3f)

    override val tag: String
        get() = mode

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val mvMatrix = WorldToScreen.getMatrix(GL11.GL_MODELVIEW_MATRIX)
        val projectionMatrix = WorldToScreen.getMatrix(GL11.GL_PROJECTION_MATRIX)
        val real2d = mode == "2D"

        if (real2d) {
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glMatrixMode(GL11.GL_PROJECTION)
            GL11.glPushMatrix()
            GL11.glLoadIdentity()
            GL11.glOrtho(0.0, mc.displayWidth.toDouble(), mc.displayHeight.toDouble(), 0.0, -1.0, 1.0)
            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPushMatrix()
            GL11.glLoadIdentity()
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GlStateManager.enableTexture2D()
            GlStateManager.depthMask(true)
            GL11.glLineWidth(1f)
        }

        for (entity in mc.theWorld.loadedEntityList) {
            if (EntityUtils.isSelected(entity, false)) {
                val color = getColor()

                when (mode) {
                    "Box" -> GLUtils.drawEntityBox(
                        entity,
                        color,
                        true,
                        true
                    )

                    "2D" -> {
                        val bb = GLUtils.interpolateEntityBB(entity)

                        val boxVertices = arrayOf(
                            doubleArrayOf(bb.minX, bb.minY, bb.minZ),
                            doubleArrayOf(bb.minX, bb.maxY, bb.minZ),
                            doubleArrayOf(bb.maxX, bb.maxY, bb.minZ),
                            doubleArrayOf(bb.maxX, bb.minY, bb.minZ),
                            doubleArrayOf(bb.minX, bb.minY, bb.maxZ),
                            doubleArrayOf(bb.minX, bb.maxY, bb.maxZ),
                            doubleArrayOf(bb.maxX, bb.maxY, bb.maxZ),
                            doubleArrayOf(bb.maxX, bb.minY, bb.maxZ)
                        )

                        var minX = Float.MAX_VALUE
                        var minY = Float.MAX_VALUE
                        var maxX = -1f
                        var maxY = -1f

                        for (boxVertex in boxVertices) {
                            val screenPos = WorldToScreen.toScreen(
                                Vector3f(
                                    boxVertex[0].toFloat(), boxVertex[1].toFloat(), boxVertex[2].toFloat()
                                ), mvMatrix, projectionMatrix, mc.displayWidth, mc.displayHeight
                            ) ?: continue

                            minX = screenPos.x.coerceAtMost(minX)
                            minY = screenPos.y.coerceAtMost(minY)
                            maxX = screenPos.x.coerceAtLeast(maxX)
                            maxY = screenPos.y.coerceAtLeast(maxY)
                        }

                        if (minX > 0 || minY > 0 || maxX <= mc.displayWidth || maxY <= mc.displayWidth) {
                            GL11.glColor4f(color.red / 255f, color.green / 255f, color.blue / 255f, 1f)
                            GL11.glBegin(GL11.GL_LINE_LOOP)
                            GL11.glVertex2f(minX, minY)
                            GL11.glVertex2f(minX, maxY)
                            GL11.glVertex2f(maxX, maxY)
                            GL11.glVertex2f(maxX, minY)
                            GL11.glEnd()
                        }
                    }
                }
            }
        }

        if (real2d) {
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glMatrixMode(GL11.GL_PROJECTION)
            GL11.glPopMatrix()
            GL11.glMatrixMode(GL11.GL_MODELVIEW)
            GL11.glPopMatrix()
            GL11.glPopAttrib()
        }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val shader = when(mode) {
            "ShaderOutline" -> OutlineShader
            "ShaderGlow" -> GlowShader

            else -> return
        }

        shader.startDraw(event.partialTicks)
        renderNameTags = false

        try {
            for (entity in mc.theWorld.loadedEntityList)
                if (EntityUtils.isSelected(entity, false))
                    if (shaderMode == "Outline")
                        mc.renderManager.renderEntityStatic(entity, mc.timer.renderPartialTicks, true)
                    else
                        GLUtils.drawFilledBB(GLUtils.interpolateEntityBB(entity))
        } catch (e: Exception) {
            LiquidCat.logger.error("An error occurred while rendering all entities for shader esp", e)
        }

        renderNameTags = true

        val radius = when(mode) {
            "ShaderOutline" -> shaderOutlineRadius
            "ShaderGlow" -> shaderGlowRadius

            else -> 1f
        }

        shader.stopDraw(getColor(), radius, 1f)
    }

    fun getColor(): Color {
        return if (rainbow) rainbow() else Color(red, green, blue)
    }

    @JvmField
    var renderNameTags = true
}
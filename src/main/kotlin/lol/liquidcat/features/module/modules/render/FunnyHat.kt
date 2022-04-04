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
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// TODO: Add rainbow, neon effects (shaders)

object FunnyHat : Module("FunnyHat", "funny china hat", ModuleCategory.RENDER) {

    private val radius by FloatValue("Radius", 0.6f, 0.5f..2f)
    private val height by FloatValue("Height", 0.3f, 0.1f..1f)

    private val rainbow by BoolValue("Rainbow", true)
    private val rainbowSpeed by IntValue("RainbowSpeed", 25, 1..50)

    private val red by IntValue("Red", 255, 0..255)
    private val green by IntValue("Green", 150, 0..255)
    private val blue by IntValue("Blue", 100, 0..255)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val pos = GLUtils.interpolate(mc.thePlayer)

        glPushMatrix()

        glTranslated(pos.x, pos.y + mc.thePlayer.height - if (mc.thePlayer.isSneaking) 0.08 else 0.0, pos.z)
        if (rainbow) glRotated(System.currentTimeMillis() / rainbowSpeed % 360.0, 0.0, 1.0, 0.0)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE)
        glEnable(GL_LINE_SMOOTH)
        glLineWidth(1f)
        glDisable(GL_CULL_FACE)
        glDisable(GL_TEXTURE_2D)
        glDisable(GL_DEPTH_TEST)
        glDepthMask(false)
        glShadeModel(GL_SMOOTH)

        glBegin(GL_TRIANGLE_FAN)
        glVertex3d(0.0, height.toDouble(), 0.0)
        for (i in 0..360) {
            val color = if (rainbow) Color.getHSBColor(i / 360f, 1f, 1f) else Color(red, green, blue)

            GLUtils.glColor(color.red, color.green, color.blue, 130)
            glVertex3d((radius * sin(i * PI / 180)), 0.0, (radius * cos(i * PI / 180)))
        }
        glEnd()

        glBegin(GL_LINE_LOOP)
        for (i in 0..360) {
            val color = if (rainbow) Color.getHSBColor(i / 360f, 1f, 1f) else Color(red, green, blue)

            GLUtils.glColor(color.red, color.green, color.blue)
            glVertex3d((radius * sin(i * PI / 180)), 0.0, (radius * cos(i * PI / 180)))
        }
        glEnd()

        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
        glEnable(GL_CULL_FACE)
        glEnable(GL_TEXTURE_2D)
        glEnable(GL_DEPTH_TEST)
        glDepthMask(true)
        glShadeModel(GL_FLAT)

        glPopMatrix()
    }
}
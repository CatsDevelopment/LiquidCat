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
import lol.liquidcat.utils.entity.renderPos
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.toRadians
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

object FunnyHat : Module("FunnyHat", "funny china hat", ModuleCategory.RENDER) {

    private val radius by FloatValue("Radius", 0.6f, 0.5f..2f)
    private val height by FloatValue("Height", 0.3f, 0.1f..1f)

    private val rainbow by BoolValue("Rainbow", false)

    private val start by FloatValue("Start", 0.5f, 0f..1f)
    private val end by FloatValue("End", 0.3f, 0f..1f)

    private val spinSpeed by IntValue("SpinSpeed", 25, 1..50)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val pos = mc.thePlayer.renderPos

        glPushMatrix()

        glTranslated(pos.x, pos.y + mc.thePlayer.height - if (mc.thePlayer.isSneaking) 0.08 else 0.0, pos.z)
        glRotated(System.currentTimeMillis() / spinSpeed % 360.0, 0.0, 1.0, 0.0)

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glLineWidth(1f)
        glDisable(GL_CULL_FACE)
        glDisable(GL_TEXTURE_2D)
        glDisable(GL_DEPTH_TEST)
        glDepthMask(false)
        glDisable(GL_ALPHA_TEST)
        glShadeModel(GL_SMOOTH)

        glBegin(GL_TRIANGLE_FAN)
        GLUtils.glColor(255, 255, 255, 130)
        glVertex3d(0.0, height.toDouble(), 0.0)
        for (i in 0..360) {
            val color = if (rainbow) Color.getHSBColor(i / 360f, 1f, 1f) else ColorUtils.hsbTransition(start, end, i)

            GLUtils.glColor(color.red, color.green, color.blue, 130)
            glVertex3d((radius * sin(i.toDouble().toRadians())), 0.0, (radius * cos(i.toDouble().toRadians())))
        }
        glEnd()

        glBegin(GL_LINE_LOOP)
        for (i in 0..360) {
            val color = if (rainbow) Color.getHSBColor(i / 360f, 1f, 1f) else ColorUtils.hsbTransition(start, end, i)

            GLUtils.glColor(color.red, color.green, color.blue)
            glVertex3d((radius * sin(i.toDouble().toRadians())), 0.0, (radius * cos(i.toDouble().toRadians())))
        }
        glEnd()

        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
        glEnable(GL_CULL_FACE)
        glEnable(GL_TEXTURE_2D)
        glEnable(GL_DEPTH_TEST)
        glDepthMask(true)
        glEnable(GL_ALPHA_TEST)
        glShadeModel(GL_FLAT)

        glPopMatrix()
    }
}
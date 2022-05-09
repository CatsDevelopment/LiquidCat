/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.JumpEvent
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.render.color.ColorUtils
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.toRadians
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.cos
import kotlin.math.sin

object FunnyJump : Module("FunnyJump", "funny jumpa circles", ModuleCategory.RENDER) {

    private val disappearTime by IntValue("Time", 1000, 1000..3000)
    private val radius by FloatValue("Radius", 2f, 1f..5f)

    private val rainbow by BoolValue("Rainbow", false)

    private val start by FloatValue("Start", 0.5f, 0f..1f)
    private val end by FloatValue("End", 0.3f, 0f..1f)

    private val circles = mutableListOf<Circle>()

    @EventTarget
    fun onJump(event: JumpEvent) {
        circles.add(Circle(System.currentTimeMillis(), mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ))
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        circles.removeIf { System.currentTimeMillis() > it.time + disappearTime }

        glPushMatrix()

        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glDisable(GL_CULL_FACE)
        glDisable(GL_TEXTURE_2D)
        glDisable(GL_DEPTH_TEST)
        glDepthMask(false)
        glDisable(GL_ALPHA_TEST)
        glShadeModel(GL_SMOOTH)

        circles.forEach { it.draw() }

        glDisable(GL_BLEND)
        glEnable(GL_CULL_FACE)
        glEnable(GL_TEXTURE_2D)
        glEnable(GL_DEPTH_TEST)
        glDepthMask(true)
        glEnable(GL_ALPHA_TEST)
        glShadeModel(GL_FLAT)

        glPopMatrix()
    }

    class Circle(val time: Long, val x: Double, val y: Double, val z: Double) {
        fun draw() {
            val dif = (System.currentTimeMillis() - time)
            val c = 255 - (dif / disappearTime.toFloat()) * 255

            glPushMatrix()

            glTranslated(x - mc.renderManager.viewerPosX, y - mc.renderManager.viewerPosY, z - mc.renderManager.viewerPosZ)

            glBegin(GL_TRIANGLE_STRIP)
            for (i in 0..360) {
                val color = if (rainbow) Color.getHSBColor(i / 360f, 1f, 1f) else ColorUtils.hsbTransition(start, end, i)
                val x = (dif * radius * 0.001 * sin(i.toDouble().toRadians()))
                val z = (dif * radius * 0.001 * cos(i.toDouble().toRadians()))

                GLUtils.glColor(color.red, color.green, color.blue, 0)
                glVertex3d(x / 2, 0.0, z / 2)

                GLUtils.glColor(color.red, color.green, color.blue, c.toInt())
                glVertex3d(x, 0.0, z)
            }
            glEnd()

            glPopMatrix()
        }
    }
}
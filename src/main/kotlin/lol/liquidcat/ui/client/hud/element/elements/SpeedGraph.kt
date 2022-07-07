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
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import org.lwjgl.opengl.GL11
import kotlin.math.pow
import kotlin.math.sqrt

@ElementInfo("SpeedGraph", true)
class SpeedGraph : Element(90.0, 112.0, align = Align(Align.Horizontal.MIDDLE, Align.Vertical.DOWN)) {

    private val multiplier by FloatValue("Multiplier", 20f, 10f..50f)
    private val height by IntValue("Height", 60, 30..150)
    private val width by IntValue("Width", 180, 100..300)
    private val thickness by FloatValue("Thickness", 1f, 1f..3f)
    private val red by IntValue("Red", 60, 0..255)
    private val green by IntValue("Green", 150, 0..255)
    private val blue by IntValue("Blue", 255, 0..255)

    private val speedList = mutableListOf<Double>()
    private var lastTick = -1

    override fun drawElement(): Border {
        if (lastTick != mc.thePlayer.ticksExisted) {
            lastTick = mc.thePlayer.ticksExisted

            val x = mc.thePlayer.posX - mc.thePlayer.prevPosX
            val z = mc.thePlayer.posZ - mc.thePlayer.prevPosZ

            var speed = sqrt(x.pow(2) + z.pow(2))

            if (speed < 0) {
                speed = -speed
            }

            speedList.add(speed)
            while (speedList.size > width) {
                speedList.removeAt(0)
            }
        }

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(thickness)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)
        GLUtils.glColor(red, green, blue)

        GL11.glBegin(GL11.GL_LINES)

        val size = speedList.size
        val start = (if (size > width) size - width else 0)
        for (i in start until size - 1) {
            val y = speedList[i] * multiplier
            val y1 = speedList[i + 1] * multiplier

            GL11.glVertex2d(i.toDouble() - start, height + 1 - y.coerceAtMost(height.toDouble()))
            GL11.glVertex2d(i + 1.0 - start, height + 1 - y1.coerceAtMost(height.toDouble()))
        }

        GL11.glEnd()

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)

        return Border(0F, 0F, width.toFloat(), height.toFloat() + 2)
    }
}
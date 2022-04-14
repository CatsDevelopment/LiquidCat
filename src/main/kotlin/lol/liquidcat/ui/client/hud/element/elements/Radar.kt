/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element.elements

import lol.liquidcat.ui.client.hud.element.Border
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.ui.client.hud.element.Side
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.utils.mc
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.toDegrees
import lol.liquidcat.utils.toRadians
import lol.liquidcat.value.IntValue
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@ElementInfo(name = "Radar", single = true)
class Radar(x: Double = 5.0, y: Double = 5.0, scale: Float = 1F, side: Side = Side(Side.Horizontal.LEFT, Side.Vertical.UP)) : Element(x, y, scale, side) {

    private val red by IntValue("Red", 0, 0..255)
    private val green by IntValue("Green", 0, 0..255)
    private val blue by IntValue("Blue", 0, 0..255)
    private val alpha by IntValue("Alpha", 140, 0..255)

    override fun drawElement(): Border {
        GLUtils.drawRect(0f, 0f, 100f, 100f, Color(red, green, blue, alpha).rgb)

        GL11.glPushMatrix()
        GL11.glEnable(GL11.GL_SCISSOR_TEST)

        GLUtils.makeScissorBox(
            renderX.toFloat() * scale,
            renderY.toFloat() * scale,
            (100 + renderX.toFloat()) * scale,
            (100 + renderY.toFloat()) * scale
        )

        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)
        GL11.glPointSize(10f)
        GLUtils.glColor(Color.WHITE)

        GL11.glBegin(GL11.GL_POINTS)

        for (entity in mc.theWorld.loadedEntityList) {
            if (EntityUtils.isSelected(entity, false)) {
                val entPos = GLUtils.interpolate(entity)
                val plrPos = GLUtils.interpolate(mc.thePlayer)

                val xDiff = entPos.x - plrPos.x
                val zDiff = entPos.z - plrPos.z

                val angleDiff = atan2(xDiff, zDiff).toDegrees()
                val angle = ((mc.thePlayer.rotationYaw + angleDiff) % 360).toRadians()
                val hyp = hypot(xDiff, zDiff) * 4

                GL11.glVertex2f((50 - hyp * sin(angle)).toFloat(), (50 - hyp * cos(angle)).toFloat())
            }
        }

        GL11.glEnd()

        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_POINT_SMOOTH)

        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GL11.glPopMatrix()

        return Border(0f, 0f, 100f, 100f)
    }
}
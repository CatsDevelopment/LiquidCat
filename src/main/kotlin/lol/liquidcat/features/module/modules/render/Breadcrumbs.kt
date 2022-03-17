/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.render.GLUtils.glColor
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.utils.render.ColorUtils.rainbow
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

class Breadcrumbs : Module("Breadcrumbs", "Leaves a trail behind you.", ModuleCategory.RENDER) {

    val colorRedValue = IntValue("R", 255, 0..255)
    val colorGreenValue = IntValue("G", 179, 0..255)
    val colorBlueValue = IntValue("B", 72, 0..255)
    val colorRainbow = BoolValue("Rainbow", false)

    private val positions = LinkedList<DoubleArray>()

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val color = if (colorRainbow.get()) rainbow() else Color(
            colorRedValue.get(),
            colorGreenValue.get(),
            colorBlueValue.get()
        )

        synchronized(positions) {
            GL11.glPushMatrix()
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            mc.entityRenderer.disableLightmap()
            GL11.glBegin(GL11.GL_LINE_STRIP)
            glColor(color)

            val renderPosX = mc.renderManager.viewerPosX
            val renderPosY = mc.renderManager.viewerPosY
            val renderPosZ = mc.renderManager.viewerPosZ

            for (pos in positions) GL11.glVertex3d(pos[0] - renderPosX, pos[1] - renderPosY, pos[2] - renderPosZ)

            GL11.glColor4d(1.0, 1.0, 1.0, 1.0)
            GL11.glEnd()
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glPopMatrix()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        synchronized(positions) {
            positions.add(
                doubleArrayOf(
                    mc.thePlayer.posX,
                    mc.thePlayer.entityBoundingBox.minY,
                    mc.thePlayer.posZ
                )
            )
        }
    }

    override fun onEnable() {
        if (mc.thePlayer == null) return

        synchronized(positions) {
            positions.add(
                doubleArrayOf(
                    mc.thePlayer.posX,
                    mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight() / 2,
                    mc.thePlayer.posZ
                )
            )
            positions.add(doubleArrayOf(mc.thePlayer.posX, mc.thePlayer.entityBoundingBox.minY, mc.thePlayer.posZ))
        }
    }

    override fun onDisable() {
        synchronized(positions) { positions.clear() }
    }
}
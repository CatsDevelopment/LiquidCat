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
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11
import java.awt.Color

class Tracers : Module("Tracers", "Draws a line to targets around you.", ModuleCategory.RENDER) {

    private val redValue = IntValue("Red", 255, 0..255)
    private val greenValue = IntValue("Green", 255, 0..255)
    private val blueValue = IntValue("Blue", 255, 0..255)
    private val thicknessValue = FloatValue("Thickness", 2f, 1f..5f)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_LINE_SMOOTH)
        GL11.glLineWidth(thicknessValue.get())
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glShadeModel(GL11.GL_SMOOTH)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_ALPHA_TEST)
        GL11.glDepthMask(false)

        GL11.glBegin(GL11.GL_LINES)

        for (entity in mc.theWorld.loadedEntityList) {
            if (EntityUtils.isSelected(entity, false))
                drawTraces(entity, Color(redValue.get(), greenValue.get(), blueValue.get(), 100))
        }

        GL11.glEnd()

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glShadeModel(GL11.GL_FLAT)
        GL11.glDisable(GL11.GL_LINE_SMOOTH)
        GL11.glEnable(GL11.GL_ALPHA_TEST)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GlStateManager.resetColor()
    }

    private fun drawTraces(entity: Entity, color: Color) {
        val x = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks
                - mc.renderManager.renderPosX)
        val y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks
                - mc.renderManager.renderPosY)
        val z = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks
                - mc.renderManager.renderPosZ)

        val eyeVector = Vec3(0.0, 0.0, 1.0)
                .rotatePitch((-Math.toRadians(mc.thePlayer.rotationPitch.toDouble())).toFloat())
                .rotateYaw((-Math.toRadians(mc.thePlayer.rotationYaw.toDouble())).toFloat())

        GLUtils.glColor(color)
        GL11.glVertex3d(eyeVector.xCoord, eyeVector.yCoord + mc.thePlayer.eyeHeight, eyeVector.zCoord)

        GLUtils.glColor(Color(redValue.get(), greenValue.get(), blueValue.get(), 0))
        GL11.glVertex3d(x, y + entity.height / 2, z)
    }
}

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
import lol.liquidcat.utils.entity.renderPos
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.toRadians
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.Entity
import net.minecraft.util.Vec3
import org.lwjgl.opengl.GL11.*

object Tracers : Module("Tracers", "Draws a line to targets around you.", ModuleCategory.RENDER) {

    private val red by IntValue("Red", 90, 0..255)
    private val green by IntValue("Green", 120, 0..255)
    private val blue by IntValue("Blue", 255, 0..255)
    private val transparency by IntValue("Transparency", 150, 0..255)

    private val thickness by FloatValue("Thickness", 2f, 1f..5f)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        glEnable(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glEnable(GL_LINE_SMOOTH)
        glLineWidth(thickness)
        glDisable(GL_TEXTURE_2D)
        glDisable(GL_DEPTH_TEST)
        glDepthMask(false)
        glDisable(GL_ALPHA_TEST)
        glShadeModel(GL_SMOOTH)

        glBegin(GL_LINES)

        for (entity in mc.theWorld.loadedEntityList) {
            if (EntityUtils.isSelected(entity, false))
                drawTrace(entity)
        }

        glEnd()

        glDisable(GL_BLEND)
        glDisable(GL_LINE_SMOOTH)
        glEnable(GL_TEXTURE_2D)
        glEnable(GL_DEPTH_TEST)
        glDepthMask(true)
        glEnable(GL_ALPHA_TEST)
        glShadeModel(GL_FLAT)
        GlStateManager.resetColor()
    }

    private fun drawTrace(entity: Entity) {
        val pos = entity.renderPos
        val eyeVec = Vec3(0.0, 0.0, 1.0)
            .rotatePitch((-mc.thePlayer.rotationPitch.toDouble().toRadians()).toFloat())
            .rotateYaw((-mc.thePlayer.rotationYaw.toDouble().toRadians()).toFloat())

        GLUtils.glColor(red, green, blue, transparency)
        glVertex3d(eyeVec.xCoord, eyeVec.yCoord + mc.thePlayer.eyeHeight - if (mc.thePlayer.isSneaking) 0.08 else 0.0, eyeVec.zCoord)

        GLUtils.glColor(red, green, blue, 0)
        glVertex3d(pos.x, pos.y + entity.eyeHeight * 1.25 / 2.0, pos.z)
    }
}
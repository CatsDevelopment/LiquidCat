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
import lol.liquidcat.utils.entity.renderDistanceTo
import lol.liquidcat.utils.entity.renderPos
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11.*
import java.awt.Color
import kotlin.math.max

object NameTags : Module("NameTags", "Changes the scale of the nametags so you can always read them.", ModuleCategory.RENDER) {

    private val red by IntValue("Red", 74, 0..255)
    private val green by IntValue("Green", 84, 0..255)
    private val blue by IntValue("Blue", 255, 0..255)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        for (entity in mc.theWorld.loadedEntityList) {
            if (EntityUtils.isSelected(entity, false))
                drawNametag(entity as EntityLivingBase)
        }
    }

    /**
     * Draws [entity] nametag
     */
    private fun drawNametag(entity: EntityLivingBase) {

        val pos = entity.renderPos
        val scale = max(0.005, mc.thePlayer.renderDistanceTo(entity) * 0.0015) / glGetFloat(GL_PROJECTION_MATRIX)
        val text = entity.displayName.unformattedText
        val width = Fonts.nunitoBold40.getStringWidth(text) / 2

        val startX = -width - 3f
        val endX = width + 3f
        val endY = Fonts.nunitoBold40.FONT_HEIGHT - 3f
        val xDiff = endX - startX

        glPushMatrix()

        glTranslated(pos.x, pos.y + entity.eyeHeight * 1.25 + 10 * scale, pos.z)

        glRotatef(-mc.renderManager.playerViewY, 0f, 1f, 0f)
        glRotatef(mc.renderManager.playerViewX, if (mc.gameSettings.thirdPersonView < 2) 1f else -1f, 0f, 0f)

        glDisable(GL_DEPTH_TEST)
        glScaled(-scale, -scale, scale)

        // Draws background
        GLUtils.drawRect(startX, -3f, endX, endY, Color(0, 0, 0, 75).rgb)

        // Draws health bar background
        GLUtils.drawRect(
            startX,
            endY,
            startX + xDiff,
            endY + 1f,
            ColorUtils.darker(Color(red, green, blue), 0.5f).rgb
        )

        // Draws health bar
        GLUtils.drawRect(
            startX,
            endY,
            startX + (entity.health.coerceAtMost(entity.maxHealth) / entity.maxHealth) * xDiff,
            endY + 1f,
            Color(red, green, blue).rgb
        )

        // Draws name
        Fonts.nunitoBold40.drawString(text, -width, (-1.5).toInt(), Color.WHITE.rgb)

        glEnable(GL_DEPTH_TEST)

        glPopMatrix()
    }
}
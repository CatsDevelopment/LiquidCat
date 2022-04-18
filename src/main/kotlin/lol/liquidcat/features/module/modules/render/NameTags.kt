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
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
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
                renderNameTag(entity as EntityLivingBase)
        }
    }

    private fun renderNameTag(entity: EntityLivingBase) {
        glPushMatrix()

        val pos = GLUtils.interpolate(entity)
        val scale = max(0.005, GLUtils.renderDistance(entity) * 0.0015) / glGetFloat(GL_PROJECTION_MATRIX)

        glTranslated(pos.x, pos.y + entity.eyeHeight * 1.25 + 10 * scale, pos.z)

        glRotatef(-mc.renderManager.playerViewY, 0f, 1f, 0f)
        glRotatef(mc.renderManager.playerViewX, if (mc.gameSettings.thirdPersonView < 2) 1f else -1f, 0f, 0f)

        glDisable(GL_DEPTH_TEST)
        glScaled(-scale, -scale, scale)

        val text = entity.name
        val width = Fonts.nunito.getStringWidth(text) / 2

        val startX = -width - 3f
        val endX = width + 3f
        val xDiff = endX - startX

        GLUtils.drawRect(startX, -3f, endX, Fonts.nunito.FONT_HEIGHT - 3f, Color(0, 0, 0, 75).rgb)
        GLUtils.drawRect(
            startX,
            Fonts.nunito.FONT_HEIGHT - 3f,
            startX + (entity.health.coerceAtMost(entity.maxHealth) / entity.maxHealth) * xDiff,
            Fonts.nunito.FONT_HEIGHT - 3f + 1.0f,
            Color(red, green, blue, 255).rgb
        )

        AWTFontRenderer.assumeNonVolatile = true
        Fonts.nunito.drawString(text, -width, 0, Color.WHITE.rgb)
        AWTFontRenderer.assumeNonVolatile = false

        glEnable(GL_DEPTH_TEST)

        glPopMatrix()
    }
}
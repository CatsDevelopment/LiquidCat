/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.IntegerValue
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import org.lwjgl.opengl.GL11.*
import java.awt.Color

@ModuleInfo(
    "NameTags",
    "Changes the scale of the nametags so you can always read them.",
    ModuleCategory.RENDER
)
class NameTags : Module() {

    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 150, 0, 255)
    private val blueValue = IntegerValue("Blue", 150, 0, 255)

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        for (entity in mc.theWorld.loadedEntityList) {
            if (EntityUtils.isSelected(entity, false))
                renderNameTag(entity as EntityLivingBase)
        }
    }

    private fun renderNameTag(entity: EntityLivingBase) {
        glPushMatrix()

        glTranslated(
                entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY + entity.eyeHeight.toDouble() + 0.55,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
        )

        glRotatef(-mc.renderManager.playerViewY, 0F, 1F, 0F)
        if (mc.gameSettings.thirdPersonView < 2)
            glRotatef(mc.renderManager.playerViewX, 1F, 0F, 0F)
        else
            glRotatef(-mc.renderManager.playerViewX, 1F, 0F, 0F)

        var distance = mc.thePlayer.getDistanceToEntity(entity) / 5f

        distance = distance.coerceAtLeast(1f)

        val scale = (distance / 100f) / glGetFloat(GL_PROJECTION_MATRIX)

        disableGlCap(GL_DEPTH_TEST, GL_LIGHTING)
        enableGlCap(GL_BLEND)
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
        glScalef(-scale, -scale, scale)

        AWTFontRenderer.assumeNonVolatile = true

        val upText = entity.name
        val downText = "Health: ${"%.1f".format(entity.health)}"
        val width = Fonts.displayRegular50.getStringWidth(upText)
            .coerceAtLeast(Fonts.displayLight25.getStringWidth(downText)) / 2

        val startX = -width - 4f
        val endX = width + 4f
        val xDiff = endX - startX

        drawRect(startX, -4f, endX, Fonts.displayRegular50.FONT_HEIGHT + 8f, Color(30, 30, 30, 150))
        drawRect(
            startX,
            20f,
            startX + (entity.health.coerceAtMost(entity.maxHealth) / entity.maxHealth) * xDiff,
            Fonts.displayRegular50.FONT_HEIGHT + 8f,
            Color(redValue.get(), greenValue.get(), blueValue.get(), 200)
        )

        Fonts.displayRegular50.drawString(upText, -width, 0, Color.WHITE.rgb)
        Fonts.displayLight25.drawString(downText, -width, Fonts.displayRegular50.FONT_HEIGHT, Color.WHITE.rgb)

        AWTFontRenderer.assumeNonVolatile = false

        resetCaps()
        glPopMatrix()
    }
}
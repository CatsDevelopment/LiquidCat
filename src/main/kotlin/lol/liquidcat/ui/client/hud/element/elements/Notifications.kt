/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element.elements

import lol.liquidcat.LiquidCat
import lol.liquidcat.ui.client.hud.element.Border
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.ui.client.hud.element.Side
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.animation.Animation
import lol.liquidcat.utils.render.animation.easing.Direction
import lol.liquidcat.utils.render.animation.easing.easings.Quart
import lol.liquidcat.value.BoolValue
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.max

/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications", single = true)
class Notifications(x: Double = 0.0, y: Double = 30.0, scale: Float = 1F, side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {

    private val blur by BoolValue("Blur", true)
    
    /**
     * Draw element
     */
    override fun drawElement(): Border {

        LiquidCat.hud.notifications.removeIf { it.endAnim.value == 1.0 }
        
        if (blur) {
            GLUtils.blur(10) {
                GL11.glPushMatrix()
                LiquidCat.hud.notifications.forEach {
                    it.drawNotification()
                    GL11.glTranslatef(0f, -37f, 0f)
                }
                GL11.glPopMatrix()
            }   
        }

        GL11.glPushMatrix()
        LiquidCat.hud.notifications.forEach {
            it.drawNotification()
            GL11.glTranslatef(0f, -37f, 0f)
        }
        GL11.glPopMatrix()

        return Border(-140f, -35f, 0f, 0f)
    }
}

enum class NotificationType {
    ENABLED,
    DISABLED
}

class Notification(private val upperMessage: String, val message: String, val type: NotificationType) {

    private val startAnim = Animation(50.0, Quart, Direction.OUT)
    val endAnim = Animation(50.0, Quart, Direction.OUT)

    fun drawNotification() {
        val upWidth = Fonts.nunitoExBold40.getStringWidth(upperMessage)
        val lwWidth = Fonts.nunito40.getStringWidth(message)
        val width = max(upWidth, lwWidth) * 1.2
        val halfWidth = width.toFloat() / 2f

        val factor = if (startAnim.value == 1.0) {
            endAnim.update()
            1.0 - endAnim.value
        } else {
            startAnim.update()
            startAnim.value
        }

        GL11.glPushMatrix()
        GL11.glTranslated((-width.toFloat() - 22) * factor - (-width.toFloat() - 22), 0.0, 0.0)

        GLUtils.drawRoundedRect((-width.toFloat() - 22), -35f, 0f, 0f, 4f,
            if (type == NotificationType.ENABLED)
                Color(74, 160, 84, 100)
            else
                Color(180, 67, 84, 100))

        Fonts.nunitoExBold40.drawCenteredString(upperMessage, -halfWidth, -24f - Fonts.nunitoExBold40.FONT_HEIGHT / 2f, Color.WHITE.rgb, false)
        Fonts.nunito40.drawCenteredString(message, -halfWidth, -10f - Fonts.nunito40.FONT_HEIGHT / 2f, Color.WHITE.rgb, false)

        GLUtils.drawImage(ResourceLocation(
            if (type == NotificationType.ENABLED)
                "${LiquidCat.CLIENT_NAME.lowercase()}/images/hud/check_icon.png"
            else
                "${LiquidCat.CLIENT_NAME.lowercase()}/images/hud/close_icon.png"

        ), -width.toInt() - 16, -26, 16, 16)

        GL11.glPopMatrix()
    }
}
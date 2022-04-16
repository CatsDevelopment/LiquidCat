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

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        LiquidCat.hud.notifications.removeIf { System.currentTimeMillis() > it.time + 2000 }

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
    var time = 0L

    init {
        time = System.currentTimeMillis()
    }

    fun drawNotification() {
        val upWidth = Fonts.nunitoExBold.getStringWidth(upperMessage)
        val lwWidth = Fonts.nunito.getStringWidth(message)
        val width = max(upWidth, lwWidth) * 1.2
        val halfWidth = width.toFloat() / 2f

        GLUtils.drawRoundedRect(-width.toFloat() - 22, -35f, 0f, 0f, 4f,
            if (type == NotificationType.ENABLED)
                Color(74, 160, 84, 230)
            else
                Color(180, 67, 84, 230))

        Fonts.nunitoExBold.drawCenteredString(upperMessage, -halfWidth, -24f - Fonts.nunitoExBold.FONT_HEIGHT / 2f, Color.WHITE.rgb, false)
        Fonts.nunito.drawCenteredString(message, -halfWidth, -10f - Fonts.nunito.FONT_HEIGHT / 2f, Color.WHITE.rgb, false)

        GLUtils.drawImage(ResourceLocation(
            if (type == NotificationType.ENABLED)
                "${LiquidCat.CLIENT_NAME.toLowerCase()}/icons/check.png"
            else
                "${LiquidCat.CLIENT_NAME.toLowerCase()}/icons/close.png"

        ), -width.toInt() - 16, -26, 16, 16)
    }
}
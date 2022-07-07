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
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.entity.boss.BossStatus
import java.awt.Color

@ElementInfo("Bossbar", true, true)
class Bossbar : Element(0.0, 0.0, align = Align(Align.Horizontal.MIDDLE, Align.Vertical.UP)) {

    private var red by IntValue("Red", 80, 0..255)
    private var green by IntValue("Green", 120, 0..255)
    private var blue by IntValue("Blue", 255, 0..255)

    private val width by FloatValue("Width", 182f, 100f..300f)

    override fun drawElement(): Border {

        val hWidth = width / 2f

        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {

            val healthWidth = BossStatus.healthScale * width

            if (healthWidth > 0) {
                GLUtils.drawRoundedRect(-hWidth, 12f, -hWidth + healthWidth, 17f, 1f, Color(red, green, blue))

                Fonts.nunitoExBold40.drawCenteredString(
                    BossStatus.bossName,
                    0f,
                    2f,
                    Color.WHITE.rgb,
                    false
                )
            }

            BossStatus.statusBarTime--
        }

        return Border(-hWidth, 0f, hWidth, 17f)
    }
}
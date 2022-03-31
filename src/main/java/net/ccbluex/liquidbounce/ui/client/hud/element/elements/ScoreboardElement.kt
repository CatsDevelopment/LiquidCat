/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FontValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import lol.liquidcat.features.module.modules.render.NoScoreboard
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.scoreboard.ScoreObjective
import net.minecraft.scoreboard.ScorePlayerTeam
import net.minecraft.scoreboard.Scoreboard
import net.minecraft.util.EnumChatFormatting
import java.awt.Color

/**
 * CustomHUD scoreboard
 *
 * Allows to move and customize minecraft scoreboard
 */
@ElementInfo(name = "Scoreboard", force = true)
class ScoreboardElement(x: Double = 5.0, y: Double = 0.0, scale: Float = 1F,
                        side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.MIDDLE)) : Element(x, y, scale, side) {

    private val textRed by IntValue("Text-R", 255, 0..255)
    private val textGreen by IntValue("Text-G", 255, 0..255)
    private val textBlue by IntValue("Text-B", 255, 0..255)

    private val bgRed by IntValue("Background-R", 0, 0..255)
    private val bgGreen by IntValue("Background-G", 0, 0..255)
    private val bgBlue by IntValue("Background-B", 0, 0..255)
    private val bgAlpha by IntValue("Background-Alpha", 95, 0..255)

    private val rect by BoolValue("Rect", false)
    private val rectColorMode by ListValue("Rect-Color", arrayOf("Custom", "Rainbow"), "Custom")
    private val rectRed by IntValue("Rect-R", 0, 0..255)
    private val rectGreen by IntValue("Rect-G", 111, 0..255)
    private val rectBlue by IntValue("Rect-B", 255, 0..255)
    private val rectAlpha by IntValue("Rect-Alpha", 255, 0..255)

    private val shadow by BoolValue("Shadow", false)
    private val font by FontValue("Font", Fonts.minecraftFont)

    /**
     * Draw element
     */
    override fun drawElement(): Border? {
        if (NoScoreboard.state)
            return null

        val textColor = textColor().rgb
        val backColor = backgroundColor().rgb

        val rectColor = Color(rectRed, rectGreen, rectBlue, rectAlpha).rgb

        val worldScoreboard: Scoreboard = mc.theWorld.scoreboard
        var currObjective: ScoreObjective? = null
        val playerTeam = worldScoreboard.getPlayersTeam(mc.thePlayer.name)

        if (playerTeam != null) {
            val colorIndex = playerTeam.chatFormat.colorIndex

            if (colorIndex >= 0)
                currObjective = worldScoreboard.getObjectiveInDisplaySlot(3 + colorIndex)
        }

        val objective = currObjective ?: worldScoreboard.getObjectiveInDisplaySlot(1) ?: return null

        val scoreboard: Scoreboard = objective.scoreboard
        var scoreCollection = scoreboard.getSortedScores(objective)
        val scores = Lists.newArrayList(Iterables.filter(scoreCollection) { input ->
            input?.playerName != null && !input.playerName.startsWith("#")
        })

        scoreCollection = if (scores.size > 15)
            Lists.newArrayList(Iterables.skip(scores, scoreCollection.size - 15))
        else
            scores

        var maxWidth = font.getStringWidth(objective.displayName)

        for (score in scoreCollection) {
            val scorePlayerTeam = scoreboard.getPlayersTeam(score.playerName)
            val width = "${ScorePlayerTeam.formatPlayerName(scorePlayerTeam, score.playerName)}: ${EnumChatFormatting.RED}${score.scorePoints}"
            maxWidth = maxWidth.coerceAtLeast(font.getStringWidth(width))
        }

        val maxHeight = scoreCollection.size * font.FONT_HEIGHT
        val l1 = -maxWidth - 3 - if (rect) 3 else 0



        Gui.drawRect(l1 - 2, -2, 5, maxHeight + font.FONT_HEIGHT, backColor)

        scoreCollection.forEachIndexed { index, score ->
            val team = scoreboard.getPlayersTeam(score.playerName)

            val name = ScorePlayerTeam.formatPlayerName(team, score.playerName)
            val scorePoints = "${EnumChatFormatting.RED}${score.scorePoints}"

            val width = 5 - if (rect) 4 else 0
            val height = maxHeight - index * font.FONT_HEIGHT

            GlStateManager.resetColor()

            font.drawString(name, l1.toFloat(), height.toFloat(), textColor, shadow)
            font.drawString(scorePoints, (width - font.getStringWidth(scorePoints)).toFloat(), height.toFloat(), textColor, shadow)

            if (index == scoreCollection.size - 1) {
                val displayName = objective.displayName

                GlStateManager.resetColor()

                font.drawString(displayName, (l1 + maxWidth / 2 - font.getStringWidth(displayName) / 2).toFloat(), (height -
                        font.FONT_HEIGHT).toFloat(), textColor, shadow)
            }

            if (rect) {
                val rectColor = when {
                    rectColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(400000000L * index).rgb
                    else -> rectColor
                }

                GLUtils.drawRect(2F, if (index == scoreCollection.size - 1) -2F else height.toFloat(), 5F, if (index == 0) font.FONT_HEIGHT.toFloat() else height.toFloat() + font.FONT_HEIGHT * 2F, rectColor)
            }
        }

        return Border(-maxWidth.toFloat() - 5 - if (rect) 3 else 0, -2F, 5F, maxHeight.toFloat() + font.FONT_HEIGHT)
    }

    private fun backgroundColor() = Color(bgRed, bgGreen, bgBlue, bgAlpha)

    private fun textColor() = Color(textRed, textGreen, textBlue)
}
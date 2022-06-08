/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element.elements

import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.ui.client.hud.element.Border
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.ui.client.hud.element.Side
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.color.ColorUtils.rainbow
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.FontValue
import lol.liquidcat.value.IntValue
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.input.Keyboard
import java.awt.Color

@ElementInfo(name = "TabGUI")
class TabGUI(x: Double = 5.0, y: Double = 25.0) : Element(x = x, y = y) {

    private val rectRed by IntValue("Rectangle Red", 0, 0..255)
    private val rectGreen by IntValue("Rectangle Green", 148, 0..255)
    private val rectBlue by IntValue("Rectangle Blue", 255, 0..255)
    private val rectAlpha by IntValue("Rectangle Alpha", 140, 0..255)
    
    private val rectRainbow by BoolValue("Rectangle Rainbow", false)
    
    private val bgRed by IntValue("Background Red", 0, 0..255)
    private val bgGreen by IntValue("Background Green", 0, 0..255)
    private val bgBlue by IntValue("Background Blue", 0, 0..255)
    private val bgAlpha by IntValue("Background Alpha", 150, 0..255)
    
    private val borderValue by BoolValue("Border", true)
    
    private val borderStrength by FloatValue("Border Strength", 2f, 1f..5f)
    
    private val borderRed by IntValue("Border Red", 0, 0..255)
    private val borderGreen by IntValue("Border Green", 0, 0..255)
    private val borderBlue by IntValue("Border Blue", 0, 0..255)
    private val borderAlpha by IntValue("Border Alpha", 150, 0..255)
    
    private val borderRainbow by BoolValue("Border Rainbow", false)
    
    private val arrows by BoolValue("Arrows", true)
    private val font by FontValue("Font", Fonts.nunito35)
    private val shadow by BoolValue("TextShadow", false)
    private val textFade by BoolValue("TextFade", true)
    private val textPositionY by FloatValue("TextPosition-Y", 2f, 0f..5f)
    private val width by FloatValue("Width", 60f, 55f..100f)
    private val tabHeight by FloatValue("TabHeight", 12f, 10f..15f)
    private val upperCaseValue by BoolValue("UpperCase", false)

    private val tabs = mutableListOf<Tab>()

    private var categoryMenu = true
    private var selectedCategory = 0
    private var selectedModule = 0

    private var tabY = 0F
    private var itemY = 0F

    init {
        for (category in ModuleCategory.values()) {
            val tab = Tab(category.displayName)

            ModuleManager.modules
                    .filter { module: Module -> category == module.category }
                    .forEach { e: Module -> tab.modules.add(e) }

            tabs.add(tab)
        }
    }

    override fun drawElement(): Border? {
        updateAnimation()

        AWTFontRenderer.assumeNonVolatile = true

        // Color
        val color = if (!rectRainbow)
            Color(rectRed, rectGreen, rectBlue, rectAlpha)
        else
            rainbow(alpha = rectAlpha)

        val backgroundColor = Color(bgRed, bgGreen, bgBlue,
                bgAlpha)

        val borderColor = if (!borderRainbow)
            Color(borderRed, borderGreen, borderBlue, borderAlpha)
        else
            rainbow(alpha = borderAlpha)

        // Draw
        val guiHeight = tabs.size * tabHeight

        if (borderValue)
            GLUtils.drawBorderedRect(1F, 0F, width, guiHeight, borderStrength, borderColor.rgb, backgroundColor.rgb)
        else
            GLUtils.drawRect(1F, 0F, width, guiHeight, backgroundColor.rgb)
        GLUtils.drawRect(1F, 1 + tabY - 1, width, tabY + tabHeight, color.rgb)
        GlStateManager.resetColor()

        var y = 1F
        tabs.forEachIndexed { index, tab ->
            val tabName = if (upperCaseValue)
                tab.tabName.uppercase()
            else
                tab.tabName

            val textX = if (side.horizontal == Side.Horizontal.RIGHT)
                width - font.getStringWidth(tabName) - tab.textFade - 3
            else
                tab.textFade + 5
            val textY = y + textPositionY

            val textColor = if (selectedCategory == index) 0xffffff else Color(210, 210, 210).rgb

            font.drawString(tabName, textX, textY, textColor, shadow)

            if (arrows) {
                if (side.horizontal == Side.Horizontal.RIGHT)
                    font.drawString(if (!categoryMenu && selectedCategory == index) ">" else "<", 3F, y + 2F,
                            0xffffff, shadow)
                else
                    font.drawString(if (!categoryMenu && selectedCategory == index) "<" else ">",
                            width - 8F, y + 2F, 0xffffff, shadow)
            }

            if (index == selectedCategory && !categoryMenu) {
                val tabX = if (side.horizontal == Side.Horizontal.RIGHT)
                    1F - tab.menuWidth
                else
                    width + 5

                tab.drawTab(
                        tabX,
                        y,
                        color.rgb,
                        backgroundColor.rgb,
                        borderColor.rgb,
                        borderStrength,
                        upperCaseValue,
                        font
                )
            }
            y += tabHeight
        }

        AWTFontRenderer.assumeNonVolatile = false

        return Border(1F, 0F, width, guiHeight)
    }

    override fun handleKey(c: Char, keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_UP -> parseAction(Action.UP)
            Keyboard.KEY_DOWN -> parseAction(Action.DOWN)
            Keyboard.KEY_RIGHT -> parseAction(if (side.horizontal == Side.Horizontal.RIGHT) Action.LEFT else Action.RIGHT)
            Keyboard.KEY_LEFT -> parseAction(if (side.horizontal == Side.Horizontal.RIGHT) Action.RIGHT else Action.LEFT)
            Keyboard.KEY_RETURN -> parseAction(Action.TOGGLE)
        }
    }

    private fun updateAnimation() {
        val delta = GLUtils.deltaTime

        val xPos = tabHeight * selectedCategory
        if (tabY.toInt() != xPos.toInt()) {
            if (xPos > tabY)
                tabY += 0.1F * delta
            else
                tabY -= 0.1F * delta
        } else
            tabY = xPos
        val xPos2 = tabHeight * selectedModule

        if (itemY.toInt() != xPos2.toInt()) {
            if (xPos2 > itemY)
                itemY += 0.1F * delta
            else
                itemY -= 0.1F * delta
        } else
            itemY = xPos2

        if (categoryMenu)
            itemY = 0F

        if (textFade) {
            tabs.forEachIndexed { index, tab ->
                if (index == selectedCategory) {
                    if (tab.textFade < 4)
                        tab.textFade += 0.05F * delta

                    if (tab.textFade > 4)
                        tab.textFade = 4F
                } else {
                    if (tab.textFade > 0)
                        tab.textFade -= 0.05F * delta

                    if (tab.textFade < 0)
                        tab.textFade = 0F
                }
            }
        } else {
            for (tab in tabs) {
                if (tab.textFade > 0)
                    tab.textFade -= 0.05F * delta

                if (tab.textFade < 0)
                    tab.textFade = 0F
            }
        }
    }

    private fun parseAction(action: Action) {
        when (action) {
            Action.UP -> if (categoryMenu) {
                --selectedCategory
                if (selectedCategory < 0) {
                    selectedCategory = tabs.size - 1
                    tabY = tabHeight * selectedCategory.toFloat()
                }
            } else {
                --selectedModule
                if (selectedModule < 0) {
                    selectedModule = tabs[selectedCategory].modules.size - 1
                    itemY = tabHeight * selectedModule.toFloat()
                }
            }

            Action.DOWN -> if (categoryMenu) {
                ++selectedCategory
                if (selectedCategory > tabs.size - 1) {
                    selectedCategory = 0
                    tabY = tabHeight * selectedCategory.toFloat()
                }
            } else {
                ++selectedModule
                if (selectedModule > tabs[selectedCategory].modules.size - 1) {
                    selectedModule = 0
                    itemY = tabHeight * selectedModule.toFloat()
                }
            }

            Action.LEFT -> if (!categoryMenu) categoryMenu = true

            Action.RIGHT -> if (categoryMenu) {
                categoryMenu = false
                selectedModule = 0
            }

            Action.TOGGLE -> if (!categoryMenu) {
                val sel = selectedModule
                tabs[selectedCategory].modules[sel].toggle()
            }
        }
    }

    /**
     * TabGUI Tab
     */
    private inner class Tab(val tabName: String) {

        val modules = mutableListOf<Module>()
        var menuWidth = 0
        var textFade = 0F

        fun drawTab(x: Float, y: Float, color: Int, backgroundColor: Int, borderColor: Int, borderStrength: Float,
                    upperCase: Boolean, fontRenderer: FontRenderer) {
            var maxWidth = 0

            for (module in modules)
                if (fontRenderer.getStringWidth(if (upperCase) module.name.uppercase() else module.name) + 4 > maxWidth)
                    maxWidth = (fontRenderer.getStringWidth(if (upperCase) module.name.uppercase() else module.name) + 7F).toInt()

            menuWidth = maxWidth

            val menuHeight = modules.size * tabHeight

            if (borderValue)
                GLUtils.drawBorderedRect(x - 1F, y - 1F, x + menuWidth - 2F, y + menuHeight - 1F, borderStrength, borderColor, backgroundColor)
            else
                GLUtils.drawRect(x - 1F, y - 1F, x + menuWidth - 2F, y + menuHeight - 1F, backgroundColor)

            GLUtils.drawRect(x - 1.toFloat(), y + itemY - 1, x + menuWidth - 2F, y + itemY + tabHeight - 1, color)
            GlStateManager.resetColor()

            modules.forEachIndexed { index, module ->
                val moduleColor = if (module.state) 0xffffff else Color(205, 205, 205).rgb

                fontRenderer.drawString(if (upperCase) module.name.uppercase() else module.name, x + 2F,
                        y + tabHeight * index + textPositionY, moduleColor, shadow)
            }
        }

    }

    /**
     * TabGUI Action
     */
    enum class Action { UP, DOWN, LEFT, RIGHT, TOGGLE }
}
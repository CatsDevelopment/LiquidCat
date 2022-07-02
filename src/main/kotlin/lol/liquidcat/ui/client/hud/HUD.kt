/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud

import lol.liquidcat.LiquidCat
import lol.liquidcat.ui.client.hud.designer.GuiHudDesigner
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.elements.*
import lol.liquidcat.utils.mc
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.GL11
import kotlin.math.max
import kotlin.math.min

class HUD {

    val elements = mutableListOf<Element>()
    val notifications = mutableListOf<Notification>()

    companion object {

        val elements = arrayOf(
                Inventory::class.java,
                Armor::class.java,
                Arraylist::class.java,
                Effects::class.java,
                Image::class.java,
                Model::class.java,
                Notifications::class.java,
                TabGUI::class.java,
                Text::class.java,
                ScoreboardElement::class.java,
                Radar::class.java,
                Bossbar::class.java,
                Keystrokes::class.java,
                SpeedGraph::class.java
        )

        /**
         * Create default HUD
         */
        @JvmStatic
        fun createDefault() = HUD()
                .addElement(Text.defaultClient())
                .addElement(Arraylist())
                .addElement(ScoreboardElement())
                .addElement(Effects())
                .addElement(Notifications())
                .addElement(Bossbar())
                .addElement(Radar())
                .addElement(Keystrokes())
                .addElement(SpeedGraph())
    }

    /**
     * Render all elements
     */
    fun render(designer: Boolean) {
        for (element in elements) {
            GL11.glPushMatrix()
            GL11.glScalef(element.scale, element.scale, element.scale)
            GL11.glTranslated(element.renderX, element.renderY, 0.0)

            try {
                element.border = element.drawElement()

                if (designer)
                    element.border?.draw()
            } catch (ex: Exception) {
                LiquidCat.logger
                        .error("Something went wrong while drawing ${element.name} element in HUD.", ex)
            }

            GL11.glPopMatrix()
        }
    }

    /**
     * Update all elements
     */
    fun update() = elements.forEach { it.updateElement() }

    /**
     * Handle mouse click
     */
    fun handleMouseClick(mouseX: Int, mouseY: Int, button: Int) {
        for (element in elements)
            element.handleMouseClick((mouseX / element.scale) - element.renderX, (mouseY / element.scale)
                    - element.renderY, button)

        if (button == 0) {
            for (element in elements.reversed()) {
                if (!element.isInBorder((mouseX / element.scale) - element.renderX,
                                (mouseY / element.scale) - element.renderY))
                    continue

                element.drag = true
                elements.remove(element)
                elements.add(element)
                break
            }
        }
    }

    /**
     * Handle released mouse key
     */
    fun handleMouseReleased() = elements.forEach { it.drag = false }

    /**
     * Handle mouse move
     */
    fun handleMouseMove(mouseX: Int, mouseY: Int) {
        if (mc.currentScreen !is GuiHudDesigner) return

        val scaledResolution = ScaledResolution(mc)

        for (element in elements) {
            val scaledX = mouseX / element.scale
            val scaledY = mouseY / element.scale
            val prevMouseX = element.prevMouseX
            val prevMouseY = element.prevMouseY

            element.prevMouseX = scaledX
            element.prevMouseY = scaledY

            if (element.drag) {
                val moveX = scaledX - prevMouseX
                val moveY = scaledY - prevMouseY

                if (moveX == 0F && moveY == 0F)
                    continue

                val border = element.border ?: continue

                val minX = min(border.x, border.x2) + 1
                val minY = min(border.y, border.y2) + 1

                val maxX = max(border.x, border.x2) - 1
                val maxY = max(border.y, border.y2) - 1

                val width = scaledResolution.scaledWidth / element.scale
                val height = scaledResolution.scaledHeight / element.scale

                if ((element.renderX + minX + moveX >= 0.0 || moveX > 0) && (element.renderX + maxX + moveX <= width || moveX < 0))
                    element.renderX = moveX.toDouble()
                if ((element.renderY + minY + moveY >= 0.0 || moveY > 0) && (element.renderY + maxY + moveY <= height || moveY < 0))
                    element.renderY = moveY.toDouble()
            }
        }
    }

    /**
     * Handle incoming key
     */
    fun handleKey(c: Char, keyCode: Int) = elements.forEach { it.handleKey(c, keyCode) }

    /**
     * Add [element] to HUD
     */
    fun addElement(element: Element): HUD {
        elements.add(element)
        element.updateElement()
        return this
    }

    /**
     * Remove [element] from HUD
     */
    fun removeElement(element: Element): HUD {
        element.destroyElement()
        elements.remove(element)
        return this
    }

    /**
     * Clear all elements
     */
    fun clearElements() {
        elements.forEach { it.destroyElement() }
        elements.clear()
    }

    /**
     * Add [notification]
     */
    fun addNotification(notification: Notification) = elements.any { it is Notifications } && notifications.add(notification)

    /**
     * Remove [notification]
     */
    fun removeNotification(notification: Notification) = notifications.remove(notification)
}
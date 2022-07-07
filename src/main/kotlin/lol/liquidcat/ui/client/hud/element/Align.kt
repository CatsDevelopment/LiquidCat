/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element

import lol.liquidcat.utils.mc
import net.minecraft.client.gui.ScaledResolution

/**
 * HUD Element alignment
 *
 * Allows you to align position of HUD element
 */
class Align(var horizontal: Horizontal = Horizontal.LEFT, var vertical: Vertical = Vertical.UP) {

    fun alignX(x: Double):  Double {

        val sr = ScaledResolution(mc)

        return when (horizontal) {
            Horizontal.LEFT -> x
            Horizontal.MIDDLE -> (sr.scaledWidth / 2) - x
            Horizontal.RIGHT -> sr.scaledWidth - x
        }
    }

    fun alignY(y: Double):  Double {

        val sr = ScaledResolution(mc)

        return when (vertical) {
            Vertical.UP -> y
            Vertical.MIDDLE -> (sr.scaledHeight / 2) - y
            Vertical.DOWN -> sr.scaledHeight - y
        }
    }

    /**
     * Horizontal alignment
     */
    enum class Horizontal(val title: String) {

        LEFT("Left"),
        MIDDLE("Middle"),
        RIGHT("Right");

        companion object {

            @JvmStatic
            fun byName(name: String) = values().find { it.title == name }
        }
    }

    /**
     * Vertical alignment
     */
    enum class Vertical(val title: String) {

        UP("Up"),
        MIDDLE("Middle"),
        DOWN("Down");

        companion object {

            @JvmStatic
            fun byName(name: String) = values().find { it.title == name }
        }
    }
}
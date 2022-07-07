/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element

import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.Value
import kotlin.math.max
import kotlin.math.min

abstract class Element(var x: Double = 2.0, var y: Double = 2.0, var scale: Float = 1f, var align: Align = Align()) {

    val info = javaClass.getAnnotation(ElementInfo::class.java)
            ?: throw IllegalArgumentException("Passed element with missing element info")

    val name
        get() = info.name

    var renderX
        get() = align.alignX(x)

        set(value) = when (align.horizontal) {
            Align.Horizontal.LEFT -> x += value
            Align.Horizontal.MIDDLE, Align.Horizontal.RIGHT -> x -= value
        }

    var renderY
        get() = align.alignY(y)

        set(value) = when (align.vertical) {
            Align.Vertical.UP -> y += value
            Align.Vertical.MIDDLE, Align.Vertical.DOWN -> y -= value
        }

    var border: Border? = null

    var drag = false
    var prevMouseX = 0F
    var prevMouseY = 0F

    /**
     * Get all values of element
     */
    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Called when element created
     */
    open fun createElement() = true

    /**
     * Called when element destroyed
     */
    open fun destroyElement() {}

    /**
     * Draw element
     */
    abstract fun drawElement(): Border?

    /**
     * Update element
     */
    open fun updateElement() {}

    /**
     * Check if [x] and [y] is in element border
     */
    open fun isInBorder(x: Double, y: Double): Boolean {
        val border = border ?: return false

        val minX = min(border.x, border.x2)
        val minY = min(border.y, border.y2)

        val maxX = max(border.x, border.x2)
        val maxY = max(border.y, border.y2)

        return minX <= x && minY <= y && maxX >= x && maxY >= y
    }

    /**
     * Called when mouse clicked
     */
    open fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {}

    /**
     * Called when key pressed
     */
    open fun handleKey(c: Char, keyCode: Int) {}

}

/**
 * Element info
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class ElementInfo(val name: String, val single: Boolean = false, val force: Boolean = false)

/**
 * Border of element
 */
data class Border(val x: Float, val y: Float, val x2: Float, val y2: Float) {

    fun draw() = GLUtils.drawBorder(x, y, x2, y2, 1f, Int.MIN_VALUE)

}
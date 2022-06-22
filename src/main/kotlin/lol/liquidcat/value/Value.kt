/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.value

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import lol.liquidcat.LiquidCat
import lol.liquidcat.file.FileManager
import lol.liquidcat.file.configs.ValuesConfig
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.gui.FontRenderer
import java.util.*
import kotlin.reflect.KProperty

// TODO: Add color value, Add range value

abstract class Value<T>(val name: String, protected var value: T) {
    fun get() = value

    fun set(newValue: T) {
        if (newValue == value) return

        val oldValue = get()

        try {
            onChange(oldValue, newValue)
            changeValue(newValue)
            onChanged(oldValue, newValue)
            FileManager.saveConfig(ValuesConfig)
        } catch (e: Exception) {
            LiquidCat.logger.error("[ValueSystem ($name)]: ${e.javaClass.name} (${e.message}) [$oldValue >> $newValue]")
        }
    }

    operator fun getValue(ref: Any?, property: KProperty<*>) = get()

    operator fun setValue(ref: Any?, property: KProperty<*>, value: T) = set(value)

    open fun changeValue(value: T) {
        this.value = value
    }

    abstract fun toJson(): JsonElement?
    abstract fun fromJson(element: JsonElement)

    protected open fun onChange(oldValue: T, newValue: T) {}
    protected open fun onChanged(oldValue: T, newValue: T) {}
}

abstract class RangeValue<T : Comparable<T>>(name: String, value: T, private val range: ClosedRange<T>) : Value<T>(name, value) {
    fun min() = range.start

    fun max() = range.endInclusive
}

/**
 * Int value represents a value with an int
 */
open class IntValue(name: String, value: Int, range: IntRange) : RangeValue<Int>(name, value, range) {
    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) value = element.asInt
    }
}

/**
 * Float value represents a value with a float
 */
open class FloatValue(name: String, value: Float, range: ClosedFloatingPointRange<Float>) : RangeValue<Float>(name, value, range) {
    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) value = element.asFloat
    }
}

/**
 * Bool value represents a value with a boolean
 */
open class BoolValue(name: String, value: Boolean) : Value<Boolean>(name, value) {

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) value = element.asBoolean || element.asString.equals("true", ignoreCase = true)
    }
}

/**
 * Text value represents a value with a string
 */
open class TextValue(name: String, value: String) : Value<String>(name, value) {

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) value = element.asString
    }
}

/**
 * Font value represents a value with a font
 */
class FontValue(valueName: String, value: FontRenderer) : Value<FontRenderer>(valueName, value) {

    override fun toJson(): JsonElement? {
        val fontDetails = Fonts.getFontDetails(value) ?: return null
        val valueObject = JsonObject()
        valueObject.addProperty("fontName", fontDetails[0] as String)
        valueObject.addProperty("fontSize", fontDetails[1] as Int)
        return valueObject
    }

    override fun fromJson(element: JsonElement) {
        if (!element.isJsonObject) return
        val valueObject = element.asJsonObject
        value = Fonts.getFontRenderer(valueObject["fontName"].asString, valueObject["fontSize"].asInt)
    }
}

/**
 * Block value represents a value with a block
 */
class BlockValue(name: String, value: Int) : IntValue(name, value, 1..197)

/**
 * List value represents a selectable list of values
 */
open class ListValue(name: String, val values: Array<String>, value: String) : Value<String>(name, value) {

    @JvmField
    var openList = false

    init {
        this.value = value
    }

    operator fun contains(string: String?): Boolean {
        return Arrays.stream(values).anyMatch { s: String -> s.equals(string, ignoreCase = true) }
    }

    override fun changeValue(value: String) {
        for (element in values) {
            if (element.equals(value, ignoreCase = true)) {
                this.value = element
                break
            }
        }
    }

    override fun toJson() = JsonPrimitive(value)

    override fun fromJson(element: JsonElement) {
        if (element.isJsonPrimitive) changeValue(element.asString)
    }
}
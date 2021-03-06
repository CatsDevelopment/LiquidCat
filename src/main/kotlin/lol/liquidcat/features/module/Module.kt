/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.Listenable
import lol.liquidcat.file.FileManager
import lol.liquidcat.file.configs.ModulesConfig
import lol.liquidcat.ui.client.hud.element.elements.Notification
import lol.liquidcat.ui.client.hud.element.elements.NotificationType
import lol.liquidcat.utils.render.animation.Animation
import lol.liquidcat.utils.render.animation.easing.Direction
import lol.liquidcat.utils.render.animation.easing.easings.Quint
import lol.liquidcat.value.Value
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard

open class Module(
    var name: String,
    var description: String,
    var category: ModuleCategory,
    keyBind: Int = Keyboard.CHAR_NONE,
    val canEnable: Boolean = true,
    hide: Boolean = false
) : Listenable {
    var keyBind = keyBind
        set(value) {
            field = value

            FileManager.saveConfig(ModulesConfig)
        }
    var hide = hide
        set(value) {
            field = value

            FileManager.saveConfig(ModulesConfig)
        }
    var state = false
        set(value) {
            if (field == value) return

            onToggle(value)

            if (!LiquidCat.loading) {
                mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("random.click"), 1f))
                LiquidCat.hud.addNotification(Notification(
                    if (value) "Module enabled" else "Module disabled",
                    if (value) "$name was enabled" else "$name was disabled",
                    if (value) NotificationType.ENABLED else NotificationType.DISABLED)
                )
            }

            slideAnim.reset()

            if (value) {
                onEnable()

                if (canEnable)
                    field = true
            } else {
                onDisable()
                field = false
            }

            FileManager.saveConfig(ModulesConfig)
        }
    // HUD
    var slideAnim = Animation(200.0, Quint, Direction.OUT)

    // Tag
    open val tag: String?
        get() = null

    val tagName: String
        get() = "$name${if (tag == null) "" else " ??7$tag"}"

    /**
     * Toggle module
     */
    fun toggle() {
        state = !state
    }

    val mc = lol.liquidcat.utils.mc

    /**
     * Called when module toggled
     */
    open fun onToggle(state: Boolean) {}

    /**
     * Called when module enabled
     */
    open fun onEnable() {}

    /**
     * Called when module disabled
     */
    open fun onDisable() {}

    /**
     * Get module by [valueName]
     */
    open fun getValue(valueName: String) = javaClass.declaredFields.map { valueField ->
        valueField.isAccessible = true
        valueField[this]
    }.filterIsInstance<Value<*>>().find { it.name.equals(valueName, ignoreCase = true) }

    /**
     * Get all values of module
     */
    open val values: List<Value<*>>
        get() = javaClass.declaredFields.map { valueField ->
            valueField.isAccessible = true
            valueField[this]
        }.filterIsInstance<Value<*>>()

    /**
     * Events should be handled when module is enabled
     */
    override fun handleEvents() = state
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntegerValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.LiquidBounceStyle
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.NullStyle
import net.ccbluex.liquidbounce.ui.client.clickgui.style.styles.SlowlyStyle
import net.ccbluex.liquidbounce.utils.render.ColorUtils.rainbow
import net.minecraft.network.play.server.S2EPacketCloseWindow
import org.lwjgl.input.Keyboard
import java.awt.Color

object ClickGUI : Module("ClickGUI", "Opens the ClickGUI.", ModuleCategory.RENDER, Keyboard.KEY_RSHIFT, canEnable = false) {

    private val styleValue: ListValue =
        object : ListValue("Style", arrayOf("LiquidBounce", "Null", "Slowly"), "Slowly") {
            override fun onChanged(oldValue: String, newValue: String) {
                updateStyle()
            }
        }

    private val colorRedValue = IntegerValue("R", 0, 0, 255)
    private val colorGreenValue = IntegerValue("G", 160, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val colorRainbow = BoolValue("Rainbow", false)

    val scaleValue = FloatValue("Scale", 1f, 0.7f, 2f)
    val maxElementsValue = IntegerValue("MaxElements", 15, 1, 20)

    override fun onEnable() {
        updateStyle()
        mc.displayGuiScreen(LiquidCat.clickGui)
    }

    private fun updateStyle() {
        when (styleValue.get().toLowerCase()) {
            "liquidbounce" -> LiquidCat.clickGui.style = LiquidBounceStyle()
            "null" -> LiquidCat.clickGui.style = NullStyle()
            "slowly" -> LiquidCat.clickGui.style = SlowlyStyle()
        }
    }

    @EventTarget(true)
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is S2EPacketCloseWindow && mc.currentScreen is ClickGui)
            event.cancelEvent()
    }

    @JvmStatic
    fun generateColor(): Color {
        return if (colorRainbow.get()) rainbow() else Color(
            colorRedValue.get(),
            colorGreenValue.get(),
            colorBlueValue.get()
        )
    }
}
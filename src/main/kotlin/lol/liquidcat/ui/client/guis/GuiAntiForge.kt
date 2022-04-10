/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.guis

import lol.liquidcat.features.misc.AntiForge
import lol.liquidcat.file.FileManager.saveConfig
import lol.liquidcat.file.FileManager.valuesConfig
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.input.Keyboard

class GuiAntiForge(private val prevGui: GuiScreen) : GuiScreen() {

    private lateinit var enabledButton: GuiButton
    private lateinit var fmlButton: GuiButton
    private lateinit var proxyButton: GuiButton
    private lateinit var payloadButton: GuiButton

    override fun initGui() {
        val dWidth = width / 2 - 100
        val dHeight = height / 4 + 50

        buttonList.add(GuiButton(1, dWidth, dHeight - 15, "Enabled (${if (AntiForge.enabled) "On" else "Off"})").also { enabledButton = it })
        buttonList.add(GuiButton(2, dWidth, dHeight + 25, "Block FML (${if (AntiForge.blockFML) "On" else "Off"})").also { fmlButton = it })
        buttonList.add(GuiButton(3, dWidth, dHeight + 25 * 2, "Block FML Proxy Packet (${if (AntiForge.blockProxyPacket) "On" else "Off"})").also { proxyButton = it })
        buttonList.add(GuiButton(4, dWidth, dHeight + 25 * 3, "Block Payload Packets (${if (AntiForge.blockPayloadPackets) "On" else "Off"})").also { payloadButton = it })
        buttonList.add(GuiButton(0, dWidth, dHeight + 5 + 25 * 4 + 5, "Back"))
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(prevGui)
            1 -> {
                AntiForge.enabled = !AntiForge.enabled
                enabledButton.displayString = "Enabled (${if (AntiForge.enabled) "On" else "Off"})"
                saveConfig(valuesConfig, false)
            }
            2 -> {
                AntiForge.blockFML = !AntiForge.blockFML
                fmlButton.displayString = "Block FML (${if (AntiForge.blockFML) "On" else "Off"})"
                saveConfig(valuesConfig, false)
            }
            3 -> {
                AntiForge.blockProxyPacket = !AntiForge.blockProxyPacket
                proxyButton.displayString = "Block FML Proxy Packet (${if (AntiForge.blockProxyPacket) "On" else "Off"})"
                saveConfig(valuesConfig, false)
            }
            4 -> {
                AntiForge.blockPayloadPackets = !AntiForge.blockPayloadPackets
                payloadButton.displayString = "Block Payload Packets (${if (AntiForge.blockPayloadPackets) "On" else "Off"})"
                saveConfig(valuesConfig, false)
            }
        }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)
        Fonts.fontBold180.drawCenteredString(
            "AntiForge",
            (width / 2f).toInt().toFloat(),
            (height / 8f + 5f).toInt().toFloat(),
            4673984,
            true
        )
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }
        super.keyTyped(typedChar, keyCode)
    }
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.ui.client

import lol.liquidcat.LiquidCat
import lol.liquidcat.file.FileManager
import lol.liquidcat.file.configs.ClickGuiConfig
import lol.liquidcat.file.configs.HudConfig
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.font.Fonts
import lol.liquidcat.utils.MiscUtils
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiSlot
import org.apache.commons.io.IOUtils
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.awt.Desktop
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipFile

class GuiScripts(private val prevGui: GuiScreen) : GuiScreen() {

    private lateinit var list: GuiList

    override fun initGui() {
        list = GuiList(this)
        list.registerScrollButtons(7, 8)
        list.elementClicked(-1, false, 0, 0)

        val j = 22
        this.buttonList.add(GuiButton(0, width - 80, height - 65, 70, 20, "Back"))
        this.buttonList.add(GuiButton(1, width - 80, j + 24, 70, 20, "Import"))
        this.buttonList.add(GuiButton(2, width - 80, j + 24 * 2, 70, 20, "Delete"))
        this.buttonList.add(GuiButton(3, width - 80, j + 24 * 3, 70, 20, "Reload"))
        this.buttonList.add(GuiButton(4, width - 80, j + 24 * 4, 70, 20, "Folder"))
        this.buttonList.add(GuiButton(5, width - 80, j + 24 * 5, 70, 20, "Docs"))
        this.buttonList.add(GuiButton(6, width - 80, j + 24 * 6, 70, 20, "Find Scripts"))
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBackground(0)

        list.drawScreen(mouseX, mouseY, partialTicks)

        drawCenteredString(Fonts.nunitoBold40, "§9§lScripts", width / 2, 28, 0xffffff)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun actionPerformed(button: GuiButton) {
        when (button.id) {
            0 -> mc.displayGuiScreen(prevGui)
            1 -> try {
                val file = MiscUtils.openFileChooser() ?: return
                val fileName = file.name

                if (fileName.endsWith(".js")) {
                    ScriptManager.importScript(file)

                    LiquidCat.clickGui = ClickGui()
                    FileManager.loadConfig(ClickGuiConfig)
                    return
                } else if (fileName.endsWith(".zip")) {
                    val zipFile = ZipFile(file)
                    val entries = zipFile.entries()
                    val scriptFiles = ArrayList<File>()

                    while (entries.hasMoreElements()) {
                        val entry = entries.nextElement()
                        val entryName = entry.name
                        val entryFile = File(ScriptManager.scriptsFolder, entryName)

                        if (entry.isDirectory) {
                            entryFile.mkdir()
                            continue
                        }

                        val fileStream = zipFile.getInputStream(entry)
                        val fileOutputStream = FileOutputStream(entryFile)

                        IOUtils.copy(fileStream, fileOutputStream)
                        fileOutputStream.close()
                        fileStream.close()

                        if (!entryName.contains("/"))
                            scriptFiles.add(entryFile)
                    }

                    scriptFiles.forEach { scriptFile -> ScriptManager.loadScript(scriptFile) }

                    LiquidCat.clickGui = ClickGui()
                    FileManager.loadConfig(ClickGuiConfig)
                    FileManager.loadConfig(HudConfig)
                    return
                }

                MiscUtils.showErrorPopup("Wrong file extension.", "The file extension has to be .js or .zip")
            } catch (t: Throwable) {
                LiquidCat.logger.error("Something went wrong while importing a script.", t)
                MiscUtils.showErrorPopup(t.javaClass.name, t.message)
            }

            2 -> try {
                if (list.getSelectedSlot() != -1) {
                    val script = ScriptManager.scripts[list.getSelectedSlot()]

                    ScriptManager.deleteScript(script)

                    LiquidCat.clickGui = ClickGui()
                    FileManager.loadConfig(ClickGuiConfig)
                    FileManager.loadConfig(HudConfig)
                }
            } catch (t: Throwable) {
                LiquidCat.logger.error("Something went wrong while deleting a script.", t)
                MiscUtils.showErrorPopup(t.javaClass.name, t.message)
            }
            3 -> try {
                ScriptManager.reloadScripts()
            } catch (t: Throwable) {
                LiquidCat.logger.error("Something went wrong while reloading all scripts.", t)
                MiscUtils.showErrorPopup(t.javaClass.name, t.message)
            }
            4 -> try {
                Desktop.getDesktop().open(ScriptManager.scriptsFolder)
            } catch (t: Throwable) {
                LiquidCat.logger.error("Something went wrong while trying to open your scripts folder.", t)
                MiscUtils.showErrorPopup(t.javaClass.name, t.message)
            }
            5 -> try {
                Desktop.getDesktop().browse(URL("https://liquidbounce.net/docs/ScriptAPI/Getting%20Started").toURI())
            } catch (ignored: Exception) { }

            6 -> try {
                Desktop.getDesktop().browse(URL("https://forum.ccbluex.net/viewforum.php?id=16").toURI())
            } catch (ignored: Exception) { }
        }
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            mc.displayGuiScreen(prevGui)
            return
        }

        super.keyTyped(typedChar, keyCode)
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        list.handleMouseInput()
    }

    private inner class GuiList(gui: GuiScreen) : GuiSlot(mc, gui.width, gui.height, 40, gui.height - 40, 30) {

        private var selectedSlot = 0

        override fun isSelected(id: Int) = selectedSlot == id

        fun getSelectedSlot() = if (selectedSlot > ScriptManager.scripts.size) -1 else selectedSlot

        override fun getSize() = ScriptManager.scripts.size

        public override fun elementClicked(id: Int, doubleClick: Boolean, var3: Int, var4: Int) {
            selectedSlot = id
        }

        override fun drawSlot(id: Int, x: Int, y: Int, var4: Int, var5: Int, var6: Int) {
            val script = ScriptManager.scripts[id]
            drawCenteredString(Fonts.nunitoBold40, "§9" + script.scriptName + " §7v" + script.scriptVersion, width / 2, y + 2, Color.LIGHT_GRAY.rgb)
            drawCenteredString(Fonts.nunitoBold40, "by §c" + script.scriptAuthors.joinToString(", "), width / 2, y + 15, Color.LIGHT_GRAY.rgb)
        }

        override fun drawBackground() { }
    }
}
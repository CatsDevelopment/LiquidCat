/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils

import java.awt.Desktop
import java.io.File
import java.net.URI
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JOptionPane

object MiscUtils {

    fun showErrorPopup(title: String, message: String?) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE)
    }

    fun showURL(url: String) {
        runCatching {
            Desktop.getDesktop().browse(URI(url))
        }.onFailure { it.printStackTrace() }
    }

    @JvmStatic
    fun openFileChooser(): File? {
        if (mc.isFullScreen)
            mc.toggleFullscreen()

        val fileChooser = JFileChooser()
        fileChooser.fileSelectionMode = JFileChooser.FILES_ONLY

        val frame = JFrame()
        frame.isVisible = true
        frame.toFront()
        frame.isVisible = false

        val action = fileChooser.showOpenDialog(frame)
        frame.dispose()

        return if (action == JFileChooser.APPROVE_OPTION)
            fileChooser.selectedFile
        else null
    }
}
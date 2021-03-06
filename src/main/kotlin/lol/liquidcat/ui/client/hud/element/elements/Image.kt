/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.ui.client.hud.element.elements

import com.google.gson.JsonElement
import lol.liquidcat.ui.client.hud.element.Border
import lol.liquidcat.ui.client.hud.element.Element
import lol.liquidcat.ui.client.hud.element.ElementInfo
import lol.liquidcat.utils.mc
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.TextValue
import lol.liquidcat.utils.MiscUtils
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import org.apache.commons.lang3.RandomStringUtils
import java.io.ByteArrayInputStream
import java.io.File
import java.nio.file.Files
import java.util.*
import javax.imageio.ImageIO


/**
 * CustomHUD image element
 *
 * Draw custom image
 */
@ElementInfo("Image")
class Image : Element() {

    companion object {

        /**
         * Create default element
         */
        fun default(): Image {
            val image = Image()

            image.x = 0.0
            image.y = 0.0

            return image
        }

    }

    private val image: TextValue = object : TextValue("Image", "") {

        override fun fromJson(element: JsonElement) {
            super.fromJson(element)

            if (get().isEmpty())
                return

            setImage(get())
        }

        override fun onChanged(oldValue: String, newValue: String) {
            if (get().isEmpty())
                return

            setImage(get())
        }

    }

    private val resourceLocation = ResourceLocation(RandomStringUtils.randomNumeric(128))
    private var width = 64
    private var height = 64

    /**
     * Draw element
     */
    override fun drawElement(): Border {
        GLUtils.drawImage(resourceLocation, 0, 0, width / 2, height / 2)

        return Border(0F, 0F, width / 2F, height / 2F)
    }

    override fun createElement(): Boolean {
        val file = MiscUtils.openFileChooser() ?: return false

        if (!file.exists()) {
            MiscUtils.showErrorPopup("Error", "The file does not exist.")
            return false
        }

        if (file.isDirectory) {
            MiscUtils.showErrorPopup("Error", "The file is a directory.")
            return false
        }

        setImage(file)
        return true
    }

    private fun setImage(image: String): Image {
        try {
            this.image.changeValue(image)

            val byteArrayInputStream = ByteArrayInputStream(Base64.getDecoder().decode(image))
            val bufferedImage = ImageIO.read(byteArrayInputStream)
            byteArrayInputStream.close()

            width = bufferedImage.width
            height = bufferedImage.height

            mc.textureManager.loadTexture(resourceLocation, DynamicTexture(bufferedImage))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    fun setImage(image: File): Image {
        try {
            setImage(Base64.getEncoder().encodeToString(Files.readAllBytes(image.toPath())))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return this
    }
}
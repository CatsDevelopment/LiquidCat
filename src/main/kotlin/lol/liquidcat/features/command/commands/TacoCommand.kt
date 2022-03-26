/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command.commands

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Listenable
import lol.liquidcat.event.Render2DEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.command.Command
import lol.liquidcat.utils.msg
import lol.liquidcat.utils.render.GLUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.util.ResourceLocation

object TacoCommand : Command("taco", emptyArray()), Listenable {
    private var toggle = false
    private var image = 0
    private var running = 0f
    private val tacoTextures = arrayOf(
            ResourceLocation("liquidbounce/taco/1.png"),
            ResourceLocation("liquidbounce/taco/2.png"),
            ResourceLocation("liquidbounce/taco/3.png"),
            ResourceLocation("liquidbounce/taco/4.png"),
            ResourceLocation("liquidbounce/taco/5.png"),
            ResourceLocation("liquidbounce/taco/6.png"),
            ResourceLocation("liquidbounce/taco/7.png"),
            ResourceLocation("liquidbounce/taco/8.png"),
            ResourceLocation("liquidbounce/taco/9.png"),
            ResourceLocation("liquidbounce/taco/10.png"),
            ResourceLocation("liquidbounce/taco/11.png"),
            ResourceLocation("liquidbounce/taco/12.png")
    )

    init {
        LiquidCat.eventManager.registerListener(this)
    }

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        toggle = !toggle
        msg(if (toggle) "§aTACO TACO TACO. :)" else "§cYou made the little taco sad! :(")
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (!toggle)
            return

        running += 0.15f * GLUtils.deltaTime
        val scaledResolution = ScaledResolution(mc)
        GLUtils.drawImage(tacoTextures[image], running.toInt(), scaledResolution.scaledHeight - 60, 64, 32)
        if (scaledResolution.scaledWidth <= running)
            running = -64f
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (!toggle) {
            image = 0
            return
        }

        image++
        if (image >= tacoTextures.size) image = 0
    }

    override fun handleEvents() = true

    override fun tabComplete(args: Array<String>): List<String> {
        return listOf("TACO")
    }
}
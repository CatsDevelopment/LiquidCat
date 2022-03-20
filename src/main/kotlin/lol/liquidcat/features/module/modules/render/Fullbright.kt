/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.ClientShutdownEvent
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.ListValue
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect

class Fullbright : Module("Fullbright", "Brightens up the world around you.", ModuleCategory.RENDER) {

    private val mode by ListValue("Mode", arrayOf("Gamma", "NightVision"), "Gamma")
    private var prevGamma = -1f

    override fun onEnable() {
        prevGamma = mc.gameSettings.gammaSetting
    }

    override fun onDisable() {
        if (prevGamma == -1f) return

        mc.gameSettings.gammaSetting = prevGamma
        prevGamma = -1f

        if (mc.thePlayer != null) mc.thePlayer.removePotionEffectClient(Potion.nightVision.id)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (state || LiquidCat.moduleManager.getModule(XRay::class.java)!!.state) {
            when (mode.toLowerCase()) {
                "gamma" -> if (mc.gameSettings.gammaSetting <= 100f) mc.gameSettings.gammaSetting++
                "nightvision" -> mc.thePlayer.addPotionEffect(PotionEffect(Potion.nightVision.id, 1337, 1))
            }
        } else if (prevGamma != -1f) {
            mc.gameSettings.gammaSetting = prevGamma
            prevGamma = -1f
        }
    }

    @EventTarget
    fun onShutdown(event: ClientShutdownEvent) {
        onDisable()
    }
}
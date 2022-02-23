/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.BoolValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion

@ModuleInfo("Zoot", "Removes all bad potion effects/fire.", ModuleCategory.PLAYER)
class Zoot : Module() {

    private val badEffectsValue = BoolValue("BadEffects", true)
    private val fireValue = BoolValue("Fire", true)
    private val noAirValue = BoolValue("NoAir", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if ((noAirValue.get() && !mc.thePlayer.onGround) || mc.thePlayer.capabilities.isCreativeMode)
            return

        if (badEffectsValue.get()) {
            val effect = mc.thePlayer.activePotionEffects
                .filter { Potion.potionTypes[it.potionID].isBadEffect }
                .maxBy { it.duration }

            if (effect != null)
                repeat(effect.duration / 20) {
                    mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                }
        }


        if (fireValue.get() && mc.thePlayer.isBurning)
            repeat(9) {
                mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
            }
    }
}
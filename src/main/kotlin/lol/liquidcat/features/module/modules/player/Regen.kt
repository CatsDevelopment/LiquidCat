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
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion

class Regen : Module("Regen", "Regenerates your health much faster.", ModuleCategory.PLAYER) {

    private val healthValue = IntValue("Health", 18, 0..20)
    private val speedValue = IntValue("Speed", 10, 1..100)
    private val noAirValue = BoolValue("NoAir", false)
    private val potionEffectValue = BoolValue("PotionEffect", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if ((noAirValue.get() && !mc.thePlayer.onGround) || mc.thePlayer.capabilities.isCreativeMode)
            return

        if (mc.thePlayer.isEntityAlive && mc.thePlayer.health < healthValue.get()) {
            if (potionEffectValue.get() && !mc.thePlayer.isPotionActive(Potion.regeneration))
                return

            repeat(speedValue.get()) {
                sendPacket(C03PacketPlayer(mc.thePlayer.onGround))
            }
        }
    }
}
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
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion

object Zoot : Module("Zoot", "Removes all bad potion effects/fire.", ModuleCategory.PLAYER) {

    private val badEffects by BoolValue("BadEffects", true)
    private val fire by BoolValue("Fire", true)
    private val noAir by BoolValue("NoAir", false)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if ((noAir && !mc.thePlayer.onGround) || mc.thePlayer.capabilities.isCreativeMode)
            return

        if (badEffects) {
            val effect = mc.thePlayer.activePotionEffects
                .filter { Potion.potionTypes[it.potionID].isBadEffect }
                .maxBy { it.duration }

            if (effect != null)
                repeat(effect.duration / 20) {
                    sendPacket(C03PacketPlayer(mc.thePlayer.onGround))
                }
        }


        if (fire && mc.thePlayer.isBurning)
            repeat(9) {
                sendPacket(C03PacketPlayer(mc.thePlayer.onGround))
            }
    }
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.minFallDistance
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer
import kotlin.math.abs

object NoFall : Module("NoFall", "Prevents you from taking fall damage.", ModuleCategory.PLAYER) {

    val mode by ListValue("Mode", arrayOf("Spoof", "Damage", "NoGround"), "Spoof")
    private val stableMotion by BoolValue("StableDamageMotion", false)

    override val tag
        get() = mode

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        val player = mc.thePlayer ?: return

        if (packet is C03PacketPlayer)
            when {
                mode == "Spoof" && player.fallDistance > player.minFallDistance -> {
                    packet.onGround = true
                    player.fallDistance = 0f
                }
                mode == "NoGround" -> packet.onGround = false

                mode == "Damage" -> {
                    if (player.fallDistance > player.minFallDistance + 1 * abs(player.motionY)) {
                        packet.onGround = true
                        player.fallDistance = 0f
                        if (stableMotion) mc.thePlayer.motionY = 0.0
                    }
                }
            }
    }
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import kotlin.random.Random

object AutoLeave : Module("AutoLeave", "Automatically makes you leave the server whenever your health is low.", ModuleCategory.COMBAT) {

    private val health by FloatValue("Health", 8f, 0f..20f)
    private val mode by ListValue("Mode", arrayOf("Quit", "InvalidPacket", "SelfHurt", "IllegalChat"), "Quit")

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer.health <= health && !mc.thePlayer.capabilities.isCreativeMode && !mc.isIntegratedServerRunning) {
            when (mode) {
                "Quit" -> mc.theWorld.sendQuittingDisconnectingPacket()
                "InvalidPacket" -> sendPacket(C04PacketPlayerPosition(Double.NaN, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, !mc.thePlayer.onGround))
                "Selfhurt" -> sendPacket(C02PacketUseEntity(mc.thePlayer, C02PacketUseEntity.Action.ATTACK))
                "IllegalChat" -> mc.thePlayer.sendChatMessage("${Random.nextInt(10000)}§")
            }

            state = false
        }
    }
}
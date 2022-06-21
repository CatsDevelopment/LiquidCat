/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.AttackEvent
import lol.liquidcat.event.EventTarget
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.IntValue
import net.minecraft.network.play.client.C02PacketUseEntity

object ComboDamage : Module("ComboDamage", "Adds extra damage when hitting on combo servers.", ModuleCategory.COMBAT) {

    val packets by IntValue("Packets", 20, 1..20)

    override val tag
        get() = packets.toString()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        repeat(packets) {
            sendPacket(C02PacketUseEntity(event.targetEntity, C02PacketUseEntity.Action.ATTACK))
        }
    }
}
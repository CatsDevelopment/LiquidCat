/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.AttackEvent
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.item.getDamage
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import net.minecraft.item.ItemSword
import net.minecraft.item.ItemTool
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C09PacketHeldItemChange

object AutoWeapon : Module("AutoWeapon", "Automatically selects the best weapon in your hotbar.", ModuleCategory.COMBAT) {

    private val silent by BoolValue("SpoofItem", false)
    private val ticks by IntValue("SpoofTicks", 10, 1..20)
    private var attackEnemy = false

    private var spoofedSlot = 0

    @EventTarget
    fun onAttack(event: AttackEvent) {
        attackEnemy = true
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet is C02PacketUseEntity && event.packet.action == C02PacketUseEntity.Action.ATTACK && attackEnemy) {
            attackEnemy = false

            // Find best weapon in hotbar (#Kotlin Style)
            val (slot, _) = (0..8)
                    .map { Pair(it, mc.thePlayer.inventory.getStackInSlot(it)) }
                    .filter { it.second != null && (it.second.item is ItemSword || it.second.item is ItemTool) }
                    .maxByOrNull { it.second.getDamage() } ?: return

            if (slot == mc.thePlayer.inventory.currentItem) // If in hand no need to swap
                return

            // Switch to best weapon
            if (silent) {
                sendPacket(C09PacketHeldItemChange(slot))
                spoofedSlot = ticks
            } else {
                mc.thePlayer.inventory.currentItem = slot
                mc.playerController.updateController()
            }

            // Resend attack packet
            sendPacket(event.packet)
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(update: UpdateEvent) {
        // Switch back to old item after some time
        if (spoofedSlot > 0) {
            if (spoofedSlot == 1)
                sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
            spoofedSlot--
        }
    }
}
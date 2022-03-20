/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventState.POST
import lol.liquidcat.event.EventState.PRE
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.MotionEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.Rotation
import lol.liquidcat.utils.item.isHotbarFull
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import lol.liquidcat.utils.timer.MSTimer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0DPacketCloseWindow
import net.minecraft.network.play.client.C16PacketClientStatus
import net.minecraft.potion.Potion
import org.apache.commons.lang3.RandomUtils

//TODO Rewrite

class AutoPot : Module("AutoPot", "Automatically throws healing potions.", ModuleCategory.COMBAT) {

    private val health by FloatValue("Health", 15f, 1f..20f)
    private val delay by IntValue("Delay", 500, 500..1000)
    private val openInv by BoolValue("OpenInv", false)
    private val simulateInv by BoolValue("SimulateInventory", true)
    private val groundDistance by FloatValue("GroundDistance", 2f, 0f..5f)
    private val mode by ListValue("Mode", arrayOf("Normal", "Jump", "Port"), "Normal")

    private val msTimer = MSTimer()
    private var potion = -1

    override val tag: String
        get() = health.toString()

    @EventTarget
    fun onMotion(motionEvent: MotionEvent) {
        if (!msTimer.hasTimePassed(delay.toLong()) || mc.playerController.isInCreativeMode)
            return

        when (motionEvent.eventState) {
            PRE -> {
                // Hotbar Potion
                val potionInHotbar = findPotion(36, 45)

                if (mc.thePlayer.health <= health && potionInHotbar != -1) {
                    if (mc.thePlayer.onGround) {
                        when (mode.toLowerCase()) {
                            "jump" -> mc.thePlayer.jump()
                            "port" -> mc.thePlayer.moveEntity(0.0, 0.42, 0.0)
                        }
                    }

                    // Prevent throwing potions into the void
                    val fallingPlayer = FallingPlayer(
                            mc.thePlayer.posX,
                            mc.thePlayer.posY,
                            mc.thePlayer.posZ,
                            mc.thePlayer.motionX,
                            mc.thePlayer.motionY,
                            mc.thePlayer.motionZ,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.moveStrafing,
                            mc.thePlayer.moveForward
                    )

                    val collisionBlock = fallingPlayer.findCollision(20)

                    if (mc.thePlayer.posY - (collisionBlock?.y ?: 0) >= groundDistance)
                        return

                    potion = potionInHotbar
                    sendPacket(C09PacketHeldItemChange(potion - 36))

                    if (mc.thePlayer.rotationPitch <= 80F) {
                        RotationUtils.setTargetRotation(Rotation(mc.thePlayer.rotationYaw, RandomUtils.nextFloat(80f, 90f)))
                    }
                    return
                }

                // Inventory Potion -> Hotbar Potion
                val potionInInventory = findPotion(9, 36)
                if (potionInInventory != -1 && !isHotbarFull()) {
                    if (openInv && mc.currentScreen !is GuiInventory)
                        return

                    val openInventory = mc.currentScreen !is GuiInventory && simulateInv

                    if (openInventory)
                        sendPacket(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))

                    mc.playerController.windowClick(0, potionInInventory, 0, 1, mc.thePlayer)

                    if (openInventory)
                        sendPacket(C0DPacketCloseWindow())

                    msTimer.reset()
                }
            }
            POST -> {
                if (potion >= 0 && RotationUtils.serverRotation.pitch >= 75F) {
                    val itemStack = mc.thePlayer.inventoryContainer.getSlot(potion).stack

                    if (itemStack != null) {
                        sendPacket(C08PacketPlayerBlockPlacement(itemStack))
                        sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))

                        msTimer.reset()
                    }

                    potion = -1
                }
            }
        }
    }

    private fun findPotion(startSlot: Int, endSlot: Int): Int {
        for (i in startSlot until endSlot) {
            val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack

            if (stack == null || stack.item !is ItemPotion || !ItemPotion.isSplash(stack.itemDamage))
                continue

            val itemPotion = stack.item as ItemPotion

            for (potionEffect in itemPotion.getEffects(stack))
                if (potionEffect.potionID == Potion.heal.id)
                    return i

            if (!mc.thePlayer.isPotionActive(Potion.regeneration))
                for (potionEffect in itemPotion.getEffects(stack))
                    if (potionEffect.potionID == Potion.regeneration.id) return i
        }

        return -1
    }
}
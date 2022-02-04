/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntegerValue
import lol.liquidcat.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.potion.Potion

@ModuleInfo(name = "Regen", description = "Regenerates your health much faster.", category = ModuleCategory.PLAYER)
class Regen : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "Spartan"), "Vanilla")
    private val healthValue = IntegerValue("Health", 18, 0, 20)
    private val foodValue = IntegerValue("Food", 18, 0, 20)
    private val speedValue = IntegerValue("Speed", 100, 1, 100)
    private val noAirValue = BoolValue("NoAir", false)
    private val potionEffectValue = BoolValue("PotionEffect", false)

    private var resetTimer = false

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (resetTimer)
            mc.timer.timerSpeed = 1F
            resetTimer = false

        if ((!noAirValue.get() || mc.thePlayer.onGround) && !mc.thePlayer.capabilities.isCreativeMode &&
                mc.thePlayer.foodStats.foodLevel > foodValue.get() && mc.thePlayer.isEntityAlive && mc.thePlayer.health < healthValue.get()) {
            if(potionEffectValue.get() && !mc.thePlayer.isPotionActive(Potion.regeneration)) 
                return
            
            when (modeValue.get().toLowerCase()) {
                "vanilla" -> {
                    repeat(speedValue.get()) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }
                }

                "spartan" -> {
                    if (MovementUtils.isMoving() || !mc.thePlayer.onGround)
                        return

                    repeat(9) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }

                    mc.timer.timerSpeed = 0.45F
                    resetTimer = true
                }
            }
        }
    }
}

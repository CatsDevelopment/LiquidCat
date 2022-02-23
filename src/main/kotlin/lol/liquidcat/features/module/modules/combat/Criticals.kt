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
import lol.liquidcat.features.module.ModuleInfo
import lol.liquidcat.value.IntegerValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition

@ModuleInfo("Criticals", "Automatically deals critical hits.", ModuleCategory.COMBAT)
class Criticals : Module() {

    val modeValue = ListValue("Mode", arrayOf("NCP", "Lowest", "Phase", "Visual"), "NCP")
    val delayValue = IntegerValue("Delay", 0, 0, 500)

    val delayTimer = MSTimer()

    override val tag: String
        get() = modeValue.get()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            if (mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWeb && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && mc.thePlayer.ridingEntity == null && !mc.gameSettings.keyBindJump.isKeyDown && delayTimer.hasTimePassed(delayValue.get().toLong())) {
                val x = mc.thePlayer.posX
                val y = mc.thePlayer.posY
                val z = mc.thePlayer.posZ

                when (modeValue.get()) {
                    "NCP" -> {
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.06251000240445849, z, false))
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                    }
                    "Lowest" -> {
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y + 0.00000000000000356, z, false))
                        mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y, z, false))
                    }
                    "Phase" -> mc.netHandler.addToSendQueue(C04PacketPlayerPosition(x, y - 0.00000000000000356, z, false))
                }

                mc.thePlayer.onCriticalHit(event.targetEntity)
                delayTimer.reset()
            }
        }
    }
}
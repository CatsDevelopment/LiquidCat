/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import lol.liquidcat.value.IntegerValue
import net.minecraft.client.settings.KeyBinding

@ModuleInfo(name = "Trigger", description = "Automatically attacks the entity you are looking at.", category = ModuleCategory.COMBAT)
class Trigger : Module() {

    private val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)
            delay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }

    private val minCPS: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)
            delay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }

    private var delay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
    private var lastSwing = 0L

    @EventTarget
    fun onRender(event: Render3DEvent) {
        if (mc.objectMouseOver != null && System.currentTimeMillis() - lastSwing >= delay &&
                EntityUtils.isSelected(mc.objectMouseOver.entityHit, true)) {
            KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode) // Minecraft Click handling
            lastSwing = System.currentTimeMillis()
            delay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
    }
}
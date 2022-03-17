/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.TextValue
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import kotlin.random.Random

//TODO Add more options

class Spammer : Module("Spammer", "Spams the chat with a given message.", ModuleCategory.MISC) {

    private val maxDelayValue: IntValue = object : IntValue("MaxDelay", 1000, 0..5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelayValueObject = minDelayValue.get()

            if (minDelayValueObject > newValue) set(minDelayValueObject)

            delay = TimeUtils.randomDelay(minDelayValue.get(), get())
        }
    }

    private val minDelayValue: IntValue = object : IntValue("MinDelay", 500, 0..5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelayValueObject = maxDelayValue.get()

            if (maxDelayValueObject < newValue) set(maxDelayValueObject)

            delay = TimeUtils.randomDelay(get(), maxDelayValue.get())
        }
    }

    private val messageValue = TextValue("Message", LiquidCat.CLIENT_NAME + " Client / https://discord.gg/asCkVB9Gj3")
    private val customValue = BoolValue("Custom", false)

    private var delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
    private val delayTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (delayTimer.hasTimePassed(delay)) {
            mc.thePlayer.sendChatMessage(
                if (customValue.get())
                    replace(messageValue.get())
                else
                    "${messageValue.get()} [${RandomUtils.randomString(5 + Random.nextInt(5))}]"
            )

            delayTimer.reset()
            delay = TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
        }
    }

    private fun replace(text: String): String {
        var replacedText = text

        while (replacedText.contains("%f"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%f")) + Random.nextFloat() + replacedText.substring(replacedText.indexOf("%f") + "%f".length)

        while (replacedText.contains("%i"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%i")) + Random.nextInt(10000) + replacedText.substring(replacedText.indexOf("%i") + "%i".length)

        while (replacedText.contains("%s"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%s")) + RandomUtils.randomString(Random.nextInt(8) + 1) + replacedText.substring(replacedText.indexOf("%s") + "%s".length)

        while (replacedText.contains("%ss"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%ss")) + RandomUtils.randomString(Random.nextInt(4) + 1) + replacedText.substring(replacedText.indexOf("%ss") + "%ss".length)

        while (replacedText.contains("%ls"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%ls")) + RandomUtils.randomString(Random.nextInt(15) + 1) + replacedText.substring(replacedText.indexOf("%ls") + "%ls".length)

        return replacedText
    }
}
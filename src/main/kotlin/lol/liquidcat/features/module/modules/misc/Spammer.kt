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
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.utils.timer.TimeUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.TextValue
import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random

//TODO Add more options

object Spammer : Module("Spammer", "Spams the chat with a given message.", ModuleCategory.MISC) {

    private val maxDelay: Int by object : IntValue("MaxDelay", 1000, 0..5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelayValueObject = minDelay

            if (minDelayValueObject > newValue) set(minDelayValueObject)

            delay = TimeUtils.randomDelay(minDelay, get())
        }
    }

    private val minDelay: Int by object : IntValue("MinDelay", 500, 0..5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelayValueObject = maxDelay

            if (maxDelayValueObject < newValue) set(maxDelayValueObject)

            delay = TimeUtils.randomDelay(get(), maxDelay)
        }
    }

    private val message by TextValue("Message", "${LiquidCat.CLIENT_NAME} Client / https://discord.gg/asCkVB9Gj3")
    private val custom by BoolValue("Custom", false)

    private var delay = TimeUtils.randomDelay(minDelay, maxDelay)
    private val delayTimer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (delayTimer.hasTimePassed(delay)) {
            mc.thePlayer.sendChatMessage(
                if (custom)
                    replace(message)
                else
                    "${message} [${RandomStringUtils.randomAlphanumeric(5 + Random.nextInt(5))}]"
            )

            delayTimer.reset()
            delay = TimeUtils.randomDelay(minDelay, maxDelay)
        }
    }

    private fun replace(text: String): String {
        var replacedText = text

        while (replacedText.contains("%f"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%f")) + Random.nextFloat() + replacedText.substring(replacedText.indexOf("%f") + "%f".length)

        while (replacedText.contains("%i"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%i")) + Random.nextInt(10000) + replacedText.substring(replacedText.indexOf("%i") + "%i".length)

        while (replacedText.contains("%s"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%s")) + RandomStringUtils.randomAlphanumeric(Random.nextInt(8) + 1) + replacedText.substring(replacedText.indexOf("%s") + "%s".length)

        while (replacedText.contains("%ss"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%ss")) + RandomStringUtils.randomAlphanumeric(Random.nextInt(4) + 1) + replacedText.substring(replacedText.indexOf("%ss") + "%ss".length)

        while (replacedText.contains("%ls"))
            replacedText = replacedText.substring(0, replacedText.indexOf("%ls")) + RandomStringUtils.randomAlphanumeric(Random.nextInt(15) + 1) + replacedText.substring(replacedText.indexOf("%ls") + "%ls".length)

        return replacedText
    }
}
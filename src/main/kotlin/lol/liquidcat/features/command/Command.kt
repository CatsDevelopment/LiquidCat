/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.command

import lol.liquidcat.LiquidCat
import lol.liquidcat.utils.msg
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.client.audio.PositionedSoundRecord
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

//@TODO completely rewrite commands
@SideOnly(Side.CLIENT)
abstract class Command(val command: String, val alias: Array<String>) : MinecraftInstance() {
    /**
     * Execute commands with provided [args]
     */
    abstract fun execute(args: Array<String>)

    /**
     * Returns a list of command completions based on the provided [args].
     * If a command does not implement [tabComplete] an [EmptyList] is returned by default.
     *
     * @param args an array of command arguments that the player has passed to the command so far
     * @return a list of matching completions for the command the player is trying to autocomplete
     * @author NurMarvin
     */
    open fun tabComplete(args: Array<String>): List<String> {
        return emptyList()
    }

    /**
     * Print [msg] to chat
     */
    protected fun chat(msg: String) = msg("§8[§9§l${LiquidCat.CLIENT_NAME}§8] §3$msg")

    /**
     * Print [syntax] of command to chat
     */
    protected fun chatSyntax(syntax: String) = msg("§8[§9§l${LiquidCat.CLIENT_NAME}§8] §3Syntax: §7${CommandManager.prefix}$syntax")

    /**
     * Print [syntaxes] of command to chat
     */
    protected fun chatSyntax(syntaxes: Array<String>) {
        msg("§8[§9§l${LiquidCat.CLIENT_NAME}§8] §3Syntax:")

        for (syntax in syntaxes)
            msg("§8> §7${CommandManager.prefix}$command ${syntax.toLowerCase()}")
    }

    /**
     * Print a syntax error to chat
     */
    protected fun chatSyntaxError() = msg("§8[§9§l${LiquidCat.CLIENT_NAME}§8] §3Syntax error")

    /**
     * Play edit sound
     */
    protected fun playEdit() = mc.soundHandler.playSound(PositionedSoundRecord.create(ResourceLocation("random.anvil_use"), 1F))
}
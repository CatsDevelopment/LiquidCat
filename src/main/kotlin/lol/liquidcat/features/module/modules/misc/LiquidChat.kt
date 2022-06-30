/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.misc

import lol.liquidcat.LiquidCat
import lol.liquidcat.chat.Client
import lol.liquidcat.chat.packet.packets.*
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.SessionEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.command.Command
import lol.liquidcat.features.command.CommandManager
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.login.UserUtils
import lol.liquidcat.utils.msg
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.value.BoolValue
import lol.liquidcat.utils.StringUtils
import net.minecraft.event.ClickEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IChatComponent
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI
import java.net.URISyntaxException
import java.util.regex.Pattern
import kotlin.concurrent.thread

object LiquidChat : Module("LiquidChat", "Allows you to chat with other LiquidBounce users.", ModuleCategory.MISC, hide = true) {

    init {
        state = true
    }

    private var jwt by object : BoolValue("JWT", false) {
        override fun onChanged(oldValue: Boolean, newValue: Boolean) {
            if(state) {
                state = false
                state = true
            }
        }
    }

    var jwtToken = ""

    val client = object : Client() {

        /**
         * Handle connect to web socket
         */
        override fun onConnect() {
            msg("§7[§a§lChat§7] §9Connecting to chat server...")
        }

        /**
         * Handle connect to web socket
         */
        override fun onConnected() {
            msg("§7[§a§lChat§7] §9Connected to chat server!")
        }

        /**
         * Handle handshake
         */
        override fun onHandshake(success: Boolean) { }

        /**
         * Handle disconnect
         */
        override fun onDisconnect() {
            msg("§7[§a§lChat§7] §cDisconnected from chat server!")
        }

        /**
         * Handle logon to web socket with minecraft account
         */
        override fun onLogon() {
            msg("§7[§a§lChat§7] §9Logging in...")
        }

        /**
         * Handle incoming packets
         */
        override fun onPacket(packet: Packet) {
            when (packet) {
                is ClientMessagePacket -> {
                    if (mc.thePlayer == null) {
                        LiquidCat.logger.info("[LiquidChat] ${packet.user.name}: ${packet.content}")
                        return
                    }

                    val chatComponent = ChatComponentText("§7[§a§lChat§7] §9${packet.user.name}: ")
                    val messageComponent = toChatComponent(packet.content)
                    chatComponent.appendSibling(messageComponent)

                    mc.thePlayer.addChatMessage(chatComponent)
                }
                is ClientPrivateMessagePacket -> msg("§7[§a§lChat§7] §c(P)§9 ${packet.user.name}: §7${packet.content}")
                is ClientErrorPacket -> {
                    val message = when(packet.message) {
                        "NotSupported" -> "This method is not supported!"
                        "LoginFailed" -> "Login Failed!"
                        "NotLoggedIn" -> "You must be logged in to use the chat! Enable LiquidChat."
                        "AlreadyLoggedIn" -> "You are already logged in!"
                        "MojangRequestMissing" -> "Mojang request missing!"
                        "NotPermitted" -> "You are missing the required permissions!"
                        "NotBanned" -> "You are not banned!"
                        "Banned" -> "You are banned!"
                        "RateLimited" -> "You have been rate limited. Please try again later."
                        "PrivateMessageNotAccepted" -> "Private message not accepted!"
                        "EmptyMessage" -> "You are trying to send an empty message!"
                        "MessageTooLong" -> "Message is too long!"
                        "InvalidCharacter" -> "Message contains a non-ASCII character!"
                        "InvalidId" -> "The given ID is invalid!"
                        "Internal" -> "An internal server error occured!"
                        else -> packet.message
                    }

                    msg("§7[§a§lChat§7] §cError: §7$message")
                }
                is ClientSuccessPacket -> {
                    when(packet.reason) {
                        "Login" -> {
                            msg("§7[§a§lChat§7] §9Logged in!")

                            msg("====================================")
                            msg("§c>> §lLiquidChat")
                            msg("§7Write message: §a.chat <message>")
                            msg("§7Write private message: §a.pchat <user> <message>")
                            msg("====================================")

                            loggedIn = true
                        }
                        "Ban" -> msg("§7[§a§lChat§7] §9Successfully banned user!")
                        "Unban" -> msg("§7[§a§lChat§7] §9Successfully unbanned user!")
                    }
                }
                is ClientNewJWTPacket -> {
                    jwtToken = packet.token
                    this@LiquidChat.jwt = true

                    state = false
                    state = true
                }
            }
        }

        /**
         * Handle error
         */
        override fun onError(cause: Throwable) {
            msg("§7[§a§lChat§7] §c§lError: §7${cause.javaClass.name}: ${cause.message}")
        }
    }

    private var loggedIn = false

    private var loginThread: Thread? = null

    private val connectTimer = MSTimer()

    init {
        CommandManager.registerCommand(object : Command("chat", arrayOf("lc", "irc")) {

            override fun execute(args: Array<String>) {
                if(args.size > 1) {
                    if (!state) {
                        chat("§cError: §7LiquidChat is disabled!")
                        return
                    }

                    if(!client.isConnected()) {
                        chat("§cError: §LiquidChat is currently not connected to the server!")
                        return
                    }

                    val message = StringUtils.toCompleteString(args, 1)

                    client.sendMessage(message)
                }else
                    chatSyntax("chat <message>")
            }

        })

        CommandManager.registerCommand(object : Command("pchat", arrayOf("privatechat", "lcpm")) {

            override fun execute(args: Array<String>) {
                if(args.size > 2) {
                    if (!state) {
                        chat("§cError: §7LiquidChat is disabled!")
                        return
                    }

                    if(!client.isConnected()) {
                        chat("§cError: §LiquidChat is currently not connected to the server!")
                        return
                    }

                    val target = args[1]
                    val message = StringUtils.toCompleteString(args, 2)

                    client.sendPrivateMessage(target, message)
                    chat("Message was successfully sent.")
                }else
                    chatSyntax("pchat <username> <message>")
            }

        })

        CommandManager.registerCommand(object : Command("chattoken", emptyArray()) {

            override fun execute(args: Array<String>) {


                if(args.size > 1) {
                    when {
                        args[1].equals("set", true) -> {
                            if(args.size > 2) {
                                jwtToken = StringUtils.toCompleteString(args, 2)
                                jwt = true

                                if(state) {
                                    state = false
                                    state = true
                                }
                            }else
                                chatSyntax("chattoken set <token>")
                        }

                        args[1].equals("generate", true) -> {
                            if (!state) {
                                chat("§cError: §7LiquidChat is disabled!")
                                return
                            }

                            client.sendPacket(ServerRequestJWTPacket())
                        }

                        args[1].equals("copy", true) -> {
                            if (jwtToken.isEmpty()) {
                                chat("§cError: §7No token set! Generate one first using '${CommandManager.prefix}chattoken generate'.")
                                return
                            }
                            val stringSelection = StringSelection(jwtToken)
                            Toolkit.getDefaultToolkit().systemClipboard.setContents(stringSelection, stringSelection)
                            chat("§aCopied to clipboard!")
                        }
                    }
                }else
                    chatSyntax("chattoken <set/copy/generate>")
            }

        })

        CommandManager.registerCommand(object : Command("chatadmin", emptyArray()) {

            override fun execute(args: Array<String>) {
                if (!state) {
                    chat("§cError: §7LiquidChat is disabled!")
                    return
                }

                if(args.size > 1) {
                    when {
                        args[1].equals("ban", true) -> {
                            if(args.size > 2) {
                                client.banUser(args[2])
                            }else
                                chatSyntax("chatadmin ban <username>")
                        }

                        args[1].equals("unban", true) -> {
                            if(args.size > 2) {
                                client.unbanUser(args[2])
                            }else
                                chatSyntax("chatadmin unban <username>")
                        }
                    }
                }else
                    chatSyntax("chatadmin <ban/unban>")
            }

        })
    }

    override fun onDisable() {
        loggedIn = false
        client.disconnect()
    }

    @EventTarget
    fun onSession(sessionEvent: SessionEvent) {
        client.disconnect()
        connect()
    }

    @EventTarget
    fun onUpdate(updateEvent: UpdateEvent) {
        if(client.isConnected() || (loginThread != null && loginThread!!.isAlive)) return

        if(connectTimer.hasTimePassed(5000)) {
            connect()
            connectTimer.reset()
        }
    }

    private fun connect() {
        if(client.isConnected() || (loginThread != null && loginThread!!.isAlive)) return

        if(jwt && jwtToken.isEmpty()) {
            msg("§7[§a§lChat§7] §cError: §7No token provided!")
            state = false
            return
        }

        loggedIn = false

        loginThread = thread {
            try {
                client.connect()

                if(jwt)
                    client.loginJWT(jwtToken)
                else if(UserUtils.isValidToken(mc.session.token))
                    client.loginMojang()
            }catch (cause: Exception) {
                LiquidCat.logger.error("LiquidChat error", cause)
                msg("§7[§a§lChat§7] §cError: §7${cause.javaClass.name}: ${cause.message}")
            }

            loginThread = null
        }
    }

    /**
     * Forge Hooks
     *
     * @author Forge
     */

    private val urlPattern = Pattern.compile("((?:[a-z0-9]{2,}:\\/\\/)?(?:(?:[0-9]{1,3}\\.){3}[0-9]{1,3}|(?:[-\\w_\\.]{1,}\\.[a-z]{2,}?))(?::[0-9]{1,5})?.*?(?=[!\"\u00A7 \n]|$))", Pattern.CASE_INSENSITIVE)

    private fun toChatComponent(string: String): IChatComponent {
        var component: IChatComponent? = null
        val matcher = urlPattern.matcher(string)
        var lastEnd = 0

        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            // Append the previous left overs.
            val part = string.substring(lastEnd, start)
            if (part.isNotEmpty()) {
                if (component == null) {
                    component = ChatComponentText(part)
                    component.chatStyle.color = EnumChatFormatting.GRAY
                } else
                    component.appendText(part)
            }

            lastEnd = end

            val url = string.substring(start, end)

            try {
                if (URI(url).scheme != null) {
                    // Set the click event and append the link.
                    val link: IChatComponent = ChatComponentText(url)

                    link.chatStyle.chatClickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, url)
                    link.chatStyle.underlined = true
                    link.chatStyle.color = EnumChatFormatting.GRAY

                    if (component == null)
                        component = link
                    else
                        component.appendSibling(link)
                    continue
                }
            } catch (e: URISyntaxException) {
            }

            if (component == null) {
                component = ChatComponentText(url)
                component.chatStyle.color = EnumChatFormatting.GRAY
            } else
                component.appendText(url)
        }

        // Append the rest of the message.
        val end = string.substring(lastEnd)
        if (component == null) {
            component = ChatComponentText(end)
            component.chatStyle.color = EnumChatFormatting.GRAY
        } else if (end.isNotEmpty())
            component.appendText(string.substring(lastEnd))
        return component
    }

}
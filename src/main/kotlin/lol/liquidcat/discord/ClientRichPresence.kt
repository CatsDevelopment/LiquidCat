/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.discord

import com.jagrosh.discordipc.IPCClient
import com.jagrosh.discordipc.IPCListener
import com.jagrosh.discordipc.entities.RichPresence
import com.jagrosh.discordipc.entities.pipe.PipeStatus
import lol.liquidcat.LiquidCat
import lol.liquidcat.features.module.ModuleManager
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import org.json.JSONObject
import java.time.OffsetDateTime
import kotlin.concurrent.thread

class ClientRichPresence : MinecraftInstance() {

    // IPC Client
    private var ipcClient: IPCClient? = null

    private var appID = 939974399233253447
    private val timestamp = OffsetDateTime.now()

    // Status of running
    private var running: Boolean = false

    /**
     * Setup Discord RPC
     */
    fun setup() {
        try {
            running = true

            ipcClient = IPCClient(appID)
            ipcClient?.setListener(object : IPCListener {

                /**
                 * Fired whenever an [IPCClient] is ready and connected to Discord.
                 *
                 * @param client The now ready IPCClient.
                 */
                override fun onReady(client: IPCClient?) {
                    thread {
                        while (running) {
                            update()

                            try {
                                Thread.sleep(1000L)
                            } catch (ignored: InterruptedException) {
                            }
                        }
                    }
                }

                /**
                 * Fired whenever an [IPCClient] has closed.
                 *
                 * @param client The now closed IPCClient.
                 * @param json A [JSONObject] with close data.
                 */
                override fun onClose(client: IPCClient?, json: JSONObject?) {
                    running = false
                }

            })
            ipcClient?.connect()
        } catch (e: Throwable) {
            LiquidCat.logger.error("Failed to setup Discord RPC.", e)
        }

    }

    /**
     * Update rich presence
     */
    fun update() {
        val builder = RichPresence.Builder()

        // Set playing time
        builder.setStartTimestamp(timestamp)

        builder.setLargeImage("funnycat", "${LiquidCat.CLIENT_NAME} ${LiquidCat.CLIENT_VERSION}")

        // Check user is ingame
        if (mc.thePlayer != null) {
            val serverData = mc.currentServerData

            // Set display infos
            builder.setDetails("Server: ${if (mc.isIntegratedServerRunning || serverData == null) "Singleplayer" else serverData.serverIP}")
            builder.setState("Enabled ${ModuleManager.modules.count { it.state }} of ${ModuleManager.modules.size} modules")
        }

        // Check ipc client is connected and send rpc
        if (ipcClient?.status == PipeStatus.CONNECTED)
            ipcClient?.sendRichPresence(builder.build())
    }

    /**
     * Shutdown ipc client
     */
    fun shutdown() {
        try {
            ipcClient?.close()
        } catch (e: Throwable) {
            LiquidCat.logger.error("Failed to close Discord RPC.", e)
        }
    }
}

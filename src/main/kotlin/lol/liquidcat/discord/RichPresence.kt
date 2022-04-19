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
import lol.liquidcat.utils.ServerUtils
import lol.liquidcat.utils.mc
import org.json.JSONObject
import java.time.OffsetDateTime
import kotlin.concurrent.thread

object RichPresence {

    /**
     * Discord application ID
     */
    private const val CLIENT_ID = 939974399233253447

    private var ipcClient: IPCClient? = null
    private val timestamp = OffsetDateTime.now()

    /**
     * Status of running
     */
    private var running = false

    /**
     * Setup Discord RPC
     */
    fun setup() {
        runCatching {
            running = true

            ipcClient = IPCClient(CLIENT_ID)
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

                            Thread.sleep(1000L)
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
        }.onFailure { LiquidCat.logger.error("Failed to setup Discord RPC.", it.message) }
    }

    /**
     * Update Discord RPC
     */
    fun update() {
        val builder = RichPresence.Builder()

        builder.setStartTimestamp(timestamp)
        builder.setLargeImage("icon", "${LiquidCat.CLIENT_NAME} ${LiquidCat.CLIENT_VERSION}")

        if (mc.thePlayer != null) {
            builder.setDetails("Playing on ${ServerUtils.remoteIp}")
            builder.setState("Enabled ${ModuleManager.modules.count { it.state }} of ${ModuleManager.modules.size} modules")
        } else
            builder.setDetails("cool client")

        // Check ipc client is connected and send rpc
        if (ipcClient?.status == PipeStatus.CONNECTED)
            ipcClient?.sendRichPresence(builder.build())
    }

    /**
     * Shutdown Discord RPC
     */
    fun shutdown() {
        runCatching {
            ipcClient?.close()
        }.onFailure { LiquidCat.logger.error("Failed to close Discord RPC.", it.message) }
    }
}

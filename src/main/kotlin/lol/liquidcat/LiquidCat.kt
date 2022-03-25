/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat

import lol.liquidcat.cape.CapeAPI.registerCapeService
import lol.liquidcat.discord.ClientRichPresence
import lol.liquidcat.event.ClientShutdownEvent
import lol.liquidcat.event.EventManager
import lol.liquidcat.features.command.CommandManager
import lol.liquidcat.features.module.ModuleManager
import lol.liquidcat.file.FileManager
import lol.liquidcat.utils.ClientUtils.disableFastRender
import lol.liquidcat.utils.ClickHandler
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.features.special.DonatorCape
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.util.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

object LiquidCat {

    const val CLIENT_NAME = "LiquidCat"
    const val CLIENT_VERSION = "1.0.0"
    const val CLIENT_CREATOR = "CCBlueX & CatsDevelopment"
    const val MINECRAFT_VERSION = "1.8.9"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    var isStarting = false

    val logger: Logger = LogManager.getLogger(CLIENT_NAME)

    // Managers
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager

    // HUD & ClickGUI
    lateinit var hud: HUD

    lateinit var clickGui: ClickGui

    // Menu Background
    var background: ResourceLocation? = null

    // Discord RPC
    private lateinit var clientRichPresence: ClientRichPresence

    /**
     * Execute if client will be started
     */
    fun startClient() {
        isStarting = true

        logger.info("Launching $CLIENT_NAME $CLIENT_VERSION, by $CLIENT_CREATOR")

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(DonatorCape())
        eventManager.registerListener(ClickHandler)

        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()

        ModuleManager.registerModules()

        // Remapper
        try {
            loadSrg()

            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            logger.error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(
            fileManager.modulesConfig, fileManager.valuesConfig, fileManager.accountsConfig,
                fileManager.friendsConfig, fileManager.xrayConfig, fileManager.shortcutsConfig)

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)

        // Register capes service
        try {
            registerCapeService()
        } catch (throwable: Throwable) {
            logger.error("Failed to register cape service", throwable)
        }

        // Setup Discord RPC
        try {
            clientRichPresence = ClientRichPresence()
            clientRichPresence.setup()
        } catch (throwable: Throwable) {
            logger.error("Failed to setup Discord RPC.", throwable)
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable optifine fastrender
        disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()

        // Set is starting status
        isStarting = false
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        logger.info("Shutting down $CLIENT_NAME...")

        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()

        // Shutdown discord rpc
        clientRichPresence.shutdown()
    }
}
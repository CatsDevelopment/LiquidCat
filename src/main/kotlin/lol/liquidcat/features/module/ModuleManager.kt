/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */

package lol.liquidcat.features.module

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventManager
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.KeyEvent
import lol.liquidcat.event.Listenable
import lol.liquidcat.features.command.CommandManager
import lol.liquidcat.features.module.modules.`fun`.Derp
import lol.liquidcat.features.module.modules.`fun`.SkinDerp
import lol.liquidcat.features.module.modules.combat.*
import lol.liquidcat.features.module.modules.exploit.*
import lol.liquidcat.features.module.modules.misc.*
import lol.liquidcat.features.module.modules.movement.*
import lol.liquidcat.features.module.modules.player.*
import lol.liquidcat.features.module.modules.render.*
import lol.liquidcat.features.module.modules.world.*

object ModuleManager : Listenable {

    /**
     * List with all registered modules
     */
    val modules = mutableListOf<Module>()

    init {
        EventManager.registerListener(this)
    }

    /**
     * Registers all modules
     */
    fun registerModules() {
        LiquidCat.logger.info("[ModuleManager] Loading modules...")

        arrayOf(
            Ambience,
            ShaderESP,
            ItemView,
            FunnyJump,
            FunnyHat,
            AutoArmor,
            AutoBow,
            AutoLeave,
            AutoPot,
            AutoSoup,
            Disabler,
            AutoWeapon,
            BowAimbot,
            Criticals,
            KillAura,
            Velocity,
            Fly,
            HighJump,
            NoSlow,
            LiquidWalk,
            SafeWalk,
            Spider,
            Strafe,
            Sprint,
            Speed,
            NoRotate,
            ChestStealer,
            Scaffold,
            CivBreak,
            FastBreak,
            FastPlace,
            ESP,
            Tracers,
            NameTags,
            FastUse,
            Fullbright,
            StorageESP,
            Projectiles,
            Nuker,
            PingSpoof,
            FastClimb,
            Step,
            AutoRespawn,
            AutoTool,
            Spammer,
            Zoot,
            GuiMove,
            Regen,
            NoFall,
            Blink,
            NameProtect,
            NoHurtCam,
            MidClick,
            XRay,
            Timer,
            SkinDerp,
            GhostHand,
            AutoWalk,
            AutoBreak,
            FreeCam,
            Aimbot,
            Eagle,
            HitBox,
            AntiCactus,
            Plugins,
            AntiHunger,
            LongJump,
            Parkour,
            FastBow,
            MultiActions,
            AirJump,
            AutoClicker,
            NoBob,
            BlockOverlay,
            NoFriends,
            BlockESP,
            Chams,
            Clip,
            FOV,
            SwingAnimation,
            Derp,
            ReverseStep,
            InventoryCleaner,
            TrueSight,
            LiquidChat,
            AntiBlind,
            NoSwing,
            BugUp,
            Breadcrumbs,
            AbortBreaking,
            CameraClip,
            SlimeJump,
            MoreCarry,
            NoPitchLimit,
            Kick,
            Liquids,
            AtAllProvider,
            ForceUnicodeChat,
            SuperKnockback,
            KeepContainer,
            VehicleOneHit,
            Reach,
            HeadRotations,
            NoJumpDelay,
            HUD,
            ResourcePackSpoof,
            NoSlowBreak,
            PortalMenu,
            NoClose,
            NoScoreboard,
            Fucker,
            ChestAura,
            AntiBot,
            ClickGUI,
            Teams
        ).apply {

            // Registers each module
            forEach { registerModule(it) }

            // Sorts modules alphabetically
            sortBy { it.name }
        }

        LiquidCat.logger.info("[ModuleManager] Loaded ${modules.size} modules.")
    }

    /**
     * Searches and returns the module by its [name]
     */
    fun getModule(name: String?) = modules.find { it.name.equals(name, true) }

    /**
     * Registers [module]
     */
    fun registerModule(module: Module) {
        modules.add(module)

        generateCommand(module)
        EventManager.registerListener(module)
    }

    /**
     * Unregisters [module]
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        EventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        CommandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}
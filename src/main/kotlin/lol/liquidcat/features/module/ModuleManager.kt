/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */

package lol.liquidcat.features.module

import lol.liquidcat.LiquidCat
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

    val modules = mutableListOf<Module>()

    init {
        LiquidCat.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        LiquidCat.logger.info("[ModuleManager] Loading modules...")

        arrayOf(
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
            forEach { registerModule(it) }
            sortBy { it.name }
        }

        LiquidCat.logger.info("[ModuleManager] Loaded ${modules.size} modules.")
    }
    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, true) }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules.add(module)

        generateCommand(module)
        LiquidCat.eventManager.registerListener(module)
    }

    /**
     * Unregister [module]
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        LiquidCat.eventManager.unregisterListener(module)
    }

    // TODO: Replace with something better? i think
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
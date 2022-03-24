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
import lol.liquidcat.features.module.modules.`fun`.Derp
import lol.liquidcat.features.module.modules.`fun`.SkinDerp
import lol.liquidcat.features.module.modules.combat.*
import lol.liquidcat.features.module.modules.exploit.*
import lol.liquidcat.features.module.modules.misc.*
import lol.liquidcat.features.module.modules.movement.*
import lol.liquidcat.features.module.modules.player.*
import lol.liquidcat.features.module.modules.render.*
import lol.liquidcat.features.module.modules.world.*
import lol.liquidcat.features.module.modules.world.Timer
import java.util.*

class ModuleManager : Listenable {

    val modules = TreeSet<Module> { module1, module2 -> module1.name.compareTo(module2.name) }
    private val moduleClassMap = hashMapOf<Class<*>, Module>()

    init {
        LiquidCat.eventManager.registerListener(this)
    }

    /**
     * Register all modules
     */
    fun registerModules() {
        LiquidCat.logger.info("[ModuleManager] Loading modules...")

        registerModules(
                FunnyHat::class.java,
                AutoArmor::class.java,
                AutoBow::class.java,
                AutoLeave::class.java,
                AutoPot::class.java,
                AutoSoup::class.java,
                Disabler::class.java,
                AutoWeapon::class.java,
                BowAimbot::class.java,
                Criticals::class.java,
                KillAura::class.java,
                Velocity::class.java,
                Fly::class.java,
                HighJump::class.java,
                NoSlow::class.java,
                LiquidWalk::class.java,
                SafeWalk::class.java,
                Spider::class.java,
                Strafe::class.java,
                Sprint::class.java,
                Speed::class.java,
                NoRotate::class.java,
                ChestStealer::class.java,
                Scaffold::class.java,
                CivBreak::class.java,
                FastBreak::class.java,
                FastPlace::class.java,
                ESP::class.java,
                Tracers::class.java,
                NameTags::class.java,
                FastUse::class.java,
                Fullbright::class.java,
                StorageESP::class.java,
                Projectiles::class.java,
                Nuker::class.java,
                PingSpoof::class.java,
                FastClimb::class.java,
                Step::class.java,
                AutoRespawn::class.java,
                AutoTool::class.java,
                Spammer::class.java,
                Zoot::class.java,
                GuiMove::class.java,
                Regen::class.java,
                NoFall::class.java,
                Blink::class.java,
                NameProtect::class.java,
                NoHurtCam::class.java,
                MidClick::class.java,
                XRay::class.java,
                Timer::class.java,
                SkinDerp::class.java,
                GhostHand::class.java,
                AutoWalk::class.java,
                AutoBreak::class.java,
                FreeCam::class.java,
                Aimbot::class.java,
                Eagle::class.java,
                HitBox::class.java,
                AntiCactus::class.java,
                Plugins::class.java,
                AntiHunger::class.java,
                LongJump::class.java,
                Parkour::class.java,
                FastBow::class.java,
                MultiActions::class.java,
                AirJump::class.java,
                AutoClicker::class.java,
                NoBob::class.java,
                BlockOverlay::class.java,
                NoFriends::class.java,
                BlockESP::class.java,
                Chams::class.java,
                Clip::class.java,
                FOV::class.java,
                SwingAnimation::class.java,
                Derp::class.java,
                ReverseStep::class.java,
                InventoryCleaner::class.java,
                TrueSight::class.java,
                LiquidChat::class.java,
                AntiBlind::class.java,
                NoSwing::class.java,
                BugUp::class.java,
                Breadcrumbs::class.java,
                AbortBreaking::class.java,
                CameraClip::class.java,
                SlimeJump::class.java,
                MoreCarry::class.java,
                NoPitchLimit::class.java,
                Kick::class.java,
                Liquids::class.java,
                AtAllProvider::class.java,
                ForceUnicodeChat::class.java,
                SuperKnockback::class.java,
                KeepContainer::class.java,
                VehicleOneHit::class.java,
                Reach::class.java,
                HeadRotations::class.java,
                NoJumpDelay::class.java,
                HUD::class.java,
                ResourcePackSpoof::class.java,
                NoSlowBreak::class.java,
                PortalMenu::class.java,
                NoClose::class.java
        )

        registerModule(NoScoreboard)
        registerModule(Fucker)
        registerModule(ChestAura)
        registerModule(AntiBot)
        registerModule(ClickGUI)
        registerModule(Teams)

        LiquidCat.logger.info("[ModuleManager] Loaded ${modules.size} modules.")
    }

    /**
     * Register [module]
     */
    fun registerModule(module: Module) {
        modules += module
        moduleClassMap[module.javaClass] = module

        generateCommand(module)
        LiquidCat.eventManager.registerListener(module)
    }

    /**
     * Register [moduleClass]
     */
    private fun registerModule(moduleClass: Class<out Module>) {
        try {
            registerModule(moduleClass.newInstance())
        } catch (e: Throwable) {
            LiquidCat.logger.error("Failed to load module: ${moduleClass.name} (${e.javaClass.name}: ${e.message})")
        }
    }

    /**
     * Register a list of modules
     */
    @SafeVarargs
    fun registerModules(vararg modules: Class<out Module>) {
        modules.forEach(this::registerModule)
    }

    /**
     * Unregister module
     */
    fun unregisterModule(module: Module) {
        modules.remove(module)
        moduleClassMap.remove(module::class.java)
        LiquidCat.eventManager.unregisterListener(module)
    }

    /**
     * Generate command for [module]
     */
    internal fun generateCommand(module: Module) {
        val values = module.values

        if (values.isEmpty())
            return

        LiquidCat.commandManager.registerCommand(ModuleCommand(module, values))
    }

    /**
     * Legacy stuff
     *
     * TODO: Remove later when everything is translated to Kotlin
     */

    /**
     * Get module by [moduleClass]
     */
    fun getModule(moduleClass: Class<*>) = moduleClassMap[moduleClass]

    operator fun get(clazz: Class<*>) = getModule(clazz)

    /**
     * Get module by [moduleName]
     */
    fun getModule(moduleName: String?) = modules.find { it.name.equals(moduleName, ignoreCase = true) }

    /**
     * Module related events
     */

    /**
     * Handle incoming key presses
     */
    @EventTarget
    private fun onKey(event: KeyEvent) = modules.filter { it.keyBind == event.key }.forEach { it.toggle() }

    override fun handleEvents() = true
}

/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.*
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.features.module.modules.render.FreeCam
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.utils.entity.getDistanceToEntityBox
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import lol.liquidcat.features.module.modules.misc.AntiBot
import lol.liquidcat.features.module.modules.misc.Teams
import lol.liquidcat.features.module.modules.player.Blink
import lol.liquidcat.utils.sendPacket
import net.ccbluex.liquidbounce.utils.RaycastUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import org.apache.commons.lang3.RandomUtils
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.utils.timer.TimeUtils
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityArmorStand
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemSword
import net.minecraft.network.play.client.*
import net.minecraft.potion.Potion
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper
import net.minecraft.world.WorldSettings
import java.awt.Color
import java.util.*
import kotlin.math.max
import kotlin.math.min

//TODO Rewrite

object KillAura : Module("KillAura", "Automatically attacks targets around you.", ModuleCategory.COMBAT) {

    /**
     * OPTIONS
     */

    // CPS - Attack speed
    private val maxCPS: Int by object : IntValue("MaxCPS", 8, 1..20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS, this.get())
        }
    }

    private val minCPS: Int by object : IntValue("MinCPS", 5, 1..20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS)
        }
    }

    private val hurtTime by IntValue("HurtTime", 10, 0..10)

    // Range
    private val range by FloatValue("Range", 3.7f, 1f..8f)
    private val throughWallsRange by FloatValue("ThroughWallsRange", 3f, 0f..8f)
    private val rangeSprintReducement by FloatValue("RangeSprintReducement", 0f, 0f..0.4f)

    // Modes
    private val priority by ListValue("Priority", arrayOf("Health", "Distance", "Direction", "LivingTime"), "Distance")
    private val targetMode by ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Switch")

    // Bypass
    private val swing by BoolValue("Swing", true)
    private val keepSprint by BoolValue("KeepSprint", true)

    // AutoBlock
    private val autoBlock by BoolValue("AutoBlock", false)
    private val interactAutoBlock by BoolValue("InteractAutoBlock", true)
    private val delayedBlock by BoolValue("DelayedBlock", true)
    private val blockRate by IntValue("BlockRate", 100, 1..100)

    // Raycast
    private val raycast by BoolValue("RayCast", true)
    private val raycastIgnored by BoolValue("RayCastIgnored", false)
    private val livingRaycast by BoolValue("LivingRayCast", true)

    // Bypass
    private val aac by BoolValue("AAC", false)

    // Turn Speed
    private val maxTurnSpeed: Float by object : FloatValue("MaxTurnSpeed", 180f, 0f..180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed
            if (v > newValue) set(v)
        }
    }

    private val minTurnSpeed: Float by object : FloatValue("MinTurnSpeed", 180f, 0f..180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed
            if (v < newValue) set(v)
        }
    }

    private val silentRotation by BoolValue("SilentRotation", true)
    private val rotationStrafe by ListValue("Strafe", arrayOf("Off", "Strict", "Silent"), "Off")
    private val randomCenter by BoolValue("RandomCenter", true)
    private val outborder by BoolValue("Outborder", false)
    private val fov by FloatValue("FOV", 180f, 0f..180f)

    // Predict
    private val predict by BoolValue("Predict", true)

    private val maxPredictSize: Float by object : FloatValue("MaxPredictSize", 1f, 0.1f..5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSize
            if (v > newValue) set(v)
        }
    }

    private val minPredictSize: Float by object : FloatValue("MinPredictSize", 1f, 0.1f..5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSize
            if (v < newValue) set(v)
        }
    }

    // Bypass
    private val failRate by FloatValue("FailRate", 0f, 0f..100f)
    private val fakeSwing by BoolValue("FakeSwing", true)
    private val noInventoryAttack by BoolValue("NoInvAttack", false)
    private val noInventoryDelay by IntValue("NoInvDelay", 200, 0..500)
    private val limitedMultiTargets by IntValue("LimitedMultiTargets", 0, 0..50)

    // Visuals
    private val mark by BoolValue("Mark", true)
    private val fakeSharp by BoolValue("FakeSharp", true)

    /**
     * MODULE
     */

    // Target
    var target: EntityLivingBase? = null
    private var currentTarget: EntityLivingBase? = null
    private var hitable = false
    private val prevTargetEntities = mutableListOf<Int>()

    // Attack delay
    private val attackTimer = MSTimer()
    private var attackDelay = 0L
    private var clicks = 0

    // Container Delay
    private var containerOpen = -1L

    // Fake block status
    var blockingStatus = false

    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
    }

    /**
     * Disable kill aura module
     */
    override fun onDisable() {
        target = null
        currentTarget = null
        hitable = false
        prevTargetEntities.clear()
        attackTimer.reset()
        clicks = 0

        stopBlocking()
    }

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST) {
            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()

            // AutoBlock
            if (autoBlock && delayedBlock && canBlock)
                startBlocking(currentTarget!!, hitable)

            return
        }

        if (rotationStrafe.equals("Off", true))
            update()
    }

    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (rotationStrafe.equals("Off", true))
            return

        update()

        if (currentTarget != null && RotationUtils.targetRotation != null) {
            when (rotationStrafe.toLowerCase()) {
                "strict" -> {
                    val (yaw) = RotationUtils.targetRotation ?: return
                    var strafe = event.strafe
                    var forward = event.forward
                    val friction = event.friction

                    var f = strafe * strafe + forward * forward

                    if (f >= 1.0E-4F) {
                        f = MathHelper.sqrt_float(f)

                        if (f < 1.0F)
                            f = 1.0F

                        f = friction / f
                        strafe *= f
                        forward *= f

                        val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                        val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())

                        mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                        mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
                    }
                    event.cancelEvent()
                }
                "silent" -> {
                    update()

                    RotationUtils.targetRotation.applyStrafeToPlayer(event)
                    event.cancelEvent()
                }
            }
        }
    }

    fun update() {
        if (cancelRun || (noInventoryAttack && (mc.currentScreen is GuiContainer ||
                        System.currentTimeMillis() - containerOpen < noInventoryDelay)))
            return

        // Update target
        updateTarget()

        if (target == null) {
            stopBlocking()
            return
        }

        // Target
        currentTarget = target

        if (!targetMode.equals("Switch", ignoreCase = true) && isEnemy(currentTarget))
            target = currentTarget
    }

    /**
     * Update event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (noInventoryAttack && (mc.currentScreen is GuiContainer ||
                        System.currentTimeMillis() - containerOpen < noInventoryDelay)) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        if (target != null && currentTarget != null) {
            while (clicks > 0) {
                runAttack()
                clicks--
            }
        }
    }

    /**
     * Render event
     */
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (noInventoryAttack && (mc.currentScreen is GuiContainer ||
                        System.currentTimeMillis() - containerOpen < noInventoryDelay)) {
            target = null
            currentTarget = null
            hitable = false
            if (mc.currentScreen is GuiContainer) containerOpen = System.currentTimeMillis()
            return
        }

        target ?: return

        if (mark && !targetMode.equals("Multi", ignoreCase = true))
            GLUtils.drawPlatform(target!!, if (hitable) Color(37, 126, 255, 70) else Color(255, 0, 0, 70))

        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) &&
                currentTarget!!.hurtTime <= hurtTime) {
            clicks++
            attackTimer.reset()
            attackDelay = TimeUtils.randomClickDelay(minCPS, maxCPS)
        }
    }

    /**
     * Handle entity move
     */
    @EventTarget
    fun onEntityMove(event: EntityMovementEvent) {
        val movedEntity = event.movedEntity

        if (target == null || movedEntity != currentTarget)
            return

        updateHitable()
    }

    /**
     * Attack enemy
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return

        // Settings
        val failRate = failRate
        val swing = swing
        val multi = targetMode.equals("Multi", ignoreCase = true)
        val openInventory = aac && mc.currentScreen is GuiInventory
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        // Close inventory when open
        if (openInventory)
            sendPacket(C0DPacketCloseWindow())

        // Check is not hitable or check failrate
        if (!hitable || failHit) {
            if (swing && (fakeSwing || failHit))
                mc.thePlayer.swingItem()
        } else {
            // Attack
            if (!multi) {
                attackEntity(currentTarget!!)
            } else {
                var targets = 0

                for (entity in mc.theWorld.loadedEntityList) {
                    val distance = mc.thePlayer.getDistanceToEntityBox(entity)

                    if (entity is EntityLivingBase && isEnemy(entity) && distance <= getRange(entity)) {
                        attackEntity(entity)

                        targets += 1

                        if (limitedMultiTargets != 0 && limitedMultiTargets <= targets)
                            break
                    }
                }
            }

            prevTargetEntities.add(if (aac) target!!.entityId else currentTarget!!.entityId)

            if (target == currentTarget)
                target = null
        }

        // Open inventory
        if (openInventory)
            sendPacket(C16PacketClientStatus(C16PacketClientStatus.EnumState.OPEN_INVENTORY_ACHIEVEMENT))
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Reset fixed target to null
        target = null

        // Settings
        val hurtTime = hurtTime
        val fov = fov
        val switchMode = targetMode.equals("Switch", ignoreCase = true)

        // Find possible targets
        val targets = mutableListOf<EntityLivingBase>()

        for (entity in mc.theWorld.loadedEntityList) {
            if (entity !is EntityLivingBase || !isEnemy(entity) || (switchMode && prevTargetEntities.contains(entity.entityId)))
                continue

            val distance = mc.thePlayer.getDistanceToEntityBox(entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= maxRange && (fov == 180F || entityFov <= fov) && entity.hurtTime <= hurtTime)
                targets.add(entity)
        }

        // Cleanup last targets when no targets found and try again
        if (targets.isEmpty()) {
            if (prevTargetEntities.isNotEmpty()) {
                prevTargetEntities.clear()
                updateTarget()
            }

            return
        }

        // Sort targets by priority
        when (priority.toLowerCase()) {
            "distance" -> targets.sortBy { mc.thePlayer.getDistanceToEntityBox(it) } // Sort by distance
            "health" -> targets.sortBy { it.health } // Sort by health
            "direction" -> targets.sortBy { RotationUtils.getRotationDifference(it) } // Sort by FOV
            "livingtime" -> targets.sortBy { -it.ticksExisted } // Sort by existence
        }

        // Find best target
        for (entity in targets) {
            // Update rotations to current target
            if (!updateRotations(entity)) // when failed then try another target
                continue

            // Set target to current entity
            target = entity
            return
        }
    }

    /**
     * Check if [entity] is selected as enemy with current target options and other modules
     */
    private fun isEnemy(entity: Entity?): Boolean {
        if (entity is EntityLivingBase && (EntityUtils.targetDead || isAlive(entity)) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.isInvisible())
                return false

            if (EntityUtils.targetPlayer && entity is EntityPlayer) {
                if (entity.isSpectator || AntiBot.isBot(entity))
                    return false

                if (EntityUtils.isFriend(entity) && !NoFriends.state)
                    return false

                return !Teams.isInYourTeam(entity)
            }

            return EntityUtils.targetMobs && EntityUtils.isMob(entity) || EntityUtils.targetAnimals &&
                    EntityUtils.isAnimal(entity)
        }

        return false
    }

    /**
     * Attack [entity]
     */
    private fun attackEntity(entity: EntityLivingBase) {
        // Stop blocking
        if (mc.thePlayer.isBlocking || blockingStatus) {
            sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM,
                    BlockPos.ORIGIN, EnumFacing.DOWN))
            blockingStatus = false
        }

        // Call attack event
        LiquidCat.eventManager.callEvent(AttackEvent(entity))

        // Attack target
        if (swing)
            mc.thePlayer.swingItem()
        sendPacket(C02PacketUseEntity(entity, C02PacketUseEntity.Action.ATTACK))

        if (keepSprint) {
            // Critical Effect
            if (mc.thePlayer.fallDistance > 0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder &&
                    !mc.thePlayer.isInWater && !mc.thePlayer.isPotionActive(Potion.blindness) && !mc.thePlayer.isRiding)
                mc.thePlayer.onCriticalHit(entity)

            // Enchant Effect
            if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, entity.creatureAttribute) > 0F)
                mc.thePlayer.onEnchantmentCritical(entity)
        } else {
            if (mc.playerController.currentGameType != WorldSettings.GameType.SPECTATOR)
                mc.thePlayer.attackTargetEntityWithCurrentItem(entity)
        }

        for (i in 0..2) {
            // Critical Effect
            if (mc.thePlayer.fallDistance > 0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null || Criticals.state && Criticals.delayTimer.hasTimePassed(Criticals.delay.toLong()) && !mc.thePlayer.isInWater && !mc.thePlayer.isInLava && !mc.thePlayer.isInWeb)
                mc.thePlayer.onCriticalHit(target)

            // Enchant Effect
            if (EnchantmentHelper.getModifierForCreature(mc.thePlayer.heldItem, target!!.creatureAttribute) > 0.0f || fakeSharp)
                mc.thePlayer.onEnchantmentCritical(target)
        }

        // Start blocking after attack
        if (mc.thePlayer.isBlocking || (autoBlock && canBlock)) {
            if (!(blockRate > 0 && Random().nextInt(100) <= blockRate))
                return

            if (delayedBlock)
                return

            startBlocking(entity, interactAutoBlock)
        }
    }

    /**
     * Update killaura rotations to enemy
     */
    private fun updateRotations(entity: Entity): Boolean {
        if(maxTurnSpeed <= 0F)
            return true

        var boundingBox = entity.entityBoundingBox

        if (predict)
            boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize, maxPredictSize),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize, maxPredictSize),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize, maxPredictSize)
            )

        val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborder && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenter,
                predict,
                mc.thePlayer.getDistanceToEntityBox(entity) < throughWallsRange
        ) ?: return false

        val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation,
                (Math.random() * (maxTurnSpeed - minTurnSpeed) + minTurnSpeed).toFloat())

        if (silentRotation)
            RotationUtils.setTargetRotation(limitedRotation, if (aac) 15 else 0)
        else
            limitedRotation.toPlayer(mc.thePlayer)

        return true
    }

    /**
     * Check if enemy is hitable with current rotations
     */
    private fun updateHitable() {
        // Disable hitable check if turn speed is zero
        if(maxTurnSpeed <= 0F) {
            hitable = true
            return
        }

        val reach = min(maxRange.toDouble(), mc.thePlayer.getDistanceToEntityBox(target!!)) + 1

        if (raycast) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach) {
                (!livingRaycast || it is EntityLivingBase && it !is EntityArmorStand) &&
                        (isEnemy(it) || raycastIgnored || aac && mc.theWorld.getEntitiesWithinAABBExcludingEntity(it, it.entityBoundingBox).isNotEmpty())
            }

            if (raycast && raycastedEntity is EntityLivingBase && (NoFriends.state || !EntityUtils.isFriend(raycastedEntity)))
                currentTarget = raycastedEntity

            hitable = if(maxTurnSpeed > 0F) currentTarget == raycastedEntity else true
        } else
            hitable = RotationUtils.isFaced(currentTarget, reach)
    }

    /**
     * Start blocking
     */
    private fun startBlocking(interactEntity: Entity, interact: Boolean) {
        if (interact) {
            sendPacket(C02PacketUseEntity(interactEntity, interactEntity.positionVector))
            sendPacket(C02PacketUseEntity(interactEntity, C02PacketUseEntity.Action.INTERACT))
        }

        sendPacket(C08PacketPlayerBlockPlacement(mc.thePlayer.inventory.getCurrentItem()))
        blockingStatus = true
    }


    /**
     * Stop blocking
     */
    private fun stopBlocking() {
        if (blockingStatus) {
            sendPacket(C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN))
            blockingStatus = false
        }
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        get() = mc.thePlayer.isSpectator || !isAlive(mc.thePlayer) || Blink.state || FreeCam.state

    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: EntityLivingBase) = entity.isEntityAlive && entity.health > 0 || aac && entity.hurtTime > 5


    /**
     * Check if player is able to block
     */
    private val canBlock: Boolean
        get() = mc.thePlayer.heldItem != null && mc.thePlayer.heldItem.item is ItemSword

    /**
     * Range
     */
    private val maxRange: Float
        get() = max(range, throughWallsRange)

    private fun getRange(entity: Entity) =
            (if (mc.thePlayer.getDistanceToEntityBox(entity) >= throughWallsRange) range else throughWallsRange) - if (mc.thePlayer.isSprinting) rangeSprintReducement else 0F

    /**
     * HUD Tag
     */
    override val tag: String?
        get() = targetMode
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.world

import lol.liquidcat.event.*
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.PlaceRotation
import lol.liquidcat.utils.Rotation
import lol.liquidcat.utils.block.PlaceInfo
import lol.liquidcat.utils.block.PlaceInfo.Companion.get
import lol.liquidcat.utils.block.getBlock
import lol.liquidcat.utils.block.isClickable
import lol.liquidcat.utils.block.isReplaceable
import lol.liquidcat.utils.item.findAutoBlockBlock
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.utils.timer.MSTimer
import lol.liquidcat.utils.timer.TimeUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.settings.GameSettings
import net.minecraft.init.Blocks
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.util.*
import java.awt.Color

object Scaffold : Module("Scaffold", "Automatically places blocks beneath your feet.", ModuleCategory.WORLD) {

    val mode by ListValue("Mode", arrayOf("Normal", "Rewinside", "Expand"), "Normal")

    private val maxDelay: Int by object : IntValue("MaxDelay", 0, 0..1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelay

            if (i > newValue) set(i)
        }
    }

    private val minDelay: Int by object : IntValue("MinDelay", 0, 0..1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelay

            if (i < newValue) set(i)
        }
    }
    private val placeableDelay by BoolValue("PlaceableDelay", false)
    private val autoBlock by BoolValue("AutoBlock", true)
    private val stayAutoBlock by BoolValue("StayAutoBlock", false)
    val sprint by BoolValue("Sprint", true)
    private val swing by BoolValue("Swing", true)
    private val search by BoolValue("Search", true)
    private val down by BoolValue("Down", true)
    private val placeMode by ListValue("PlaceTiming", arrayOf("Pre", "Post"), "Post")

    private val eagle by BoolValue("Eagle", false)
    private val eagleSilent by BoolValue("EagleSilent", false)
    private val blocksToEagle by IntValue("BlocksToEagle", 0, 0..10)

    private val expandLength by IntValue("ExpandLength", 5, 1..6)

    private val rotations by BoolValue("Rotations", true)
    private val keepLength by IntValue("KeepRotationLength", 0, 0..20)
    private val keepRotation by BoolValue("KeepRotation", false)

    private val timer by FloatValue("Timer", 1f, 0.1f..10f)
    private val speedModifier by FloatValue("SpeedModifier", 1f, 0f..2f)

    private val sameY by BoolValue("SameY", false)
    private val safeWalk by BoolValue("SafeWalk", true)
    private val airSafe by BoolValue("AirSafe", false)

    private val counterDisplay by BoolValue("Counter", true)
    private val mark by BoolValue("Mark", false)

    private var targetPlace: PlaceInfo? = null
    private var launchY = 0
    private var lockRotation: Rotation? = null
    private var slot = 0
    private val delayTimer = MSTimer()
    private var delay: Long = 0
    private var placedBlocksWithoutEagle = 0
    private var eagleSneaking = false
    private var shouldGoDown = false

    override fun onEnable() {
        mc.thePlayer ?: return

        launchY = mc.thePlayer.posY.toInt()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        mc.timer.timerSpeed = timer
        shouldGoDown = down && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && blocksAmount > 1
        if (shouldGoDown) mc.gameSettings.keyBindSneak.pressed = false
        if (mc.thePlayer.onGround) {

            // Eagle
            if (eagle && !shouldGoDown) {
                if (placedBlocksWithoutEagle >= blocksToEagle) {
                    val shouldEagle = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down().getBlock() == Blocks.air
                    if (eagleSilent) {
                        if (eagleSneaking != shouldEagle) {
                            sendPacket(
                                C0BPacketEntityAction(
                                    mc.thePlayer,
                                    if (shouldEagle) C0BPacketEntityAction.Action.START_SNEAKING else C0BPacketEntityAction.Action.STOP_SNEAKING
                                )
                            )
                        }
                        eagleSneaking = shouldEagle
                    } else mc.gameSettings.keyBindSneak.pressed = shouldEagle
                    placedBlocksWithoutEagle = 0
                } else placedBlocksWithoutEagle++
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return
        val packet = event.packet

        if (packet is C09PacketHeldItemChange) {
            slot = packet.slotId
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val eventState = event.eventState

        // Lock Rotation
        if (rotations && keepRotation && lockRotation != null) RotationUtils.setTargetRotation(
            lockRotation
        )

        // Place block
        if (placeMode.equals(eventState.stateName, ignoreCase = true)) place()

        // Update and search for new block
        if (eventState == EventState.PRE) update()

        // Reset placeable delay
        if (targetPlace == null && placeableDelay) delayTimer.reset()
    }

    private fun update() {
        if (if (autoBlock) findAutoBlockBlock() == -1 else mc.thePlayer.heldItem == null ||
                    mc.thePlayer.heldItem.item !is ItemBlock
        ) return
        findBlock(mode.equals("expand", ignoreCase = true))
    }

    /**
     * Search for new target block
     */
    private fun findBlock(expand: Boolean) {
        val blockPosition = if (shouldGoDown) if (mc.thePlayer.posY == mc.thePlayer.posY.toInt() + 0.5) BlockPos(
            mc.thePlayer.posX,
            mc.thePlayer.posY - 0.6,
            mc.thePlayer.posZ
        ) else BlockPos(
            mc.thePlayer.posX, mc.thePlayer.posY - 0.6, mc.thePlayer.posZ
        ).down() else if (mc.thePlayer.posY == mc.thePlayer.posY.toInt() + 0.5) BlockPos(
            mc.thePlayer
        ) else BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).down()
        if (!expand && (!blockPosition.isReplaceable() || search(blockPosition, !shouldGoDown))) return
        if (expand) {
            for (i in 0 until expandLength) {
                if (search(
                        blockPosition.add(
                            if (mc.thePlayer.horizontalFacing == EnumFacing.WEST) -i else if (mc.thePlayer.horizontalFacing == EnumFacing.EAST) i else 0,
                            0,
                            if (mc.thePlayer.horizontalFacing == EnumFacing.NORTH) -i else if (mc.thePlayer.horizontalFacing == EnumFacing.SOUTH) i else 0
                        ), false
                    )
                ) return
            }
        } else if (search) {
            for (x in -1..1) for (z in -1..1) if (search(blockPosition.add(x, 0, z), !shouldGoDown)) return
        }
    }

    /**
     * Place target block
     */
    private fun place() {
        if (targetPlace == null) {
            if (placeableDelay) delayTimer.reset()
            return
        }
        if (!delayTimer.hasTimePassed(delay) || sameY && launchY - 1 != targetPlace!!.vec3.yCoord.toInt()) return
        var blockSlot = -1
        var itemStack = mc.thePlayer.heldItem
        if (mc.thePlayer.heldItem == null || mc.thePlayer.heldItem.item !is ItemBlock) {
            if (!autoBlock) return
            blockSlot = findAutoBlockBlock()
            if (blockSlot == -1) return
            sendPacket(C09PacketHeldItemChange(blockSlot - 36))
            itemStack = mc.thePlayer.inventoryContainer.getSlot(blockSlot).stack
        }
        if (mc.playerController.onPlayerRightClick(
                mc.thePlayer, mc.theWorld, itemStack, targetPlace!!.blockPos,
                targetPlace!!.enumFacing, targetPlace!!.vec3
            )
        ) {
            delayTimer.reset()
            delay = TimeUtils.randomDelay(minDelay, maxDelay)
            if (mc.thePlayer.onGround) {
                val modifier = speedModifier
                mc.thePlayer.motionX *= modifier.toDouble()
                mc.thePlayer.motionZ *= modifier.toDouble()
            }
            if (swing) mc.thePlayer.swingItem() else sendPacket(C0APacketAnimation())
        }
        if (!stayAutoBlock && blockSlot >= 0) sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))

        // Reset
        targetPlace = null
    }

    /**
     * Disable scaffold module
     */
    override fun onDisable() {
        if (mc.thePlayer == null) return
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.pressed = false
            if (eagleSneaking) sendPacket(
                C0BPacketEntityAction(
                    mc.thePlayer,
                    C0BPacketEntityAction.Action.STOP_SNEAKING
                )
            )
        }
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindRight)) mc.gameSettings.keyBindRight.pressed = false
        if (!GameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) mc.gameSettings.keyBindLeft.pressed = false
        lockRotation = null
        mc.timer.timerSpeed = 1f
        shouldGoDown = false
        if (slot != mc.thePlayer.inventory.currentItem) sendPacket(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
    }

    /**
     * Entity movement event
     *
     * @param event
     */
    @EventTarget
    fun onMove(event: MoveEvent) {
        if (!safeWalk || shouldGoDown) return
        if (airSafe || mc.thePlayer.onGround) event.isSafeWalk = true
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (counterDisplay) {
            GlStateManager.pushMatrix()
            val info = "Blocks: §7$blocksAmount"
            val scaledResolution = ScaledResolution(mc)
            GLUtils.drawBorderedRect(
                (scaledResolution.scaledWidth / 2 - 2).toFloat(),
                (scaledResolution.scaledHeight / 2 + 5).toFloat(),
                (scaledResolution.scaledWidth / 2 + Fonts.nunitoBold40.getStringWidth(info) + 2).toFloat(),
                (scaledResolution.scaledHeight / 2 + 16).toFloat(),
                3f,
                Color.BLACK.rgb,
                Color.BLACK.rgb
            )
            GlStateManager.resetColor()
            Fonts.nunitoBold40.drawString(
                info,
                scaledResolution.scaledWidth / 2,
                scaledResolution.scaledHeight / 2 + 7,
                Color.WHITE.rgb
            )
            GlStateManager.popMatrix()
        }
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        if (!mark) return
        for (i in 0 until if (mode.equals("Expand", ignoreCase = true)) expandLength + 1 else 2) {
            val blockPos = BlockPos(
                mc.thePlayer.posX + if (mc.thePlayer.horizontalFacing == EnumFacing.WEST) -i else if (mc.thePlayer.horizontalFacing == EnumFacing.EAST) i else 0,
                mc.thePlayer.posY - (if (mc.thePlayer.posY == mc.thePlayer.posY.toInt() + 0.5) 0.0 else 1.0) - if (shouldGoDown) 1.0 else 0.0,
                mc.thePlayer.posZ + if (mc.thePlayer.horizontalFacing == EnumFacing.NORTH) -i else if (mc.thePlayer.horizontalFacing == EnumFacing.SOUTH) i else 0
            )
            val placeInfo = get(blockPos)
            if (blockPos.isReplaceable() && placeInfo != null) {
                GLUtils.drawBlockBox(blockPos, Color(68, 117, 255, 100), true, false)
                break
            }
        }
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */
    private fun search(blockPosition: BlockPos, checks: Boolean): Boolean {
        if (!blockPosition.isReplaceable()) return false
        val eyesPos = Vec3(
            mc.thePlayer.posX,
            mc.thePlayer.entityBoundingBox.minY + mc.thePlayer.getEyeHeight(),
            mc.thePlayer.posZ
        )
        var placeRotation: PlaceRotation? = null
        for (side in EnumFacing.values()) {
            val neighbor = blockPosition.offset(side)
            if (!neighbor.isClickable()) continue
            val dirVec = Vec3(side.directionVec)
            var xSearch = 0.1
            while (xSearch < 0.9) {
                var ySearch = 0.1
                while (ySearch < 0.9) {
                    var zSearch = 0.1
                    while (zSearch < 0.9) {
                        val posVec = Vec3(blockPosition).addVector(xSearch, ySearch, zSearch)
                        val distanceSqPosVec = eyesPos.squareDistanceTo(posVec)
                        val hitVec = posVec.add(Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5))
                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(
                                posVec.add(dirVec)
                            ) || mc.theWorld.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null)
                        ) {
                            zSearch += 0.1
                            continue
                        }

                        // face block
                        val diffX = hitVec.xCoord - eyesPos.xCoord
                        val diffY = hitVec.yCoord - eyesPos.yCoord
                        val diffZ = hitVec.zCoord - eyesPos.zCoord
                        val diffXZ = MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ).toDouble()
                        val rotation = Rotation(
                            MathHelper.wrapAngleTo180_float(Math.toDegrees(Math.atan2(diffZ, diffX)).toFloat() - 90f),
                            MathHelper.wrapAngleTo180_float(-Math.toDegrees(Math.atan2(diffY, diffXZ)).toFloat())
                        )
                        val rotationVector = RotationUtils.getVectorForRotation(rotation)
                        val vector = eyesPos.addVector(
                            rotationVector.xCoord * 4,
                            rotationVector.yCoord * 4,
                            rotationVector.zCoord * 4
                        )
                        val obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true)
                        if (!(obj.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && obj.blockPos == neighbor)) {
                            zSearch += 0.1
                            continue
                        }
                        if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(
                                placeRotation.rotation
                            )
                        ) placeRotation = PlaceRotation(PlaceInfo(neighbor, side.opposite, hitVec), rotation)
                        zSearch += 0.1
                    }
                    ySearch += 0.1
                }
                xSearch += 0.1
            }
        }
        if (placeRotation == null) return false
        if (rotations) {
            RotationUtils.setTargetRotation(placeRotation.rotation, keepLength)
            lockRotation = placeRotation.rotation
        }
        targetPlace = placeRotation.placeInfo
        return true
    }

    /**
     * @return hotbar blocks amount
     */
    private val blocksAmount: Int
        get() {
            var amount = 0
            for (i in 36..44) {
                val itemStack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (itemStack != null && itemStack.item is ItemBlock) amount += itemStack.stackSize
            }
            return amount
        }
    override val tag: String
        get() = mode
}
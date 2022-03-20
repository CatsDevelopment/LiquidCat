/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.block.getBlock
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.sendPacket
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.minecraft.block.BlockAir
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

//TODO Rewrite and convert to AntiVoid module

class BugUp : Module("BugUp", "Automatically setbacks you after falling a certain distance.", ModuleCategory.MOVEMENT) {
    private val mode by ListValue("Mode", arrayOf("TeleportBack", "FlyFlag", "OnGroundSpoof"), "FlyFlag")
    private val maxFallDistance by IntValue("MaxFallDistance", 10, 2..255)
    private val maxDistanceWithoutGround by FloatValue("MaxDistanceToSetback", 2.5f, 1f..30f)
    private val indicator by BoolValue("Indicator", true)

    private var detectedLocation: BlockPos? = null
    private var lastFound = 0F
    private var prevX = 0.0
    private var prevY = 0.0
    private var prevZ = 0.0

    override fun onDisable() {
        prevX = 0.0
        prevY = 0.0
        prevZ = 0.0
    }

    @EventTarget
    fun onUpdate(e: UpdateEvent) {
        detectedLocation = null

        if (mc.thePlayer.onGround && BlockPos(mc.thePlayer.posX, mc.thePlayer.posY - 1.0, mc.thePlayer.posZ).getBlock() !is BlockAir) {
            prevX = mc.thePlayer.prevPosX
            prevY = mc.thePlayer.prevPosY
            prevZ = mc.thePlayer.prevPosZ
        }

        if (!mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isInWater) {
            val fallingPlayer = FallingPlayer(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    mc.thePlayer.motionX,
                    mc.thePlayer.motionY,
                    mc.thePlayer.motionZ,
                    mc.thePlayer.rotationYaw,
                    mc.thePlayer.moveStrafing,
                    mc.thePlayer.moveForward
            )

            detectedLocation = fallingPlayer.findCollision(60)

            if (detectedLocation != null && abs(mc.thePlayer.posY - detectedLocation!!.y) +
                    mc.thePlayer.fallDistance <= maxFallDistance) {
                lastFound = mc.thePlayer.fallDistance
            }

            if (mc.thePlayer.fallDistance - lastFound > maxDistanceWithoutGround) {
                when (mode.toLowerCase()) {
                    "teleportback" -> {
                        mc.thePlayer.setPositionAndUpdate(prevX, prevY, prevZ)
                        mc.thePlayer.fallDistance = 0F
                        mc.thePlayer.motionY = 0.0
                    }
                    "flyflag" -> {
                        mc.thePlayer.motionY += 0.1
                        mc.thePlayer.fallDistance = 0F
                    }
                    "ongroundspoof" -> sendPacket(C03PacketPlayer(true))
                }
            }
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (detectedLocation == null || !indicator ||
                mc.thePlayer.fallDistance + (mc.thePlayer.posY - (detectedLocation!!.y + 1)) < 3)
            return

        val x = detectedLocation!!.x
        val y = detectedLocation!!.y
        val z = detectedLocation!!.z

        val renderManager = mc.renderManager

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glLineWidth(2f)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(false)

        GLUtils.glColor(Color(255, 0, 0, 90))
        GLUtils.drawFilledBB(AxisAlignedBB(
                x - renderManager.renderPosX,
                y + 1 - renderManager.renderPosY,
                z - renderManager.renderPosZ,
                x - renderManager.renderPosX + 1.0,
                y + 1.2 - renderManager.renderPosY,
                z - renderManager.renderPosZ + 1.0)
        )

        GL11.glEnable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)

        val fallDist = floor(mc.thePlayer.fallDistance + (mc.thePlayer.posY - (y + 0.5))).toInt()

        renderNameTag("${fallDist}m (~${max(0, fallDist - 3)} damage)", x + 0.5, y + 1.7, z + 0.5)

        GlStateManager.resetColor()
    }

    fun renderNameTag(string: String?, x: Double, y: Double, z: Double) {
        val renderManager = mc.renderManager
        GL11.glPushMatrix()
        GL11.glTranslated(x - renderManager.renderPosX, y - renderManager.renderPosY, z - renderManager.renderPosZ)
        GL11.glNormal3f(0f, 1f, 0f)
        GL11.glRotatef(-mc.renderManager.playerViewY, 0f, 1f, 0f)
        GL11.glRotatef(mc.renderManager.playerViewX, 1f, 0f, 0f)
        GL11.glScalef(-0.05f, -0.05f, 0.05f)

        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        val width = Fonts.font35.getStringWidth(string!!) / 2

        Gui.drawRect(-width - 1, -1, width + 1, Fonts.font35.FONT_HEIGHT, Int.MIN_VALUE)
        Fonts.font35.drawString(string, -width.toFloat(), 1.5f, Color.WHITE.rgb, true)

        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glDisable(GL11.GL_BLEND)

        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glPopMatrix()
    }
}
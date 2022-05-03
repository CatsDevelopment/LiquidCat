/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.PacketEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.strafe
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import net.minecraft.client.entity.EntityOtherPlayerMP
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction

object FreeCam : Module("FreeCam", "Allows you to move out of your body.", ModuleCategory.RENDER) {

    private val speed by FloatValue("Speed", 0.8f, 0.1f..2f)
    private val fly by BoolValue("Fly", true)
    private val noClip by BoolValue("NoClip", true)

    private var fakePlayer: EntityOtherPlayerMP? = null
    private var oldX = 0.0
    private var oldY = 0.0
    private var oldZ = 0.0

    override fun onEnable() {
        mc.thePlayer ?: return

        oldX = mc.thePlayer.posX
        oldY = mc.thePlayer.posY
        oldZ = mc.thePlayer.posZ

        // Creates a copy of the player
        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead

        // Adds a copy to the world
        mc.theWorld.addEntityToWorld(-1000, fakePlayer)
    }

    override fun onDisable() {
        if (mc.thePlayer == null || fakePlayer == null)
            return

        mc.thePlayer.setPosition(oldX, oldY, oldZ)

        // Deletes a copy of the player
        mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)
        fakePlayer = null

        mc.thePlayer.setVelocity(0.0, 0.0, 0.0)

        // Updates chunks
        mc.renderGlobal.loadRenderers()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (noClip) mc.thePlayer.noClip = true

        mc.thePlayer.fallDistance = 0f

        if (fly) {
            val speed = speed.toDouble()

            mc.thePlayer.setVelocity(0.0, 0.0, 0.0)

            if (mc.gameSettings.keyBindJump.isKeyDown)
                mc.thePlayer.motionY += speed

            if (mc.gameSettings.keyBindSneak.isKeyDown)
                mc.thePlayer.motionY -= speed

            mc.thePlayer.strafe(speed)
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        // Cancels some client packets
        if (packet is C03PacketPlayer || packet is C0BPacketEntityAction)
            event.cancelEvent()
    }
}
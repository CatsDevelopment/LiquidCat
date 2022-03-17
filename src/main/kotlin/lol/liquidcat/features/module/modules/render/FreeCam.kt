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

//TODO Rewrite?

class FreeCam : Module("FreeCam", "Allows you to move out of your body.", ModuleCategory.RENDER) {

    private val speedValue = FloatValue("Speed", 0.8f, 0.1f..2f)
    private val flyValue = BoolValue("Fly", true)
    private val noClipValue = BoolValue("NoClip", true)

    private var fakePlayer: EntityOtherPlayerMP? = null
    private var oldX = 0.0
    private var oldY = 0.0
    private var oldZ = 0.0

    override fun onEnable() {
        mc.thePlayer ?: return

        oldX = mc.thePlayer.posX
        oldY = mc.thePlayer.posY
        oldZ = mc.thePlayer.posZ

        fakePlayer = EntityOtherPlayerMP(mc.theWorld, mc.thePlayer.gameProfile)
        fakePlayer!!.clonePlayer(mc.thePlayer, true)
        fakePlayer!!.rotationYawHead = mc.thePlayer.rotationYawHead
        fakePlayer!!.copyLocationAndAnglesFrom(mc.thePlayer)

        mc.theWorld.addEntityToWorld(-1000, fakePlayer)

        if (noClipValue.get()) mc.thePlayer.noClip = true
    }

    override fun onDisable() {
        if (mc.thePlayer == null || fakePlayer == null) return

        mc.thePlayer.setPositionAndRotation(oldX, oldY, oldZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch)
        mc.theWorld.removeEntityFromWorld(fakePlayer!!.entityId)

        fakePlayer = null

        mc.thePlayer.motionX = 0.0
        mc.thePlayer.motionY = 0.0
        mc.thePlayer.motionZ = 0.0
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (noClipValue.get()) mc.thePlayer.noClip = true

        mc.thePlayer.fallDistance = 0f

        if (flyValue.get()) {
            val speed = speedValue.get()

            mc.thePlayer.motionY = 0.0
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0

            if (mc.gameSettings.keyBindJump.isKeyDown) mc.thePlayer.motionY += speed.toDouble()
            if (mc.gameSettings.keyBindSneak.isKeyDown) mc.thePlayer.motionY -= speed.toDouble()

            mc.thePlayer.strafe(speed = speed.toDouble())
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer || packet is C0BPacketEntityAction) event.cancelEvent()
    }
}
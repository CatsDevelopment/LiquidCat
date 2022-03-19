package lol.liquidcat.features.module.modules.movement

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.moving
import lol.liquidcat.utils.entity.strafe
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue

//TODO Add more modes

class Speed : Module("Speed", "Makes you faster.", ModuleCategory.MOVEMENT) {

    private val modeValue = ListValue("Mode", arrayOf("Vanilla"), "Vanilla")
    private val speedValue = FloatValue("Speed", 1f, 1f..10f)

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (modeValue.get()) {
            "Vanilla" -> {
                if (mc.thePlayer.onGround && mc.thePlayer.moving) mc.thePlayer.jump()

                mc.thePlayer.strafe(speedValue.get().toDouble())
            }
        }
    }
}
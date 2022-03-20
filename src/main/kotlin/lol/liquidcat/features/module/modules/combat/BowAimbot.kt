/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.combat

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.event.UpdateEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBow
import java.awt.Color

//TODO Rewrite

class BowAimbot : Module("BowAimbot", "Automatically aims at players when using a bow.", ModuleCategory.COMBAT) {

    private val silent by BoolValue("Silent", true)
    private val predict by BoolValue("Predict", true)
    private val throughWalls by BoolValue("ThroughWalls", false)
    private val predictSize by FloatValue("PredictSize", 2F, 0.1f..5f)
    private val priority by ListValue("Priority", arrayOf("Health", "Distance", "Direction"), "Direction")
    private val mark by BoolValue("Mark", true)

    private var target: Entity? = null

    override fun onDisable() {
        target = null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        target = null

        if (mc.thePlayer.itemInUse?.item is ItemBow) {
            val entity = getTarget(throughWalls, priority) ?: return

            target = entity
            RotationUtils.faceBow(target, silent, predict, predictSize)
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (target != null && !priority.equals("Multi", ignoreCase = true) && mark)
            GLUtils.drawPlatform(target!!, Color(37, 126, 255, 70))
    }

    private fun getTarget(throughWalls: Boolean, priorityMode: String): Entity? {
        val targets = mc.theWorld.loadedEntityList.filter {
            it is EntityLivingBase && EntityUtils.isSelected(it, true) &&
                    (throughWalls || mc.thePlayer.canEntityBeSeen(it))
        }

        return when (priorityMode.toUpperCase()) {
            "DISTANCE" -> targets.minBy { mc.thePlayer.getDistanceToEntity(it) }
            "DIRECTION" -> targets.minBy { RotationUtils.getRotationDifference(it) }
            "HEALTH" -> targets.minBy { (it as EntityLivingBase).health }
            else -> null
        }
    }

    fun hasTarget() = target != null && mc.thePlayer.canEntityBeSeen(target)
}
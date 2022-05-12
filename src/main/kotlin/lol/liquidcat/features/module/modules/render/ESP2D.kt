/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render3DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.utils.entity.renderBoundingBox
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.WorldToScreen
import lol.liquidcat.utils.render.color.ColorUtils
import lol.liquidcat.utils.render.color.applyAlpha
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector3f
import java.awt.Color
import kotlin.math.max
import kotlin.math.min

object ESP2D : Module("ESP2D", "Allows you to see targets through walls.", ModuleCategory.RENDER) {

    private val boxMode by ListValue("BoxMode", arrayOf("None", "Border", "Corners", "Corners1", "Corners2"), "Border")
    private val healthBar by BoolValue("HealthBar", true)
    private val armorBar by BoolValue("ArmorBar", true)
    val showName by BoolValue("ShowName", true)

    private val red by IntValue("Red", 255, 0..255)
    private val green by IntValue("Green", 255, 0..255)
    private val blue by IntValue("Blue", 255, 0..255)

    private val bgColor = Color.BLACK.applyAlpha(100).rgb
    private val armorColor = Color(105, 120, 255).rgb

    @EventTarget
    fun onRender3D(event: Render3DEvent) {

        val mvMatrix = WorldToScreen.getMatrix(GL11.GL_MODELVIEW_MATRIX)
        val projectionMatrix = WorldToScreen.getMatrix(GL11.GL_PROJECTION_MATRIX)
        val color = Color(red, green, blue).rgb

        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPushMatrix()
        GL11.glLoadIdentity()

        GL11.glOrtho(0.0, mc.displayWidth.toDouble(), mc.displayHeight.toDouble(), 0.0, -1.0, 1.0)

        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPushMatrix()
        GL11.glLoadIdentity()

        for (entity in mc.theWorld.loadedEntityList) {
            if (EntityUtils.isSelected(entity, false)) {

                entity as EntityLivingBase

                val bb = entity.renderBoundingBox

                val vertices = arrayOf(
                    doubleArrayOf(bb.minX, bb.minY, bb.minZ),
                    doubleArrayOf(bb.minX, bb.maxY, bb.minZ),
                    doubleArrayOf(bb.maxX, bb.maxY, bb.minZ),
                    doubleArrayOf(bb.maxX, bb.minY, bb.minZ),
                    doubleArrayOf(bb.minX, bb.minY, bb.maxZ),
                    doubleArrayOf(bb.minX, bb.maxY, bb.maxZ),
                    doubleArrayOf(bb.maxX, bb.maxY, bb.maxZ),
                    doubleArrayOf(bb.maxX, bb.minY, bb.maxZ)
                )

                var minX = Float.MAX_VALUE
                var minY = Float.MAX_VALUE
                var maxX = -1f
                var maxY = -1f

                for (vertex in vertices) {
                    val screenPos = WorldToScreen.toScreen(
                        Vector3f(vertex[0].toFloat(), vertex[1].toFloat(), vertex[2].toFloat()),
                        mvMatrix, projectionMatrix, mc.displayWidth, mc.displayHeight
                    ) ?: continue

                    minX = screenPos.x.coerceAtMost(minX)
                    minY = screenPos.y.coerceAtMost(minY)
                    maxX = screenPos.x.coerceAtLeast(maxX)
                    maxY = screenPos.y.coerceAtLeast(maxY)
                }

                if (minX > 0 || minY > 0 || maxX <= mc.displayWidth || maxY <= mc.displayWidth) {

                    if (showName) {
                        val halfWidth = Fonts.minecraftFont.getStringWidth(entity.name) / 2

                        Fonts.minecraftFont.drawString(entity.name, minX.toInt() + (maxX.toInt() - minX.toInt()) / 2 - halfWidth, minY.toInt() - Fonts.minecraftFont.FONT_HEIGHT, -1)
                    }

                    if (healthBar) {
                        val percent = max(0f, min(entity.health, entity.health / max(entity.health, entity.maxHealth)))
                        val height = (maxY - minY) * (1 - percent)

                        if (entity.health.isNaN()) continue

                        GLUtils.drawRect(minX - 6, minY, minX - 2, maxY, bgColor)
                        GLUtils.drawRect(minX - 5, minY + height + 1, minX - 3, maxY - 1, ColorUtils.mix(Color.GREEN, Color.RED, percent).rgb)
                    }

                    if (armorBar) {
                        val width = (maxX - minX) * (1 - max(0f, min(entity.totalArmorValue.toFloat(), entity.totalArmorValue / max(entity.totalArmorValue.toFloat(), 20f))))

                        GLUtils.drawRect(minX, maxY + 2, maxX, maxY + 6, bgColor)
                        GLUtils.drawRect(minX + 1, maxY + 3, maxX - width - 1, maxY + 6 - 1, armorColor)
                    }

                    when (boxMode) {
                        "Border" -> {
                            GLUtils.drawRect(minX, minY, minX + 3, maxY, bgColor)
                            GLUtils.drawRect(maxX - 3, minY, maxX, maxY, bgColor)
                            GLUtils.drawRect(minX, minY, maxX, minY + 3, bgColor)
                            GLUtils.drawRect(minX, maxY - 3, maxX, maxY, bgColor)

                            GLUtils.drawRect(minX + 1, minY + 1, minX + 2, maxY - 1, color)
                            GLUtils.drawRect(maxX - 2, minY + 1, maxX - 1, maxY - 1, color)
                            GLUtils.drawRect(minX + 1, minY + 1, maxX - 1, minY + 2, color)
                            GLUtils.drawRect(minX + 1, maxY - 2, maxX - 1, maxY - 1, color)
                        }

                        "Corners" -> {
                            val a1 = (maxX - minX) * 0.75f
                            val a2 = (maxY - minY) * 0.75f

                            GLUtils.drawRect(minX + a1, minY, maxX, minY + 3, bgColor)
                            GLUtils.drawRect(minX, minY, maxX - a1, minY + 3, bgColor)
                            GLUtils.drawRect(minX + a1, maxY - 3, maxX, maxY, bgColor)
                            GLUtils.drawRect(minX, maxY - 3, maxX - a1, maxY, bgColor)

                            GLUtils.drawRect(minX, minY + a2, minX + 3, maxY, bgColor)
                            GLUtils.drawRect(minX, minY, minX + 3, maxY - a2, bgColor)
                            GLUtils.drawRect(maxX - 3, minY + a2, maxX, maxY, bgColor)
                            GLUtils.drawRect(maxX - 3, minY, maxX, maxY - a2, bgColor)

                            GLUtils.drawRect(minX + 1 + a1, minY + 1, maxX - 1, minY + 2, color)
                            GLUtils.drawRect(minX + 1, minY + 1, maxX - 1 - a1, minY + 2, color)
                            GLUtils.drawRect(minX + 1 + a1, maxY - 2, maxX - 1, maxY - 1, color)
                            GLUtils.drawRect(minX + 1, maxY - 2, maxX - 1 - a1, maxY - 1, color)

                            GLUtils.drawRect(minX + 1, minY + 1 + a2, minX + 2, maxY - 1, color)
                            GLUtils.drawRect(minX + 1, minY + 1, minX + 2, maxY - 1 - a2, color)
                            GLUtils.drawRect(maxX - 2, minY + 1 + a2, maxX - 1, maxY - 1, color)
                            GLUtils.drawRect(maxX - 2, minY + 1, maxX - 1, maxY - 1 - a2, color)
                        }

                        "Corners1" -> {
                            val a1 = (maxX - minX) * 0.75f

                            GLUtils.drawRect(minX, minY, minX + 3, maxY, bgColor)
                            GLUtils.drawRect(maxX - 3, minY, maxX, maxY, bgColor)

                            GLUtils.drawRect(minX + a1, minY, maxX, minY + 3, bgColor)
                            GLUtils.drawRect(minX, minY, maxX - a1, minY + 3, bgColor)
                            GLUtils.drawRect(minX + a1, maxY - 3, maxX, maxY, bgColor)
                            GLUtils.drawRect(minX, maxY - 3, maxX - a1, maxY, bgColor)

                            GLUtils.drawRect(minX + 1, minY + 1, minX + 2, maxY - 1, color)
                            GLUtils.drawRect(maxX - 2, minY + 1, maxX - 1, maxY - 1, color)

                            GLUtils.drawRect(minX + 1 + a1, minY + 1, maxX - 1, minY + 2, color)
                            GLUtils.drawRect(minX + 1, minY + 1, maxX - 1 - a1, minY + 2, color)
                            GLUtils.drawRect(minX + 1 + a1, maxY - 2, maxX - 1, maxY - 1, color)
                            GLUtils.drawRect(minX + 1, maxY - 2, maxX - 1 - a1, maxY - 1, color)
                        }

                        "Corners2" -> {
                            val a1 = (maxY - minY) * 0.75f

                            GLUtils.drawRect(minX, minY, maxX, minY + 3, bgColor)
                            GLUtils.drawRect(minX, maxY - 3, maxX, maxY, bgColor)

                            GLUtils.drawRect(minX, minY + a1, minX + 3, maxY, bgColor)
                            GLUtils.drawRect(minX, minY, minX + 3, maxY - a1, bgColor)
                            GLUtils.drawRect(maxX - 3, minY + a1, maxX, maxY, bgColor)
                            GLUtils.drawRect(maxX - 3, minY, maxX, maxY - a1, bgColor)

                            GLUtils.drawRect(minX + 1, minY + 1, maxX - 1, minY + 2, color)
                            GLUtils.drawRect(minX + 1, maxY - 2, maxX - 1, maxY - 1, color)

                            GLUtils.drawRect(minX + 1, minY + 1 + a1, minX + 2, maxY - 1, color)
                            GLUtils.drawRect(minX + 1, minY + 1, minX + 2, maxY - 1 - a1, color)
                            GLUtils.drawRect(maxX - 2, minY + 1 + a1, maxX - 1, maxY - 1, color)
                            GLUtils.drawRect(maxX - 2, minY + 1, maxX - 1, maxY - 1 - a1, color)
                        }
                    }
                }
            }
        }

        GL11.glMatrixMode(GL11.GL_PROJECTION)
        GL11.glPopMatrix()

        GL11.glMatrixMode(GL11.GL_MODELVIEW)
        GL11.glPopMatrix()
    }
}
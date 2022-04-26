/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.features.module.modules.render

import lol.liquidcat.LiquidCat
import lol.liquidcat.event.EventTarget
import lol.liquidcat.event.Render2DEvent
import lol.liquidcat.features.module.Module
import lol.liquidcat.features.module.ModuleCategory
import lol.liquidcat.utils.entity.EntityUtils
import lol.liquidcat.utils.entity.renderBoundingBox
import lol.liquidcat.utils.entity.renderPos
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.shader.shaders.GlowShader
import lol.liquidcat.utils.render.shader.shaders.OutlineShader
import lol.liquidcat.value.BoolValue
import lol.liquidcat.value.FloatValue
import lol.liquidcat.value.IntValue
import lol.liquidcat.value.ListValue
import net.minecraft.client.renderer.ChestRenderer
import net.minecraft.client.renderer.entity.RenderEntity
import net.minecraft.client.renderer.entity.RendererLivingEntity
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher
import net.minecraft.tileentity.TileEntityChest
import java.awt.Color

object ShaderESP : Module("ShaderESP", "cool shader", ModuleCategory.RENDER) {

    val mode by ListValue(
        "Mode",
        arrayOf("Glow", "Outline"),
        "Glow"
    )

    private val shape by ListValue("Shape", arrayOf("Outline", "Box"), "Outline")
    private val radius by FloatValue("Radius", 4f, 1f..10f)
    private val glowDivider by FloatValue("GlowDivider", 250f, 10f..500f)

    private val red by IntValue("Red", 87, 0..255)
    private val green by IntValue("Green", 65, 0..255)
    private val blue by IntValue("Blue", 255, 0..255)

    private val storages by BoolValue("Storages", false)

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val shader = when (mode) {
            "Outline" -> OutlineShader
            "Glow" -> GlowShader

            else -> return
        }

        if (shader == GlowShader)
            GlowShader.divinder = glowDivider

        shader.startDraw(event.partialTicks)

        runCatching {
            for (entity in mc.theWorld.loadedEntityList)
                if (EntityUtils.isSelected(entity, false))
                    if (shape == "Outline")
                        mc.renderManager.renderEntityStatic(entity, mc.timer.renderPartialTicks, true)
                    else
                        GLUtils.drawFilledBB(entity.renderBoundingBox)

            if (storages)
                for (tileEntity in mc.theWorld.loadedTileEntityList)
                    if (tileEntity is TileEntityChest)
                        TileEntityRendererDispatcher.instance.renderTileEntityAt(
                            tileEntity,
                            tileEntity.pos.x - mc.renderManager.renderPosX,
                            tileEntity.pos.y - mc.renderManager.renderPosY,
                            tileEntity.pos.z - mc.renderManager.renderPosZ,
                            event.partialTicks
                        )
        }.onFailure {
            LiquidCat.logger.error("An error occurred while rendering all entities for shader esp", it.message)
        }

        shader.stopDraw(Color(red, green, blue), radius)
    }
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.shader.shaders

import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.shader.Shader
import net.minecraft.client.gui.ScaledResolution
import org.lwjgl.opengl.GL20

object BackgroundShader : Shader("background.frag") {
    private var time = 0f

    override fun setupUniforms() {
        setupUniform("iResolution")
        setupUniform("iTime")
    }

    override fun updateUniforms() {
        val scaledResolution = ScaledResolution(mc)
        val resolutionID = getUniform("iResolution")
        if (resolutionID > -1) GL20.glUniform2f(
            resolutionID,
            scaledResolution.scaledWidth.toFloat() * 2,
            scaledResolution.scaledHeight.toFloat() * 2
        )
        val timeID = getUniform("iTime")
        if (timeID > -1) GL20.glUniform1f(timeID, time)
        time += 0.005f * GLUtils.deltaTime
    }
}
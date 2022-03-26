/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.shader.shaders

import lol.liquidcat.utils.mc
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.shader.Shader
import org.lwjgl.opengl.GL20

object BackgroundShader : Shader("background.frag") {
    private var time = 0f

    override fun setupUniforms() {
        setupUniform("iResolution")
        setupUniform("iTime")
    }

    override fun updateUniforms() {
        GL20.glUniform2f(getUniform("iResolution"), mc.displayWidth.toFloat(), mc.displayHeight.toFloat())
        GL20.glUniform1f(getUniform("iTime"), time)

        time += 0.001f * GLUtils.deltaTime
    }
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.shader.shaders

import lol.liquidcat.utils.render.shader.FramebufferShader
import org.lwjgl.opengl.GL20

object OutlineShader : FramebufferShader("outline.frag") {
    override fun setupUniforms() {
        setupUniform("texture")
        setupUniform("texelSize")
        setupUniform("color")
        setupUniform("divider")
        setupUniform("radius")
        setupUniform("maxSample")
    }

    override fun updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0)
        GL20.glUniform2f(
            getUniform("texelSize"),
            1f / mc.displayWidth * (radius * quality),
            1f / mc.displayHeight * (radius * quality)
        )
        GL20.glUniform3f(getUniform("color"), red, green, blue)
        GL20.glUniform1f(getUniform("radius"), radius)
    }
}
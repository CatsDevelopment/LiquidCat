/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.shader.shaders

import lol.liquidcat.utils.mc
import lol.liquidcat.utils.render.shader.FramebufferShader
import org.lwjgl.opengl.GL20

object GlowShader : FramebufferShader("glow.frag") {

    var divinder = 100f

    override fun setupUniforms() {
        setupUniform("texture")
        setupUniform("texelSize")
        setupUniform("color")
        setupUniform("divider")
        setupUniform("radius")
    }

    override fun updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0)
        GL20.glUniform2f(
            getUniform("texelSize"),
            1f / mc.displayWidth,
            1f / mc.displayHeight
        )
        GL20.glUniform3f(getUniform("color"), red, green, blue)
        GL20.glUniform1f(getUniform("divider"), divinder)
        GL20.glUniform1f(getUniform("radius"), radius)
    }
}
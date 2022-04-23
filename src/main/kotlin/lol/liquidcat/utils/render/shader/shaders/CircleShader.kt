/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.shader.shaders

import lol.liquidcat.utils.render.shader.Shader
import org.lwjgl.opengl.GL20

object CircleShader : Shader("circle.frag") {

    override fun setupUniforms() {
        setupUniform("color")
        setupUniform("size")
    }

    override fun updateUniforms() {
        GL20.glUniform4f(getUniform("color"), 1f, 1f, 1f, 1f)
        GL20.glUniform2f(getUniform("size"), 100f, 100f)
    }
}
/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */package lol.liquidcat.utils.render.shader.shaders

import lol.liquidcat.utils.render.shader.Shader
import org.lwjgl.opengl.GL20

object RoundBRectShader : Shader("roundrectbordered.frag") {

    override fun setupUniforms() {
        setupUniform("rectColor")
        setupUniform("borderColor")
        setupUniform("size")
        setupUniform("radius")
        setupUniform("borderThickness")
    }

    override fun updateUniforms() {
        GL20.glUniform4f(getUniform("rectColor"), 1f, 1f, 1f, 1f)
        GL20.glUniform4f(getUniform("borderColor"), 0f, 0f, 0f, 1f)
        GL20.glUniform2f(getUniform("size"), 100f, 100f)
        GL20.glUniform1f(getUniform("radius"), 50f)
        GL20.glUniform1f(getUniform("borderThickness"), 10f)
    }
}
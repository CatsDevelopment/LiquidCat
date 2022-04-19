package lol.liquidcat.utils.render.shader.shaders

import lol.liquidcat.utils.render.shader.Shader
import org.lwjgl.opengl.GL20

object SRoundRectShader : Shader("separateroundrect.frag") {

    override fun setupUniforms() {
        setupUniform("color")
        setupUniform("size")
        setupUniform("radius")
    }

    override fun updateUniforms() {
        GL20.glUniform4f(getUniform("color"), 1f, 1f, 1f, 1f)
        GL20.glUniform2f(getUniform("size"), 100f, 100f)
        GL20.glUniform4f(getUniform("radius"), 10f, 10f, 10f, 10f)
    }
}
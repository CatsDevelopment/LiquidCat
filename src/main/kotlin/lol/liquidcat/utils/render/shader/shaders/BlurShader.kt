/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.shader.shaders

import lol.liquidcat.utils.gaussian
import lol.liquidcat.utils.mc
import lol.liquidcat.utils.render.GLUtils
import lol.liquidcat.utils.render.shader.Shader
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.shader.Framebuffer
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL20

object BlurShader : Shader("blur.frag") {

    private var blurFBO = Framebuffer(mc.displayWidth, mc.displayHeight, true)

    override fun setupUniforms() {
        setupUniform("radius")
        setupUniform("direction")
        setupUniform("texture")
        setupUniform("texelsize")
        setupUniform("weights")
    }

    override fun updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0)
        GL20.glUniform2f(getUniform("texelsize"), 1f / mc.displayWidth, 1f / mc.displayHeight)
    }

    fun blur(radius: Int) {

        // Not possible to use a dynamic FloatBuffer in GLSL uniforms so the size must already be preset
        val weights = BufferUtils.createFloatBuffer(128)

        (0..radius).forEach { weights.put(gaussian(it, radius / 2f)) }

        // Old texture
        val oldTexture = glGetInteger(GL_TEXTURE_BINDING_2D)
        val sr = ScaledResolution(mc)

        // Resize blur FBO
        if (blurFBO.framebufferWidth != mc.displayWidth || blurFBO.framebufferHeight != mc.displayHeight)
            blurFBO = Framebuffer(mc.displayWidth, mc.displayHeight, true)

        blurFBO.framebufferClear()

        // Horizontal blur
        blurFBO.bindFramebuffer(true)
        startShader()

        GL20.glUniform1i(getUniform("radius"), radius)
        GL20.glUniform2f(getUniform("direction"), 1f, 0f)

        weights.rewind()
        GL20.glUniform1(getUniform("weights"), weights)

        glBindTexture(GL_TEXTURE_2D, mc.framebuffer.framebufferTexture)
        GLUtils.drawQuads(0f, 0f, sr.scaledWidth.toFloat(), sr.scaledHeight.toFloat())
        stopShader()

        blurFBO.unbindFramebuffer()

        // Vertical blur
        mc.framebuffer.bindFramebuffer(true)
        startShader()
        GL20.glUniform1i(getUniform("radius"), radius)
        GL20.glUniform2f(getUniform("direction"), 0f, 1f)

        weights.rewind()
        GL20.glUniform1(getUniform("weights"), weights)

        glBindTexture(GL_TEXTURE_2D, blurFBO.framebufferTexture)
        GLUtils.drawQuads(0f, 0f, sr.scaledWidth.toFloat(), sr.scaledHeight.toFloat())
        stopShader()

        // Reset texture
        glBindTexture(GL_TEXTURE_2D, oldTexture)
    }
}
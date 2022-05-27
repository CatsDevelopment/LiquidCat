/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.shader

import lol.liquidcat.LiquidCat
import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20.*
import org.lwjgl.opengl.OpenGLException

abstract class Shader(fragmentShader: String) {

    /**
     * Shader program
     */
    private var program = 0

    /**
     * Shader uniforms
     */
    private var uniforms = mutableMapOf<String, Int>()

    /**
     * Starts shader rendering
     */
    fun startShader() {
        if (program != 0) {
            GL11.glPushMatrix()
            glUseProgram(program)

            uniforms.clear()

            setupUniforms()
            updateUniforms()
        }
    }

    /**
     * Stops shader rendering
     */
    fun stopShader() {
        if (program != 0) {
            glUseProgram(0)
            GL11.glPopMatrix()
        }
    }

    private fun createShader(shaderType: Int, shaderSrc: String): Int {

        // Creates a shader
        val shader = glCreateShader(shaderType)

        if (shader == 0)
            throw OpenGLException("An error occurred while creating shader.")

        glShaderSource(shader, shaderSrc)

        // Compiles shader
        glCompileShader(shader)

        // Checks for errors during shader compilation
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL11.GL_FALSE) {

            // Deletes shader
            glDeleteShader(shader)

            throw OpenGLException("An error occurred while compiling shader.")
        }

        return shader
    }

    private fun createProgram(vertShader: Int, fragShader: Int): Int {

        // Creates a program
        val program = glCreateProgram()

        // Attaches shaders
        glAttachShader(program, vertShader)
        glAttachShader(program, fragShader)

        // Links program
        glLinkProgram(program)

        // Checks for errors during program linking
        if (glGetProgrami(program, GL_LINK_STATUS) == GL11.GL_FALSE) {

            glDeleteProgram(program)

            // Deletes shaders
            glDeleteProgram(vertShader)
            glDeleteProgram(fragShader)

            throw OpenGLException("An error occurred while linking program.")
        }

        // Detaches shaders
        glDetachShader(program, vertShader)
        glDetachShader(program, fragShader)

        return program
    }

    init {
        runCatching {

            // Links vertex shader code
            val vertStream = javaClass.getResourceAsStream("/assets/minecraft/${LiquidCat.CLIENT_NAME.lowercase()}/shader/vertex.vert")
            val vertShader = createShader(GL_VERTEX_SHADER, IOUtils.toString(vertStream))
            IOUtils.closeQuietly(vertStream)

            // Links fragment shader code
            val fragStream = javaClass.getResourceAsStream("/assets/minecraft/${LiquidCat.CLIENT_NAME.lowercase()}/shader/fragment/$fragmentShader")
            val fragShader = createShader(GL_FRAGMENT_SHADER, IOUtils.toString(fragStream))
            IOUtils.closeQuietly(fragStream)

            program = createProgram(vertShader, fragShader)
        }.onFailure { it.printStackTrace() }
    }

    abstract fun setupUniforms()
    abstract fun updateUniforms()

    private fun setUniform(uniformName: String, location: Int) {
        uniforms[uniformName] = location
    }

    fun setupUniform(uniformName: String) {
        setUniform(uniformName, glGetUniformLocation(program, uniformName))
    }

    fun getUniform(uniformName: String): Int {
        return uniforms[uniformName] ?: 0
    }
}
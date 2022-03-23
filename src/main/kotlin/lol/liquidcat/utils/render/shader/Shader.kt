/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render.shader

import lol.liquidcat.LiquidCat
import lol.liquidcat.LiquidCat.logger
import org.apache.commons.io.IOUtils
import org.lwjgl.opengl.*

abstract class Shader(fragmentShader: String) {
    private var program = 0
    private lateinit var uniformsMap: MutableMap<String, Int>

    fun startShader() {
        GL11.glPushMatrix()
        GL20.glUseProgram(program)

        uniformsMap = HashMap()
        setupUniforms()

        updateUniforms()
    }

    fun stopShader() {
        GL20.glUseProgram(0)
        GL11.glPopMatrix()
    }

    abstract fun setupUniforms()
    abstract fun updateUniforms()

    private fun createShader(shaderSource: String, shaderType: Int): Int {
        var shader = 0

        return try {
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType)

            if (shader == 0)
                return 0

            ARBShaderObjects.glShaderSourceARB(shader, shaderSource)
            ARBShaderObjects.glCompileShaderARB(shader)

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE)
                throw RuntimeException("Error creating shader: " + getLogInfo(shader))

            shader
        } catch (e: Exception) {
            ARBShaderObjects.glDeleteObjectARB(shader)
            throw e
        }
    }

    private fun getLogInfo(i: Int): String {
        return ARBShaderObjects.glGetInfoLogARB(i, ARBShaderObjects.glGetObjectParameteriARB(i, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB))
    }

    fun setUniform(uniformName: String, location: Int) {
        uniformsMap[uniformName] = location
    }

    fun setupUniform(uniformName: String) {
        setUniform(uniformName, GL20.glGetUniformLocation(program, uniformName))
    }

    fun getUniform(uniformName: String): Int {
        return uniformsMap[uniformName] ?: 0
    }

    init {
        var vertexShaderID = 0
        var fragmentShaderID = 0

        try {
            val vertexStream = javaClass.getResourceAsStream("/assets/minecraft/${LiquidCat.CLIENT_NAME.toLowerCase()}/shader/vertex.vert")
            vertexShaderID = createShader(IOUtils.toString(vertexStream), ARBVertexShader.GL_VERTEX_SHADER_ARB)
            IOUtils.closeQuietly(vertexStream)

            val fragmentStream = javaClass.getResourceAsStream("/assets/minecraft/${LiquidCat.CLIENT_NAME.toLowerCase()}/shader/fragment/$fragmentShader")
            fragmentShaderID = createShader(IOUtils.toString(fragmentStream), ARBFragmentShader.GL_FRAGMENT_SHADER_ARB)
            IOUtils.closeQuietly(fragmentStream)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (vertexShaderID != 0 && fragmentShaderID != 0) {
            program = ARBShaderObjects.glCreateProgramObjectARB()

            if (program != 0) {
                ARBShaderObjects.glAttachObjectARB(program, vertexShaderID)
                ARBShaderObjects.glAttachObjectARB(program, fragmentShaderID)
                ARBShaderObjects.glLinkProgramARB(program)
                ARBShaderObjects.glValidateProgramARB(program)
                logger.info("[Shader] Successfully loaded: $fragmentShader")
            }
        }
    }
}
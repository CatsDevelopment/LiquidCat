/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.utils.render

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Matrix4f
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector3f
import org.lwjgl.util.vector.Vector4f

object WorldToScreen {
    fun getMatrix(matrix: Int): Matrix4f {
        val buffer = BufferUtils.createFloatBuffer(16)

        GL11.glGetFloat(matrix, buffer)

        return Matrix4f().load(buffer) as Matrix4f
    }

    fun toScreen(pointInWorld: Vector3f, view: Matrix4f, projection: Matrix4f, screenWidth: Int, screenHeight: Int): Vector2f? {
        val clipSpacePos = multiply(multiply(Vector4f(pointInWorld.x, pointInWorld.y, pointInWorld.z, 1.0f), view), projection)
        val ndcSpacePos = Vector3f(clipSpacePos.x / clipSpacePos.w, clipSpacePos.y / clipSpacePos.w, clipSpacePos.z / clipSpacePos.w)

        val screenX = (ndcSpacePos.x + 1.0f) / 2.0f * screenWidth
        val screenY = (1.0f - ndcSpacePos.y) / 2.0f * screenHeight

        return if (ndcSpacePos.z < -1.0 || ndcSpacePos.z > 1.0) null else Vector2f(screenX, screenY)
    }

    private fun multiply(vec: Vector4f, mat: Matrix4f): Vector4f {
        return Vector4f(
            vec.x * mat.m00 + vec.y * mat.m10 + vec.z * mat.m20 + vec.w * mat.m30,
            vec.x * mat.m01 + vec.y * mat.m11 + vec.z * mat.m21 + vec.w * mat.m31,
            vec.x * mat.m02 + vec.y * mat.m12 + vec.z * mat.m22 + vec.w * mat.m32,
            vec.x * mat.m03 + vec.y * mat.m13 + vec.z * mat.m23 + vec.w * mat.m33
        )
    }
}
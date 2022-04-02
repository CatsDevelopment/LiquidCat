/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */

package lol.liquidcat.utils.login

import com.google.gson.JsonParser
import lol.liquidcat.utils.io.HttpUtils
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicHeader
import org.json.JSONObject

object UserUtils {

    /**
     * Check if token is valid
     *
     * Exam
     * 7a7c4193280a4060971f1e73be3d9bdb
     * 89371141db4f4ec485d68d1f63d01eec
     */
    @JvmStatic
    fun isValidTokenOffline(token: String) = token.length >= 32

    fun isValidToken(token: String): Boolean {
        val client = HttpClients.createDefault()
        val headers = arrayOf(
            BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json")
        )

        val request = HttpPost("https://authserver.mojang.com/validate")
        request.setHeaders(headers)

        val body = JSONObject()
        body.put("accessToken", token)
        request.entity = StringEntity(body.toString())

        val response = client.execute(request)

        return response.statusLine.statusCode == 204
    }

    /**
     * Gets the Username of a player by his [uuid]
     */
    fun getUsername(uuid: String): String? {
        val httpConnection = HttpUtils.get("https://api.mojang.com/user/profiles/$uuid/names")
        val jsonElement = JsonParser().parse(httpConnection)

        if (jsonElement.isJsonObject) {
            return jsonElement.asJsonArray.last().asJsonObject.get("name").asString
        }

        return null
    }

    /**
     * Gets the UUID of a player by his [username]
     */
    fun getUUID(username: String): String {
        val httpConnection = HttpUtils.get("https://api.mojang.com/users/profiles/minecraft/$username")
        val jsonElement = JsonParser().parse(httpConnection)

        if (jsonElement.isJsonObject) {
            return jsonElement.asJsonObject.get("id").asString
        }

        return ""
    }
}
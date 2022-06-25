/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.Gson
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import lol.liquidcat.utils.login.MinecraftAccount
import java.io.*

object AccountsConfig : FileConfig(File(FileManager.mainDir, "accounts.json")) {

    @JvmField
    val altManagerMinecraftAccounts: MutableList<MinecraftAccount> = ArrayList()

    /**
     * Load config from file
     */
    override fun load() {
        val accountList = Gson().fromJson<List<*>>(BufferedReader(FileReader(file)), MutableList::class.java) ?: return
        altManagerMinecraftAccounts.clear()

        for (account in accountList) {
            val information = account.toString().split(":".toRegex()).toTypedArray()
            if (information.size >= 3) altManagerMinecraftAccounts.add(
                MinecraftAccount(
                    information[0],
                    information[1],
                    information[2]
                )
            ) else if (information.size == 2) altManagerMinecraftAccounts.add(
                MinecraftAccount(
                    information[0], information[1]
                )
            ) else altManagerMinecraftAccounts.add(
                MinecraftAccount(
                    information[0]
                )
            )
        }
    }

    /**
     * Save config to file
     */
    override fun save() {
        val accountList: MutableList<String> = ArrayList()
        altManagerMinecraftAccounts.forEach { accountList.add(it.name + ":" + (it.password ?: "") + ":" + (it.accountName ?: "")) }
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.gson.toJson(accountList))
        printWriter.close()
    }
}
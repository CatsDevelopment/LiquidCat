/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package lol.liquidcat.file.configs

import com.google.gson.Gson
import lol.liquidcat.file.FileConfig
import lol.liquidcat.file.FileManager
import net.ccbluex.liquidbounce.utils.login.MinecraftAccount
import java.io.*

class AccountsConfig(file: File?) : FileConfig(file!!) {

    @JvmField
    val altManagerMinecraftAccounts: MutableList<MinecraftAccount> = ArrayList()

    /**
     * Load config from file
     */
    override fun loadConfig() {
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
            ) else altManagerMinecraftAccounts.add(MinecraftAccount(information[0]))
        }
    }

    /**
     * Save config to file
     */
    override fun saveConfig() {
        val accountList: MutableList<String> = ArrayList()
        for (minecraftAccount in altManagerMinecraftAccounts) accountList.add(minecraftAccount.name + ":" + (if (minecraftAccount.password == null) "" else minecraftAccount.password) + ":" + if (minecraftAccount.accountName == null) "" else minecraftAccount.accountName)
        val printWriter = PrintWriter(FileWriter(file))
        printWriter.println(FileManager.PRETTY_GSON.toJson(accountList))
        printWriter.close()
    }
}
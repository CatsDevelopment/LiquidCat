/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.features.module.modules.misc;

import lol.liquidcat.LiquidCat;
import lol.liquidcat.event.EventTarget;
import lol.liquidcat.event.Render2DEvent;
import lol.liquidcat.features.module.Module;
import lol.liquidcat.features.module.ModuleCategory;
import lol.liquidcat.features.module.ModuleInfo;
import lol.liquidcat.utils.ClientUtilsKt;
import net.ccbluex.liquidbounce.file.configs.FriendsConfig;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Mouse;

@ModuleInfo(name = "MidClick", description = "Allows you to add a player as a friend by right clicking him.", category = ModuleCategory.MISC)
public class MidClick extends Module {

    private boolean wasDown;

    @EventTarget
    public void onRender(Render2DEvent event) {
        if(mc.currentScreen != null)
            return;

        if(!wasDown && Mouse.isButtonDown(2)) {
            final Entity entity = mc.objectMouseOver.entityHit;

            if(entity instanceof EntityPlayer) {
                final String playerName = ColorUtils.stripColor(entity.getName());
                final FriendsConfig friendsConfig = LiquidCat.fileManager.friendsConfig;

                if(!friendsConfig.isFriend(playerName)) {
                    friendsConfig.addFriend(playerName);
                    LiquidCat.fileManager.saveConfig(friendsConfig);
                    ClientUtilsKt.msg("§a§l" + playerName + "§c was added to your friends.");
                }else{
                    friendsConfig.removeFriend(playerName);
                    LiquidCat.fileManager.saveConfig(friendsConfig);
                    ClientUtilsKt.msg("§a§l" + playerName + "§c was removed from your friends.");
                }
            }else
                ClientUtilsKt.msg("§c§lError: §aYou need to select a player.");
        }

        wasDown = Mouse.isButtonDown(2);
    }

}

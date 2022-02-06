/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import lol.liquidcat.event.EventTarget;
import lol.liquidcat.event.UpdateEvent;
import lol.liquidcat.features.module.Module;
import lol.liquidcat.features.module.ModuleCategory;
import lol.liquidcat.features.module.ModuleInfo;

@ModuleInfo(name = "LadderJump", description = "Boosts you up when touching a ladder.", category = ModuleCategory.MOVEMENT)
public class LadderJump extends Module {

    static boolean jumped;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(mc.thePlayer.onGround) {
            if(mc.thePlayer.isOnLadder()) {
                mc.thePlayer.motionY = 1.5D;
                jumped = true;
            }else
                jumped = false;
        }else if(!mc.thePlayer.isOnLadder() && jumped)
            mc.thePlayer.motionY += 0.059D;
    }
}

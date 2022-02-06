/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import lol.liquidcat.event.EventTarget;
import lol.liquidcat.event.JumpEvent;
import lol.liquidcat.features.module.Module;
import lol.liquidcat.features.module.ModuleCategory;
import lol.liquidcat.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import lol.liquidcat.value.FloatValue;
import lol.liquidcat.value.ListValue;
import net.minecraft.block.BlockSlime;

@ModuleInfo(name = "SlimeJump", description = "Allows you to to jump higher on slime blocks.", category = ModuleCategory.MOVEMENT)
public class SlimeJump extends Module {

    private final FloatValue motionValue = new FloatValue("Motion", 0.42F, 0.2F, 1F);
    private final ListValue modeValue = new ListValue("Mode", new String[] {"Set", "Add"}, "Add");

    @EventTarget
    public void onJump(JumpEvent event) {
        if(mc.thePlayer != null && mc.theWorld != null && BlockUtils.getBlock(mc.thePlayer.getPosition().down()) instanceof BlockSlime) {
            event.cancelEvent();

            switch(modeValue.get().toLowerCase()) {
                case "set":
                    mc.thePlayer.motionY = motionValue.get();
                    break;
                case "add":
                    mc.thePlayer.motionY += motionValue.get();
                    break;
            }
        }
    }
}
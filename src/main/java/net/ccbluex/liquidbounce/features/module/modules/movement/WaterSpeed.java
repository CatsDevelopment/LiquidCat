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
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import lol.liquidcat.value.FloatValue;
import net.minecraft.block.BlockLiquid;

@ModuleInfo(name = "WaterSpeed", description = "Allows you to swim faster.", category = ModuleCategory.MOVEMENT)
public class WaterSpeed extends Module {

    private final FloatValue speedValue = new FloatValue("Speed", 1.2F, 1.1F, 1.5F);

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(mc.thePlayer.isInWater() && BlockUtils.getBlock(mc.thePlayer.getPosition()) instanceof BlockLiquid) {
            final float speed = speedValue.get();

            mc.thePlayer.motionX *= speed;
            mc.thePlayer.motionZ *= speed;
        }
    }
}
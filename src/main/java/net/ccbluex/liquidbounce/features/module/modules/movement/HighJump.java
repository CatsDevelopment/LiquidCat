/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import lol.liquidcat.event.EventTarget;
import lol.liquidcat.event.JumpEvent;
import lol.liquidcat.event.MoveEvent;
import lol.liquidcat.event.UpdateEvent;
import lol.liquidcat.features.module.Module;
import lol.liquidcat.features.module.ModuleCategory;
import lol.liquidcat.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.MovementUtils;
import net.ccbluex.liquidbounce.utils.block.BlockUtils;
import lol.liquidcat.value.BoolValue;
import lol.liquidcat.value.FloatValue;
import lol.liquidcat.value.ListValue;
import net.minecraft.block.BlockPane;
import net.minecraft.util.BlockPos;

@ModuleInfo(name = "HighJump", description = "Allows you to jump higher.", category = ModuleCategory.MOVEMENT)
public class HighJump extends Module {

    private final FloatValue heightValue = new FloatValue("Height", 2F, 1.1F, 5F);
    private final ListValue modeValue = new ListValue("Mode", new String[] {"Vanilla", "Damage", "AACv3", "DAC", "Mineplex"}, "Vanilla");
    private final BoolValue glassValue = new BoolValue("OnlyGlassPane", false);

    @EventTarget
    public void onUpdate(final UpdateEvent event) {
        if(glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        switch(modeValue.get().toLowerCase()) {
            case "damage":
                if(mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround)
                    mc.thePlayer.motionY += 0.42F * heightValue.get();
                break;
            case "aacv3":
                if(!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.059D;
                break;
            case "dac":
                if(!mc.thePlayer.onGround) mc.thePlayer.motionY += 0.049999;
                break;
            case "mineplex":
                if(!mc.thePlayer.onGround) MovementUtils.strafe(0.35F);
                break;
        }
    }

    @EventTarget
    public void onMove(final MoveEvent event) {
        if(glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        if(!mc.thePlayer.onGround) {
            if ("mineplex".equals(modeValue.get().toLowerCase())) {
                mc.thePlayer.motionY += mc.thePlayer.fallDistance == 0 ? 0.0499D : 0.05D;
            }
        }
    }

    @EventTarget
    public void onJump(final JumpEvent event) {
        if(glassValue.get() && !(BlockUtils.getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)) instanceof BlockPane))
            return;

        switch(modeValue.get().toLowerCase()) {
            case "vanilla":
                event.setMotion(event.getMotion() * heightValue.get());
                break;
            case "mineplex":
                event.setMotion(0.47F);
                break;
        }
    }

    @Override
    public String getTag() {
        return modeValue.get();
    }
}

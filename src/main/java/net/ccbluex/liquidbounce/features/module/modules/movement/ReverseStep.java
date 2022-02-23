/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.features.module.modules.movement;

import lol.liquidcat.event.EventTarget;
import lol.liquidcat.event.JumpEvent;
import lol.liquidcat.event.UpdateEvent;
import lol.liquidcat.features.module.Module;
import lol.liquidcat.features.module.ModuleCategory;
import lol.liquidcat.features.module.ModuleInfo;
import lol.liquidcat.utils.block.BlockUtils;
import lol.liquidcat.value.FloatValue;
import net.minecraft.block.BlockLiquid;
import net.minecraft.util.AxisAlignedBB;

@ModuleInfo(name = "ReverseStep", description = "Allows you to step down blocks faster.", category = ModuleCategory.MOVEMENT)
public class ReverseStep extends Module {

    private final FloatValue motionValue = new FloatValue("Motion", 1F, 0.21F, 1F);

    private boolean jumped;

    @EventTarget(ignoreCondition = true)
    public void onUpdate(UpdateEvent event) {
        if(mc.thePlayer.onGround)
            jumped = false;

        if(mc.thePlayer.motionY > 0)
            jumped = true;

        if(!getState())
            return;

        if(BlockUtils.collideBlock(mc.thePlayer.getEntityBoundingBox(), block -> block instanceof BlockLiquid) || BlockUtils.collideBlock(new AxisAlignedBB(mc.thePlayer.getEntityBoundingBox().maxX, mc.thePlayer.getEntityBoundingBox().maxY, mc.thePlayer.getEntityBoundingBox().maxZ, mc.thePlayer.getEntityBoundingBox().minX, mc.thePlayer.getEntityBoundingBox().minY - 0.01D, mc.thePlayer.getEntityBoundingBox().minZ), block -> block instanceof BlockLiquid))
            return;

        if(!mc.gameSettings.keyBindJump.isKeyDown() && !mc.thePlayer.onGround && !mc.thePlayer.movementInput.jump && mc.thePlayer.motionY <= 0D && mc.thePlayer.fallDistance <= 1F && !jumped)
            mc.thePlayer.motionY = -motionValue.get();
    }

    @EventTarget(ignoreCondition = true)
    public void onJump(JumpEvent event) {
        jumped = true;
    }
}
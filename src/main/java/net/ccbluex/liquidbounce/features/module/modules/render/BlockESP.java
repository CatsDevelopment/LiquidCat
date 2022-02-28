/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import lol.liquidcat.event.EventTarget;
import lol.liquidcat.event.Render3DEvent;
import lol.liquidcat.event.UpdateEvent;
import lol.liquidcat.features.module.Module;
import lol.liquidcat.features.module.ModuleCategory;
import lol.liquidcat.features.module.ModuleInfo;
import lol.liquidcat.utils.block.BlockExtensionsKt;
import lol.liquidcat.utils.block.BlockUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.timer.MSTimer;
import lol.liquidcat.value.BlockValue;
import lol.liquidcat.value.BoolValue;
import lol.liquidcat.value.IntegerValue;
import lol.liquidcat.value.ListValue;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ModuleInfo(name = "BlockESP", description = "Allows you to see a selected block through walls.", category = ModuleCategory.RENDER)
public class BlockESP extends Module {
    private final ListValue modeValue = new ListValue("Mode", new String[] {"Box", "2D"}, "Box");

    private final BlockValue blockValue = new BlockValue("Block", 168);
    private final IntegerValue radiusValue = new IntegerValue("Radius", 40, 5, 120);

    private final IntegerValue colorRedValue = new IntegerValue("R", 255, 0, 255);
    private final IntegerValue colorGreenValue = new IntegerValue("G", 179, 0, 255);
    private final IntegerValue colorBlueValue = new IntegerValue("B", 72, 0, 255);
    private final BoolValue colorRainbow = new BoolValue("Rainbow", false);

    private final MSTimer searchTimer = new MSTimer();
    private final List<BlockPos> posList = new ArrayList<>();
    private Thread thread;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        if(searchTimer.hasTimePassed(1000L) && (thread == null || !thread.isAlive())) {
            final int radius = radiusValue.get();
            final Block selectedBlock = Block.getBlockById(blockValue.get());

            if(selectedBlock == null || selectedBlock == Blocks.air)
                return;

            thread = new Thread(() -> {
                final List<BlockPos> blockList = new ArrayList<>();

                for(int x = -radius; x < radius; x++) {
                    for(int y = radius; y > -radius; y--) {
                        for(int z = -radius; z < radius; z++) {
                            final int xPos = ((int) mc.thePlayer.posX + x);
                            final int yPos = ((int) mc.thePlayer.posY + y);
                            final int zPos = ((int) mc.thePlayer.posZ + z);

                            final BlockPos blockPos = new BlockPos(xPos, yPos, zPos);
                            final Block block = BlockExtensionsKt.getBlock(blockPos);

                            if(block == selectedBlock)
                                blockList.add(blockPos);
                        }
                    }
                }

                searchTimer.reset();

                synchronized(posList) {
                    posList.clear();
                    posList.addAll(blockList);
                }
            }, "BlockESP-BlockFinder");
            thread.start();
        }
    }

    @EventTarget
    public void onRender3D(Render3DEvent event) {
        synchronized(posList) {
            final Color color = colorRainbow.get() ? ColorUtils.rainbow() : new Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get());

            for(final BlockPos blockPos : posList) {
                switch(modeValue.get().toLowerCase()) {
                    case "box":
                        RenderUtils.drawBlockBox(blockPos, color, true);
                        break;
                    case "2d":
                        RenderUtils.draw2D(blockPos, color.getRGB(), Color.BLACK.getRGB());
                        break;
                }
            }
        }
    }

    @Override
    public String getTag() {
        return BlockUtils.getBlockName(blockValue.get());
    }
}

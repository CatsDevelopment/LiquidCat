/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds;

import lol.liquidcat.LiquidCat;
import lol.liquidcat.event.MoveEvent;
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class SpeedMode extends MinecraftInstance {

    public final String modeName;

    public SpeedMode(final String modeName) {
        this.modeName = modeName;
    }

    public boolean isActive() {
        final Speed speed = ((Speed) LiquidCat.moduleManager.getModule(Speed.class));

        return speed != null && !mc.thePlayer.isSneaking() && speed.getState() && speed.modeValue.get().equals(modeName);
    }

    public abstract void onMotion();

    public abstract void onUpdate();

    public abstract void onMove(final MoveEvent event);

    public void onTick() {
    }

    public void onEnable() {
    }

    public void onDisable() {
    }
}

/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import lol.liquidcat.utils.render.GLUtils;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.awt.*;

@Mixin(GuiButton.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButton extends Gui {

   @Shadow
   public boolean visible;

   @Shadow
   public int xPosition;

   @Shadow
   public int yPosition;

   @Shadow
   public int width;

   @Shadow
   public int height;

   @Shadow
   protected boolean hovered;

   @Shadow
   public boolean enabled;

   @Shadow
   protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

   @Shadow
   public String displayString;

   @Shadow
   @Final
   protected static ResourceLocation buttonTextures;

   private int alpha;

   /**
    * @author CCBlueX
    */
   @Overwrite
   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (visible) {
         hovered = (mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height);

         final int delta = GLUtils.deltaTime;

         if (enabled && hovered) {
            alpha += 2 * delta;

            if (alpha >= 255) alpha = 255;
         } else {
            alpha -= 2 * delta;

            if (alpha <= 0) alpha = 0;
         }

         GLUtils.drawBorderedRect(
                 xPosition,
                 yPosition,
                 xPosition + width,
                 yPosition + height,
                 1f,
                 new Color(0, 255, 133, alpha).getRGB(),
                 enabled ? new Color(15, 15, 17, 155).getRGB() : new Color(27, 27, 31, 155).getRGB());

         mc.getTextureManager().bindTexture(buttonTextures);
         mouseDragged(mc, mouseX, mouseY);

         AWTFontRenderer.Companion.setAssumeNonVolatile(true);
         Fonts.nunito40.drawCenteredString(displayString, (xPosition + width / 2f), (yPosition + height / 2f) - Fonts.nunito40.FONT_HEIGHT / 4f, Color.WHITE.getRGB(), false);
         AWTFontRenderer.Companion.setAssumeNonVolatile(false);

         GlStateManager.resetColor();
      }
   }
}
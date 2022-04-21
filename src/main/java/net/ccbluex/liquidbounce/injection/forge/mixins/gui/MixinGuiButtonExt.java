package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import lol.liquidcat.utils.render.GLUtils;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.awt.*;

@Mixin(GuiButtonExt.class)
@SideOnly(Side.CLIENT)
public abstract class MixinGuiButtonExt extends GuiButton {

   private int alpha;

   public MixinGuiButtonExt(int p_i1020_1_, int p_i1020_2_, int p_i1020_3_, String p_i1020_4_) {
      super(p_i1020_1_, p_i1020_2_, p_i1020_3_, p_i1020_4_);
   }

   public MixinGuiButtonExt(int p_i46323_1_, int p_i46323_2_, int p_i46323_3_, int p_i46323_4_,
                            int p_i46323_5_, String p_i46323_6_) {
      super(p_i46323_1_, p_i46323_2_, p_i46323_3_, p_i46323_4_, p_i46323_5_, p_i46323_6_);
   }

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

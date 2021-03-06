/*
 * LiquidCat Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CatsDevelopment/LiquidCat
 */
package net.ccbluex.liquidbounce.ui.client.clickgui.style.styles;

import net.ccbluex.liquidbounce.ui.client.clickgui.Panel;
import net.ccbluex.liquidbounce.ui.client.clickgui.elements.ButtonElement;
import net.ccbluex.liquidbounce.ui.client.clickgui.elements.ModuleElement;
import net.ccbluex.liquidbounce.ui.client.clickgui.style.Style;
import net.ccbluex.liquidbounce.ui.font.AWTFontRenderer;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Mouse;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.List;

import lol.liquidcat.utils.MathUtils;
import lol.liquidcat.utils.block.BlockExtensions;
import lol.liquidcat.utils.render.GLUtils;
import lol.liquidcat.value.BlockValue;
import lol.liquidcat.value.BoolValue;
import lol.liquidcat.value.FloatValue;
import lol.liquidcat.value.FontValue;
import lol.liquidcat.value.IntValue;
import lol.liquidcat.value.ListValue;
import lol.liquidcat.value.Value;

@SideOnly(Side.CLIENT)
public class SlowlyStyle extends Style {

    private boolean mouseDown;
    private boolean rightMouseDown;

    public static float drawSlider(final float value, final float min, final float max, final int x, final int y, final int width, final int mouseX, final int mouseY, final Color color) {
        final float displayValue = Math.max(min, Math.min(value, max));

        final float sliderValue = (float) x + (float) width * (displayValue - min) / (max - min);

        GLUtils.drawRect(x, y, x + width, y + 2, Integer.MAX_VALUE);
        GLUtils.drawRect(x, y, sliderValue, y + 2, color.getRGB());
        GLUtils.drawCircle(sliderValue - 3f, y + 1 - 3f, sliderValue + 3f, y + 1 + 3f, color);

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + 3 && Mouse.isButtonDown(0)) {
            double i = MathHelper.clamp_double(((double) mouseX - (double) x) / ((double) width - 3), 0, 1);

            BigDecimal bigDecimal = new BigDecimal(Double.toString((min + (max - min) * i)));
            bigDecimal = bigDecimal.setScale(2, 4);
            return bigDecimal.floatValue();
        }

        return value;
    }

    @Override
    public void drawPanel(int mouseX, int mouseY, Panel panel) {
        GLUtils.drawRoundedRect((float) panel.getX(), (float) panel.getY() - 3, (float) panel.getX() + panel.getWidth(), (float) panel.getY() + 17, 0.3f, new Color(21, 23, 26));

        if (panel.getFade() > 0) {
            GLUtils.drawRect((float) panel.getX(), (float) panel.getY() + 17, (float) panel.getX() + panel.getWidth(), panel.getY() + 19 + panel.getFade(), new Color(21, 23, 26).getRGB());
            GLUtils.drawRoundedRect((float) panel.getX(), panel.getY() + 17 + panel.getFade(), (float) panel.getX() + panel.getWidth(), panel.getY() + 19 + panel.getFade() + 5, 0.3f, new Color(21, 23, 26));
        }
        GlStateManager.resetColor();
        float textWidth = Fonts.nunito35.getStringWidth("??f" + StringUtils.stripControlCodes(panel.getName()));
        Fonts.nunito35.drawString(panel.getName(), (int) (panel.getX() - (textWidth - 100.0F) / 2F), panel.getY() + 7 - 3, Color.WHITE.getRGB());
    }

    @Override
    public void drawDescription(int mouseX, int mouseY, String text) {
        int textWidth = Fonts.nunito35.getStringWidth(text);

        GLUtils.drawRoundedRect(mouseX + 9, mouseY, mouseX + textWidth + 14, mouseY + Fonts.nunito35.FONT_HEIGHT + 3, 0.3f, new Color(26, 30, 33));
        GlStateManager.resetColor();
        Fonts.nunito35.drawString(text, mouseX + 12, mouseY + (Fonts.nunito35.FONT_HEIGHT / 2), Color.WHITE.getRGB());
    }

    @Override
    public void drawButtonElement(int mouseX, int mouseY, ButtonElement buttonElement) {
        GLUtils.drawRect(buttonElement.getX(), buttonElement.getY() - 1, buttonElement.getX() + buttonElement.getWidth(), buttonElement.getY() + buttonElement.getHeight() + 1, hoverColor(buttonElement.getColor() != Integer.MAX_VALUE ? new Color(7, 152, 252) : new Color(30, 33, 37), buttonElement.hoverTime).getRGB());

        GlStateManager.resetColor();

        Fonts.nunito35.drawString(buttonElement.getDisplayName(), buttonElement.getX() + 5, buttonElement.getY() + 5, Color.WHITE.getRGB());
    }

    /*public static boolean drawCheckbox(final boolean value, final int x, final int y, final int mouseX, final int mouseY, final Color color) {
        GLUtils.drawRect(x, y, x + 20, y + 10, value ? Color.GREEN : Color.RED);
        RenderUtils.drawFilledCircle(x + (value ? 15 : 5),y + 5, 5, Color.WHITE);

        if(mouseX >= x && mouseX <= x + 20 && mouseY >= y && mouseY <= y + 10 && Mouse.isButtonDown(0))
            return !value;

        return value;
    }*/

    @Override
    public void drawModuleElement(int mouseX, int mouseY, ModuleElement moduleElement) {
        GLUtils.drawRect(moduleElement.getX(), moduleElement.getY() - 1, moduleElement.getX() + moduleElement.getWidth(), moduleElement.getY() + moduleElement.getHeight() + 1, hoverColor(new Color(30, 33, 37), moduleElement.hoverTime).getRGB());
        GLUtils.drawRect(moduleElement.getX(), moduleElement.getY() - 1, moduleElement.getX() + moduleElement.getWidth(), moduleElement.getY() + moduleElement.getHeight() + 1, hoverColor(new Color(7, 152, 252, moduleElement.slowlyFade), moduleElement.hoverTime).getRGB());

        GlStateManager.resetColor();
        Fonts.nunito35.drawString(moduleElement.getDisplayName(), moduleElement.getX() + 5, moduleElement.getY() + 5, Color.WHITE.getRGB());

        // Draw settings
        final List<Value<?>> moduleValues = moduleElement.getModule().getValues();

        if (!moduleValues.isEmpty()) {
            Fonts.nunito35.drawString(">", moduleElement.getX() + moduleElement.getWidth() - 8, moduleElement.getY() + 5, Color.WHITE.getRGB());

            if (moduleElement.isShowSettings()) {
                if (moduleElement.getSettingsWidth() > 0F && moduleElement.slowlySettingsYPos > moduleElement.getY() + 6)
                    GLUtils.drawRoundedRect(moduleElement.getX() + moduleElement.getWidth() + 4, moduleElement.getY() + 6, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), moduleElement.slowlySettingsYPos + 2, 0.3f, new Color(35, 36, 42));

                moduleElement.slowlySettingsYPos = moduleElement.getY() + 6;
                for (final Value value : moduleValues) {
                    boolean isNumber = value.get() instanceof Number;

                    if (isNumber) {
                        AWTFontRenderer.Companion.setAssumeNonVolatile(false);
                    }

                    if (value instanceof BoolValue) {
                        final String text = value.getName();
                        final float textWidth = Fonts.nunito35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        if (mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= moduleElement.slowlySettingsYPos && mouseY <= moduleElement.slowlySettingsYPos + 12 && Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                            final BoolValue boolValue = (BoolValue) value;

                            boolValue.set(!boolValue.get());
                            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                        }

                        Fonts.nunito35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 2, ((BoolValue) value).get() ? Color.WHITE.getRGB() : Integer.MAX_VALUE);
                        moduleElement.slowlySettingsYPos += 11;
                    } else if (value instanceof ListValue) {
                        final ListValue listValue = (ListValue) value;

                        final String text = value.getName();
                        final float textWidth = Fonts.nunito35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 16)
                            moduleElement.setSettingsWidth(textWidth + 16);

                        Fonts.nunito35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 2, 0xffffff);
                        Fonts.nunito35.drawString(listValue.openList ? "-" : "+", (int) (moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() - (listValue.openList ? 5 : 6)), moduleElement.slowlySettingsYPos + 2, 0xffffff);

                        if (mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= moduleElement.slowlySettingsYPos && mouseY <= moduleElement.slowlySettingsYPos + Fonts.nunito35.FONT_HEIGHT && Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                            listValue.openList = !listValue.openList;
                            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                        }

                        moduleElement.slowlySettingsYPos += Fonts.nunito35.FONT_HEIGHT + 1;

                        for (final String valueOfList : listValue.getValues()) {
                            final float textWidth2 = Fonts.nunito35.getStringWidth("> " + valueOfList);

                            if (moduleElement.getSettingsWidth() < textWidth2 + 12)
                                moduleElement.setSettingsWidth(textWidth2 + 12);

                            if (listValue.openList) {
                                if (mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= moduleElement.slowlySettingsYPos + 2 && mouseY <= moduleElement.slowlySettingsYPos + 14 && Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                                    listValue.set(valueOfList);
                                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                                }

                                GlStateManager.resetColor();
                                Fonts.nunito35.drawString("> " + valueOfList, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 2, listValue.get() != null && listValue.get().equalsIgnoreCase(valueOfList) ? Color.WHITE.getRGB() : Integer.MAX_VALUE);
                                moduleElement.slowlySettingsYPos += Fonts.nunito35.FONT_HEIGHT + 1;
                            }
                        }

                        if (!listValue.openList) {
                            moduleElement.slowlySettingsYPos += 1;
                        }
                    } else if (value instanceof FloatValue) {
                        final FloatValue floatValue = (FloatValue) value;
                        final String text = value.getName() + "??f: " + MathUtils.round(floatValue.get(), 2);
                        final float textWidth = Fonts.nunito35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        final float valueOfSlide = drawSlider(floatValue.get(), floatValue.min(), floatValue.max(), moduleElement.getX() + moduleElement.getWidth() + 8, moduleElement.slowlySettingsYPos + 14, (int) moduleElement.getSettingsWidth() - 12, mouseX, mouseY, new Color(7, 152, 252));

                        if (valueOfSlide != floatValue.get())
                            floatValue.set(valueOfSlide);

                        Fonts.nunito35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 3, 0xffffff);
                        moduleElement.slowlySettingsYPos += 19;
                    } else if (value instanceof IntValue) {
                        final IntValue intValue = (IntValue) value;
                        final String text = value.getName() + "??f: " + (value instanceof BlockValue ? BlockExtensions.getBlockName(intValue.get()) + " (" + intValue.get() + ")" : intValue.get());
                        final float textWidth = Fonts.nunito35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        final float valueOfSlide = drawSlider(intValue.get(), intValue.min(), intValue.max(), moduleElement.getX() + moduleElement.getWidth() + 8, moduleElement.slowlySettingsYPos + 14, (int) moduleElement.getSettingsWidth() - 12, mouseX, mouseY, new Color(7, 152, 252));

                        if (valueOfSlide != intValue.get())
                            intValue.set((int) valueOfSlide);

                        Fonts.nunito35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 3, 0xffffff);
                        moduleElement.slowlySettingsYPos += 19;
                    } else if (value instanceof FontValue) {
                        final FontValue fontValue = (FontValue) value;
                        final FontRenderer fontRenderer = fontValue.get();

                        String displayString = "Font: Unknown";

                        if (fontRenderer instanceof GameFontRenderer) {
                            final GameFontRenderer liquidFontRenderer = (GameFontRenderer) fontRenderer;

                            displayString = "Font: " + liquidFontRenderer.getDefaultFont().getFont().getName() + " - " + liquidFontRenderer.getDefaultFont().getFont().getSize();
                        } else if (fontRenderer == Fonts.minecraftFont)
                            displayString = "Font: Minecraft";
                        else {
                            final Object[] objects = Fonts.getFontDetails(fontRenderer);

                            if (objects != null) {
                                displayString = objects[0] + ((int) objects[1] != -1 ? " - " + objects[1] : "");
                            }
                        }

                        Fonts.nunito35.drawString(displayString, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 2, Color.WHITE.getRGB());
                        int stringWidth = Fonts.nunito35.getStringWidth(displayString);

                        if (moduleElement.getSettingsWidth() < stringWidth + 8)
                            moduleElement.setSettingsWidth(stringWidth + 8);

                        if ((Mouse.isButtonDown(0) && !mouseDown || Mouse.isButtonDown(1) && !rightMouseDown) && mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= moduleElement.slowlySettingsYPos && mouseY <= moduleElement.slowlySettingsYPos + 12) {
                            final List<FontRenderer> fonts = Fonts.getFonts();

                            if (Mouse.isButtonDown(0)) {
                                for (int i = 0; i < fonts.size(); i++) {
                                    final FontRenderer font = fonts.get(i);

                                    if (font == fontRenderer) {
                                        i++;

                                        if (i >= fonts.size())
                                            i = 0;

                                        fontValue.set(fonts.get(i));
                                        break;
                                    }
                                }
                            } else {
                                for (int i = fonts.size() - 1; i >= 0; i--) {
                                    final FontRenderer font = fonts.get(i);

                                    if (font == fontRenderer) {
                                        i--;

                                        if (i >= fonts.size())
                                            i = 0;

                                        if (i < 0)
                                            i = fonts.size() - 1;

                                        fontValue.set(fonts.get(i));
                                        break;
                                    }
                                }
                            }
                        }

                        moduleElement.slowlySettingsYPos += 11;
                    } else {
                        final String text = value.getName() + "??f: " + value.get();
                        final float textWidth = Fonts.nunito35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        GlStateManager.resetColor();
                        Fonts.nunito35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, moduleElement.slowlySettingsYPos + 4, 0xffffff);
                        moduleElement.slowlySettingsYPos += 12;
                    }

                    if (isNumber) {
                        AWTFontRenderer.Companion.setAssumeNonVolatile(true);
                    }
                }

                moduleElement.updatePressed();
                mouseDown = Mouse.isButtonDown(0);
                rightMouseDown = Mouse.isButtonDown(1);
            }
        }
    }

    private Color hoverColor(final Color color, final int hover) {
        final int r = color.getRed() - (hover * 2);
        final int g = color.getGreen() - (hover * 2);
        final int b = color.getBlue() - (hover * 2);

        return new Color(Math.max(r, 0), Math.max(g, 0), Math.max(b, 0), color.getAlpha());
    }
}

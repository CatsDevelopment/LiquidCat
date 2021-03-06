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
import java.util.List;

import lol.liquidcat.features.module.modules.render.ClickGUI;
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
public class NullStyle extends Style {

    private boolean mouseDown;
    private boolean rightMouseDown;

    @Override
    public void drawPanel(int mouseX, int mouseY, Panel panel) {
        GLUtils.drawRect((float) panel.getX() - 3, (float) panel.getY(), (float) panel.getX() + panel.getWidth() + 3, (float) panel.getY() + 19, ClickGUI.generateColor().getRGB());
        if(panel.getFade() > 0)
            GLUtils.drawBorderedRect((float) panel.getX(), (float) panel.getY() + 19, (float) panel.getX() + panel.getWidth(), panel.getY() + 19 + panel.getFade(), 1, Integer.MIN_VALUE, Integer.MIN_VALUE);
        GlStateManager.resetColor();
        float textWidth = Fonts.nunito35.getStringWidth("??f" + StringUtils.stripControlCodes(panel.getName()));
        Fonts.nunito35.drawString("??f" + panel.getName(), (int) (panel.getX() - (textWidth - 100.0F) / 2F), panel.getY() + 7, Integer.MAX_VALUE);
    }

    @Override
    public void drawDescription(int mouseX, int mouseY, String text) {
        int textWidth = Fonts.nunito35.getStringWidth(text);

        GLUtils.drawRect(mouseX + 9, mouseY, mouseX + textWidth + 14, mouseY + Fonts.nunito35.FONT_HEIGHT + 3, ClickGUI.generateColor().getRGB());
        GlStateManager.resetColor();
        Fonts.nunito35.drawString(text, mouseX + 12, mouseY + (Fonts.nunito35.FONT_HEIGHT / 2), Integer.MAX_VALUE);
    }

    @Override
    public void drawButtonElement(int mouseX, int mouseY, ButtonElement buttonElement) {
        GlStateManager.resetColor();
        Fonts.nunito35.drawString(buttonElement.getDisplayName(), (int) (buttonElement.getX() - (Fonts.nunito35.getStringWidth(buttonElement.getDisplayName()) - 100.0f) / 2.0f), buttonElement.getY() + 6, buttonElement.getColor());
    }

    @Override
    public void drawModuleElement(int mouseX, int mouseY, ModuleElement moduleElement) {
        final int guiColor = ClickGUI.generateColor().getRGB();
        GlStateManager.resetColor();
        Fonts.nunito35.drawString(moduleElement.getDisplayName(), (int) (moduleElement.getX() - (Fonts.nunito35.getStringWidth(moduleElement.getDisplayName()) - 100.0f) / 2.0f), moduleElement.getY() + 6, moduleElement.getModule().getState() ? guiColor : Integer.MAX_VALUE);

        final List<Value<?>> moduleValues = moduleElement.getModule().getValues();

        if(!moduleValues.isEmpty()) {
            Fonts.nunito35.drawString("+", moduleElement.getX() + moduleElement.getWidth() - 8, moduleElement.getY() + (moduleElement.getHeight() / 2), Color.WHITE.getRGB());

            if(moduleElement.isShowSettings()) {
                int yPos = moduleElement.getY() + 4;
                for(final Value value : moduleValues) {
                    boolean isNumber = value.get() instanceof Number;

                    if (isNumber) {
                        AWTFontRenderer.Companion.setAssumeNonVolatile(false);
                    }

                    if (value instanceof BoolValue) {
                        String text = value.getName();
                        float textWidth = Fonts.nunito35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        GLUtils.drawRect(moduleElement.getX() + moduleElement.getWidth() + 4, yPos + 2, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), yPos + 14, Integer.MIN_VALUE);

                        if (mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= yPos + 2 && mouseY <= yPos + 14) {
                            if (Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                                final BoolValue boolValue = (BoolValue) value;

                                boolValue.set(!boolValue.get());
                                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                            }
                        }

                        GlStateManager.resetColor();
                        Fonts.nunito35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, yPos + 4, ((BoolValue) value).get() ? guiColor : Integer.MAX_VALUE);
                        yPos += 12;
                    }else if(value instanceof ListValue) {
                        ListValue listValue = (ListValue) value;

                        String text = value.getName();
                        float textWidth = Fonts.nunito35.getStringWidth(text);

                        if(moduleElement.getSettingsWidth() < textWidth + 16)
                            moduleElement.setSettingsWidth(textWidth + 16);

                        GLUtils.drawRect(moduleElement.getX() + moduleElement.getWidth() + 4, yPos + 2, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), yPos + 14, Integer.MIN_VALUE);
                        GlStateManager.resetColor();
                        Fonts.nunito35.drawString("??c" + text, moduleElement.getX() + moduleElement.getWidth() + 6, yPos + 4, 0xffffff);
                        Fonts.nunito35.drawString(listValue.openList ? "-" : "+", (int) (moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() - (listValue.openList ? 5 : 6)), yPos + 4, 0xffffff);

                        if(mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= yPos + 2 && mouseY <= yPos + 14) {
                            if(Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                                listValue.openList = !listValue.openList;
                                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                            }
                        }

                        yPos += 12;

                        for(final String valueOfList : listValue.getValues()) {
                            final float textWidth2 = Fonts.nunito35.getStringWidth(">" + valueOfList);

                            if(moduleElement.getSettingsWidth() < textWidth2 + 12)
                                moduleElement.setSettingsWidth(textWidth2 + 12);

                            if (listValue.openList) {
                                GLUtils.drawRect(moduleElement.getX() + moduleElement.getWidth() + 4, yPos + 2, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), yPos + 14, Integer.MIN_VALUE);

                                if(mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= yPos + 2 && mouseY <= yPos + 14) {
                                    if(Mouse.isButtonDown(0) && moduleElement.isntPressed()) {
                                        listValue.set(valueOfList);
                                        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                                    }
                                }

                                GlStateManager.resetColor();
                                Fonts.nunito35.drawString(">", moduleElement.getX() + moduleElement.getWidth() + 6, yPos + 4, Integer.MAX_VALUE);
                                Fonts.nunito35.drawString(valueOfList, moduleElement.getX() + moduleElement.getWidth() + 14, yPos + 4, listValue.get() != null && listValue.get().equalsIgnoreCase(valueOfList) ? guiColor : Integer.MAX_VALUE);
                                yPos += 12;
                            }
                        }
                    }else if(value instanceof FloatValue) {
                        FloatValue floatValue = (FloatValue) value;
                        String text = value.getName() + "??f: ??c" + MathUtils.round(floatValue.get(), 2);
                        float textWidth = Fonts.nunito35.getStringWidth(text);

                        if(moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        GLUtils.drawRect(moduleElement.getX() + moduleElement.getWidth() + 4, yPos + 2, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), yPos + 24, Integer.MIN_VALUE);
                        GLUtils.drawRect(moduleElement.getX() + moduleElement.getWidth() + 8, yPos + 18, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() - 4, yPos + 19, Integer.MAX_VALUE);
                        float sliderValue = moduleElement.getX() + moduleElement.getWidth() + ((moduleElement.getSettingsWidth() - 12) * (floatValue.get() - floatValue.min()) / (floatValue.max() - floatValue.min()));
                        GLUtils.drawRect(8 + sliderValue, yPos + 15, sliderValue + 11, yPos + 21, guiColor);

                        if(mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() - 4 && mouseY >= yPos + 15 && mouseY <= yPos + 21) {
                            if(Mouse.isButtonDown(0)) {
                                double i = MathHelper.clamp_double((mouseX - moduleElement.getX() - moduleElement.getWidth() - 8) / (moduleElement.getSettingsWidth() - 12), 0, 1);
                                floatValue.set((float) MathUtils.round((float) (floatValue.min() + (floatValue.max() - floatValue.min()) * i), 2));
                            }
                        }

                        GlStateManager.resetColor();
                        Fonts.nunito35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, yPos + 4, 0xffffff);
                        yPos += 22;
                    }else if(value instanceof IntValue) {
                        IntValue intValue = (IntValue) value;
                        String text = value.getName() + "??f: ??c" + (value instanceof BlockValue ? BlockExtensions.getBlockName(intValue.get()) + " (" + intValue.get() + ")" : intValue.get());
                        float textWidth = Fonts.nunito35.getStringWidth(text);

                        if(moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        GLUtils.drawRect(moduleElement.getX() + moduleElement.getWidth() + 4, yPos + 2, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), yPos + 24, Integer.MIN_VALUE);
                        GLUtils.drawRect(moduleElement.getX() + moduleElement.getWidth() + 8, yPos + 18, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() - 4, yPos + 19, Integer.MAX_VALUE);
                        float sliderValue = moduleElement.getX() + moduleElement.getWidth() + ((moduleElement.getSettingsWidth() - 12) * (intValue.get() - intValue.min()) / (intValue.max() - intValue.min()));
                        GLUtils.drawRect(8 + sliderValue, yPos + 15, sliderValue + 11, yPos + 21, guiColor);

                        if(mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= yPos + 15 && mouseY <= yPos + 21) {
                            if(Mouse.isButtonDown(0)) {
                                double i = MathHelper.clamp_double((mouseX - moduleElement.getX() - moduleElement.getWidth() - 8) / (moduleElement.getSettingsWidth() - 12), 0, 1);
                                intValue.set((int) (intValue.min() + (intValue.max() - intValue.min()) * i));
                            }
                        }

                        GlStateManager.resetColor();
                        Fonts.nunito35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, yPos + 4, 0xffffff);
                        yPos += 22;
                    }else if(value instanceof FontValue) {
                        final FontValue fontValue = (FontValue) value;
                        final FontRenderer fontRenderer = fontValue.get();

                        GLUtils.drawRect(moduleElement.getX() + moduleElement.getWidth() + 4, yPos + 2, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), yPos + 14, Integer.MIN_VALUE);

                        String displayString = "Font: Unknown";

                        if (fontRenderer instanceof GameFontRenderer) {
                            final GameFontRenderer liquidFontRenderer = (GameFontRenderer) fontRenderer;

                            displayString = "Font: " + liquidFontRenderer.getDefaultFont().getFont().getName() + " - " + liquidFontRenderer.getDefaultFont().getFont().getSize();
                        }else if(fontRenderer == Fonts.minecraftFont)
                            displayString = "Font: Minecraft";
                        else{
                            final Object[] objects = Fonts.getFontDetails(fontRenderer);

                            if(objects != null) {
                                displayString = objects[0] + ((int) objects[1] != -1 ? " - " + objects[1] : "");
                            }
                        }

                        Fonts.nunito35.drawString(displayString, moduleElement.getX() + moduleElement.getWidth() + 6, yPos + 4, Color.WHITE.getRGB());
                        int stringWidth = Fonts.nunito35.getStringWidth(displayString);

                        if(moduleElement.getSettingsWidth() < stringWidth + 8)
                            moduleElement.setSettingsWidth(stringWidth + 8);

                        if((Mouse.isButtonDown(0) && !mouseDown || Mouse.isButtonDown(1) && !rightMouseDown) && mouseX >= moduleElement.getX() + moduleElement.getWidth() + 4 && mouseX <= moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth() && mouseY >= yPos + 4 && mouseY <= yPos + 12) {
                            final List<FontRenderer> fonts = Fonts.getFonts();

                            if(Mouse.isButtonDown(0)) {
                                for(int i = 0; i < fonts.size(); i++) {
                                    final FontRenderer font = fonts.get(i);

                                    if(font == fontRenderer) {
                                        i++;

                                        if(i >= fonts.size())
                                            i = 0;

                                        fontValue.set(fonts.get(i));
                                        break;
                                    }
                                }
                            }else{
                                for(int i = fonts.size() - 1; i >= 0; i--) {
                                    final FontRenderer font = fonts.get(i);

                                    if(font == fontRenderer) {
                                        i--;

                                        if(i >= fonts.size())
                                            i = 0;

                                        if(i < 0)
                                            i = fonts.size() - 1;

                                        fontValue.set(fonts.get(i));
                                        break;
                                    }
                                }
                            }
                        }

                        yPos += 11;
                    }else{
                        String text = value.getName() + "??f: ??c" + value.get();
                        float textWidth = Fonts.nunito35.getStringWidth(text);

                        if (moduleElement.getSettingsWidth() < textWidth + 8)
                            moduleElement.setSettingsWidth(textWidth + 8);

                        GLUtils.drawRect(moduleElement.getX() + moduleElement.getWidth() + 4, yPos + 2, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), yPos + 14, Integer.MIN_VALUE);
                        GlStateManager.resetColor();
                        Fonts.nunito35.drawString(text, moduleElement.getX() + moduleElement.getWidth() + 6, yPos + 4, 0xffffff);
                        yPos += 12;
                    }

                    if (isNumber) {
                        // This state is cleaned up in ClickGUI
                        AWTFontRenderer.Companion.setAssumeNonVolatile(true);
                    }
                }

                moduleElement.updatePressed();
                mouseDown = Mouse.isButtonDown(0);
                rightMouseDown = Mouse.isButtonDown(1);

                if(moduleElement.getSettingsWidth() > 0F && yPos > moduleElement.getY() + 4)
                    GLUtils.drawBorderedRect(moduleElement.getX() + moduleElement.getWidth() + 4, moduleElement.getY() + 6, moduleElement.getX() + moduleElement.getWidth() + moduleElement.getSettingsWidth(), yPos + 2, 1F, Integer.MIN_VALUE, 0);
            }
        }
    }
}

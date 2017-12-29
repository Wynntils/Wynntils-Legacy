package com.wynndevs.core.gui.screen;

import com.wynndevs.ConfigValues;
import com.wynndevs.core.Reference;
import com.wynndevs.core.config.ConfigCategory;
import com.wynndevs.core.config.ConfigParser;
import com.wynndevs.core.events.ClientEvents;
import com.wynndevs.core.gui.CoreGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ResourceLocation;
import java.io.IOException;

public class ConfigGui extends CoreGuiScreen {

    private static final ResourceLocation TEXTURE_OPTIONS = new ResourceLocation(Reference.MOD_ID, "textures/gui/options.png");

    ConfigCategory category = ConfigParser.getMappedConfig(ConfigValues.class);
    int page = 1;

    public ConfigGui(Minecraft mc) {
        super(mc);
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        int slots = (page - 1) * 5;
        int startPage = page * 5;

        int x = width / 2;

        int real_amount = category.getValues().size() + category.getSubCategories().size();
        if(category.getInheritance() != null) {
            real_amount++;
        }

        int max_pages = (real_amount <= 5 ? 1 : (int)Math.ceil(real_amount / 5d));

        if(mouseX > x - 65 && mouseX < x - 38 && mouseY > 52 && mouseY < 65) {
            if(page >= 2) {
                page--;
            }
            return;
        }
        if(mouseX > x + 36 && mouseX < x + 65 && mouseY > 52 && mouseY < 65) {
            if(max_pages > page) {
                page++;
            }
            return;
        }

        if(category.getInheritance() != null && page == 1) {
            if(mouseX > x - 60 && mouseX < x + 57 && mouseY > (75 + (slots * 17)) && mouseY < (90 + (slots * 17))) {
                category = category.getInheritance();
                mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            }
            slots++;
        }

        for(ConfigCategory cfg : category.getSubCategories()) {
            if(slots > startPage) {
                break;
            }
            if(slots <= category.getSubCategories().size()) {
                if(mouseX > x - 60 && mouseX < x + 57 && mouseY > (75 + (slots * 17)) && mouseY < (90 + (slots * 17))) {
                    category = cfg;
                    mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                }
                slots++;
                continue;
            }
        }

        for(String key : category.getValues().keySet()) {
            if(slots > startPage) {
                break;
            }
            if(slots <= category.getValues().size()) {
                ConfigCategory.AdvancedField value = category.getValues().get(key);

                if(!value.getValue()) {
                    if(mouseX > x - 118 && mouseX < x - 108 && mouseY > (72 + (slots * 20)) && mouseY < (90 + (slots * 20))) {
                        try{
                            value.getField().setBoolean(value.getInstance(), true);
                            category.getValues().put(key, new ConfigCategory.AdvancedField(true, value.getField(), value.getInstance()));
                            ClientEvents.syncConfig();
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                        }catch (Exception ex) { }
                    }
                }else{
                    if(mouseX > x - 118 && mouseX < x - 108 && mouseY > (72 + (slots * 20)) && mouseY < (90 + (slots * 20))) {
                        try{
                            value.getField().setBoolean(value.getInstance(), false);
                            category.getValues().put(key, new ConfigCategory.AdvancedField(false, value.getField(), value.getInstance()));
                            ClientEvents.syncConfig();
                            mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, -1.0F));
                        }catch (Exception ex) { }
                    }
                }

                slots++;
                continue;
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int x = width / 2;

        int real_amount = category.getValues().size() + category.getSubCategories().size();
        if(category.getInheritance() != null) {
            real_amount++;
        }

        int max_pages = (real_amount <= 5 ? 1 : (int)Math.ceil(real_amount / 5d));

        mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);
        drawTexturedModalRect(x - 128, 5, 768, 256, 256, 193);

        if(mouseX > x - 65 && mouseX < x - 38 && mouseY > 52 && mouseY < 65) {
            if(page >= 2) {
                drawTexturedModalRect(x - 65, 51, 0, 208, 28, 14);
            }else{
                drawTexturedModalRect(x - 65, 51, 0, 194, 28, 14);
            }
        }else{
            drawTexturedModalRect(x - 65, 51, 0, 194, 28, 14);
        }
        if(mouseX > x + 36 && mouseX < x + 65 && mouseY > 52 && mouseY < 65) {
            if(max_pages > page) {
                drawTexturedModalRect(x + 37, 51, 28, 208, 28, 14);
            }else{
                drawTexturedModalRect(x + 37, 51, 28, 194, 28, 14);
            }
        }else{
            drawTexturedModalRect(x + 37, 51, 28, 194, 28, 14);
        }


        int slots = 0;

        int slotPage = (page - 1) * 5;
        int startPage = page * 5;

        if(category.getInheritance() != null && page == 1) {
            mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            if(mouseX > x - 60 && mouseX < x + 57 && mouseY > (75 + (slots * 17)) && mouseY < (90 + (slots * 17))) {
                drawTexturedModalRect(x - 60, 75 + (slots * 17), 0, 236, 116, 14);
            }else{
                drawTexturedModalRect(x - 60, 75 + (slots * 17), 0, 222, 116, 14);
            }
            drawStringPlain("Back", (width - fontRenderer.getStringWidth("Back")) / 2, 78 + (slots * 17), 1);

            slots++;
        }

        int catCount = 0;
        for(ConfigCategory cfg : category.getSubCategories()) {
            catCount++;
            if(catCount < slotPage + 1) {
                continue;
            }
            if(slots > startPage) {
                break;
            }
            if(slots <= category.getSubCategories().size()) {
                mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                if(mouseX > x - 60 && mouseX < x + 57 && mouseY > (75 + (slots * 17)) && mouseY < (90 + (slots * 17))) {
                    drawTexturedModalRect(x - 60, 75 + (slots * 17), 0, 236, 116, 14);
                }else{
                    drawTexturedModalRect(x - 60, 75 + (slots * 17), 0, 222, 116, 14);
                }
                String text = cfg.getName();
                drawStringPlain(text, (width - fontRenderer.getStringWidth(text)) / 2, 78 + (slots * 17), 1);

                slots++;
                continue;
            }
        }

        int valCount = 0;
        for(String key : category.getValues().keySet()) {
            valCount++;
            if(valCount < slotPage + 1) {
                continue;
            }
            if(slots > startPage) {
                break;
            }
            if(slots <= category.getValues().size()) {
                ConfigCategory.AdvancedField value = category.getValues().get(key);

                mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                if(!value.getValue()) {
                    if(mouseX > x - 118 && mouseX < x - 108 && mouseY > (72 + (slots * 20)) && mouseY < (88 + (slots * 20))) {
                        drawTexturedModalRect(x - 117, 72 + (slots * 20), 240, 208, 8, 14);
                    }else {
                        drawTexturedModalRect(x - 117, 72 + (slots * 20), 240, 194, 8, 14);
                    }
                }else{
                    if(mouseX > x - 118 && mouseX < x - 108 && mouseY > (72 + (slots * 20)) && mouseY < (88 + (slots * 20))) {
                        drawTexturedModalRect(x - 117, 72 + (slots * 20), 248, 208, 8, 14);
                    }else{
                        drawTexturedModalRect(x - 117, 72 + (slots * 20), 248, 194, 8, 14);
                    }
                }

                drawStringPlain(key, x - 100, 75 + (slots * 20), 1);

                slots++;
                continue;
            }
        }

        drawCenteredStringPlain("Page " + page + "/" + max_pages, x, 54, 1);
    }

}

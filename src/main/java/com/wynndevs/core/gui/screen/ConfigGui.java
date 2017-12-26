package com.wynndevs.core.gui.screen;

import com.wynndevs.ConfigValues;
import com.wynndevs.core.Reference;
import com.wynndevs.core.config.ConfigCategory;
import com.wynndevs.core.config.ConfigParser;
import com.wynndevs.core.gui.CoreGuiScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.sql.Ref;
import java.util.ArrayList;

public class ConfigGui extends CoreGuiScreen {

    private static final ResourceLocation TEXTURE_OPTIONS = new ResourceLocation(Reference.MOD_ID, "textures/gui/options.png");

    ConfigCategory category = ConfigParser.getMappedConfig(ConfigValues.class);
    int page = 1;

    public ConfigGui(Minecraft mc) {
        super(mc);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int x = width / 2;
        int y = height / 2;

        int max_pages = category.getValues().size() <= 5 ? 1 : (int)Math.floor(category.getValues().size() / 5);

        mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);
        drawTexturedModalRect(x - 128, 5, 768, 256, 256, 193);

        int slots = (page - 1) * 5;
        int startPage = page * 5;

        for(ConfigCategory cfg : category.getSubCategories()) {
            if(slots > startPage) {
                break;
            }
            if(slots <= category.getSubCategories().size()) {
                if(mouseX > x - 60 && mouseX < x + 57 && mouseY > (75 - (slots * 17)) && mouseY < (90 - (slots * 17))) {
                    drawTexturedModalRect(x - 60, 75 + (slots * 20), 0, 236, 116, 14);
                }else{
                    drawTexturedModalRect(x - 60, 75 + (slots * 20), 0, 222, 116, 14);
                }
                String text = cfg.getName();
                drawStringPlain(text, (width - fontRenderer.getStringWidth(text)) / 2, 78 + (slots * 17), 1);

                slots++;
                continue;
            }
        }

        mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);
        for(String key : category.getValues().keySet()) {
            if(slots > startPage) {
                break;
            }
            if(slots <= category.getValues().size()) {
                boolean value = category.getValues().get(key);

                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

                if(!value) {
                    if(mouseX > x - 117 && mouseX < x - 109 && mouseY > (72 + (slots * 17)) && mouseY < (90 + (slots * 17))) {
                        drawTexturedModalRect(x - 117, 72 + (slots * 20), 240, 208, 8, 14);
                    }else {
                        drawTexturedModalRect(x - 117, 72 + (slots * 20), 240, 194, 8, 14);
                    }
                }else{
                    if(mouseX > x - 117 && mouseX < x - 109 && mouseY > (72 + (slots * 17)) && mouseY < (90 + (slots * 17))) {
                        drawTexturedModalRect(x - 117, 72 + (slots * 20), 248, 208, 8, 14);
                    }else{
                        drawTexturedModalRect(x - 117, 72 + (slots * 20), 248, 194, 8, 14);
                    }
                }

                drawStringPlain(key, x - 105, 75 + (slots * 20), 1);

                slots++;
                continue;
            }
        }

        drawStringPlain("X = " + (mouseX - x) + " Y= " + (mouseY), mouseX, mouseY + 10, 1);

        drawCenteredStringPlain("Page " + page + "/" + max_pages, x, 54, Integer.parseInt("858585", 16));
    }

}

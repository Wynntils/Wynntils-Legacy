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

import java.util.ArrayList;

public class ConfigGui extends CoreGuiScreen {

    private static final ResourceLocation TEXTURE_OPTIONS = new ResourceLocation(Reference.MOD_ID, "textures/gui/options.png");

    ArrayList<ConfigCategory> cfgs = ConfigParser.getMappedConfig(ConfigValues.class);
    int page = 1;

    String selectedCategory = "main";

    public ConfigGui(Minecraft mc) {
        super(mc);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int x = width / 2;
        int y = height / 2;

        int max_pages = cfgs.size() <= 5 ? 1 : (int)Math.floor(cfgs.size() / 5);

        mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);
        drawTexturedModalRect(x - 128, 5, 768, 256, 256, 193);

        drawCenteredStringPlain("Page " + page + "/" + max_pages, x, 54, Integer.parseInt("858585", 16));

        int slots = 0;
        GL11.glPushMatrix();
        mc.getTextureManager().bindTexture(TEXTURE_OPTIONS);



        GL11.glPopMatrix();
    }

}

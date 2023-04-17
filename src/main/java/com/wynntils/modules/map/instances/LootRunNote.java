package com.wynntils.modules.map.instances;

import com.google.gson.*;
import com.wynntils.McIf;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.colors.MinecraftChatColors;
import com.wynntils.core.utils.StringUtils;
import com.wynntils.core.utils.objects.Location;

import java.lang.reflect.Type;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.text.TextFormatting;

import static net.minecraft.client.renderer.GlStateManager.*;

public class LootRunNote {

    private Location location;
    private String note;

    private static final ScreenRenderer renderer = new ScreenRenderer();

    public LootRunNote(Location location, String note) {
        this.location = location;
        this.note = note;
    }

    public Location getLocation() {
        return location;
    }

    public String getNote() {
        return note;
    }

    public String getLocationString() {
        return "(" + (int) location.x + ", " + (int) location.y + ", " + (int) location.z + ")";
    }

    public String getShortLocationString() {
        return (int) location.x + "," + (int) location.y + "," + (int) location.z;
    }

    public void drawNote(CustomColor color) {
        RenderManager render = McIf.mc().getRenderManager();
        FontRenderer fr = render.getFontRenderer();

        if (McIf.player().getDistanceSq(location.x, location.y, location.z) > 4096f)
            return; // only draw nametag when close

        String note = MinecraftChatColors.translateAlternateColorCodes('&', this.note);
        String[] lines = StringUtils.wrapTextBySize(note, 200);
        int offsetY = -(fr.FONT_HEIGHT * lines.length) / 2;

        for (String line : lines) {
            drawNametag(line, color, (float) (location.x - render.viewerPosX), (float) (location.y - render.viewerPosY + 2), (float) (location.z - render.viewerPosZ), offsetY, render.playerViewY, render.playerViewX, render.options.thirdPersonView == 2);
            offsetY += fr.FONT_HEIGHT;
        }
    }

    private static void drawNametag(String input, CustomColor color, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal) {
        pushAttrib();
        pushMatrix();
        {
            ScreenRenderer.beginGL(0, 0); // we set to 0 because we don't want the ScreenRender to handle this thing
            {
                // positions
                translate(x, y, z); // translates to the correct postion
                glNormal3f(0.0F, 1.0F, 0.0F);
                rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
                rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
                scale(-0.025F, -0.025F, 0.025F);
                disableLighting();

                int middlePos = (int) renderer.getStringWidth(TextFormatting.getTextWithoutFormattingCodes(input)) / 2;

                // draws the label
                renderer.drawString(input, -middlePos, verticalShift, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                // renders twice to replace the areas that are overlaped by tile entities
                enableDepth();
                renderer.drawString(input, -middlePos, verticalShift, color, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);

                // returns back to normal
                enableDepth();
                enableLighting();
                disableBlend();
                color(1.0f, 1.0f, 1.0f, 1.0f);
            }
            ScreenRenderer.endGL();
        }
        popMatrix();
        popAttrib();
    }

    // Compatibility with Artemis
    public static class LootrunNoteSerializer implements JsonDeserializer<LootRunNote> {
        @Override
        public LootRunNote deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject object = json.getAsJsonObject();

            // Artemis used to store the location in a "position" field, but now it's in "location", like legacy. Support both.
            JsonObject location = object.has("location") ? object.get("location").getAsJsonObject() : object.get("position").getAsJsonObject();

            Location locationParsed = new Location(location.get("x").getAsDouble(), location.get("y").getAsDouble(), location.get("z").getAsDouble());

            // Artemis stores components, Wynntils stores strings. Drop styling and just use the text.
            String text = object.get("note").isJsonPrimitive() ? object.get("note").getAsString() : object.get("note").getAsJsonObject().get("text").getAsString();

            return new LootRunNote(locationParsed, text);
        }
    }
}

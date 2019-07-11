package com.wynntils.modules.utilities.overlays.ui;

import com.wynntils.ModCore;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.instances.PlayerInfo;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.modules.utilities.managers.QuickCastManager;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.MouseEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class GuiQuickCast extends GuiScreen {
    private float pointerX = 0;
    private float pointerY = 0;
    private final Quarter[] quarters = new Quarter[4];

    private GuiQuickCast() {
        super();

        allowUserInput = true;

        ClassType wynnClass = PlayerInfo.getPlayerInfo().getCurrentClass();
        int playerLevel = PlayerInfo.getPlayerInfo().getLevel();
        for (int i = 0; i < 4; ++i) {
            int spell = quarterSpellMap[i];
            int grade = 0;
            for (int j = 0; j < 3; ++j) if (playerLevel >= levelRequirements[spell][j]) ++grade;
            quarters[i] = new Quarter().setSegment((4 - i) % 4).setText(getMessage(wynnClass, spell, grade), ModCore.mc().fontRenderer);
        }
    }

    private static final int[] quarterSpellMap = { 2, 0, 3, 1 };  // First quadrant has third spell, second quadrant has first spell, etc.

    private static final int hoverTransitionTicks = 25;  // Ticks taken to transition to hovered size
    private static final int dehoverTransitionTicks = 10;  // Ticks taken to return to normal after not hovering

    // Fraction of (max of screen height and width) that the radius of an inactive quarter is
    private static final float inactiveInnerRadius = 0.2f;
    private static final float inactiveOuterRadius = 0.8f;

    // Same as above but when active (hovered over)
    private static final float activeInnerRadius = 0.25f;
    private static final float activeOuterRadius = 0.9f;

    // Number of segments to draw the arcs in
    private static final int innerSegments = 10;
    private static final int outerSegments = 25;

    private static final int padding = 4;
    private static final int textPadding = 5;
    private static final int textWidth = 125;  // In pixels

    // Square of the radius needed to count has "hovering"
    private static final float hoverRadiusSq = inactiveInnerRadius * inactiveInnerRadius / 4f;

    private static final int[][] levelRequirements = {
        {  1, 16, 36 },  // e.g. spell 1 grade 2 is acquired at level 16
        { 11, 26, 46 },
        { 21, 36, 56 },  // e.g. spell 3 is unlocked (grade 1) at level 21
        { 31, 46, 66 }
    };

    private static String[] classNames = { "mage", "archer", "warrior", "assassin" };

    private static ITextComponent getMessage(ClassType wynnClass, int spell, int grade) {
        int wynnClassIndex = wynnClass.ordinal();

        if (grade == 0) {
            ITextComponent firstLine;
            ITextComponent secondLine;
            ITextComponent message = new TextComponentString("")
                .appendSibling(firstLine = new TextComponentTranslation("wynntils.utilities.ui.quick_cast.locked_spell"))
                .appendSibling(new TextComponentString("\n"))
                .appendSibling(secondLine = new TextComponentTranslation("wynntils.utilities.ui.quick_cast.locked_spell.unlock_condition", levelRequirements[spell][grade]));
            firstLine.getStyle().setColor(TextFormatting.DARK_GRAY);
            secondLine.getStyle().setColor(TextFormatting.DARK_GRAY);
            return message;
        }

        String key = "wynntils.utilities.ui.quick_cast." + classNames[wynnClassIndex] + ".spell_" + (spell + 1);

        ITextComponent name = new TextComponentTranslation(key + ".name");
        ITextComponent combo;
        ITextComponent description;
        ITextComponent message = new TextComponentString("")
            .appendSibling(name)
            .appendSibling(new TextComponentString(name.getUnformattedComponentText().endsWith("\n") ? "" : " "))
            .appendSibling(combo = new TextComponentString("(")
                .appendSibling(new TextComponentTranslation(key + ".combo"))
                .appendSibling(new TextComponentString(")"))
            )
            .appendSibling(new TextComponentString("\n"))
            .appendSibling(description = new TextComponentTranslation(key + ".description"))
            .appendSibling(new TextComponentString("\n"))
            .appendSibling(new TextComponentString(new TextComponentTranslation(key + ".grade_" + grade).getUnformattedComponentText()));
        combo.getStyle().setColor(TextFormatting.BLUE);
        description.getStyle().setItalic(true);

        message.getStyle().setColor(TextFormatting.WHITE);
        return message;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float centreX = width / 2f;
        float centreY = height / 2f;
        int maxDimension = Math.min(width, height);
        ScreenRenderer.beginGL(0, 0);
        GlStateManager.enableAlpha();
        GlStateManager.disableTexture2D();

        boolean currentlyCulling = GL11.glGetBoolean(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_CULL_FACE);

        quarters[0].draw(centreX, centreY, maxDimension);
        quarters[1].draw(centreX, centreY, maxDimension);
        quarters[2].draw(centreX, centreY, maxDimension);
        quarters[3].draw(centreX, centreY, maxDimension);

        if (currentlyCulling) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        }

        GlStateManager.enableTexture2D();

        quarters[0].drawText(centreX, centreY, maxDimension);
        quarters[1].drawText(centreX, centreY, maxDimension);
        quarters[2].drawText(centreX, centreY, maxDimension);
        quarters[3].drawText(centreX, centreY, maxDimension);

        ScreenRenderer.endGL();
    }

    private static long closeTime = Long.MAX_VALUE;
    private static final long closeGraceTime = 250;  // Time after closing to ignore mouse movement

    private void close() {
        if (!(pointerX * pointerX + pointerY * pointerY <= hoverRadiusSq || Math.min(Math.abs(pointerX), Math.abs(pointerY)) * Math.min(width, height) <= padding)) {
            int quarter = pointerX > 0 ? (pointerY > 0 ? 3 : 0) : (pointerY > 0 ? 2 : 1);
            int spell = quarterSpellMap[quarter];
            switch (spell) {
                case 0:
                    QuickCastManager.castFirstSpell();
                    break;
                case 1:
                    QuickCastManager.castSecondSpell();
                    break;
                case 2:
                    QuickCastManager.castThirdSpell();
                    break;
                case 3:
                    QuickCastManager.castFourthSpell();
                    break;
            }
        }

        ModCore.mc().displayGuiScreen(null);
        closeTime = System.currentTimeMillis() + closeGraceTime;
    }

    @Override
    public void updateScreen() {
        if (pointerX * pointerX + pointerY * pointerY <= hoverRadiusSq || Math.min(Math.abs(pointerX), Math.abs(pointerY)) * Math.min(width, height) <= padding) {
            // No hovering; in the middle or in the padding
            quarters[0].tick(false);
            quarters[1].tick(false);
            quarters[2].tick(false);
            quarters[3].tick(false);
        } else {
            if (pointerX > 0) {
                quarters[1].tick(false);
                quarters[2].tick(false);
                boolean positiveY = pointerY > 0;
                quarters[0].tick(!positiveY);
                quarters[3].tick(positiveY);
            } else {
                quarters[0].tick(false);
                quarters[3].tick(false);
                boolean positiveY = pointerY > 0;
                quarters[1].tick(!positiveY);
                quarters[2].tick(positiveY);
            }
        }
    }

    @Override public boolean doesGuiPauseGame() { return false; }

    @Override protected void mouseClicked(int mouseX, int mouseY, int mouseButton) { }

    @Override protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) { }

    @Override
    public void handleInput()  {
        if (Mouse.isCreated()) {
            while (Mouse.next()) {
                mouseHandled = false;
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre(this))) continue;
                handleMouseInput();
                mouseHandled = true;
            }
        }

        // Ignore keyboard
    }

    @Override
    public void handleMouseInput() {
        // These are fractions of the way across the screen
        int maxDimension = Math.min(mc.displayWidth, mc.displayHeight);
        pointerX = ((float) Mouse.getEventX() - (mc.displayWidth / 2f)) / maxDimension;
        pointerY = (0.5f - ((float) Mouse.getEventY() / (mc.displayHeight - 1f))) * mc.displayHeight / maxDimension;

        int button = Mouse.getEventButton();

        if (button == 0 && !Mouse.getEventButtonState()) {
            close();
        }
    }

    private static class Quarter {
        float innerRadius = inactiveInnerRadius;
        float outerRadius = inactiveOuterRadius;
        float transitionAmount = 0f;  // At 0, fully transitioned to inactive, at 1, fully transitioned to active
        float textScale;
        static final float hoverTransitionDelta = 1f / hoverTransitionTicks;
        static final float dehoverTransitionDelta = 1f / dehoverTransitionTicks;
        ITextComponent lines;
        FontRenderer fontRenderer;
        String[] text;
        int[] textWidths;
        int textboxWidth;
        int segment;

        void tick(boolean hovering) {
            if (hovering) {
                if (transitionAmount != 1f) {
                    transitionAmount += hoverTransitionDelta;
                    if (transitionAmount > 1f) transitionAmount = 1f;
                    updateRadii();
                }
            } else if (transitionAmount != 0f) {
                transitionAmount -= dehoverTransitionDelta;
                if (transitionAmount < 0f) transitionAmount = 0f;
                updateRadii();
            }
        }

        Quarter setText(ITextComponent lines, FontRenderer fontRenderer) {
            this.lines = lines;
            this.fontRenderer = fontRenderer;
            return this;
        }

        Quarter setSegment(int segment) {
            this.segment = segment;
            return this;
        }

        static final double innerDTheta = -Math.PI / (2D * (innerSegments));  // How much the angle changes with each segment
        static final float sinInnerDTheta = (float) Math.sin(innerDTheta);
        static final float cosInnerDTheta = (float) Math.cos(innerDTheta);

        static final double outerDTheta = Math.PI / (2D * (outerSegments));
        static final float sinOuterDTheta = (float) Math.sin(outerDTheta);
        static final float cosOuterDTheta = (float) Math.cos(outerDTheta);

        static final int nPoints = innerSegments + outerSegments + 4;

        float[] cachedActivePoints = null;
        float[] cachedInactivePoints = null;

        int cachedDimension = 0;

        static void drawCached(float[] cache, float centreX, float centreY, float alpha) {
            GL11.glColor4f(0f, 0f, 0f, alpha);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            for (int i = 0; i < nPoints * 2;) {
                GL11.glVertex3f(cache[i++] + centreX, cache[i++] + centreY, 0f);
            }
            GL11.glEnd();
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }

        // sinHalfPi[i] == sin(i * pi / 2)
        static final int[] sinHalfPi = { 0, 1, 0, -1, 0, 1, 0 };
        // cosHalfPi[i] == sinHalfPi[i + 1]
        static final int[] centreXDelta = { +1, +1, -1, -1 };
        static final int[] centreYDelta = { -1, +1, +1, -1 };

        void drawText(float centreX, float centreY, float maxDimension) {
            float innerOffset = innerRadius * maxDimension * MathHelper.SQRT_2 / 2f;
            float textboxTrueWidth = textboxWidth * textScale;
            final float x = centreX + centreXDelta[segment] * ((textboxTrueWidth + textPadding + padding + innerOffset) / 2f);
            float y = 0f;
            switch (centreYDelta[segment]) {
                case -1: y = centreY - ((padding + textPadding + innerOffset) / 2f + textboxTrueWidth); break;
                case 1: y = centreY + (padding + textPadding + innerOffset) / 2f; break;
            }
            GL11.glPushMatrix();
            GL11.glTranslatef(x, y, 0f);
            GL11.glScalef(textScale, textScale, 0f);
            y = 0;
            for (int i = 0, textLength = text.length; i < textLength; ++i) {
                fontRenderer.drawStringWithShadow(text[i], -textWidths[i] / 2f, y, 0xFF000000);
                y += fontRenderer.FONT_HEIGHT;
            }
            GL11.glPopMatrix();
        }

        void draw(float centreX, float centreY, int maxDimension) {
            if (maxDimension != cachedDimension) {
                cachedDimension = maxDimension;
                cachedActivePoints = null;
                cachedInactivePoints = null;
                final float available_radius = Math.min(inactiveOuterRadius, activeOuterRadius) - Math.min(inactiveInnerRadius, activeInnerRadius);
                double unscaledWidth = ((available_radius * maxDimension - padding * 2 - textPadding * 2) / 4D * MathHelper.SQRT_2);
                textScale = (float) unscaledWidth / textWidth;
                textboxWidth = (int) (unscaledWidth / textScale);
                List<ITextComponent> split = GuiUtilRenderComponents.splitText(lines, textboxWidth, fontRenderer, false, false);
                text = new String[split.size()];
                textWidths = new int[split.size()];
                for (int i = 0, splitSize = split.size(); i < splitSize; ++i) {
                    String line = split.get(i).getFormattedText();
                    text[i] = line;
                    textWidths[i] = fontRenderer.getStringWidth(line);
                }
            }

            centreX += centreXDelta[segment] * (padding / 2f);
            centreY += centreYDelta[segment] * (padding / 2f);


            boolean caching = false;
            float[] cache = null;
            int j = 0;

            if (transitionAmount == 0f) {
                if (cachedInactivePoints != null) {
                    drawCached(cachedInactivePoints, centreX, centreY, getAlpha());
                    return;
                }
                cache = cachedInactivePoints = new float[nPoints * 2];
                caching = true;
            } else if (transitionAmount == 1f) {
                if (cachedActivePoints != null) {
                    drawCached(cachedActivePoints, centreX, centreY, getAlpha());
                    return;
                }
                cache = cachedActivePoints = new float[nPoints * 2];
                caching = true;
            }

            float innerRadius = this.innerRadius * (maxDimension / 2f);
            float outerRadius = this.outerRadius * (maxDimension / 2f);

            float innerStartY = innerRadius * sinHalfPi[segment];
            float innerStartX = innerRadius * sinHalfPi[segment + 1];

            float innerEndY = innerRadius * sinHalfPi[segment + 3];
            float innerEndX = innerRadius * sinHalfPi[segment];

            float innerMiddleY = innerRadius * centreYDelta[segment];
            float innerMiddleX = innerRadius * centreXDelta[segment];

            GL11.glColor4f(0f, 0f, 0f, getAlpha());
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);

            // Draw inner arc

            if (caching) { cache[j++] = innerMiddleX; cache[j++] = innerMiddleY; }
            GL11.glVertex3f(innerMiddleX + centreX, innerMiddleY + centreY, 0f);

            float y = innerStartY;
            float x = innerStartX;

            for (int i = 0; i < innerSegments - 1; ++i) {
                if (caching) { cache[j++] = x; cache[j++] = y; }
                GL11.glVertex3f(x + centreX, y + centreY, 0f);
                float tempX = x;
                x = cosInnerDTheta * tempX - sinInnerDTheta * y;
                y = sinInnerDTheta * tempX + cosInnerDTheta * y;

            }

            if (caching) { cache[j++] = x; cache[j++] = y; }
            GL11.glVertex3f(x + centreX, y + centreY, 0f);

            if (caching) { cache[j++] = innerEndX; cache[j++] = innerEndY; }
            GL11.glVertex3f(innerEndX + centreX, innerEndY + centreY, 0f);


            // Draw outer arc

            y = outerRadius * sinHalfPi[segment + 3];
            x = outerRadius * sinHalfPi[segment];

            for (int i = 0; i < outerSegments - 1; ++i) {
                if (caching) { cache[j++] = x; cache[j++] = y; }
                GL11.glVertex3f(x + centreX, y + centreY, 0f);
                float tempX = x;
                x = cosOuterDTheta * tempX - sinOuterDTheta * y;
                y = sinOuterDTheta * tempX + cosOuterDTheta * y;
            }

            if (caching) { cache[j++] = x; cache[j++] = y; }
            GL11.glVertex3f(x + centreX, y + centreY, 0f);

            y = outerRadius * sinHalfPi[segment];
            x = outerRadius * sinHalfPi[segment + 1];
            if (caching) { cache[j++] = x; cache[j++] = y; }
            GL11.glVertex3f(x + centreX, y + centreY, 0f);

            if (caching) { cache[j++] = innerStartX; cache[j] = innerStartY; }
            GL11.glVertex3f(innerStartX + centreX, innerStartY + centreY, 0f);

            GL11.glEnd();
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }

        // Map 0->1 to 0->1
        static float transitionSpeedFunction(float transitionAmount) {
            // Linear: Radius increases at a linear rate
            // return transitionAmount;

            // Linear in area: Area increases at a linear rate
            return MathHelper.sqrt(transitionAmount);

            // Quadratic: Slow increase at first then very fast
            // return transitionAmount * transitionAmount;
        }

        float getAlpha() {
            return 0.5f + transitionAmount * 0.25f;
        }

        void updateRadii() {
            float multiplier = transitionSpeedFunction(transitionAmount);
            innerRadius = inactiveInnerRadius + multiplier * (activeInnerRadius - inactiveInnerRadius);
            outerRadius = inactiveOuterRadius + multiplier * (activeOuterRadius - inactiveOuterRadius);
        }
    }

    private static boolean canStart() {
        return ModCore.mc().currentScreen == null && PlayerInfo.getPlayerInfo().getCurrentClass() != ClassType.NONE;
    }

    private static long waitingTime = Long.MAX_VALUE;
    private static final long maxWait = 1000;  // 1 second

    public static void displayOnClick() {
        Mouse.poll();
        if (Mouse.isButtonDown(0)) {
            if (canStart()) {
                ModCore.mc().displayGuiScreen(new GuiQuickCast());
            }
            waitingTime = Long.MAX_VALUE;
        } else {
            waitingTime = System.currentTimeMillis() + maxWait;
        }
        closeTime = Long.MAX_VALUE;
    }

    public static void handleClick(MouseEvent e) {
        if (closeTime != Long.MAX_VALUE) {
            if (closeTime < System.currentTimeMillis()) {
                closeTime = Long.MAX_VALUE;
                return;
            }
            if (e.getButton() == -1) {
                e.setCanceled(true);
            }
            return;
        }
        if (waitingTime == Long.MAX_VALUE) return;
        if (waitingTime < System.currentTimeMillis()) {
            waitingTime = Long.MAX_VALUE;
            return;
        }
        if (e.getButton() == 0 && e.isButtonstate()) {
            if (canStart()) {
                ModCore.mc().displayGuiScreen(new GuiQuickCast());
            }
            waitingTime = Long.MAX_VALUE;
        }
    }
}

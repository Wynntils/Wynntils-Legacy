package com.wynndevs.modules.expansion.misc;

import com.wynndevs.modules.expansion.ExpReference;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * A GuiScreen replacement that supports putting tooltips onto GuiButtons.
 */
public abstract class GuiScreenMod extends GuiScreen
{
    /** Show a white "?" in the top right part of any button with a tooltip assigned to it */
    public static boolean ShowTooltipButtonEffect = false;

    /** Show an aqua "?" in the top right part of any button with a tooltip assigned to it when mouseovered */
    public static boolean ShowTooltipButtonMouseoverEffect = false;

    /** Putting this string into a tooltip will cause a line break */
    public String tooltipNewlineDelimeter = "/n";

    /** The amount of time in milliseconds until a tooltip is rendered */
    public long tooltipDelay = 0;

    /** The maximum width in pixels a tooltip can occupy before word wrapping occurs */
    public int tooltipMaxWidth = 700;

    protected int tooltipXOffset = 2;
    protected int tooltipYOffset = 5;

    private final static int LINE_HEIGHT = 11;

    private long mouseoverTime = 0;
    private long prevSystemTime = -1;

    private boolean returningGUI = false;
    //private boolean infoGUI = false;

    public void drawScreen(int mouseX, int mouseY, float f)
    {
        super.drawScreen(mouseX, mouseY, f);

        DrawTooltipScreen(mouseX, mouseY);
    }


    public void addReturnButton(boolean back){
        if(back)
            this.buttonList.add(new GuiButton(200, width / 2 - 100, height - 30, "Back"));
        if(!back)
            this.buttonList.add(new GuiButton(200, width / 2 - 100, height - 30, "Done"));

        returningGUI = true;
    }
    public void addInfo(){
        this.buttonList.add(new GuiButton(201,0,0,20,20,"?"));
        //infoGUI = true;
    }
    public void returnButton(int buttonId,GuiScreen returnToGuiScreen){
        if(buttonId == 200 && this.returningGUI){
            mc.displayGuiScreen(returnToGuiScreen);
        }
    }

    public void drawDefault(String menu)
    {
        drawDefaultBackground();
        GL11.glColor4f(1F, 1F, 0F, 1F);
        ///
        drawCenteredString(mc.fontRenderer, "ElytraMC Enhancer Mod", width / 2, 30, 0xFF6600);
        drawCenteredString(mc.fontRenderer, "by SHsuperCM", width / 2, 40, 0xFF6600);
        drawCenteredString(mc.fontRenderer, menu, width / 2, 60, 0xffffffff);
    }

    /**
     * This method must be overriden. Gets a tooltip String for a specific button.
     * Recommended to use a switch/case statement for buttonId for easy implementation.
     * @param buttonId The ID of the button this tooltip corresponds to
     * @return The tooltip string for the specified buttonId. null if no tooltip exists for this button.
     */
    protected abstract String GetButtonTooltip(int buttonId);

    /**
     * Gets a protected/private field from a class using reflection.
     *
     * @param <T>           The return type of the field you are getting
     * @param <E>           The class the field is in
     * @param classToAccess The ".class" of the class the field is in
     * @param instance      The instance of the class
     * @param fieldNames    comma seperated names the field may have (i.e. obfuscated, non obfuscated).
     *                      Obfustated field names can be found in fml/conf/fields.csv
     * @return
     */
    public static <T, E> T GetFieldByReflection(Class <? super E> classToAccess, E instance, String... fieldNames){
        Field field = null;
        for (String fieldName : fieldNames) {
            try {
                field = classToAccess.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
            }

            if (field != null) break;
        }

        if (field != null) {
            field.setAccessible(true);
            T fieldT = null;
            try {
                fieldT = (T) field.get(instance);
            } catch (IllegalArgumentException | IllegalAccessException ignored) {
            }

            return fieldT;
        }

        return null;
    }

    /**
     * Determines if a GuiButton is being mouseovered.
     *
     * @param mouseX
     * @param mouseY
     * @param button
     * @return true if this button is mouseovered
     */
    protected boolean IsButtonMouseovered(int mouseX, int mouseY, GuiButton button){
        if (mouseX >= button.x && mouseX <= button.x + button.getButtonWidth() && mouseY >= button.y && button.visible) {
            //for some god-forsaken reason they made GuiButton.getButtonWidth() public but not height,
            //so use reflection to grab it
            int buttonHeight = GetFieldByReflection(GuiButton.class, button, "height", "field_146121_g");
            return mouseY <= button.y + buttonHeight;
        }
        return false;
    }

    /**
     * Renders any special effects applied to tooltip buttons, and renders any tooltips for GuiButtons
     * that are being mouseovered.
     * @param mouseX
     * @param mouseY
     */
    protected void DrawTooltipScreen(int mouseX, int mouseY)
    {
        if(ShowTooltipButtonEffect)
            RenderTooltipButtonEffect();

        int mousedOverButtonId = -1;

        //find out which button is being mouseovered
        for (GuiButton aButtonList : buttonList) {

            if (IsButtonMouseovered(mouseX, mouseY, aButtonList)) {
                mousedOverButtonId = aButtonList.id;

                if (ShowTooltipButtonMouseoverEffect && GetButtonTooltip(mousedOverButtonId) != null)
                    RenderTooltipButtonMouseoverEffect(aButtonList);

                break;
            }
        }

        //calculate how long this button has been mouseovered for
        if(mousedOverButtonId > -1)
        {
            long systemTime = System.currentTimeMillis();

            if(prevSystemTime > 0)
                mouseoverTime += systemTime - prevSystemTime;

            prevSystemTime = systemTime;
        }
        else
        {
            mouseoverTime = 0;
        }

        //render the button's tooltip
        if(mouseoverTime > tooltipDelay)
        {
            String tooltip = GetButtonTooltip(mousedOverButtonId);
            if(tooltip != null)
            {
                RenderTooltip(mouseX, mouseY, tooltip);
            }
        }
    }

    /**
     * Render anything special onto buttons that have tooltips assigned to them when they are mousevered.
     * @param button
     */
    protected void RenderTooltipButtonMouseoverEffect(GuiButton button)
    {
        boolean flag = mc.fontRenderer.getUnicodeFlag();
        mc.fontRenderer.setUnicodeFlag(true);
        if(button.visible)
            mc.fontRenderer.drawStringWithShadow(FontCodes.AQUA + "?", button.x+button.getButtonWidth()-5, button.y, 0xFFFFFF);
        mc.fontRenderer.setUnicodeFlag(flag);
    }

    /**
     * Renders a tooltip at (x,y).
     * @param x
     * @param y
     * @param tooltip
     */
    protected void RenderTooltip(int x, int y, String tooltip)
    {
        String[] tooltipArray = ParseTooltipArrayFromString(tooltip);

        int tooltipWidth = GetTooltipWidth(tooltipArray);
        int tooltipHeight = GetTooltipHeight(tooltipArray);

        int tooltipX = x + tooltipXOffset;
        int tooltipY = y + tooltipYOffset;

        if(tooltipX > width - tooltipWidth - 7)
            tooltipX = width - tooltipWidth - 7;
        if(tooltipY > height -  tooltipHeight - 7)
            tooltipY = height -  tooltipHeight - 7;
        
        //render the background inside box
        int innerAlpha = -0xFEFFFF0;  //very very dark purple
        drawGradientRect(tooltipX, tooltipY - 1, tooltipX + tooltipWidth + 6, tooltipY, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX, tooltipY + tooltipHeight + 6, tooltipX + tooltipWidth + 6, tooltipY + tooltipHeight + 7, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX, tooltipY, tooltipX + tooltipWidth + 6, tooltipY + tooltipHeight + 6, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX - 1, tooltipY, tooltipX, tooltipY + tooltipHeight + 6, innerAlpha, innerAlpha);
        drawGradientRect(tooltipX + tooltipWidth + 6, tooltipY, tooltipX + tooltipWidth + 7, tooltipY + tooltipHeight + 6, innerAlpha, innerAlpha);

        //render the background outside box
        int outerAlpha1 = 0x505000FF;
        int outerAlpha2 = (outerAlpha1 & 0xFEFEFE) >> 1 | outerAlpha1 & -0x1000000;
        drawGradientRect(tooltipX, tooltipY + 1, tooltipX + 1, tooltipY + tooltipHeight + 5, outerAlpha1, outerAlpha2);
        drawGradientRect(tooltipX + tooltipWidth + 5, tooltipY + 1, tooltipX + tooltipWidth + 6, tooltipY + tooltipHeight + 5, outerAlpha1, outerAlpha2);
        drawGradientRect(tooltipX, tooltipY, tooltipX + tooltipWidth + 6, tooltipY + 1, outerAlpha1, outerAlpha1);
        drawGradientRect(tooltipX, tooltipY + tooltipHeight + 5, tooltipX + tooltipWidth + 6, tooltipY + tooltipHeight + 6, outerAlpha2, outerAlpha2);

        //render the foreground text
		this.itemRender.zLevel = 200.0F;
        int lineCount = 0;
        for (String s : tooltipArray)
        {
			this.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(Items.BOWL), tooltipX - 14 + (ExpReference.getMsgLength(s, 1.0f)), tooltipY - 6 + (lineCount * LINE_HEIGHT), s);
            //mc.fontRenderer.drawStringWithShadow(s, tooltipX + 3, tooltipY + 3 + lineCount * LINE_HEIGHT, 0xFFFFFF);
            lineCount++;
        }
        this.itemRender.zLevel = 0.0F;

    }

    /**
     * Render anything special onto all buttons that have tooltips assigned to them.
     */
    protected void RenderTooltipButtonEffect(){
        for (GuiButton aButtonList : buttonList) {

            if (GetButtonTooltip(aButtonList.id) != null) {
                boolean flag = mc.fontRenderer.getUnicodeFlag();
                mc.fontRenderer.setUnicodeFlag(true);
                if (aButtonList.visible)
                    mc.fontRenderer.drawStringWithShadow("?", aButtonList.x + aButtonList.getButtonWidth() - 5, aButtonList.y, 0x99FFFFFF);
                mc.fontRenderer.setUnicodeFlag(flag);
            }
        }
    }

    /**
     * Decodes any font codes into something useable by the FontRenderer.
     * @param s E.x.: "Hello,_nI am your _ltooltip_r and you love me."
     * @return E.x. output (html not included): <br>"Hello,<br>I am your <b>tooltip</b> and you love me."
     */
    public String StringCode(String s)
    {
        return s.replace("&0", FontCodes.BLACK)
                .replace("&1", FontCodes.DARK_BLUE)
                .replace("&2", FontCodes.DARK_GREEN)
                .replace("&3", FontCodes.DARK_AQUA)
                .replace("&4", FontCodes.DARK_RED)
                .replace("&5", FontCodes.DARK_PURPLE)
                .replace("&6", FontCodes.GOLD)
                .replace("&7", FontCodes.GRAY)
                .replace("&8", FontCodes.DARK_GREY)
                .replace("&9", FontCodes.BLUE)
                .replace("&a", FontCodes.GREEN)
                .replace("&b", FontCodes.AQUA)
                .replace("&c", FontCodes.RED)
                .replace("&d", FontCodes.LIGHT_PURPLE)
                .replace("&e", FontCodes.YELLOW)
                .replace("&f", FontCodes.WHITE)
                .replace("&k", FontCodes.OBFUSCATED)
                .replace("&l", FontCodes.BOLD)
                .replace("&m", FontCodes.STRIKETHROUGH)
                .replace("&n", FontCodes.UNDERLINE)
                .replace("&o", FontCodes.ITALICS)
                .replace("&r", FontCodes.RESET);
    }

    /***
     * Gets the width of the tooltip in pixels.
     * @param tooltipArray
     * @return
     */
	public int GetTooltipWidth(String[] tooltipArray)
    {
        int longestWidth = 0;
        for(String s : tooltipArray)
        {
            int width = mc.fontRenderer.getStringWidth(s);
            if(width > longestWidth)
                longestWidth = width;
        }
        return longestWidth;
    }

    /**
     * Gets the height of the tooltip in pixels.
     * @param tooltipArray
     * @return
     */
    public int GetTooltipHeight(String[] tooltipArray)
    {
        int tooltipHeight = mc.fontRenderer.FONT_HEIGHT - 2;
        if (tooltipArray.length > 1)
        {
            tooltipHeight += (tooltipArray.length - 1) * LINE_HEIGHT;
        }
        return tooltipHeight;
    }

    /**
     * Converts a String representation of a tooltip into a String[], and also decodes any font codes used.
     * @param s Ex: "Hello,_nI am your _ltooltip_r and you love me."
     * @return An array of Strings such that each String width does not exceed tooltipMaxWidth
     */
    protected String[] ParseTooltipArrayFromString(String s){
        s = StringCode(s);
        String[] tooltipSections = s.split(tooltipNewlineDelimeter);
        ArrayList <String> tooltipArrayList = new ArrayList<String>();

        for (String section : tooltipSections) {
            StringBuilder tooltip = new StringBuilder();
            String[] tooltipWords = section.split(" ");

            for (String tooltipWord : tooltipWords) {
                int lineWidthWithNextWord = mc.fontRenderer.getStringWidth(tooltip + tooltipWord);
                if (lineWidthWithNextWord > tooltipMaxWidth) {
                    tooltipArrayList.add(tooltip.toString().trim());
                    tooltip = new StringBuilder(tooltipWord + " ");
                } else {
                    tooltip.append(tooltipWord).append(" ");
                }
            }

            tooltipArrayList.add(tooltip.toString().trim());
        }

        String[] tooltipArray = new String[tooltipArrayList.size()];
        tooltipArrayList.toArray(tooltipArray);

        return tooltipArray;
    }

    // Shadow
    public void drawString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawString(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
    public void drawCenteredString(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawCenteredString(fontRendererIn,text,(int)Math.floor(x / size),(int)Math.floor(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
    
    public void drawSplitString(FontRenderer fontRenderer, String str, int x, int y, int wrapWidth, float size, float padding, int textColor)
    {
        //GL11.glScalef(size,size,size);
        //float mSize = (float)Math.pow(size,-1);
        
        int i = 0;
        for (String string:fontRenderer.listFormattedStringToWidth(str,wrapWidth)) {
            drawString(fontRenderer,string,x,y + Math.round(i * size * fontRenderer.FONT_HEIGHT * padding),size,textColor);
            i++;
        }
    
        //GL11.glScalef(mSize,mSize,mSize);
    }
    
    
    // Without Shadow
    public void drawStringPlain(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawStringPlain(fontRendererIn,text,Math.round(x / size),Math.round(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }
    public void drawCenteredStringPlain(FontRenderer fontRendererIn, String text, int x, int y, float size, int color) {
        GL11.glScalef(size,size,size);
        float mSize = (float)Math.pow(size,-1);
        this.drawCenteredStringPlain(fontRendererIn,text,(int)Math.floor(x / size),(int)Math.floor(y / size),color);
        GL11.glScalef(mSize,mSize,mSize);
    }

    public void drawSplitStringPlain(FontRenderer fontRenderer, String str, int x, int y, int wrapWidth, float size, float padding, int textColor)
    {
        //GL11.glScalef(size,size,size);
        //float mSize = (float)Math.pow(size,-1);
        
        int i = 0;
        for (String string:fontRenderer.listFormattedStringToWidth(str,wrapWidth)) {
            drawStringPlain(fontRenderer,string,x,y + Math.round(i * size * fontRenderer.FONT_HEIGHT * padding),size,textColor);
            i++;
        }
        
        //GL11.glScalef(mSize,mSize,mSize);
    }
    
    
    // Without Shadow Renderer
    public void drawCenteredStringPlain(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
    	this.fontRenderer.drawString(text, (x - fontRendererIn.getStringWidth(text) / 2), y, color);
    }
    
    public void drawStringPlain(FontRenderer fontRendererIn, String text, int x, int y, int color)
    {
    	this.fontRenderer.drawString(text, x, y, color);
    }

    public class FontCodes
    {
        //color codes for rendered strings
        public static final String BLACK = "\2470";
        public static final String DARK_BLUE = "\2471";
        public static final String DARK_GREEN = "\2472";
        public static final String DARK_AQUA = "\2473";
        public static final String DARK_RED = "\2474";
        public static final String DARK_PURPLE = "\2475";
        public static final String GOLD = "\2476";
        public static final String GRAY = "\2477";
        public static final String DARK_GREY = "\2478";
        public static final String BLUE = "\2479";
        public static final String GREEN = "\247a";
        public static final String AQUA = "\247b";
        public static final String RED = "\247c";
        public static final String LIGHT_PURPLE = "\247d";
        public static final String YELLOW = "\247e";
        public static final String WHITE = "\247f";

        //font styles
        public static final String OBFUSCATED = "\247k";
        public static final String BOLD = "\247l";
        public static final String STRIKETHROUGH = "\247m";
        public static final String UNDERLINE = "\247n";
        public static final String ITALICS = "\247o";

        public static final String RESET = "\247r";
    }
}
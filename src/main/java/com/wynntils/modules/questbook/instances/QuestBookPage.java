package com.wynntils.modules.questbook.instances;

import com.wynntils.Reference;
import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.SmartFontRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.core.framework.rendering.textures.Textures;
import com.wynntils.core.utils.Easing;
import com.wynntils.modules.core.config.CoreDBConfig;
import com.wynntils.modules.core.enums.UpdateStream;
import com.wynntils.modules.questbook.configs.QuestBookConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;

public class QuestBookPage extends GuiScreen {

    protected final ScreenRenderer render = new ScreenRenderer();
    private long time;

    //Page specific information
    private String title = "";
    private IconContainer icon;
    protected boolean requestOpening;

    private boolean showSearchBar;
    private String searchBarText;
    private boolean searchBarFocused;
    protected int currentPage;
    protected boolean acceptNext, acceptBack;
    protected int selected;

    //Animation
    protected long lastTick;
    protected boolean animationCompleted;

    private boolean keepForTime;
    private long text_flicker;
    private long delay = Minecraft.getSystemTime();

    //Colours
    protected static final CustomColor background_1 = CustomColor.fromString("000000", 0.3f);
    protected static final CustomColor background_2 = CustomColor.fromString("000000", 0.2f);
    protected static final CustomColor background_3 = CustomColor.fromString("00ff00", 0.3f);
    protected static final CustomColor background_4 = CustomColor.fromString("008f00", 0.2f);
    protected static final CustomColor unselected_cube = new CustomColor(0, 0, 0, 0.2f);
    protected static final CustomColor selected_cube = new CustomColor(0, 0, 0, 0.3f);
    protected static final CustomColor selected_cube_2 = CustomColor.fromString("#adf8b3", 0.3f);

    /**
     * Base class for all questbook pages
     * @param title a string displayed on the left page
     * @param showSearchBar boolean of whether there is a searchbar needed for that page
     * @param icon the icon that corresponds to the page
     */
    public QuestBookPage(String title, boolean showSearchBar, IconContainer icon) {
        this.title = title;
        this.showSearchBar = showSearchBar;
        this.icon = icon;
    }

    /**
     * Resets all basic information needed for various features on all pages
     */
    @Override
    public void initGui() {
        currentPage = 1;
        selected = 0;
        searchBarText = "";
        searchUpdate("");
        time = Minecraft.getSystemTime();
        text_flicker = Minecraft.getSystemTime();
        lastTick = Minecraft.getMinecraft().world.getWorldTime();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        int x = width / 2;
        int y = height / 2;

        ScreenRenderer.beginGL(0,0);
        {
            if (requestOpening) {
                float animationTick = Easing.BACK_IN.ease((Minecraft.getSystemTime() - time) + 1000, 1f, 1f, 600f);
                animationTick /= 10f;

                if (animationTick <= 1) {
                    ScreenRenderer.scale(animationTick);

                    x = (int) (x / animationTick);
                    y = (int) (y / animationTick);
                } else {
                    ScreenRenderer.resetScale();
                    requestOpening = false;
                }

            } else {
                x = width / 2;
                y = height / 2;
            }

            render.drawRect(Textures.UIs.quest_book, x - (339 / 2), y - (220 / 2), 0, 0, 339, 220);

            ScreenRenderer.scale(0.7f);
            render.drawString(CoreDBConfig.INSTANCE.updateStream == UpdateStream.STABLE ? "Stable v" + Reference.VERSION : "CE Build " + (Reference.BUILD_NUMBER == -1 ? "?" : Reference.BUILD_NUMBER), (x - 80) / 0.7f, (y + 86) / 0.7f, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.MIDDLE, SmartFontRenderer.TextShadow.NORMAL);
            ScreenRenderer.resetScale();

            render.drawRect(Textures.UIs.quest_book, x - 168, y - 81, 34, 222, 168, 33);

            ScreenRenderer.scale(2f);
            render.drawString(title, (x - 158f) / 2, (y - 74) / 2, CommonColors.YELLOW, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
            ScreenRenderer.resetScale();

            /*Render search bar when needed*/
            if (showSearchBar) {
                render.drawRect(Textures.UIs.quest_book, x + 13, y - 109, 52, 255, 133, 23);
                //searchBar
                if (searchBarText.length() <= 0 && !QuestBookConfig.INSTANCE.searchBoxClickRequired) {
                    render.drawString("Type to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else if (searchBarText.length() <= 0 && !searchBarFocused) {
                    render.drawString("Click to search", x + 32, y - 97, CommonColors.LIGHT_GRAY, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                } else {

                    String text = searchBarText;

                    if (render.getStringWidth(text) >= 110) {
                        int remove = searchBarText.length();
                        while (render.getStringWidth((text = searchBarText.substring(searchBarText.length() - remove))) >= 110) {
                            remove -= 1;
                        }
                    }

                    if (Minecraft.getSystemTime() - text_flicker >= 500) {
                        keepForTime = !keepForTime;
                        text_flicker = Minecraft.getSystemTime();
                    }

                    if (keepForTime && (searchBarFocused || !QuestBookConfig.INSTANCE.searchBoxClickRequired)) {
                        render.drawString(text + "_", x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    } else {
                        render.drawString(text, x + 32, y - 97, CommonColors.WHITE, SmartFontRenderer.TextAlignment.LEFT_RIGHT, SmartFontRenderer.TextShadow.NONE);
                    }
                }
            }
        }
        ScreenRenderer.endGL();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());

        int posX = ((res.getScaledWidth()/2) - mouseX); int posY = ((res.getScaledHeight()/2) - mouseY);

        if (showSearchBar) {
            if (posX >= -145 && posX <= -13 && posY >= 86 && posY <= 100) {
                searchBarFocused = true;
                if (mouseButton == 1) {
                    searchBarText = "";
                    searchUpdate(searchBarText);
                }
            } else {
                searchBarFocused = false;
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        int mDwehll = Mouse.getEventDWheel() * CoreDBConfig.INSTANCE.scrollDirection.getScrollDirection();

        if (mDwehll <= -1 && (Minecraft.getSystemTime() - delay >= 100)) {
            if (acceptNext) {
                delay = Minecraft.getSystemTime();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage++;
            }
        } else if(mDwehll >= 1 && (Minecraft.getSystemTime() - delay >= 100)) {
            if (acceptBack) {
                delay = Minecraft.getSystemTime();
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1f));
                currentPage--;
            }
        }
        super.handleMouseInput();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_LSHIFT || keyCode == Keyboard.KEY_RSHIFT || keyCode == Keyboard.KEY_LCONTROL || keyCode == Keyboard.KEY_RCONTROL) return;
        if (!QuestBookConfig.INSTANCE.searchBoxClickRequired || searchBarFocused) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (searchBarText.length() <= 0) {
                    return;
                }

                if (Keyboard.isKeyDown(Keyboard.KEY_RCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                    searchBarText = "";
                else searchBarText = searchBarText.substring(0, searchBarText.length() - 1);

                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, 1f));
                text_flicker = System.currentTimeMillis();
                keepForTime = false;
            } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
                searchBarText = searchBarText + typedChar;
                Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.BLOCK_NOTE_HAT, 1f));
                text_flicker = System.currentTimeMillis();
                keepForTime = true;
            }
            currentPage = 1;
            searchUpdate(searchBarText);
        }
        super.keyTyped(typedChar, keyCode);
    }

    protected void renderHoveredText(List<String> hoveredText, int mouseX, int mouseY) {
        ScreenRenderer.beginGL(0, 0);
        {
            GlStateManager.disableLighting();
            if(hoveredText != null) drawHoveringText(hoveredText, mouseX, mouseY);
        }
        ScreenRenderer.endGL();
    }

    protected void searchUpdate(String currentText) { }

    protected boolean doesSearchMatch(String toCheck, String searchText) {
        if (QuestBookConfig.INSTANCE.useFuzzySearch) {
            int i = 0, j = 0;
            char[] toCheckArray = toCheck.toCharArray();
            for (char c : searchText.toCharArray()) {
                for (; i < toCheck.length(); ) {
                    if (c == toCheckArray[i]) {
                        i++;
                        j++;
                        break;
                    }
                    i++;
                }
            }
            return j == searchText.length();
        } else {
            return toCheck.contains(searchText);
        }
    }

    public void open(boolean requestOpening) {
        this.requestOpening = requestOpening;
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    public void updateSearch() {
        searchUpdate(searchBarText);
    }

    public IconContainer getIcon() {
        return icon;
    }

    /**
     * Can be null
     * @return a list of strings - each index representing a new line.
     */
    public List<String> getHoveredDescription() { return null; }
}

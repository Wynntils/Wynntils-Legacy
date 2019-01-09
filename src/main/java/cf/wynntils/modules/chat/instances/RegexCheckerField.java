package cf.wynntils.modules.chat.instances;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;

import java.util.regex.Pattern;

public class RegexCheckerField extends GuiTextField {

    public RegexCheckerField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
        super(componentId, fontrendererObj, x, y, par5Width, par6Height);
    }

    boolean validRegex = false;

    @Override
    public void writeText(String textToWrite) {
        super.writeText(textToWrite);

        try{
            Pattern.compile(getText());
            validRegex = true;
        }catch (Exception ex) { }
    }

    @Override
    public void drawTextBox() {
        if(validRegex) GlStateManager.color(0, 1, 0);
        else GlStateManager.color(1, 0, 0);

        super.drawTextBox();
    }
}

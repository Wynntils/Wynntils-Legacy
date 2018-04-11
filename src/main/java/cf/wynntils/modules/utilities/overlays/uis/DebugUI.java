package cf.wynntils.modules.utilities.overlays.uis;

import cf.wynntils.core.framework.rendering.ScreenRenderer;
import cf.wynntils.core.framework.rendering.colors.CommonColors;
import cf.wynntils.core.framework.rendering.textures.Textures;
import cf.wynntils.core.framework.ui.UI;
import cf.wynntils.core.framework.ui.UIElement;
import cf.wynntils.core.framework.ui.elements.*;

import java.util.List;

public class DebugUI extends UI {
    public UIEButton buttonA = new UIEButton("this is a pretty interesting text",Textures.UIs.button_a,0.5f,0.5f,5,-30,-20,true,(ui, mouseButton) -> {
        ((DebugUI)ui).buttonA.active = false;
        ((DebugUI)ui).buttonB.active = true;
        ((DebugUI)ui).slider.active = false;
        ((DebugUI)ui).textBoxTing.active = false;
    });

    public UIEButton buttonB = new UIEButton("this is also an interesting text",Textures.UIs.button_b,0.5f,0.5f,5,-10,-10,false,(ui, mouseButton) -> {
        ((DebugUI)ui).buttonA.active = true;
        ((DebugUI)ui).buttonB.active = false;
        ((DebugUI)ui).slider.active = true;
        ((DebugUI)ui).textBoxTing.active = true;
    });

    public UIETextBox textBoxTing = new UIETextBox(0.5f,0.5f,5,25,150,true,"Write Text Here",true, (ui, s) -> {});

    public UIESlider slider = new UIESlider.Horizontal(CommonColors.GRAY, Textures.UIs.button_b,0.5f,0.5f,5,50,150,true,30f,26f,0.5f,0.2f,null);


    public int button_amount = 100;

    public UIEList buttons = new UIEList(0.5f,0.5f,-70,-100);
    public UIESlider vslider = new UIESlider.Vertical(CommonColors.CYAN, Textures.UIs.button_b,0.5f,0.5f,-30,-100, 200, true, -100f,100-(button_amount*(20)), 1f, 0f,null);

    public UIEButton.Toggle toggleButton = new UIEButton.Toggle("Dis Buddon: On",Textures.UIs.button_a,"Dis Buddon: Off", Textures.UIs.button_a, true, 0.5f, 0.5f, 10,-100,-10,true,null);
    public UIEButton.Enum enumButton = new UIEButton.Enum(ds -> "val: " + ds,Textures.UIs.button_b,TestEnum.class,TestEnum.dis_b,0.5f,0.5f,15,-80,-10,true,null);

    @Override
    public void onInit() {
        for(int i = 0; i < button_amount; i++)
            buttons.add(new UIEButton("lol"+i,Textures.UIs.button_a,0f,0f,0,i*(15+5), 35,true,(ui, mouseButton) -> {
                System.out.println("buddon bressed");
            }));

        buttons.elements.forEach((uie) -> {uie.visible = false;});
    }

    @Override
    public void onClose() {

    }

    @Override
    public void onTick() {

    }

    @Override
    public void onRenderPreUIE(ScreenRenderer render) {
        drawDefaultBackground();
        CommonUIFeatures.drawBook();
        render.drawString(this.slider.progress + "",screenWidth/2+5,screenHeight/2+75, CommonColors.WHITE);
        render.drawString(" -> " + this.slider.getValue(),screenWidth/2+5,screenHeight/2+86, CommonColors.WHITE);

        buttons.position.offsetY = (int)vslider.getValue();

        ScreenRenderer.createMask(Textures.Masks.full,screenWidth/2-70,screenHeight/2-100,screenWidth/2-35,screenHeight/2+100);
        buttons.elements.forEach((uie) -> {
            int oy = uie.position.offsetY+buttons.position.offsetY;
            if(((UIEClickZone)uie).active = oy >= -115 && oy <= 100)
                uie.render(mouseX,mouseY);
        });
        ScreenRenderer.clearMask();
    }

    @Override
    public void onRenderPostUIE(ScreenRenderer render) {

    }

    @Override
    public void onWindowUpdate() {

    }

    public enum TestEnum {
        dis_a("DIS A"),
        dis_b("DAT B"),
        dis_c("DEESE C");


        public String displayName;

        TestEnum(String displayName) {
            this.displayName = displayName;
        }
    }
}

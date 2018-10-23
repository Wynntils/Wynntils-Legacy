package cf.wynntils.core.utils;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.network.NetworkManager;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public enum ReflectionFields {

    ItemRenderer_itemRenderer(ItemRenderer.class, "itemRenderer", "field_178112_h"),
    NetworkManager_packetListener(NetworkManager.class, "packetListener", "field_150744_m", "field_150744_m"),
    NetHandlerPlayClient_guiScreenServer(NetHandlerPlayClient.class, "guiScreenServer", "field_147307_j"),
    NetHandlerPlayClient_profile(NetHandlerPlayClient.class, "profile", "field_175107_d"),
    GuiNewChat_chatLines(GuiNewChat.class, "chatLines", "field_146252_h"),
    ChatLine_lineString(ChatLine.class, "lineString", "field_74541_b"),
    GuiChest_lowerChestInventory(GuiChest.class, "lowerChestInventory", "field_147015_w");

    Field field;

    ReflectionFields(Class<?> holdingClass, String... values) {
        this.field = ReflectionHelper.findField(holdingClass, values);
        this.field.setAccessible(true);
    }

    public Object getValue(Object parent) {
        try{
            return field.get(parent);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setValue(Object parent, Object value) {
        try{
            field.set(parent,value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //todo more shit here that is not ghetto lazy code
}

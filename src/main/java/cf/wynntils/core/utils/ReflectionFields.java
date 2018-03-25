package cf.wynntils.core.utils;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.reflect.Field;

public enum ReflectionFields {

    ItemRenderer_itemRenderer(ItemRenderer.class,"itemRenderer", "field_178112_h");

    Field field;

    ReflectionFields(Class<?> holdingClass, String deobfName, String srgName) {
        this.field = ReflectionHelper.findField(holdingClass,deobfName,srgName);
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
    //todo more shit here that is not ghetto lazy code
}

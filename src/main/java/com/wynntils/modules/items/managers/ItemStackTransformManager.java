package com.wynntils.modules.items.managers;

import com.wynntils.McIf;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ItemStackTransformManager {
    private static final List<ConditionalTransformer<Entity>> entityTransformers = new ArrayList<>();
    private static final List<ConditionalTransformer<GuiScreen>> guiTransformers = new ArrayList<>();
    private static final List<ItemTransformer> inventoryTransformers = new ArrayList<>();
    private static final List<ItemTransformer> globalTransformers = new ArrayList<>();

    public static ItemStack guiTransform(ItemStack stack, int id) {
        stack = globalTransform(stack);

        if (id <= 0) {
            for (ItemTransformer transformer : inventoryTransformers) {
                stack = transformer.transform(stack);
            }
        } else if (id == McIf.player().openContainer.windowId) { //This seems to be the only other handled case
            GuiScreen openGUI = McIf.mc().currentScreen;
            for (ConditionalTransformer<GuiScreen> transformer : guiTransformers) {
                stack = transformer.transform(stack, openGUI);
            }
        }

        return stack;
    }


    public static ItemStack entityTransform(ItemStack stack, int id) {
        stack = globalTransform(stack);

        Entity entity = McIf.world().getEntityByID(id);

        if (entity != null) {
            for (ConditionalTransformer<Entity> transformer : entityTransformers) {
                stack = transformer.transform(stack, entity);
            }
        }

        return stack;
    }

    public static ItemStack globalTransform(ItemStack stack) {
        for (ItemTransformer transformer : globalTransformers) {
            stack = transformer.transform(stack);
        }

        return stack;
    }

    public static void registerEntityTransform(ConditionalTransformer<Entity> transform) {
        entityTransformers.add(transform);
    }

    public static void registerGuiTransform(ConditionalTransformer<GuiScreen> transform) {
        guiTransformers.add(transform);
    }

    public static void registerInventoryTransform(ItemTransformer transform) {
        inventoryTransformers.add(transform);
    }

    public static void registerGlobalTransform(ItemTransformer transform) {
        globalTransformers.add(transform);
    }

    @FunctionalInterface
    public interface ItemTransformer {
        ItemStack transform(ItemStack stack);
    }

    public static class ConditionalTransformer<T> {
        ItemTransformer transformer;
        Predicate<T> shouldTransform;

        public ConditionalTransformer(ItemTransformer transformer, Predicate<T> shouldTransform) {
            this.transformer = transformer;
            this.shouldTransform = shouldTransform;
        }

        public ItemStack transform(ItemStack stack, T input) {
            return shouldTransform.test(input) ? transformer.transform(stack) : stack;
        }
    }

    public static class ItemConsumer implements ItemTransformer {
        Consumer<ItemStack> consumer;

        public ItemConsumer(Consumer<ItemStack> consumer) {
            this.consumer = consumer;
        }

        @Override
        public ItemStack transform(ItemStack stack) {
            consumer.accept(stack);
            return stack;
        }
    }
}


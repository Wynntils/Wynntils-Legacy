/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.core.utils.helpers;

import com.wynntils.core.utils.StringUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.ClickEvent;

import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Used to create a {@link ClickEvent} that runs a Runnable.
 */
public class TextAction {

    private static final String prefix = "TextAction_" + UUID.randomUUID().toString().replace("-", "");

    public static boolean isCommandPrefix(String s) {
        return prefix.equals(s);
    }

    private static Map<String, Constructor<? extends Runnable>> staticNameMap = new HashMap<>();
    private static Map<String, List<Runnable>> dynamicNameMap = new HashMap<>();

    /**
     * Gets an event that will construct the given class and then call the {@link Runnable#run()} method.
     *
     * @param clazz The class object (that implements Runnable)
     * @return A click event that constructs then runs the class
     */
    public static ClickEvent getStaticEvent(Class<? extends Runnable> clazz) {
        String name = clazz.getName();
        if (!staticNameMap.containsKey(name)) {
            Constructor<? extends Runnable> cons;
            try {
                cons = clazz.getDeclaredConstructor();
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            cons.setAccessible(true);
            staticNameMap.put(name, cons);
        }
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wynntils " + prefix + " static " + name);
    }

    /**
     * Gets an event that will call a given runnable. If the runnable does not depend on a closure or fields,
     * please use {@link #getStaticEvent(Class)}.
     *
     * Note that this *will* leak (but very slowly), since the lifetime of the command string is not trackable.
     *
     * @param action The runnable to be called
     * @return A click event that will call the runnable
     */
    public static ClickEvent getDynamicEvent(Runnable action) {
        String clazz = action.getClass().getName();
        List<Runnable> runnables = dynamicNameMap.computeIfAbsent(clazz, k -> new ArrayList<>());
        int index = runnables.size();
        runnables.add(action);
        return new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/wynntils " + prefix + " dynamic " + clazz + (" " + index));
    }

    public static <T extends ITextComponent> T withStaticEvent(T text, Class<? extends Runnable> clazz) {
        text.getStyle().setClickEvent(getStaticEvent(clazz));
        return text;
    }

    public static <T extends ITextComponent> T withDynamicEvent(T text, Runnable action) {
        text.getStyle().setClickEvent(getDynamicEvent(action));
        return text;
    }

    public static void processCommand(String[] args) {
        if (args == null || args.length == 0 || !isCommandPrefix(args[0])) return;
        if (args.length == 3 && args[1].equals("static") && args[2] != null) {
            Constructor<? extends Runnable> cons = staticNameMap.get(args[2]);
            if (cons == null) return;
            Runnable action;
            try {
                action = cons.newInstance();
            } catch (ReflectiveOperationException err) {
                err.printStackTrace();
                return;
            }
            try {
                action.run();
            } catch (Throwable err) {
                err.printStackTrace();
            }
            return;
        }
        if (args.length == 4 && args[1].equals("dynamic") && args[2] != null && StringUtils.isValidInteger(args[3])) {
            List<Runnable> runnables = dynamicNameMap.get(args[2]);
            if (runnables == null) return;
            int index = Integer.parseInt(args[3]);
            if (0 > index || index >= runnables.size()) return;
            try {
                runnables.get(index).run();
            } catch (Throwable err) {
                err.printStackTrace();
            }
        }
    }

}

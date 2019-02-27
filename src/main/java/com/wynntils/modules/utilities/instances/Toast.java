package com.wynntils.modules.utilities.instances;

import net.minecraft.client.Minecraft;

public class Toast {
    private ToastType type;
    private String title;
    private String[] subtitle;
    private long creationTime;
    private float animated;
    private int height, Y;

    public Toast(ToastType type, String title, String subTitle) {
        this.type = type;
        this.title = title;
        this.subtitle = wrapText(subTitle);

        this.creationTime = Minecraft.getSystemTime();
        this.animated = 160;
    }

    public enum ToastType {
        QUEST_COMPLETED, TERRITORY, DISCOVERY, AREA_DISCOVERED
    }

    public long getCreationTime() {
        return creationTime;
    }

    public ToastType getToastType() {
        return this.type;
    }

    public String getTitle() {
        return this.title;
    }

    public String[] getSubtitle() {
        return this.subtitle;
    }

    public float getAnimated() {
        return this.animated;
    }

    public void setAnimated(float animated) {
        this.animated = animated;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    public int getY() {
        return this.Y;
    }

    public void setY(int y) {
        this.Y = y;
    }

    private String[] wrapText(String s) {
        String[] sa = s.split(" ");
        String result = "";
        int length = 0;

        for (String o: sa) {
            if (length + o.length() >= 24) {
                result += ",";
                length = 0;
            }
            result += o + " ";
            length += o.length();
        }

        return result.split(",");
    }
}

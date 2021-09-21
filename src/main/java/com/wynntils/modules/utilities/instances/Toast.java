/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.utilities.instances;

import com.wynntils.McIf;
import com.wynntils.core.utils.StringUtils;

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
        this.subtitle = StringUtils.wrapText(subTitle, 24);

        this.creationTime = McIf.getSystemTime();
        this.animated = 160;
    }

    public enum ToastType {
        QUEST_COMPLETED, TERRITORY, DISCOVERY, AREA_DISCOVERED, LEVEL_UP
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

}

/*
 *  * Copyright © Wynntils - 2018 - 2021.
 */

package com.wynntils.modules.core.instances;

import com.wynntils.McIf;
import com.wynntils.core.framework.enums.professions.GatheringMaterial;
import com.wynntils.core.framework.enums.professions.ProfessionType;
import net.minecraft.client.Minecraft;

public class GatheringBake {

    ProfessionType type = null;
    GatheringMaterial material = null;

    int materialAmount = 0;
    double xpAmount = 0;
    double xpPercentage = 0;

    long created = McIf.getSystemTime();

    public GatheringBake() { }

    public void setCreated(long created) {
        this.created = created;
    }

    public void setMaterial(GatheringMaterial material) {
        this.material = material;
    }

    public void setMaterialAmount(int materialAmount) {
        this.materialAmount = materialAmount;
    }

    public void setType(ProfessionType type) {
        this.type = type;
    }

    public void setXpAmount(double xpAmount) {
        this.xpAmount = xpAmount;
    }

    public void setXpPercentage(double xpPercentage) {
        this.xpPercentage = xpPercentage;
    }

    public double getXpAmount() {
        return xpAmount;
    }

    public double getXpPercentage() {
        return xpPercentage;
    }

    public GatheringMaterial getMaterial() {
        return material;
    }

    public int getMaterialAmount() {
        return materialAmount;
    }

    public long getCreated() {
        return created;
    }

    public ProfessionType getType() {
        return type;
    }

    public boolean isReady() {
        return type != null && material != null;
    }

    public boolean isInvalid() {
        return McIf.getSystemTime() - created >= 600;
    }

}

package com.wynntils.modules.utilities.overlays.hud;

import java.util.PriorityQueue;

import com.wynntils.Reference;
import com.wynntils.core.framework.enums.ClassType;
import com.wynntils.core.framework.enums.DamageType;
import com.wynntils.core.framework.overlays.Overlay;
import com.wynntils.core.framework.rendering.colors.CustomColor;
import com.wynntils.modules.utilities.configs.OverlayConfig;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;

public class StatsOverlay extends Overlay {
    public StatsOverlay() {
        super("Stats Overlay", 100, 20, true,
                1f, 0f,
                -3, 200,
                OverlayGrowFrom.TOP_RIGHT);
    }

    private static PriorityQueue<DamageEvent> damageEvents = new PriorityQueue<>();
    private static int[] damageTotals = new int[DamageType.values().length];

    public static void reset() {
        damageTotals = new int[DamageType.values().length];
        damageEvents = new PriorityQueue<>();
    }

    public static void recordDamage(DamageType damageType, int amount) {
        if (!OverlayConfig.Stats.INSTANCE.showDPS) return;

        damageEvents.add(new DamageEvent(mc.world.getWorldTime(), damageType, amount));
        damageTotals[damageType.ordinal()] += amount;
    }
    
    private static final int LINE_HEIGHT = 12;
    private static final CustomColor TEXT_COLOR = new CustomColor(1, 1, 1, 1);
    
    @Override
    public void render(Pre event) {
        if (!Reference.onWorld || getPlayerInfo().getCurrentClass() == ClassType.NONE || event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        // calculate height based on the number of shown statistics
        int numShownStats = 0;
        if (OverlayConfig.Stats.INSTANCE.showDPS) numShownStats += damageTotals.length+1;
        staticSize.y = LINE_HEIGHT * numShownStats;

        int lineNum = 0;
        if (OverlayConfig.Stats.INSTANCE.showDPS)
            lineNum += renderDPS(lineNum);
    }
    
    private int renderDPS(int startLine) {
        // remove damage events outside the configured interval
        long worldTime = mc.world.getWorldTime();
        int dpsInterval = OverlayConfig.Stats.INSTANCE.dpsInterval;
        long startOfInterval = worldTime - (dpsInterval * 20);
        while (!damageEvents.isEmpty() && damageEvents.peek().worldTime < startOfInterval) {
            DamageEvent e = damageEvents.remove();
            damageTotals[e.damageType.ordinal()] -= e.amount;
        }
        int summedTotals = 0;
        int i;
        for (i = 0; i < damageTotals.length; ++i) {
            summedTotals += damageTotals[i];
            DamageType type = DamageType.values()[i];
            drawString(
                    type.textFormat+type.symbol+" "+(damageTotals[i] / dpsInterval),
                    -90+(50*(i%2)),
                    LINE_HEIGHT*(1+startLine+(i/2)),
                    TEXT_COLOR);
        }
        drawString(
                "DPS: "+(summedTotals / dpsInterval),
                -100,
                LINE_HEIGHT*startLine,
                TEXT_COLOR);
        return i;
    }

    private static class DamageEvent implements Comparable<DamageEvent> {
        final DamageType damageType;
        final int amount;
        final long worldTime;
        
        private DamageEvent(long worldTime, DamageType damageType, int amount) {
            this.worldTime = worldTime;
            this.damageType = damageType;
            this.amount = amount;
        }

        @Override
        public int compareTo(DamageEvent o) {
            return Long.compare(this.worldTime, o.worldTime);
        }
    }
}

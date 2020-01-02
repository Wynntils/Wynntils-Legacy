/*
 *  * Copyright © Wynntils - 2018 - 2020.
 */

package com.wynntils.modules.map.overlays.objects;

import com.wynntils.core.framework.rendering.ScreenRenderer;
import com.wynntils.core.framework.rendering.colors.CommonColors;
import com.wynntils.modules.map.configs.MapConfig;
import com.wynntils.modules.map.instances.PathWaypointProfile;
import com.wynntils.modules.map.instances.PathWaypointProfile.PathPoint;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapPathWaypointIcon extends MapIcon {
    private static final float pathWidth = 1.5f;
    private static final float outlineWidth = 0.5f;

    private PathWaypointProfile profile;
    private Map<Float, PathSegment[]> pathCache = new HashMap<>();

    public MapPathWaypointIcon(PathWaypointProfile profile) {
        this.profile = profile;
    }

    public PathWaypointProfile getProfile() {
        return profile;
    }

    public void profileChanged() {
        pathCache = new HashMap<>();
    }

    @Override
    public int getPosX() {
        return profile.getPosX();
    }

    @Override
    public int getPosZ() {
        return profile.getPosZ();
    }

    @Override
    public float getSizeX() {
        return profile.getSizeX();
    }

    @Override
    public float getSizeZ() {
        return profile.getSizeZ();
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public int getZoomNeeded() {
        return ANY_ZOOM;
    }

    @Override
    public boolean isEnabled(boolean forMinimap) {
        return profile.isEnabled;
    }

    @Override
    public boolean followRotation() {
        return true;
    }

    @Override
    public boolean hasDynamicLocation() {
        return false;
    }

    @Override
    public void renderAt(ScreenRenderer renderer, float centreX, float centreZ, float sizeMultiplier, float blockScale) {
        if (profile.size() == 0) return;

        int posX = getPosX();
        int posZ = getPosZ();

        int size = profile.size();
        float pathWidth = MapPathWaypointIcon.pathWidth * sizeMultiplier;
        float outlineWidth = pathWidth + MapPathWaypointIcon.outlineWidth * sizeMultiplier;

        if (size == 0) return;

        // Draw paths

        if (size == 1) {
            // Single point
            PathPoint p = profile.getPoint(0);

            float width = outlineWidth;
            float x = (p.getX() - posX) * blockScale + centreX;
            float z = (p.getZ() - posZ) * blockScale + centreZ;
            renderer.drawRect(CommonColors.BLACK, (int) (x - width), (int) (z - width), (int) (x + width), (int) (z + width));
            width = pathWidth;
            renderer.drawRect(profile.getColor(), (int) (x - width), (int) (z - width), (int) (x + width), (int) (z + width));

            return;
        }

        Point drawingOrigin = ScreenRenderer.drawingOrigin();
        centreX += drawingOrigin.x;
        centreZ += drawingOrigin.y;

        PathSegment[] path = getPath(blockScale);

        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();

        // Render outline
        CommonColors.BLACK.applyColor();
        renderPath(path, centreX, centreZ, outlineWidth);

        // Render inner
        profile.getColor().applyColor();
        renderPath(path, centreX, centreZ, pathWidth);

        CommonColors.WHITE.applyColor();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
    }

    private static List<MapIcon> pathWaypoints = null;

    public static List<MapIcon> getPathWaypoints() {
        if (pathWaypoints == null) resetPathWaypoints();
        return pathWaypoints;
    }

    public static void resetPathWaypoints() {
        if (pathWaypoints == null) {
            pathWaypoints = new ArrayList<>();
        } else {
            pathWaypoints.clear();
        }
        MapConfig.Waypoints.INSTANCE.pathWaypoints.forEach(c -> pathWaypoints.add(new MapPathWaypointIcon(c)));
    }

    private void renderPath(PathSegment[] path, float offsetX, float offsetZ, float width) {
        GlStateManager.glBegin(GL11.GL_TRIANGLE_STRIP);

        int i = 0;
        int len = path.length;

        if (len == 1) {
            // Single line; Make sure edges are square instead of cut off
            PathSegment segment = path[0];

            float tangentialUnitX = -segment.perpendicularUnitZ;
            float tangentialUnitZ = segment.perpendicularUnitX;
            float fromX = segment.fromX - tangentialUnitX * width;
            float fromZ = segment.fromZ - tangentialUnitZ * width;
            float toX = segment.toX + tangentialUnitX * width;
            float toZ = segment.toZ + tangentialUnitZ * width;
            float perpendicularX = segment.perpendicularUnitX * width;
            float perpendicularZ = segment.perpendicularUnitZ * width;
            GlStateManager.glVertex3f(fromX - perpendicularX + offsetX, fromZ - perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(fromX + perpendicularX + offsetX, fromZ + perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(toX - perpendicularX + offsetX, toZ - perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(toX + perpendicularX + offsetX, toZ + perpendicularZ + offsetZ, 0);

            GlStateManager.glEnd();
            return;
        }

        if (!profile.isCircular) {
            // Render first and last segment differently (Ends are square instead of cut off)
            ++i;
            --len;

            PathSegment first = path[0];
            float tangentialUnitX = -first.perpendicularUnitZ;
            float tangentialUnitZ = first.perpendicularUnitX;
            float fromX = first.fromX - tangentialUnitX * width;
            float fromZ = first.fromZ - tangentialUnitZ * width;
            float perpendicularX = first.perpendicularUnitX * width;
            float perpendicularZ = first.perpendicularUnitZ * width;
            GlStateManager.glVertex3f(fromX - perpendicularX + offsetX, fromZ - perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(fromX + perpendicularX + offsetX, fromZ + perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(first.toX - perpendicularX + offsetX, first.toZ - perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(first.toX + perpendicularX + offsetX, first.toZ + perpendicularZ + offsetZ, 0);
        }

        for (; i < len; ++i) {
            PathSegment segment = path[i];
            float perpendicularX = segment.perpendicularUnitX * width;
            float perpendicularZ = segment.perpendicularUnitZ * width;
            GlStateManager.glVertex3f(segment.fromX - perpendicularX + offsetX, segment.fromZ - perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(segment.fromX + perpendicularX + offsetX, segment.fromZ + perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(segment.toX - perpendicularX + offsetX, segment.toZ - perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(segment.toX + perpendicularX + offsetX, segment.toZ + perpendicularZ + offsetZ, 0);
        }

        if (profile.isCircular) {
            // Add the first part of the first segment so there isn't a triangular hole at the end
            PathSegment segment = path[0];
            float perpendicularX = segment.perpendicularUnitX * width;
            float perpendicularZ = segment.perpendicularUnitZ * width;
            GlStateManager.glVertex3f(segment.fromX - perpendicularX + offsetX, segment.fromZ - perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(segment.fromX + perpendicularX + offsetX, segment.fromZ + perpendicularZ + offsetZ, 0);
        } else {
            PathSegment last = path[i];
            float tangentialUnitX = -last.perpendicularUnitZ;
            float tangentialUnitZ = last.perpendicularUnitX;
            float toX = last.toX + tangentialUnitX * width;
            float toZ = last.toZ + tangentialUnitZ * width;
            float perpendicularX = last.perpendicularUnitX * width;
            float perpendicularZ = last.perpendicularUnitZ * width;
            GlStateManager.glVertex3f(last.fromX - perpendicularX + offsetX, last.fromZ - perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(last.fromX + perpendicularX + offsetX, last.fromZ + perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(toX - perpendicularX + offsetX, toZ - perpendicularZ + offsetZ, 0);
            GlStateManager.glVertex3f(toX + perpendicularX + offsetX, toZ + perpendicularZ + offsetZ, 0);
        }

        GlStateManager.glEnd();
    }

    private PathSegment[] getPath(float blockScale) {
        PathSegment[] cached = pathCache.getOrDefault(blockScale, null);
        if (cached != null) return cached;


        int posX = getPosX();
        int posZ = getPosZ();

        int size = profile.size();
        if (size == 0) return null;

        int nSegments = profile.size() + (profile.isCircular ? 1 : 0) - 1;
        ArrayList<PathSegment> path = new ArrayList<>();
        path.ensureCapacity(nSegments);
        PathPoint first;
        PathPoint second = profile.getPoint(0);
        for (int i = 1; i < nSegments + 1; ++i) {
            first = second;
            second = profile.getPoint(i == size ? 0 : i);

            PathSegment segment = new PathSegment();

            segment.fromX = (first.getX() - posX) * blockScale;
            segment.fromZ = (first.getZ() - posZ) * blockScale;
            segment.toX = (second.getX() - posX) * blockScale;
            segment.toZ = (second.getZ() - posZ) * blockScale;

            int tangentialX = second.getX() - first.getX();
            int tangentialZ = second.getZ() - first.getZ();
            double segmentSize = Math.sqrt(tangentialX * tangentialX + tangentialZ * tangentialZ);
            float tangentialUnitX = (float) (tangentialX / segmentSize);
            float tangentialUnitZ = (float) (tangentialZ / segmentSize);
            segment.perpendicularUnitX = +tangentialUnitZ;
            segment.perpendicularUnitZ = -tangentialUnitX;

            path.add(segment);
        }

        PathSegment[] toCache = path.toArray(new PathSegment[0]);
        pathCache.put(blockScale, toCache);
        return toCache;
    }

    /**
     * Represents one segment of the path (Connecting two points, `from` and `to`)
     * `perpendicularUnit` is a vector perpendicular to the segment, to give it width.
     */
    private static class PathSegment {
        float fromX;
        float fromZ;
        float toX;
        float toZ;

        float perpendicularUnitX;
        float perpendicularUnitZ;
    }
}

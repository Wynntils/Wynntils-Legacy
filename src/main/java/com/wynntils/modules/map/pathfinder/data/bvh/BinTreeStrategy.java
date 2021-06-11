package com.wynntils.modules.map.pathfinder.data.bvh;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;

import com.wynntils.modules.map.pathfinder.data.bvh.ISplitStrategy.ISplitStrategyFactory;
import com.wynntils.modules.map.pathfinder.util.IBoundingBox;
import com.wynntils.modules.map.pathfinder.util.IBoundingBox.AxisAlignedBoundingBox;

public class BinTreeStrategy implements ISplitStrategy, ISplitStrategyFactory {
	
	@Override
	public List<Setting> getSettings () {
		return Collections.emptyList();
	}
	
	@Override
	public ISplitStrategy getStrategy (final Map<String, String> settings) {
		return this;
	}
	
	@Override
	public <T extends IBoundingBox> List<List<T>> split (final List<T> elements) {
		final AxisAlignedBoundingBox bounds = elements.parallelStream().map(AxisAlignedBoundingBox::fromIAABB).reduce(new AxisAlignedBoundingBox(),
				AxisAlignedBoundingBox::mergeBounds);
		final Point3d span = bounds.size();
		final List<List<T>> result = Arrays.asList(new ArrayList<>(), new ArrayList<>());
		if (span.x > span.y && span.x > span.z) {
			final double mid = bounds.mid().x;
			elements.forEach(e -> result.get(e.mid().x < mid ? 0 : 1).add(e));
		} else if (span.y > span.z) {
			final double mid = bounds.mid().y;
			elements.forEach(e -> result.get(e.mid().y < mid ? 0 : 1).add(e));
		} else {
			final double mid = bounds.mid().z;
			elements.forEach(e -> result.get(e.mid().z < mid ? 0 : 1).add(e));
		}
		return result;
	}
	
	@Override
	public int bucketCount () {
		return 2;
	}
	
}

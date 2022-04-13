package com.wynntils.modules.map.pathfinder.data.bvh;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.vecmath.Point3d;

import com.wynntils.modules.map.pathfinder.data.bvh.ISplitStrategy.ISplitStrategyFactory;
import com.wynntils.modules.map.pathfinder.util.IBoundingBox;

public class OctTreeStrategy implements ISplitStrategy, ISplitStrategyFactory {
	@Override
	public <T extends IBoundingBox> List<List<T>> split (final List<T> elements) {
		final Point3d mid = new Point3d();
		elements.stream().map(T::mid).forEach(p -> mid.add(p)); // find weighted mid as split point
		mid.scale(1. / elements.size());
		// decide octant by xyz 'digits' (less than mid = 1 / else = 0)
		// e.g. x less, y,z greater -> 100 -> bin 4 -> oct[4]
		final List<List<T>> octantList = IntStream.range(0, 8).mapToObj(i -> new LinkedList<T>())
				.collect(Collectors.toList());
		elements.forEach(e -> {
			int index = 0;
			final Point3d bbMid = e.mid();
			if (bbMid.x < mid.x) {
				index |= (1 << 0);
			}
			if (bbMid.y < mid.y) {
				index |= (1 << 1);
			}
			if (bbMid.z < mid.z) {
				index |= (1 << 2);
			}
			octantList.get(index).add(e);
		});
		return octantList;
	}
	
	@Override
	public int bucketCount () {
		return 8;
	}
	
	@Override
	public List<Setting> getSettings () {
		return Collections.emptyList();
	}
	
	@Override
	public ISplitStrategy getStrategy (final Map<String, String> settings) {
		return this;
	}
}

package com.wynntils.modules.map.pathfinder.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import javax.vecmath.Point3d;

import org.junit.jupiter.api.Test;

import com.wynntils.modules.map.pathfinder.util.IBoundingBox;
import com.wynntils.modules.map.pathfinder.util.IBoundingBox.AxisAlignedBoundingBox;

class BoundingVolumeHierarchyTest {
	/**
	 * Add and remove a large number of elements in quick succession.
	 */
	@Test
	void addRemoveBulkTest () {
		final BoundingVolumeHierarchy<IBoundingBox> bvh = new BoundingVolumeHierarchy<>();
		final Random random = new Random("Wynntils".hashCode()); // fixed seed for reproducible test
		final int range = 1 << 7;
		final Supplier<Point3d> nextPoint = () -> new Point3d(random.nextDouble(), random.nextDouble(),
				random.nextDouble());
		final int cycles = 1 << 4;
		final int inserts = 1 << 4;
		IBoundingBox nearestTest = null;
		final Point3d reference = new Point3d();
		for (int c = 0; c < cycles; c++) {
			// prepare bvh and reference element for findNearest()
			final List<IBoundingBox> removals = new ArrayList<>(inserts);
			for (int i = 0; i < inserts; i++) {
				final IBoundingBox bb = new AxisAlignedBoundingBox(nextPoint.get(), nextPoint.get());
				bvh.add(bb);
				if (nearestTest == null || bb.squareDistance(reference) < nearestTest.squareDistance(reference)) {
					nearestTest = bb;
				}
				if (random.nextBoolean()) {
					removals.add(bb);
				}
			}
			// test bvh against reference element
			IBoundingBox bvhNearest = bvh.findNearest(reference);
			assertEquals(nearestTest, bvhNearest, "Cycle: " + c + ", expected dist: "
					+ nearestTest.squareDistance(reference) + ", actual dist: " + bvhNearest.squareDistance(reference));
			// remove marked elements
			for (final IBoundingBox bb : removals) {
				if (bb == nearestTest) {
					nearestTest = null;
				}
				bvh.remove(bb);
			}
			// test again after deletions
			if (nearestTest != null) {
				bvhNearest = bvh.findNearest(reference);
				assertEquals(nearestTest, bvhNearest,
						"Cycle: " + c + ", expected dist: " + nearestTest.squareDistance(reference) + ", actual dist: "
								+ bvhNearest.squareDistance(reference));
			}
		}
	}
	
	@Test
	void addTest () {
		final BoundingVolumeHierarchy<IBoundingBox> bvh = new BoundingVolumeHierarchy<>();
		final int range = 1 << 6;
		final IBoundingBox[][] expected = new IBoundingBox[range][range];
		for (int y = 0; y < range; y++) {
			for (int x = 0; x < range; x++) {
				final IBoundingBox bb = new AxisAlignedBoundingBox(new Point3d(x, y, 0));
				bvh.add(expected[x][y] = bb);
			}
		}
		for (int y = 0; y < range; y++) {
			for (int x = 0; x < range; x++) {
				assertEquals(expected[x][y], bvh.findNearest(new Point3d(x, y, 0)));
			}
		}
	}
}

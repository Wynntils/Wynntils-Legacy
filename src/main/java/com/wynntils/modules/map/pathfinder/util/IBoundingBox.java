package com.wynntils.modules.map.pathfinder.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.BinaryOperator;

import javax.vecmath.Point3d;

import com.wynntils.core.utils.objects.Pair;

/**
 * General bounding box interface.
 *
 * @author Kepler-17c
 */
public interface IBoundingBox {
	/**
	 * Combines
	 *
	 * @param other
	 * @return
	 */
	default IBoundingBox mergeWith (final IBoundingBox other) {
		return AxisAlignedBoundingBox.mergeBounds(this, other);
	}
	
	default Point3d getLower () {
		return this.getBounds().getLower();
	}
	
	default Point3d getUpper () {
		return this.getBounds().getUpper();
	}
	
	default Point3d size () {
		return this.getBounds().size();
	}
	
	default Point3d mid () {
		return this.getBounds().mid();
	}
	
	default double squareDistance (final Point3d point) {
		return AxisAlignedBoundingBox.squareDistance(AxisAlignedBoundingBox.fromIAABB(this), point);
	}
	
	default boolean contains (final IBoundingBox other) {
		return this.getBounds().contains(other);
	}
	
	default double volume () {
		return this.getBounds().volume();
	}
	
	default Pair<Point3d, Point3d> definingPoints () {
		return new Pair<>(this.getLower(), this.getUpper());
	}
	
	AxisAlignedBoundingBox getBounds ();
	
	public class AxisAlignedBoundingBox implements IBoundingBox {
		public static final Point3d NEUTRAL_LOWER = doubleToPoint3d(Double.POSITIVE_INFINITY);
		public static final Point3d NEUTRAL_UPPER = doubleToPoint3d(Double.NEGATIVE_INFINITY);
		public static final double EPSILON = 1.0 / (1L << 48);
		public static final double ONE_PLUS_EPS = 1.0 + EPSILON;
		public static final double ONE_MINUS_EPS = 1.0 - EPSILON;
		
		private final Point3d lower;
		private final Point3d upper;
		private final Point3d size;
		private final Point3d mid;
		
		public AxisAlignedBoundingBox () {
			this.lower = NEUTRAL_LOWER;
			this.upper = NEUTRAL_UPPER;
			this.size = new Point3d();
			this.mid = new Point3d();
		}
		
		public AxisAlignedBoundingBox (final Point3d... points) {
			final Pair<Point3d, Point3d> definingPoints = Arrays.stream(points)
					.collect( () -> new Pair<>(NEUTRAL_LOWER, NEUTRAL_UPPER), (acc, val) -> {
						acc.a = min(acc.a, val);
						acc.b = max(acc.b, val);
					}, (acc, val) -> {
						acc.a = min(acc.a, val.a);
						acc.b = max(acc.b, val.b);
					});
			this.lower = definingPoints.a;
			this.lower.scale(ONE_MINUS_EPS);
			this.upper = definingPoints.b;
			this.upper.scale(ONE_PLUS_EPS);
			this.size = new Point3d();
			this.mid = new Point3d();
			this.updateSizeAndMid();
		}
		
		public AxisAlignedBoundingBox (final Collection<IBoundingBox> points) {
			final Pair<Point3d, Point3d> definingPoints = points.stream().map(IBoundingBox::definingPoints)
					.collect( () -> new Pair<>(NEUTRAL_LOWER, NEUTRAL_UPPER), (acc, val) -> {
						acc.a = min(acc.a, val.a);
						acc.b = min(acc.b, val.b);
					}, (acc, val) -> {
						acc.a = min(acc.a, val.a);
						acc.b = min(acc.b, val.b);
					});
			this.lower = definingPoints.a;
			this.lower.scale(ONE_MINUS_EPS);
			this.upper = definingPoints.b;
			this.upper.scale(ONE_PLUS_EPS);
			this.size = new Point3d();
			this.mid = new Point3d();
			this.updateSizeAndMid();
		}
		
		private static Point3d doubleToPoint3d (final double d) {
			return new Point3d(d, d, d);
		}
		
		public static AxisAlignedBoundingBox fromIAABB (final IBoundingBox iaabb) {
			return new AxisAlignedBoundingBox(iaabb.getLower(), iaabb.getUpper());
		}
		
		private static Point3d min (final Point3d a, final Point3d b) {
			return new Point3d(Math.min(a.x, b.x), Math.min(a.y, b.y), Math.min(a.z, b.z));
		}
		
		private static Point3d max (final Point3d a, final Point3d b) {
			return new Point3d(Math.max(a.x, b.x), Math.max(a.y, b.y), Math.max(a.z, b.z));
		}
		
		public static AxisAlignedBoundingBox mergeBounds (final IBoundingBox a, final IBoundingBox b) {
			return new AxisAlignedBoundingBox(mergePoints(a.getLower(), b.getLower(), Math::min),
					mergePoints(a.getUpper(), b.getUpper(), Math::max));
		}
		
		private void updateSizeAndMid () {
			// calculate size
			final Point3d span = new Point3d(this.upper);
			span.sub(this.lower);
			this.size.set(span);
			// calculate centre point
			span.scaleAdd(.5, this.lower);
			this.mid.set(span);
		}
		
		public void add (final Point3d point) {
			final Point3d marginLower = new Point3d(point);
			marginLower.scale(ONE_MINUS_EPS);
			final Point3d marginUpper = new Point3d(point);
			marginUpper.scale(ONE_PLUS_EPS);
			this.lower.set(min(this.lower, marginLower));
			this.upper.set(max(this.upper, marginUpper));
			this.updateSizeAndMid();
		}
		
		@Override
		public IBoundingBox mergeWith (final IBoundingBox boundingBox) {
			return mergeBounds(this, boundingBox);
		}
		
		@Override
		public Point3d getLower () {
			return this.lower;
		}
		
		@Override
		public Point3d getUpper () {
			return this.upper;
		}
		
		public double getMinX () {
			return this.lower.x;
		}
		
		public double getMinY () {
			return this.lower.y;
		}
		
		public double getMinZ () {
			return this.lower.z;
		}
		
		public double getMaxX () {
			return this.upper.x;
		}
		
		public double getMaxY () {
			return this.upper.y;
		}
		
		public double getMaxZ () {
			return this.upper.z;
		}
		
		@Override
		public Point3d size () {
			return this.size;
		}
		
		@Override
		public Point3d mid () {
			return this.mid;
		}
		
		@Override
		public double squareDistance (final Point3d point) {
			return squareDistance(this, point);
		}
		
		@Override
		public boolean contains (final IBoundingBox other) {
			final Point3d otherLower = other.getLower();
			final Point3d otherUpper = other.getUpper();
			return this.lower.x <= otherLower.x && this.lower.y <= otherLower.y && this.lower.z <= otherLower.z
					&& otherUpper.x <= this.upper.x && otherUpper.y <= this.upper.y && otherUpper.z <= this.upper.z;
		}
		
		@Override
		public double volume () {
			return this.size.x * this.size.y * this.size.z;
		}
		
		@Override
		public AxisAlignedBoundingBox getBounds () {
			return this;
		}
		
		@Override
		public String toString () {
			return "[" + this.lower + ", " + this.upper + "]";
		}
		
		private static Point3d mergePoints (final Point3d a, final Point3d b,
				final BinaryOperator<Double> mergeFuntion) {
			return new Point3d(mergeFuntion.apply(a.x, b.x), mergeFuntion.apply(a.y, b.y),
					mergeFuntion.apply(a.z, b.z));
		}
		
		private static double squareDistance (final AxisAlignedBoundingBox bb, final Point3d point) {
			// view along coords as 1D intervals
			// separate calculation works because of super-position
			final double xDist = point.x < bb.lower.x ? bb.lower.x - point.x
					: point.x > bb.upper.x ? point.x - bb.upper.x : 0;
			final double yDist = point.y < bb.lower.y ? bb.lower.y - point.y
					: point.y > bb.upper.y ? point.y - bb.upper.y : 0;
			final double zDist = point.z < bb.lower.z ? bb.lower.z - point.z
					: point.z > bb.upper.z ? point.z - bb.upper.z : 0;
			// combine 1D linear distances to 3D squared distance
			return (xDist * xDist) + (yDist * yDist) + (zDist * zDist);
		}
	}
}

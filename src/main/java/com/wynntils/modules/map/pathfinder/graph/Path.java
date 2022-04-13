package com.wynntils.modules.map.pathfinder.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.vecmath.Point3d;

import com.wynntils.core.utils.objects.Location;
import com.wynntils.modules.map.instances.LootRunNote;
import com.wynntils.modules.map.instances.LootRunPath;
import com.wynntils.modules.map.pathfinder.util.IBoundingBox;
import com.wynntils.modules.map.pathfinder.util.Referenceable;

/**
 * A simple mutable path to be used in a graph structure.
 * 
 * @author Kepler-17c
 */
class Path implements IBoundingBox, Referenceable {
	/**
	 * Serialisation ID.
	 */
	private final UUID uuid;
	/**
	 * All points of the path, including start- and end-node.
	 */
	private final List<Point3d> points;
	/**
	 * Bounds of the path's points.
	 */
	private AxisAlignedBoundingBox bounds;
	/**
	 * Same path with reversed point order.
	 */
	private Path reversed;
	/**
	 * Sum of all line segments.
	 */
	private double length;
	/**
	 * Node at the start of the path.
	 */
	private Node startNode;
	/**
	 * Node at the end of the path.
	 */
	private Node endNode;
	/**
	 * Whether this path is directed or bi-directional.
	 */
	private boolean directed;
	
	/**
	 * Deserialisation constructor with given ID.
	 *
	 * @param uuid
	 *            for serialisation.
	 * @param startNode
	 *            of the path.
	 * @param points
	 *            between the nodes.
	 * @param endNode
	 *            of the path.
	 */
	public Path (final UUID uuid, final Node startNode, final List<Point3d> points, final Node endNode) {
		this.uuid = uuid;
		this.bounds = new AxisAlignedBoundingBox(points.toArray(new Point3d[0]));
		this.bounds.add(startNode.position);
		this.bounds.add(endNode.position);
		this.startNode = startNode;
		this.endNode = endNode;
		this.points = new ArrayList<>();
		this.points.add(new Point3d(startNode.position));
		this.points.addAll(points);
		this.points.add(new Point3d(endNode.position));
		this.updateLength();
		this.directed = false;
	}
	
	/**
	 * Helper constructor for path-splitting.
	 *
	 * @param startNode
	 *            of the path.
	 * @param points
	 *            between nodes.
	 * @param endNode
	 *            of the path.
	 */
	private Path (final Node startNode, final List<Point3d> points, final Node endNode) {
		this(UUID.randomUUID(), startNode, points, endNode);
	}
	
	/**
	 * Base constructor for a new path.
	 *
	 * @param startNode
	 *            of the path.
	 * @param endNode
	 *            of the path.
	 */
	public Path (final Node startNode, final Node endNode) {
		this(startNode, Collections.emptyList(), endNode);
	}
	
	/**
	 * Helper constructor for reverse paths. It makes a shallow copy to keep the instances linked.
	 *
	 * @param source
	 *            to create the shallow copy from.
	 */
	private Path (final Path source) {
		this.uuid = UUID.randomUUID();
		this.points = source.points;
		this.startNode = source.startNode;
		this.endNode = source.endNode;
	}
	
	@Override
	public UUID getUuid () {
		return this.uuid;
	}
	
	/**
	 * Gets the directionality of the path.
	 *
	 * @return Whether the path is directed.
	 */
	public boolean isDirected () {
		return this.directed;
	}
	
	/**
	 * Tries to append a point to the path.
	 * <p>
	 * The end-node has to be free and will be moved.
	 * </p>
	 *
	 * @param point
	 *            to be appended.
	 * @return Whether appending succeeded.
	 */
	public boolean appendPoint (final Point3d point) {
		if (this.endNode.degree() != 1) {
			return false;
		}
		this.bounds.add(point);
		this.points.add(point);
		this.endNode.position.set(point);
		this.updateLength();
		return true;
	}
	
	/**
	 * Tries to prepend a point to the path.
	 * <p>
	 * The start-node has to be free and will be moved.
	 * </p>
	 *
	 * @param point
	 *            to be prepended.
	 * @return Whether prepending succeeded.
	 */
	public boolean prependPoint (final Point3d point) {
		if (this.startNode.degree() != 1) {
			return false;
		}
		this.bounds.add(point);
		this.points.add(0, point);
		this.startNode.position.set(point);
		this.updateLength();
		return true;
	}
	
	/**
	 * Tries to insert a point on the path.
	 *
	 * @param point
	 *            to be inserted.
	 * @return Whether inserting succeeded.
	 */
	public boolean insertPoint (final Point3d point) {
		// get nearest point on path, to find neighbours later
		final Point3d pathPoint = new Point3d();
		final int pathIndex = this.getNearestPointOnPath(point, pathPoint);
		if (pathIndex == 0 || pathIndex == this.points.size() - 1) { // next to a node, might lie outside
			final Point3d v0 = new Point3d(this.points.get(pathIndex));
			v0.sub(pathPoint);
			final Point3d v1 = new Point3d(this.points.get(pathIndex + (pathIndex == 0 ? 1 : -1)));
			v1.sub(pathPoint);
			if (Path.dot(v0, v1) > 0) { // is outside of the path -> try append or prepend
				if (pathIndex == 0 ? this.prependPoint(point) : this.appendPoint(point)) {
					this.bounds.add(point);
					this.updateLength();
				}
			}
		}
		// is on the path
		this.points.add(pathIndex + 1, point);
		this.bounds.add(point);
		this.updateLength();
		return true;
	}
	
	/**
	 * Removes a point from the path. The point has to be an inner path point or a free node (degree 1).
	 *
	 * @param index
	 *            of the point to be removed.
	 * @return Whether removing succeeded.
	 */
	public boolean deletePoint (final int index) {
		if (index < 0 || index >= this.points.size() || this.points.size() <= 2) {
			return false;
		} else if ((index == 0 && this.startNode.degree() != 1)
				|| (index == this.points.size() - 1 && this.endNode.degree() != 1)) {
			return false;
		} else {
			this.points.remove(index);
			if (index == 0) {
				this.startNode.position.set(this.points.get(index));
			} else if (index == this.points.size()) {
				this.endNode.position.set(this.points.get(index - 1));
			}
			this.updateLength();
			this.bounds = new AxisAlignedBoundingBox(this.points.toArray(new Point3d[0]));
			return true;
		}
	}
	
	/**
	 * Attaches the path to a node.
	 *
	 * @param node
	 *            to connect to.
	 * @return The old start/end node being replace by the new node, or {@code null} if it couldn't be connected.
	 */
	public Node connectToNode (final Node node) {
		if (this.startNode.position.distanceSquared(node.position) < this.endNode.position
				.distanceSquared(node.position)) {
			if (this.startNode.degree() != 1) {
				return null;
			}
			final Node oldNode = this.startNode;
			this.points.add(0, new Point3d(node.position));
			this.startNode = node;
			this.bounds.add(node.position);
			return oldNode;
		} else {
			if (this.endNode.degree() != 1) {
				return null;
			}
			final Node oldNode = this.endNode;
			this.points.add(new Point3d(node.position));
			this.endNode = node;
			this.bounds.add(node.position);
			return oldNode;
		}
	}
	
	/**
	 * Moves a point to another position.
	 *
	 * @param index
	 *            of the point to be moved.
	 * @param newPosition
	 *            to move it to.
	 * @return Whether moving succeeded.
	 */
	public boolean movePoint (final int index, final Point3d newPosition) {
		if (0 <= index && index < this.points.size()) {
			this.points.set(index, newPosition);
			this.bounds = new AxisAlignedBoundingBox(this.points.toArray(new Point3d[0]));
			this.updateLength();
			return true;
		}
		return false;
	}
	
	/**
	 * Updates the length of the path.
	 */
	public void updateLength () {
		double sum = 0;
		for (int i = 0; i < this.points.size() - 1; i++) {
			sum += this.points.get(i).distance(this.points.get(i + 1));
		}
		this.length = sum;
	}
	
	/**
	 * Gets the length of the path.
	 *
	 * @return The length.
	 */
	public double getLength () {
		return this.length;
	}
	
	/**
	 * Gets the start node.
	 *
	 * @return The start node.
	 */
	public Node getStart () {
		return this.startNode;
	}
	
	/**
	 * Gets the end node.
	 *
	 * @return The end node.
	 */
	public Node getEnd () {
		return this.endNode;
	}
	
	/**
	 * Gets all points of the path (nodes included).
	 *
	 * @return Points of the path.
	 */
	public List<Point3d> getPoints () {
		return this.points;
	}
	
	@Override
	public AxisAlignedBoundingBox getBounds () {
		return this.bounds;
	}
	
	/**
	 * Returns a reversed version of the path. All points and start/end node are swapped in their order.
	 *
	 * @return The reversed path.
	 */
	public Path getReversed () {
		return this.reversed == null ? this.reversed = new ReversePath(this) : this.reversed;
	}
	
	/**
	 * Splits this path into two.
	 *
	 * @param position
	 *            where to split the path.
	 * @param insertNewNode
	 *            toggles inserting the given point as node, or just removing a segment.
	 * @return The newly created paths, or {@code null} if splitting failed.
	 */
	public List<Path> split (final Point3d position, final boolean insertNewNode) {
		final int splitIndex = this.getNearestPointOnPath(position, null);
		Path left;
		Path right;
		if (insertNewNode) {
			final Node splitNode = new Node(position);
			left = splitIndex == 0 ? new Path(this.startNode, splitNode)
					: new Path(this.startNode, this.points.subList(1, splitIndex + 1), splitNode);
			right = splitIndex >= this.points.size() - 2 ? new Path(splitNode, this.endNode)
					: new Path(splitNode, this.points.subList(splitIndex + 1, this.points.size() - 1), this.endNode);
			left.relinkToNodes();
			right.relinkToNodes();
		} else {
			if (splitIndex < 1 || splitIndex > this.points.size() - 3) {
				return null;
			}
			final Node splitNodeLeft = new Node(this.points.get(splitIndex));
			final Node splitNodeRight = new Node(this.points.get(splitIndex + 1));
			left = new Path(this.startNode, this.points.subList(1, splitIndex), splitNodeLeft);
			right = new Path(splitNodeRight, this.points.subList(splitIndex + 2, this.points.size() - 1), this.endNode);
			left.relinkToNodes();
			right.relinkToNodes();
		}
		this.unlinkFromNodes();
		return Arrays.asList(left, right);
	}
	
	/**
	 * Remove references to this path from it's start/end nodes.
	 */
	public void unlinkFromNodes () {
		this.startNode.connectedPaths.remove(this);
		this.endNode.connectedPaths.remove(this);
	}
	
	/**
	 * Add references to this path to it's start/end nodes.
	 */
	public void relinkToNodes () {
		if (!this.startNode.connectedPaths.contains(this)) {
			this.startNode.connectedPaths.add(this);
		}
		if (!this.endNode.connectedPaths.contains(this)) {
			this.endNode.connectedPaths.add(this);
		}
	}
	
	/**
	 * Finds the point in this path nearest to another given point.
	 *
	 * @param point
	 *            to compare against.
	 * @param result
	 *            is a return variable to hold the results position.
	 * @return The index of the closest point.
	 */
	public int getNearestPathPoint (final Point3d point, final Point3d result) {
		int nearest = -1;
		double dist = Double.POSITIVE_INFINITY;
		for (int i = 0; i < this.points.size(); i++) {
			final double d = point.distanceSquared(this.points.get(i));
			if (d < dist) {
				nearest = i;
				dist = d;
			}
		}
		if (result != null) {
			result.set(this.points.get(nearest));
		}
		return nearest;
	}
	
	/**
	 * Projects the given point onto the path's line segments.
	 *
	 * @param point
	 *            to be projected.
	 * @param result
	 *            of the projection.
	 * @return The index of the line segment.
	 */
	public int getNearestPointOnPath (final Point3d point, final Point3d result) {
		double minDist = Double.POSITIVE_INFINITY;
		int index = -1; // required initialisation - won't be returned, because paths always have at least two points
		Point3d pathPoint = null;
		for (int i = 0; i < this.points.size() - 1; i++) {
			final Point3d start = this.points.get(i);
			final Point3d end = this.points.get(i + 1);
			final Point3d dir = new Point3d(end);
			dir.sub(start);
			// use approximation via point-to-line-distance (true spline distance is overkill here)
			/*-
			 * Start from:
			 * (start + t * dir - point) * dir = 0
			 * Vectors "point to point-on-line" and "dir" are perpendicular.
			 * Expand and re-arrange:
			 * start*dir + t*dir*dir - point*dir = 0
			 * t*dir*dir = point*dir - start*dir
			 * t = (point*dir - start*dir) / (dir*dir)
			 */
			final double t = (Path.dot(point, dir) - Path.dot(start, dir)) / Path.dot(dir, dir);
			if (t < 0) {
				final double dist = start.distanceSquared(point);
				if (dist < minDist) {
					minDist = dist;
					index = i;
					pathPoint = start;
				}
			} else if (t > 1) {
				final double dist = end.distanceSquared(point);
				if (dist < minDist) {
					minDist = dist;
					index = i + 1;
					pathPoint = end;
				}
			} else {
				final Point3d linePoint = new Point3d(start);
				linePoint.interpolate(end, t);
				final double dist = linePoint.distanceSquared(point);
				if (dist < minDist) {
					minDist = dist;
					index = i;
					pathPoint = linePoint;
				}
			}
		}
		if (result != null) {
			result.set(pathPoint);
		}
		return index;
	}
	
	@Override
	public double squareDistance (final Point3d point) {
		final Point3d pathPoint = new Point3d();
		this.getNearestPointOnPath(point, pathPoint);
		return point.distanceSquared(pathPoint);
	}
	
	/**
	 * Calculates the distance from a given point to this path.
	 *
	 * @param point
	 *            to get the distance of.
	 * @return The distance to the given point.
	 */
	public double getDistance (final Point3d point) {
		return Math.sqrt(this.squareDistance(point));
	}
	
	/**
	 * Converts this path to a {@link LootRunPath} that can be shown in-game.
	 *
	 * @deprecated This is only intended for testing and will be removed eventually.
	 * @return This path as {@link LootRunPath}.
	 */
	@Deprecated
	public LootRunPath toEditorLootRunPath () {
		final List<LootRunNote> labels = new ArrayList<>();
		final Function<Point3d, Location> locMap = (final Point3d p) -> {
			final Point3d offsetResult = new Point3d(0, 1d, 0);
			offsetResult.add(p);
			return new Location(offsetResult);
		};
		labels.add(new LootRunNote(locMap.apply(this.startNode.position), "Start Node"));
		final AtomicInteger index = new AtomicInteger();
		labels.addAll(this.points.stream().map(locMap)
				.map(loc -> new LootRunNote(loc, index.getAndIncrement() + "\n" + loc.toString()))
				.collect(Collectors.toList()));
		labels.add(new LootRunNote(locMap.apply(this.endNode.position), "End Node"));
		return new LootRunPath(this.points, Collections.emptyList(), labels);
	}
	
	/**
	 * Calculate the dot product of two given vectors.
	 *
	 * @param a
	 *            the first vector.
	 * @param b
	 *            the second vector.
	 * @return The dot product.
	 */
	private static double dot (final Point3d a, final Point3d b) {
		return (a.x * b.x) + (a.y * b.y) + (a.z * b.z);
	}
	
	/**
	 * Wrapper to reverse a paths direction.
	 *
	 * @author Kepler-17c
	 */
	private static class ReversePath extends Path {
		/**
		 * The original path.
		 */
		private final Path source;
		
		/**
		 * Wraps a normal path.
		 *
		 * @param source
		 *            to wrap.
		 */
		public ReversePath (final Path source) {
			super(source);
			this.source = source;
		}
		
		@Override
		public boolean appendPoint (final Point3d point) {
			return super.prependPoint(point);
		}
		
		@Override
		public boolean prependPoint (final Point3d point) {
			return super.appendPoint(point);
		}
		
		@Override
		public Node getStart () {
			return super.getEnd();
		}
		
		@Override
		public Node getEnd () {
			return super.getStart();
		}
		
		@Override
		public List<Point3d> getPoints () {
			final int size = super.points.size();
			return IntStream.rangeClosed(1, size).map(i -> size - i).mapToObj(super.points::get)
					.collect(Collectors.toList());
		}
		
		@Override
		public Path getReversed () {
			return this.source;
		}
	}
}

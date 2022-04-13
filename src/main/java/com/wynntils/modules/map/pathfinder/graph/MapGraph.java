package com.wynntils.modules.map.pathfinder.graph;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;

import javax.vecmath.Point3d;

import com.wynntils.modules.map.instances.LootRunPath;
import com.wynntils.modules.map.pathfinder.FibonacciHeapMinQueue;
import com.wynntils.modules.map.pathfinder.data.BoundingVolumeHierarchy;

/**
 * At its core a basic graph implementation. Added on top are a bounding-volume-hierarchy for efficient search of nodes
 * or vertices, and path-finding with Dijkstra's algorithm.
 *
 * @author Kepler-17c
 */
public class MapGraph {
	
	/**
	 * Nodes of the graph. Used for quick spatial search.
	 */
	protected final BoundingVolumeHierarchy<Node> nodeTree;
	/**
	 * Vertices of the graph. Used for quick spatial search.
	 */
	protected final BoundingVolumeHierarchy<Path> pathTree;
	
	/**
	 * Create an empty graph or load an existing one. If {@code file} is {@code null}, this creates a new empty graph.
	 *
	 * @param file
	 *            The file to load.
	 */
	public MapGraph () {
		this.nodeTree = new BoundingVolumeHierarchy<>();
		this.pathTree = new BoundingVolumeHierarchy<>();
	}
	
	public LootRunPath findPath (final Point3d from, final Point3d to) {
		final Path startPath = this.pathTree.findNearest(from);
		final Path endPath = this.pathTree.findNearest(to);
		// insert temporary path splits to create start- & target-nodes for Dijkstra
		final List<Path> temporaryPaths = new ArrayList<>();
		final Point3d startPoint = new Point3d();
		Node startNode;
		final Point3d endPoint = new Point3d();
		final Function<Node, Boolean> endNode;
		if (startPath == endPath) { // split into 3 parts
			// find start-point and -node
			startPath.getNearestPointOnPath(from, startPoint);
			final List<Path> firstSplit = startPath.split(startPoint, true);
			temporaryPaths.addAll(firstSplit);
			startNode = firstSplit.get(0).getEnd();
			// find end-point and -node
			final double leftDist = firstSplit.get(0).squareDistance(to);
			final double rightDist = firstSplit.get(1).squareDistance(to);
			final Path endSegment = leftDist < rightDist ? firstSplit.get(0) : firstSplit.get(1);
			endPath.getNearestPointOnPath(to, endPoint);
			final List<Path> secondSplit = endSegment.split(endPoint, true);
			temporaryPaths.addAll(secondSplit);
			endNode = node -> node == secondSplit.get(0).getEnd();
		} else { // split each into 2 parts
			startPath.getNearestPointOnPath(from, startPoint);
			endPath.getNearestPointOnPath(to, endPoint);
			final List<Path> startSplit = startPath.split(startPoint, true);
			final List<Path> endSplit = endPath.split(endPoint, true);
			startNode = startSplit.get(0).getEnd();
			endNode = node -> node == endSplit.get(0).getEnd();
		}
		final List<Path> shortestPath = this.dijkstra(startNode, endNode);
		temporaryPaths.forEach(Path::unlinkFromNodes);
		startPath.relinkToNodes();
		endPath.relinkToNodes();
		if (shortestPath == null) {
			return null;
		}
		// combine result paths and return
		final List<Point3d> result = new ArrayList<>();
		result.add(from);
		result.add(startPoint);
		shortestPath.forEach(segment -> {
			final List<Point3d> points = segment.getPoints();
			result.addAll(points.subList(1, points.size()));
		});
		result.add(to);
		return new LootRunPath(result, null, null);
	}
	
	/**
	 * Use Dijkstra's algorithm to find the nearest node from {@code startNode} to satisfy the search criteria.
	 *
	 * @param startNode
	 *            The node to start the search from.
	 * @param isTargetNode
	 *            A function of search-criteria, to match encountered nodes against.
	 * @return The shortest path found to a node satisfying {@code isTargetNode}, or {@code null} if there is no such
	 *         node in reach.
	 */
	private List<Path> dijkstra (final Node startNode, final Function<Node, Boolean> isTargetNode) {
		// prepare nodes
		this.nodeTree.forEach(node -> node.dijkstraDistance = Double.POSITIVE_INFINITY);
		// set up working queue
		final Comparator<Node> nodeComparator = (a, b) -> Double.compare(a.dijkstraDistance, b.dijkstraDistance);
		final Queue<Node> shortestDistanceNode = new FibonacciHeapMinQueue<>(nodeComparator);
		startNode.dijkstraDistance = 0;
		shortestDistanceNode.add(startNode);
		// actual algorithm
		Node activeNode = null;
		while (!shortestDistanceNode.isEmpty() && !isTargetNode.apply(activeNode = shortestDistanceNode.poll())) {
			for (final Path p : activeNode.connectedPaths) {
				if (p.getStart() == activeNode || !p.isDirected()) {
					final double distanceSum = activeNode.dijkstraDistance + p.getLength();
					final Node nextNode = p.getStart() == activeNode ? p.getEnd() : p.getStart();
					if (distanceSum < nextNode.dijkstraDistance) {
						shortestDistanceNode.remove(nextNode);
						nextNode.dijkstraDistance = distanceSum;
						nextNode.dijkstraPath = p;
						shortestDistanceNode.add(nextNode);
					}
				}
			}
		}
		if (!isTargetNode.apply(activeNode)) {
			return null; // a path does not exist for the given input
		}
		final List<Path> result = new LinkedList<>();
		while (activeNode != startNode) {
			final Path path = activeNode.dijkstraPath.getEnd() == activeNode // if oriented correctly
					? activeNode.dijkstraPath // use it
					: activeNode.dijkstraPath.getReversed(); // else reverse
			result.add(0, path);
			activeNode = path.getStart();
		}
		return result;
	}
}

package com.wynntils.modules.map.pathfinder.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.vecmath.Point3d;

import com.wynntils.modules.map.pathfinder.util.IBoundingBox;
import com.wynntils.modules.map.pathfinder.util.Referenceable;

/**
 * A mutable node to be used in a graph structure.
 *
 * @author Kepler-17c
 */
class Node implements IBoundingBox, Referenceable {
	/**
	 * Serialisation ID.
	 */
	private final UUID uuid;
	/**
	 * Helper variable for {@link IBoundingBox}.
	 */
	private final AxisAlignedBoundingBox bounds;
	/**
	 * Paths connected to this node.
	 */
	final List<Path> connectedPaths;
	/**
	 * Position of the node in space.
	 */
	Point3d position;
	/**
	 * Whether this node has been fully processed in creating the graph.
	 */
	private boolean closed;
	/**
	 * Holds the distance during path-finding.
	 */
	double dijkstraDistance;
	/**
	 * Holds the origin path during path-finding.
	 */
	Path dijkstraPath;
	
	/**
	 * Deserialisation constructor with given ID.
	 *
	 * @param position
	 *            of the node.
	 * @param closed
	 *            status of graph generation.
	 * @param uuid
	 *            for serialisation.
	 */
	public Node (final Point3d position, final boolean closed, final UUID uuid) {
		this.uuid = uuid;
		this.bounds = new AxisAlignedBoundingBox(position);
		this.connectedPaths = new ArrayList<>();
		this.position = position;
		this.closed = closed;
	}
	
	/**
	 * Base constructor for a new node.
	 * <p>
	 * It's state is set to closed and a random UUID is generated.
	 * </p>
	 *
	 * @param position
	 *            of the node.
	 */
	public Node (final Point3d position) {
		this(position, false, UUID.randomUUID());
	}
	
	/**
	 * Gets the number of connected paths registered in this node.
	 *
	 * @return The number of connected nodes.
	 */
	public int degree () {
		return this.connectedPaths.size();
	}
	
	@Override
	public UUID getUuid () {
		return this.uuid;
	}
	
	/**
	 * Gets the processing status from graph generation.
	 *
	 * @return The state of this node.
	 */
	public boolean isClosed () {
		return this.closed;
	}
	
	/**
	 * Closes the node.
	 */
	public void close () {
		this.closed = true;
	}
	
	@Override
	public AxisAlignedBoundingBox getBounds () {
		return this.bounds;
	}
}

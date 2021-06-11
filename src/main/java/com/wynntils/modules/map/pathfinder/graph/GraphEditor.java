package com.wynntils.modules.map.pathfinder.graph;

import java.util.List;

import javax.vecmath.Point3d;

import com.wynntils.modules.map.instances.LootRunPath;

public class GraphEditor {
	private MapGraph mapGraph;
	/**
	 * Editor-state selected node.
	 */
	private Node selectedNode;
	
	/**
	 * Editor-state selected path.
	 */
	private Path selectedPath;
	
	/**
	 * Editor-state: Path containing the selected point.
	 */
	private Path selectedPointPath;
	/**
	 * Editor-state selected point.
	 */
	private int selectedPointIndex;
	
	/**
	 * Cashed output path for editor overlay.
	 */
	private LootRunPath generatedOutputPath;
	
	/**
	 * Editor operation: Get a displayable representation of the selected path.
	 *
	 * @return A displayable representation of the selected path.
	 */
	public LootRunPath getSelectedPath () {
		return this.generatedOutputPath == null && this.selectedPath != null
				? this.generatedOutputPath = this.selectedPath.toEditorLootRunPath()
				: this.generatedOutputPath;
	}
	
	/**
	 * Editor operation: Select the point nearest to the given position.
	 *
	 * @param position
	 *            The search position for finding the nearest point.
	 * @return The selected point's position.
	 */
	public Point3d selectPoint (final Point3d position) {
		this.selectedPointPath = this.mapGraph.pathTree.findNearest(position);
		final Point3d result = new Point3d();
		this.selectedPointIndex = this.selectedPointPath.getNearestPathPoint(position, result);
		return result;
	}
	
	/**
	 * Editor operation: Move the selected point the the given new position.
	 *
	 * @param point
	 *            Where to move the selected point to.
	 * @return Whether this operation succeeded.
	 */
	public boolean moveSelectedPoint (final Point3d point) {
		if (this.selectedPointPath == null) {
			return false;
		}
		this.selectedPointPath.movePoint(this.selectedPointIndex, point);
		if (this.selectedPointIndex == 0) {
			this.mapGraph.nodeTree.updateBounds(this.selectedPointPath.getStart());
		}
		if (this.selectedPointIndex == this.selectedPointPath.getPoints().size() - 1) {
			this.mapGraph.nodeTree.updateBounds(this.selectedPointPath.getEnd());
		}
		this.mapGraph.pathTree.updateBounds(this.selectedPointPath);
		return true;
	}
	
	/**
	 * Editor operation: Select the nearest node in the graph.
	 *
	 * @param position
	 *            The search position for finding the nearest node.
	 * @return Whether this operation changed the node selection.
	 */
	public boolean selectNode (final Point3d position) {
		return null != (this.selectedNode = this.mapGraph.nodeTree.findNearest(position));
	}
	
	/**
	 * Editor operation: Select the nearest path/vertex in the graph.
	 *
	 * @param point
	 *            The search position for finding the nearest vertex.
	 * @return Whether this operation changed the path selection.
	 */
	public boolean selectPath (final Point3d point) {
		final Path tmp = this.mapGraph.pathTree.findNearest(point);
		if (tmp == this.selectedPath) {
			return false;
		} else {
			this.selectedPath = tmp;
			this.generatedOutputPath = null;
			return true;
		}
	}
	
	/**
	 * Editor operation: Append a point to either end of the selected path. The nearest end to the given point is chosen
	 * for this operation. Appending the point fails if there are other paths connected to that end.
	 *
	 * @param point
	 *            The point to append to the path.
	 * @return Whether this operation was successful.
	 */
	public boolean appendPointToSelectedPath (final Point3d point) {
		if (this.selectedPath == null) {
			return false;
		}
		if (point.distanceSquared(this.selectedPath.getStart().position) < point
				.distanceSquared(this.selectedPath.getEnd().position)) {
			if (this.selectedPath.prependPoint(point)) {
				this.mapGraph.nodeTree.updateBounds(this.selectedPath.getStart());
				this.mapGraph.pathTree.updateBounds(this.selectedPath);
				this.generatedOutputPath = null;
				return true;
			}
		} else {
			if (this.selectedPath.appendPoint(point)) {
				this.mapGraph.nodeTree.updateBounds(this.selectedPath.getEnd());
				this.mapGraph.pathTree.updateBounds(this.selectedPath);
				this.generatedOutputPath = null;
				return true;
			}
		}
		return false;
	}
	
	public boolean insertPointInSelectedPath (final Point3d point) {
		if (this.selectedPath == null) {
			return false;
		}
		this.selectedPath.insertPoint(point);
		this.generatedOutputPath = null;
		return true;
	}
	
	public boolean deleteSelectedPoint () {
		if (this.selectedPointPath == null) {
			return false;
		}
		if (this.selectedPointPath.getPoints().size() == 2) {
			final Node startNode = this.selectedPointPath.getStart();
			final Node endNode = this.selectedPointPath.getEnd();
			final int startPaths = startNode.degree();
			final int endPaths = endNode.degree();
			final boolean deletePath = (this.selectedPointIndex == 0 && startPaths == 1)
					|| (this.selectedPointIndex == 1 && endPaths == 1);
			if (deletePath) {
				this.selectedPointPath.unlinkFromNodes();
				if (startNode.degree() == 0) {
					this.mapGraph.nodeTree.remove(startNode);
				}
				if (endNode.degree() == 0) {
					this.mapGraph.nodeTree.remove(endNode);
				}
				this.mapGraph.pathTree.remove(this.selectedPointPath);
				this.selectedPointPath = null;
				return true;
			}
			return false;
		}
		if (this.selectedPointPath.deletePoint(this.selectedPointIndex)) {
			return true;
		}
		return false;
	}
	
	public boolean connectPathToSelectedNode (final Point3d point) {
		if (this.selectedNode == null) {
			return false;
		}
		final Node oldPathNode = this.mapGraph.pathTree.findNearest(point).connectToNode(this.selectedNode);
		if (oldPathNode == null) {
			return false;
		} else {
			this.mapGraph.nodeTree.remove(oldPathNode);
			return true;
		}
	}
	
	public boolean cutSelectedPath (final Point3d point) {
		if (this.selectedPath == null) {
			return false;
		}
		final List<Path> newPaths = this.selectedPath.split(point, false);
		if (newPaths.size() == 2) {
			this.mapGraph.pathTree.remove(this.selectedPath);
			this.selectedPath = null;
			newPaths.forEach(p -> this.mapGraph.pathTree.add(p));
			return true;
		} else {
			// undo changes if split was not successful
			newPaths.forEach(Path::unlinkFromNodes);
			this.selectedPath.relinkToNodes();
			return false;
		}
	}
	
	public boolean attachPathToSelectedNode (final Point3d position) {
		if (this.selectedNode == null) {
			return false;
		}
		final Node node = new Node(position);
		final Path path = new Path(this.selectedNode, node);
		this.mapGraph.nodeTree.add(node);
		this.mapGraph.pathTree.add(path);
		return true;
	}
	
	public void createNode (final Point3d position) {
		this.mapGraph.nodeTree.add(new Node(position));
	}
	
	public boolean closeSelectedNode () {
		if (this.selectedNode == null) {
			return false;
		}
		this.selectedNode.close();
		return true;
	}
}

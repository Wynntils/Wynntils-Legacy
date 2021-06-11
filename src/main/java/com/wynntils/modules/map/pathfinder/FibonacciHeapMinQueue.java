package com.wynntils.modules.map.pathfinder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Queue;
import java.util.stream.Collectors;

public class FibonacciHeapMinQueue<T> implements Queue<T> {
	private final Comparator<T> comparator;
	private final List<Tree<T>> heap;
	private int minIndex;
	private final Map<T, Tree<T>> objectCache;
	
	public FibonacciHeapMinQueue (final Comparator<T> comparator) {
		this.comparator = comparator;
		this.heap = new ArrayList<>();
		this.minIndex = -1;
		this.objectCache = new HashMap<>();
	}
	
	@Override
	public boolean add (final T value) {
		if (value == null) {
			return false;
		}
		final Tree<T> heapElement = new Tree<>(value);
		this.insertAndClean(heapElement);
		this.objectCache.put(value, heapElement);
		return true;
	}
	
	@Override
	public boolean isEmpty () {
		return this.heap.isEmpty() || this.heap.stream().filter(Objects::nonNull).count() == 0;
	}
	
	private void updateMinIndex () {
		T min = null;
		for (final Tree<T> tree : this.heap) {
			if (tree != null && (min == null || this.comparator.compare(tree.value, min) < 0)) {
				min = tree.value;
				this.minIndex = this.heap.indexOf(tree);
			}
		}
	}
	
	private void insertAndClean (Tree<T> entry) {
		boolean updateMinIndex = this.isEmpty() // new element must be min
				|| this.heap.get(this.minIndex) == null // got removed in poll, clean up
				|| this.comparator.compare(entry.value, this.heap.get(this.minIndex).value) < 0;
		// iterate add operation
		do {
			// detect possible collision
			final int degree = entry.getDegree();
			// ensure heap size
			while (this.heap.size() <= degree) {
				this.heap.add(null);
			}
			final Tree<T> oldEntry = this.heap.get(degree);
			if (oldEntry == null) {
				this.heap.set(degree, entry);
				entry = null;
				if (updateMinIndex) {
					this.minIndex = degree;
				}
			} else {
				entry = Tree.mergeTrees(entry, oldEntry, this.comparator);
				this.heap.set(degree, null);
				if (this.minIndex == degree) {
					updateMinIndex = true;
				}
			}
		} while (entry != null);
	}
	
	private List<T> listCopy () {
		final List<T> result = this.heap.stream().filter(Objects::nonNull).map(Tree::listCopy).flatMap(List::stream)
				.collect(Collectors.toList());
		result.sort(this.comparator);
		return result;
	}
	
	@Override
	public int size () {
		return this.heap.stream().filter(Objects::nonNull).mapToInt(Tree::size).sum();
	}
	
	@Override
	public boolean contains (final Object o) {
		return this.objectCache.containsKey(o);
	}
	
	@Override
	public Iterator<T> iterator () {
		final List<T> listCopy = this.listCopy();
		return listCopy.iterator();
	}
	
	@Override
	public Object[] toArray () {
		return this.listCopy().toArray();
	}
	
	@Override
	public <E> E[] toArray (final E[] a) {
		return this.listCopy().toArray(a);
	}
	
	@Override
	public boolean remove (final Object o) {
		final Tree<T> value = this.objectCache.remove(o);
		if (value == null) {
			return false;
		}
		final List<Tree<T>> nodesToReInsert = new ArrayList<>();
		nodesToReInsert.addAll(value.childNodes);
		if (value.parent == null) {
			this.heap.set(this.heap.indexOf(value), null);
		} else {
			Tree<T> activeNode = value.parent;
			activeNode.childNodes.remove(value);
			while (activeNode != null) {
				if (activeNode.parent == null) {
					nodesToReInsert.add(activeNode);
					this.heap.set(this.heap.indexOf(activeNode), null);
					activeNode = null;
				} else if (activeNode.markedRemoved) {
					final Tree<T> parent = activeNode.parent;
					parent.childNodes.remove(activeNode);
					activeNode.parent = null;
					nodesToReInsert.add(activeNode);
					activeNode = parent;
				} else {
					activeNode.markedRemoved = true;
					activeNode = null;
				}
			}
		}
		nodesToReInsert.forEach(n -> n.markedRemoved = false);
		nodesToReInsert.forEach(n -> n.parent = null);
		nodesToReInsert.forEach(this::insertAndClean);
		if (this.heap.get(this.minIndex) == null) {
			this.updateMinIndex();
		}
		return true;
	}
	
	@Override
	public boolean containsAll (final Collection<?> c) {
		return c.stream().map(e -> this.objectCache.containsKey(e)).reduce(false, (acc, val) -> acc || val);
	}
	
	@Override
	public boolean addAll (final Collection<? extends T> c) {
		if (c.isEmpty()) {
			return false;
		} else {
			c.forEach(e -> this.add(e));
			return true;
		}
	}
	
	@Override
	public boolean removeAll (final Collection<?> c) {
		return c.stream().map(this::remove).reduce(false, (acc, val) -> acc || val);
	}
	
	@Override
	public boolean retainAll (final Collection<?> c) {
		final List<T> heapContents = this.listCopy();
		heapContents.retainAll(c);
		if (heapContents.size() != this.size()) {
			this.heap.clear();
			this.addAll(heapContents);
			return true;
		}
		return false;
	}
	
	@Override
	public void clear () {
		this.heap.clear();
		this.objectCache.clear();
	}
	
	@Override
	public boolean offer (final T e) {
		return this.add(e);
	}
	
	@Override
	public T remove () {
		final T polled = this.poll();
		if (polled == null) {
			throw new NoSuchElementException("Queue is empty.");
		}
		return polled;
	}
	
	@Override
	public T poll () {
		if (this.isEmpty()) {
			return null;
		}
		// take min from heap
		final Tree<T> minTree = this.heap.set(this.minIndex, null);
		// re-insert child nodes
		minTree.childNodes.forEach(entry -> {
			entry.parent = null;
			this.insertAndClean(entry);
		});
		// shrink over-grown heap
		int last = this.heap.size() - 1;
		if (!this.heap.isEmpty() && this.heap.get(last) == null) {
			while (last > 0 && this.heap.get(last - 1) == null) {
				this.heap.remove(last--);
			}
		}
		this.updateMinIndex();
		this.objectCache.remove(minTree.value);
		return minTree.value;
	}
	
	@Override
	public T element () {
		final T peeked = this.peek();
		if (peeked == null) {
			throw new NoSuchElementException("Queue is empty.");
		}
		return peeked;
	}
	
	@Override
	public T peek () {
		return this.isEmpty() ? null : this.heap.get(this.minIndex).value;
	}
	
	private static class Tree<E> {
		private final List<Tree<E>> childNodes;
		private final E value;
		private Tree<E> parent;
		private boolean markedRemoved;
		
		public Tree (final E value) {
			this.childNodes = new ArrayList<>();
			this.value = value;
			this.parent = null;
			this.markedRemoved = false;
		}
		
		public int getDegree () {
			return this.childNodes.size();
		}
		
		public void addChild (final Tree<E> child) {
			this.childNodes.add(child);
			child.parent = this;
		}
		
		public int size () {
			return 1 + this.childNodes.stream().collect(Collectors.summingInt(Tree::size));
		}
		
		public List<E> listCopy () {
			final List<E> result = new ArrayList<>();
			final Queue<Tree<E>> openNodes = new LinkedList<>();
			openNodes.add(this);
			while (!openNodes.isEmpty()) {
				final Tree<E> node = openNodes.poll();
				result.add(node.value);
				openNodes.addAll(node.childNodes);
			}
			return result;
		}
		
		public static <T> Tree<T> mergeTrees (final Tree<T> a, final Tree<T> b, final Comparator<T> comparator) {
			if (comparator.compare(a.value, b.value) < 0) {
				a.addChild(b);
				return a;
			} else {
				b.addChild(a);
				return b;
			}
		}
	}
}

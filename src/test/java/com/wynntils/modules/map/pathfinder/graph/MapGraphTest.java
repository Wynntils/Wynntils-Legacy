package com.wynntils.modules.map.pathfinder.graph;

import java.io.File;
import java.util.Arrays;
import java.util.Stack;
import java.util.UUID;

class MapGraphTest {
	// general constants
	private static final File RESOURCES = new File("src/test/resources/");
	// test resources
	private static final File EMPTY_GRAPH = new File(MapGraphTest.RESOURCES, "emptyGraph.json");
	private static final File SINGLE_PATH = new File(MapGraphTest.RESOURCES, "singlePath.json");
	private static final File LONG_SINGLE_PATH = new File(MapGraphTest.RESOURCES, "longSinglePath.json");
	
	private static File generateTestDirectory () {
		final String name = "tmp-" + UUID.randomUUID();
		return new File(MapGraphTest.RESOURCES, name);
	}
	
	private static boolean cleanUpTestDir (final File dir) {
		if (dir == null) {
			return false;
		}
		final Stack<File> stack = new Stack<>();
		stack.push(dir);
		while (!stack.isEmpty()) {
			final File tmp = stack.pop();
			if (tmp.isFile()) {
				if (!tmp.delete()) {
					return false;
				}
			} else if (tmp.isDirectory()) {
				final File[] childs = tmp.listFiles();
				if (childs.length == 0) {
					if (!tmp.delete()) {
						return false;
					}
				} else {
					stack.push(tmp);
					Arrays.asList(childs).forEach(stack::push);
				}
			} else {
				return false;
			}
		}
		return true;
	}
}

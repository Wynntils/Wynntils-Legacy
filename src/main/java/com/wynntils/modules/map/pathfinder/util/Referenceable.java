package com.wynntils.modules.map.pathfinder.util;

import java.util.UUID;

/**
 * Serialisation helper to allow cyclic references by storing IDs instead of embedded objects.
 * 
 * @author Kepler-17c
 */
public interface Referenceable {
	/**
	 * Returns the object's serialisation UUID.
	 * <p>
	 * It should stay the same for the same object throughout repeated serialisation and deserialisation.<br />
	 * Minor changes should not affect the ID. With major changes it depends on the specific case. In general, ID
	 * changes should be avoided if the object stays the same (depending on what that means for a type).
	 * </p>
	 *
	 * @return The serialisation ID.
	 */
	UUID getUuid ();
}

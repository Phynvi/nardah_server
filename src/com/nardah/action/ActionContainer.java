package com.nardah.action;

import com.nardah.action.impl.ButtonAction;
import com.nardah.action.impl.MobAction;
import com.nardah.action.impl.ObjectAction;
import com.nardah.util.Utility;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;

import java.util.List;
import java.util.stream.Collectors;

/**
 * A container to handle specific {@link Action}s.
 * @author Artem Batutin <artembatutin@gmail.com>
 */
public class ActionContainer<E extends Action> {
	
	/**
	 * All of the registered events.
	 */
	private final Int2ObjectArrayMap<E> events = new Int2ObjectArrayMap<>();
	
	/**
	 * Registering an listener to the container.
	 * @param i the identifier.
	 * @param listener listener to register.
	 */
	public void register(int i, E listener) {
		events.put(i, listener);
	}
	
	/**
	 * Registering the listener from the container.
	 * @param i the identifier.
	 */
	public void deregister(int i) {
		events.remove(i);
	}
	
	/**
	 * Gets the {@link Action} from this container.
	 * @param i the indetifier.
	 * @return the event.
	 */
	public E get(int i) {
		if(!events.containsKey(i))
			return null;
		return events.get(i);
	}
	
	public static void loadEvents() {
		for(String directory : Utility.getSubDirectories(ActionInitializer.class)) {
			try {
				List<ActionInitializer> s = Utility.getClassesInDirectory(ActionInitializer.class.getPackage().getName() + "." + directory).stream().map(clazz -> (ActionInitializer) clazz).collect(Collectors.toList());
				s.forEach(ActionInitializer::init);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		ButtonAction.init();
		ObjectAction.init();
		MobAction.init();
	}
	
	/**
	 * Gets the events.
	 * @return events.
	 */
	public Int2ObjectArrayMap<E> events() {
		return events;
	}
	
}

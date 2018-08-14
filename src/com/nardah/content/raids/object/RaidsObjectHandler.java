package com.nardah.content.raids.object;

import com.nardah.content.raids.object.impl.LingTree;

import java.util.HashMap;
import java.util.Map;

public class RaidsObjectHandler {

	public static Map<RaidsObjectData, RaidsObject> objects = new HashMap<>();

	public static void load() {

		objects.put(RaidsObjectData.TREE, new LingTree());
	}
}

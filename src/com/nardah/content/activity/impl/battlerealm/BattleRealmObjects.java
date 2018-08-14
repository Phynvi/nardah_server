package com.nardah.content.activity.impl.battlerealm;

import com.nardah.game.world.object.ObjectDirection;
import com.nardah.game.world.object.ObjectType;
import com.nardah.game.world.position.Position;

import java.util.ArrayList;

public class BattleRealmObjects {
	public static ArrayList<ObjectArgs> battleRealmObjects = new ArrayList<>();
	
	public static class ObjectArgs {
		public final int id;
		public final Position position;
		public final ObjectDirection rotation;
		public final ObjectType type;
		
		public ObjectArgs(int id, Position position, ObjectDirection rotation, ObjectType type) {
			//            System.out.println("Added a new objectArg: id=" + id + "pos="+position + "rot=" + rotation + "type="+type);
			this.id = id;
			this.position = position;
			this.rotation = rotation;
			this.type = type;
		}
	}
}

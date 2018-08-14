package com.nardah.content.halloffame;

import java.util.Comparator;

public class FameBoardComparer implements Comparator<FameBoardPlayer> {

	@Override
	public int compare(FameBoardPlayer o1, FameBoardPlayer o2) {
		return Integer.compare(o1.getKills(), o2.getKills());
	}

}

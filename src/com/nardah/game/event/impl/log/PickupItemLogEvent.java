package com.nardah.game.event.impl.log;

import com.nardah.game.world.items.ground.GroundItem;
import com.nardah.game.world.entity.actor.player.Player;

public class PickupItemLogEvent extends LogEvent {

	private final Player player;
	private final GroundItem groundItem;

	public PickupItemLogEvent(Player player, GroundItem groundItem) {
		this.player = player;
		this.groundItem = groundItem;
	}

	@Override
	public void onLog() throws Exception {
		if((groundItem.item.getValue() * groundItem.item.getAmount()) < 250_000) {
			return;
		}

		/*JdbcSession session = new JdbcSession(PostgreService.getConnection());
		long logId = session.autocommit(false).sql("INSERT INTO log.log(log_time) VALUES (?::timestamp) RETURNING id")
				.set(dateTime).insert(new SingleOutcome<>(Long.class));

		session.sql("INSERT INTO log.pickup_item_log(player_id, log_id, item_id, item_amount) VALUES (?, ?, ?, ?)")
				.set(player.getMemberId()).set(logId).set(groundItem.item.getId()).set(groundItem.item.getAmount())
				.execute().commit();*/
	}

}

package com.nardah.game.world.entity.combat.attack.listener.item;

import com.nardah.game.world.entity.combat.effect.impl.CombatPoisonEffect;
import com.nardah.game.world.entity.combat.effect.impl.CombatVenomEffect;
import com.nardah.util.RandomUtils;
import com.nardah.game.world.entity.combat.attack.listener.ItemCombatListenerSignature;
import com.nardah.game.world.entity.combat.attack.listener.SimplifiedListener;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.equipment.Equipment;

@ItemCombatListenerSignature(requireAll = false, items = {12_931, 13_197, 13_199})
public class ZulrahHelm extends SimplifiedListener<Player> {

	@Override
	public void hit(Player attacker, Actor defender, Hit hit) {
		if(!defender.isNpc()) {
			return;
		}

		Item helm = attacker.equipment.get(Equipment.HELM_SLOT);

		if(helm == null) {
			return;
		}

		if(helm.matchesId(12_931)) {
			serp(attacker, defender, this);
		} else if(helm.matchesId(13_197)) {
			tanz(attacker, defender, this);
		} else if(helm.matchesId(13_199)) {
			magma(attacker, defender, this);
		}
	}

	private static void serp(Player attacker, Actor defender, ZulrahHelm listener) {
		if(attacker.serpentineHelmCharges > 0) {
			attacker.serpentineHelmCharges -= 2;
			boolean poisonous = false;

			if(attacker.equipment.hasWeapon()) {
				poisonous = CombatPoisonEffect.getPoisonType(attacker.equipment.getWeapon()).isPresent();
			}

			if(!poisonous && attacker.equipment.hasAmmo()) {
				poisonous = CombatPoisonEffect.getPoisonType(attacker.equipment.get(Equipment.ARROWS_SLOT)).isPresent();
			}

			if(CombatVenomEffect.isVenomous(attacker.equipment.getWeapon())) {
				defender.venom();
			} else if(RandomUtils.success(poisonous ? 0.50 : 1 / 6.0)) {
				defender.venom();
			}
		}

		if(attacker.serpentineHelmCharges == 0) {
			attacker.message("Your Serpentine helm is out of charges.");
			if(attacker.equipment.contains(13199)) {
				attacker.equipment.replace(13_199, Equipment.HELM_SLOT, true);
				attacker.inventory.add(13918, 1);
				attacker.getCombat().removeListener(listener);
			}
			if(attacker.equipment.contains(13197)) {
				attacker.equipment.replace(13_197, Equipment.HELM_SLOT, true);
				attacker.inventory.add(13196, 1);
				attacker.getCombat().removeListener(listener);
			}
			if(attacker.equipment.contains(12931)) {
				attacker.equipment.replace(12_931, Equipment.HELM_SLOT, true);
				attacker.inventory.add(12929, 1);
				attacker.getCombat().removeListener(listener);
			}
		}

	}

	private static void tanz(Player attacker, Actor defender, ZulrahHelm listener) {
		if(attacker.tanzaniteHelmCharges > 0) {
			attacker.tanzaniteHelmCharges -= 2;
			boolean poisonous = false;

			if(attacker.equipment.hasWeapon()) {
				poisonous = CombatPoisonEffect.getPoisonType(attacker.equipment.getWeapon()).isPresent();
			}

			if(!poisonous && attacker.equipment.hasAmmo()) {
				poisonous = CombatPoisonEffect.getPoisonType(attacker.equipment.get(Equipment.ARROWS_SLOT)).isPresent();
			}

			if(CombatVenomEffect.isVenomous(attacker.equipment.getWeapon())) {
				defender.venom();
			} else if(RandomUtils.success(poisonous ? 0.50 : 1 / 6.0)) {
				defender.venom();
			}
		}

		if(attacker.tanzaniteHelmCharges == 0) {
			attacker.message("Your Tanzanite helm is out of charges.");
			attacker.equipment.replace(13_197, 13_196, Equipment.HELM_SLOT, true);
			attacker.getCombat().removeListener(listener);
		}
	}

	private static void magma(Player attacker, Actor defender, ZulrahHelm listener) {
		if(attacker.magmaHelmCharges > 0) {
			attacker.magmaHelmCharges -= 2;
			boolean poisonous = false;

			if(attacker.equipment.hasWeapon()) {
				poisonous = CombatPoisonEffect.getPoisonType(attacker.equipment.getWeapon()).isPresent();
			}

			if(!poisonous && attacker.equipment.hasAmmo()) {
				poisonous = CombatPoisonEffect.getPoisonType(attacker.equipment.get(Equipment.ARROWS_SLOT)).isPresent();
			}

			if(CombatVenomEffect.isVenomous(attacker.equipment.getWeapon())) {
				defender.venom();
			} else if(RandomUtils.success(poisonous ? 0.50 : 1 / 6.0)) {
				defender.venom();
			}
		}

		if(attacker.magmaHelmCharges == 0) {
			attacker.message("Your Magma helm is out of charges.");
			attacker.equipment.replace(13_199, 13_198, Equipment.HELM_SLOT, true);
			attacker.getCombat().removeListener(listener);
		}
	}

}

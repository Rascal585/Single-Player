package com.openrsc.server.event.rsc.impl;

import com.openrsc.server.constants.Skills;
import com.openrsc.server.event.rsc.GameTickEvent;
import com.openrsc.server.external.PrayerDef;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.model.world.World;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class PrayerDrainEvent extends GameTickEvent {

	private ConcurrentHashMap<PrayerDef, Long> activePrayers = new ConcurrentHashMap<PrayerDef, Long>();
	private double partialPoints = 0.0;

	public PrayerDrainEvent(World world, Player owner, int delay) {
		super(world, owner, 1, "Prayer Drain Event");
	}

	@Override
	public void run() {
		refreshActivePrayers();
		for (Entry<PrayerDef, Long> entry : activePrayers.entrySet()) {
			PrayerDef def = entry.getKey();
			long lastDrain = entry.getValue();
			int drainDelay = (int) (180000.0 / def.getDrainRate() * (1 + getPlayerOwner().getPrayerPoints() / 30.0));
			if (System.currentTimeMillis() - lastDrain >= drainDelay) {
				entry.setValue(System.currentTimeMillis());
				drainPrayer();
			}
		}
		if (partialPoints >= 1.0) {
			drainPrayer();
			partialPoints = 0.0;
		}
	}

	private void drainPrayer() {
		if (getPlayerOwner().getSkills().getLevel(Skills.PRAYER) > 0) {
			getPlayerOwner().getSkills().decrementLevel(Skills.PRAYER);
		} else {
			getPlayerOwner().getPrayers().resetPrayers();
			activePrayers.clear();
			getPlayerOwner().message("You have run out of prayer points. Return to a church to recharge");
		}
	}

	private void refreshActivePrayers() {
		for (int x = 0; x <= 13; x++) {
			PrayerDef prayer = getPlayerOwner().getWorld().getServer().getEntityHandler().getPrayerDef(x);
			if (getPlayerOwner().getPrayers().isPrayerActivated(x) && !activePrayers.containsKey(prayer)) {
				activePrayers.put(prayer, System.currentTimeMillis());
			} else if (!getPlayerOwner().getPrayers().isPrayerActivated(x) && activePrayers.containsKey(prayer)) {
				double timePrayerUsed = System.currentTimeMillis() - activePrayers.get(prayer);
				double drainDelay = (180000 / (prayer.getDrainRate() * (1 + getPlayerOwner().getPrayerPoints() / 30.0)));
				partialPoints += timePrayerUsed / drainDelay;
				activePrayers.remove(prayer);
			}
			if (getPlayerOwner().getPrayers().isPrayerActivated(x) && (getPlayerOwner().getSkills().getLevel(Skills.PRAYER) < 1)) {
				getPlayerOwner().getPrayers().resetPrayers();
				getPlayerOwner().message("You have run out of prayer points. Return to a church to recharge");
				activePrayers.clear();
				break;
			}
		}
	}
}

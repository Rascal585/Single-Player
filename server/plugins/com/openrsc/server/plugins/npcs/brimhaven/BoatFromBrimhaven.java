package com.openrsc.server.plugins.npcs.brimhaven;

import com.openrsc.server.model.Point;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.listeners.action.IndirectTalkToNpcListener;
import com.openrsc.server.plugins.listeners.action.ObjectActionListener;
import com.openrsc.server.plugins.listeners.action.TalkToNpcListener;
import com.openrsc.server.plugins.listeners.executive.IndirectTalkToNpcExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.ObjectActionExecutiveListener;
import com.openrsc.server.plugins.listeners.executive.TalkToNpcExecutiveListener;

import static com.openrsc.server.plugins.Functions.*;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;

public class BoatFromBrimhaven implements TalkToNpcExecutiveListener,
	TalkToNpcListener, IndirectTalkToNpcExecutiveListener, IndirectTalkToNpcListener,
	ObjectActionListener, ObjectActionExecutiveListener {

	@Override
	public void onTalkToNpc(Player p, Npc n) {
		int option = showMenu(p, n, "Can I board this ship?",
			"Does Karamja have any unusual customs then?");
		if (option == 0) {
			onIndirectTalkToNpc(p, n);
		} else if (option == 1) {
			npcTalk(p, n, "I'm not that sort of customs officer");
		}
	}

	@Override
	public void onIndirectTalkToNpc(Player p, final Npc n) {
		npcTalk(p, n, "You need to be searched before you can board");
		int sub_opt = showMenu(p, n, "Why?",
			"Search away I have nothing to hide",
			"You're not putting your hands on my things");
		if (sub_opt == 0) {
			npcTalk(p, n,
				"Because Asgarnia has banned the import of intoxicating spirits");
		} else if (sub_opt == 1) {
			if (hasItem(p, ItemId.KARAMJA_RUM.id(), 1)) {
				npcTalk(p, n, "Aha trying to smuggle rum are we?");
				message(p, "The customs official confiscates your rum");
				removeItem(p, ItemId.KARAMJA_RUM.id(), 1);
			} else {
				npcTalk(p,
					n,
					"Well you've got some odd stuff, but it's all legal",
					"Now you need to pay a boarding charge of 30 gold");
				int pay_opt = showMenu(p, n, false, "Ok", "Oh, I'll not bother then");
				if (pay_opt == 0) {
					if (removeItem(p, ItemId.COINS.id(), 30)) {
						playerTalk(p, n, "Ok");
						message(p, "You pay 30 gold", "You board the ship");
						movePlayer(p, 538, 617, true);
						p.message("The ship arrives at Ardougne");
					} else { // not enough money
						playerTalk(p, n,
							"Oh dear I don't seem to have enough money");
					}
				} else if (pay_opt == 1) {
					playerTalk(p, n, "Oh, I'll not bother then");
				}
			}
		} else if (sub_opt == 2) {
			npcTalk(p, n, "You're not getting on this ship then");
		}
	}

	@Override
	public void onObjectAction(GameObject obj, String command, Player p) {
		if (obj.getID() == 320 || (obj.getID() == 321)) {
			if (command.equals("board")) {
				if (p.getX() < 467 || p.getX() > 468) {
					return;
				}
				Npc official = getNearestNpc(p, NpcId.CUSTOMS_OFFICIAL.id(), 5);
				if (official != null) {
					official.initializeIndirectTalkScript(p);
				} else {
					p.message("I need to speak to the customs official before boarding the ship.");
				}
			}
		}
	}

	@Override
	public boolean blockTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.CUSTOMS_OFFICIAL.id();
	}

	@Override
	public boolean blockIndirectTalkToNpc(Player p, Npc n) {
		return n.getID() == NpcId.CUSTOMS_OFFICIAL.id();
	}

	@Override
	public boolean blockObjectAction(GameObject arg0, String arg1, Player arg2) {
		return (arg0.getID() == 320 && arg0.getLocation().equals(Point.location(468, 651)))
			|| (arg0.getID() == 321 && arg0.getLocation().equals(Point.location(468, 646)));
	}
}

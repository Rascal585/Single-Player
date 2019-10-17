package com.openrsc.server.event.rsc.impl.combat.scripts;

import com.openrsc.server.Server;
import com.openrsc.server.model.entity.Mob;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author n0m
 */
public class CombatScriptLoader {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Map<String, CombatScript> combatScripts = new HashMap<String, CombatScript>();
	private final Map<String, OnCombatStartScript> combatStartScripts = new HashMap<String, OnCombatStartScript>();
	private final Map<String, CombatAggroScript> combatAggroScripts = new HashMap<String, CombatAggroScript>();

	private final Server server;

	public CombatScriptLoader (Server server) {
		this.server = server;
	}

	private void loadCombatScripts() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		for (Class<?> c : getServer().getPluginHandler().loadClasses("com.openrsc.server.event.rsc.impl.combat.scripts.all")) {
			Object classInstance = c.getConstructor().newInstance();
			if (classInstance instanceof CombatScript) {
				CombatScript script = (CombatScript) classInstance;
				combatScripts.put(classInstance.getClass().getName(), script);
			}
			if (classInstance instanceof OnCombatStartScript) {
				OnCombatStartScript script = (OnCombatStartScript) classInstance;
				combatStartScripts.put(classInstance.getClass().getName(), script);
			}
			if (classInstance instanceof CombatAggroScript) {
				CombatAggroScript script = (CombatAggroScript) classInstance;
				combatAggroScripts.put(classInstance.getClass().getName(), script);
			}
		}
	}

	public void checkAndExecuteCombatScript(final Mob attacker, final Mob victim) {
		for (CombatScript script : combatScripts.values()) {
			if (script.shouldExecute(attacker, victim)) {
				script.executeScript(attacker, victim);
			}
		}
	}

	public void checkAndExecuteOnStartCombatScript(final Mob attacker, final Mob victim) {
		try {
			for (OnCombatStartScript script : combatStartScripts.values()) {
				if (script.shouldExecute(attacker, victim)) {
					script.executeScript(attacker, victim);
				}
			}
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
	}
	
	public void checkAndExecuteCombatAggroScript(final Npc npc, final Player player) {
		try {
			for (CombatAggroScript script : combatAggroScripts.values()) {
				if (script.shouldExecute(npc, player)) {
					script.executeScript(npc, player);
				}
			}
		} catch (Throwable e) {
			LOGGER.catching(e);
		}
	}

	public void load() {
		try {
			loadCombatScripts();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			LOGGER.catching(e);
		} catch (NoSuchMethodException | InvocationTargetException e) {
			LOGGER.catching(e);
		}
	}

	public Server getServer() {
		return server;
	}
}

package net.faiden.skyrush.listeners.server;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import net.golema.database.support.GameStatus;

public class ServerListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerListPing(ServerListPingEvent event) {
		switch (GameStatus.LOBBY) {
		case LOBBY:
			event.setMaxPlayers(24);
			break;
		case GAME:
			event.setMaxPlayers(28);
			break;
		default:
			break;
		}
	}
}
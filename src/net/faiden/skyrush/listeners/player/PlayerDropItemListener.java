package net.faiden.skyrush.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import net.golema.database.support.GameStatus;

public class PlayerDropItemListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		switch (GameStatus.getStatus()) {
		case LOBBY:
			event.setCancelled(true);
			break;
		case GAME:
			event.setCancelled(false);
			break;
		case FINISH:
			event.setCancelled(true);
			break;
		default:
			break;
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		if(!(GameStatus.isStatus(GameStatus.GAME)))
			event.setCancelled(true);
	}
}
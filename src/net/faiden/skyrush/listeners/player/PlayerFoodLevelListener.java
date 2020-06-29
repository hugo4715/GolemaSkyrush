package net.faiden.skyrush.listeners.player;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import net.golema.database.support.GameStatus;

public class PlayerFoodLevelListener implements Listener {

	@EventHandler(priority = EventPriority.NORMAL)
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		switch (GameStatus.getStatus()) {
		case LOBBY:
			event.setFoodLevel(20);
			break;
		case FINISH:
			event.setFoodLevel(20);
			break;
		default:
			break;
		}
	}
}
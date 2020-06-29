package net.faiden.skyrush.listeners.world;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import net.golema.database.support.GameStatus;

public class WorldListener implements Listener {
	
	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if(GameStatus.isStatus(GameStatus.GAME))
			event.setCancelled(true);
	}
}
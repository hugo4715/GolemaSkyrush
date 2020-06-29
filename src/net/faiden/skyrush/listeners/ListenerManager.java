package net.faiden.skyrush.listeners;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import net.faiden.skyrush.listeners.block.BlockBreakListener;
import net.faiden.skyrush.listeners.block.BlockPlaceListener;
import net.faiden.skyrush.listeners.entity.EntityDamageListener;
import net.faiden.skyrush.listeners.entity.EntityDeathListener;
import net.faiden.skyrush.listeners.player.PlayerChatListener;
import net.faiden.skyrush.listeners.player.PlayerDeathListener;
import net.faiden.skyrush.listeners.player.PlayerDropItemListener;
import net.faiden.skyrush.listeners.player.PlayerFoodLevelListener;
import net.faiden.skyrush.listeners.player.PlayerInteractListener;
import net.faiden.skyrush.listeners.player.PlayerJoinListener;
import net.faiden.skyrush.listeners.player.PlayerMoveListener;
import net.faiden.skyrush.listeners.player.PlayerQuitListener;
import net.faiden.skyrush.listeners.server.ServerListener;
import net.faiden.skyrush.listeners.world.WorldListener;
import net.golema.api.patch.KnockbackFixerListener;

public class ListenerManager {
	
	public Plugin plugin;
	public PluginManager pluginManager;

	/**
	 * Constructeur du ListenerManager.
	 * 
	 * @param plugin
	 */
	public ListenerManager(Plugin plugin) {
		this.plugin = plugin;
		this.pluginManager = Bukkit.getPluginManager();
	}
	
	// Enregistrement des Listeners du SkyRush.
	public void registerListeners() {
		
		// Gestion des événements avec les Blocks.
		pluginManager.registerEvents(new BlockBreakListener(), plugin);
		pluginManager.registerEvents(new BlockPlaceListener(), plugin);
		
		// Gestion des événements avec les Entity.
		pluginManager.registerEvents(new EntityDamageListener(), plugin);
		pluginManager.registerEvents(new EntityDeathListener(), plugin);
		
		// Gestion des événements avec les Player.
		pluginManager.registerEvents(new PlayerJoinListener(), plugin);
		pluginManager.registerEvents(new PlayerQuitListener(), plugin);
		pluginManager.registerEvents(new PlayerInteractListener(), plugin);
		pluginManager.registerEvents(new PlayerDeathListener(), plugin);
		pluginManager.registerEvents(new PlayerChatListener(), plugin);
		pluginManager.registerEvents(new PlayerDropItemListener(), plugin);
		pluginManager.registerEvents(new PlayerFoodLevelListener(), plugin);
		pluginManager.registerEvents(new PlayerMoveListener(), plugin);
		
		// Gestion des événements avec le Server.
		pluginManager.registerEvents(new ServerListener(), plugin);
		
		// Gestion des événements avec le World.
		pluginManager.registerEvents(new WorldListener(), plugin);
		
		// Mise en place du Patch des KB.
		pluginManager.registerEvents(new KnockbackFixerListener(), plugin);
	}
}
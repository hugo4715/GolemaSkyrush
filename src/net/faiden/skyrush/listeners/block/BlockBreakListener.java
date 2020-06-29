package net.faiden.skyrush.listeners.block;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.golema.api.builder.titles.ActionBarBuilder;
import net.golema.database.support.builder.items.ItemBuilder;

public class BlockBreakListener implements Listener {

	public Map<Block, Location> blockLocationMap = new HashMap<Block, Location>();

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {

		// Initialisation des variables
		Player player = event.getPlayer(); 
		GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
		Block block = event.getBlock();

		// Bloquer le cassage des blocks.
		if ((!(block.getType().equals(Material.COAL_ORE))) && (!(block.getType().equals(Material.IRON_ORE)))
				&& (!(block.getType().equals(Material.GOLD_ORE))) && (!(block.getType().equals(Material.DIAMOND_ORE)))
				&& (!(SkyRush.instance.blockPlayerMap.containsKey(block)))
				&& (!(block.getLocation().equals(SkyRush.instance.blockPlayerMap.get(block))))) {
			event.setCancelled(true);
			new ActionBarBuilder(ChatColor.GOLD + "" + ChatColor.BOLD + "ATTENTION" + ChatColor.GRAY + "│ "
					+ ChatColor.RED + "Vous ne pouvez détruire ces blocs-là.").sendTo(player);
		} else {
			SkyRush.instance.blockPlayerMap.remove(block);
			gamePlayer.setBlocBreak(gamePlayer.getBlocBreak() + 1);
		}

		// Bloquer les block de recharges.
		if ((blockLocationMap.containsKey(block)) && (block.getLocation().equals(blockLocationMap.get(block)))) {
			event.setCancelled(true);
		}

		// Changer le système de minages.
		switch (block.getType()) {
		case COAL_ORE:
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0f, 1.0f);
			block.setType(Material.STAINED_GLASS);
			block.setData((byte) 15);
			blockLocationMap.put(block, block.getLocation());
			player.getInventory().addItem(new ItemBuilder().type(Material.COAL).build());
			this.remplaceBlock(block.getLocation(), Material.COAL_ORE, 3);
			break;
		case IRON_ORE:
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0f, 1.0f);
			block.setType(Material.STAINED_GLASS);
			block.setData((byte) 1);
			blockLocationMap.put(block, block.getLocation());
			player.getInventory().addItem(new ItemBuilder().type(Material.IRON_INGOT).build());
			this.remplaceBlock(block.getLocation(), Material.IRON_ORE, 6);
			break;
		case GOLD_ORE:
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0f, 1.0f);
			block.setType(Material.STAINED_GLASS);
			block.setData((byte) 4);
			blockLocationMap.put(block, block.getLocation());
			player.getInventory().addItem(new ItemBuilder().type(Material.GOLD_INGOT).build());
			this.remplaceBlock(block.getLocation(), Material.GOLD_ORE, 5);
			break;
		case DIAMOND_ORE:
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0f, 1.0f);
			block.setType(Material.STAINED_GLASS);
			block.setData((byte) 9);
			blockLocationMap.put(block, block.getLocation());
			player.getInventory().addItem(new ItemBuilder().type(Material.DIAMOND).build());
			this.remplaceBlock(block.getLocation(), Material.DIAMOND_ORE, 10);
			break;
		default:
			break;
		}
	}

	/**
	 * Remettre le block en place une fois le temps écoulé.
	 * 
	 * @param location
	 * @param material
	 */
	public void remplaceBlock(Location location, Material material, int time) {
		Bukkit.getScheduler().runTaskLater(SkyRush.instance, new Runnable() {
			@Override
			public void run() {
				location.getBlock().setType(material);
				blockLocationMap.remove(location.getBlock());
				Bukkit.getWorld("world").playSound(location, Sound.ANVIL_LAND, 1.0f, 2.0f);
			}
		}, time * 20L);
	}
}
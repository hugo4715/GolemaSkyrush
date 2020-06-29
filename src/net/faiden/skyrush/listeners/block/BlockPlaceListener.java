package net.faiden.skyrush.listeners.block;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.golema.api.builder.titles.ActionBarBuilder;
import net.golema.database.support.locations.Cuboid;

public class BlockPlaceListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockPlace(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
		Block block = event.getBlock(); 

		// Vérification de l'anti-tower.
		if (event.getBlock().getLocation().getBlockY() >= 80) {
			event.setCancelled(true);
			new ActionBarBuilder(ChatColor.GOLD + "" + ChatColor.BOLD + "ATTENTION" + ChatColor.GRAY + "│ "
					+ ChatColor.RED + "Vous ne pouvez pas poser de blocs à cette hauteur.").sendTo(player);
			event.setCancelled(true);
			player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1.0f, 1.0f);
			return;
		}

		// Enregistrer un block posé.
		SkyRush.instance.blockPlayerMap.put(block, block.getLocation());
		gamePlayer.setBlocPlaces(gamePlayer.getBlocPlaces() + 1);

		// Cancelled des blocs interdits
		for (Cuboid cuboid : SkyRush.cuboidLockBlockList) {
			if (cuboid.IsArena(block.getLocation())) {
				new ActionBarBuilder(ChatColor.GOLD + "" + ChatColor.BOLD + "ATTENTION" + ChatColor.GRAY + "│ "
						+ ChatColor.RED + "Vous ne pouvez pas poser de blocs ici.").sendTo(player);
				event.setCancelled(true);
			}
		}
	}
}
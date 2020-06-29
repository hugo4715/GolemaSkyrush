package net.faiden.skyrush.listeners.player;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.spigotmc.event.entity.EntityMountEvent;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.GameUtils;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.kits.KitMenu;
import net.faiden.skyrush.manager.teams.TeamMenu;
import net.faiden.skyrush.manager.trade.TradeMainMenu;
import net.golema.database.support.GameStatus;
import net.golema.database.support.servers.SwitchServer;

public class PlayerInteractListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		// Initialisation de la variable du Joueur.
		Player player = event.getPlayer();

		// Gestion de l'Interaction.
		if ((event.getItem() == null) || (event.getItem().getType().equals(Material.AIR)))
			return; 
		if ((event.getAction().equals(Action.RIGHT_CLICK_AIR))
				|| (event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {

			// Interaction en fonction des Status.
			switch (GameStatus.getStatus()) {
			case LOBBY:
				switch (event.getItem().getType()) {
				case BANNER:
					new TeamMenu(player);
					break;
				case NAME_TAG:
					new KitMenu(player);
					break;
				case BED:
					SwitchServer.sendPlayerToLobby(player, false);
					break;
				default:
					break;
				}
				break;
			case GAME:
				break;
			case FINISH:
				break;
			default:
				break;
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		if (event.getRightClicked() == null)
			return;
		event.setCancelled(true);

		// Gestions des PNJ.
		if (event.getRightClicked() instanceof Villager) {
			event.setCancelled(true);

			/** SHOP */
			if ((event.getRightClicked().getLocation().equals(GameUtils.BLUEPNJ_LOCATION))
					|| (event.getRightClicked().getLocation().equals(GameUtils.REDPNJ_LOCATION))) {
				GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
				if ((gamePlayer != null) && (!(gamePlayer.isSpectator()))) {
					new TradeMainMenu(player);
					player.sendMessage("§6§lSkyRush §8§l» §eOuverture du shop de partie...");
					return;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityMount(EntityMountEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		event.setCancelled(true);
	}
}
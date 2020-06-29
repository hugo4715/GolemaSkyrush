package net.faiden.skyrush.listeners.entity;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.teams.TeamInfo;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.golema.api.builder.titles.ActionBarBuilder;
import net.golema.database.support.GameStatus;
import net.golema.database.support.builder.TitleBuilder;

public class EntityDamageListener implements Listener {

	public List<Player> playerSpamTitle = new ArrayList<Player>();
	public List<Player> playerSpamMessage = new ArrayList<Player>();

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamage(EntityDamageEvent event) {
		if ((event.getEntity() instanceof ArmorStand)
				|| (event.getEntity() instanceof Villager)) {
			event.setCancelled(true);
		} 
		switch (GameStatus.getStatus()) {
		case LOBBY:
			event.setCancelled(true);
			break;
		case GAME:
			// Réduction des dégats de chutes.
			if ((event.getCause().equals(DamageCause.FALLING_BLOCK)) || (event.getCause().equals(DamageCause.FALL))) {
				event.setDamage((event.getDamage() / 100) * 40);
			}
			break;
		case FINISH:
			event.setCancelled(true);
			break;
		default:
			break;
		}
	} 

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		switch (GameStatus.getStatus()) {
		case GAME:
			// Récupération de la Variable.
			if (event.getEntity() instanceof Player) {
				Player playerEntity = (Player) event.getEntity();
				// Gestion des Kills d'un Joueur.
				if (event.getDamager() instanceof Player) {
					Player playerKiller = (Player) event.getDamager();
					SkyRush.instance.playerKillerMap.put(playerEntity, playerKiller);

					// AntiSpawnKill Alerte.
					if (SkyRush.instance.playerSpawnKillList.contains(playerEntity)) {
						new ActionBarBuilder(ChatColor.RED + "Evitez le spawn-kill.").sendTo(playerKiller);
						event.setCancelled(true);
					}
				}

				// Gestion des Kills à l'Arc.
				if (event.getDamager() instanceof Arrow) {
					Arrow projectil = (Arrow) event.getDamager();
					Entity shooter = (Entity) projectil.getShooter();
					if (shooter instanceof Player) {
						Player shooterPlayer = (Player) shooter;
						SkyRush.instance.playerKillerMap.put(playerEntity, shooterPlayer);
					}
				}
			}

			// Bloquer les coups contre son propre Golem.
			if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Golem)) {
				Player player = (Player) event.getDamager();
				Golem golem = (Golem) event.getEntity();
				TeamInfo teamInfo = TeamManager.getPlayerTeam(player);
				if (TeamManager.golemTeamList.get(teamInfo).getGolem().equals(golem)) {
					if (!(playerSpamMessage.contains(player))) {
						player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "ATTENTION" + ChatColor.GRAY + "│ "
								+ ChatColor.RED + "Vous ne pouvez pas attaquer votre Golem.");
						playerSpamMessage.add(player);
						this.removeSpamList(player, 5);
					}
					event.setCancelled(true);
				} else {
					for (Player playerAdversaire : Bukkit.getOnlinePlayers()) {
						GamePlayer gamePlayer = SkyRush.getGamePlayer(playerAdversaire);
						if ((gamePlayer != null) && (!(gamePlayer.isSpectator()))
								&& (!(TeamManager.isPlayerInTeam(playerAdversaire, teamInfo)))) {
							if (!(playerSpamTitle.contains(player))) {
								new TitleBuilder(ChatColor.RED + "│ ATTENTION│",
										ChatColor.GOLD + "Votre golem est attaqué.").send(playerAdversaire);
								playerSpamTitle.add(player);
								this.removeSpamList(player, 8);
							}
						}
					}
					event.setCancelled(false);
				}
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Remove du Title de Spam.
	 */
	public void removeSpamList(Player player, int time) {
		Bukkit.getScheduler().runTaskLater(SkyRush.instance, new Runnable() {
			@Override
			public void run() {
				playerSpamTitle.remove(player);
				playerSpamMessage.remove(player);
			}
		}, time * 20L);
	}
}
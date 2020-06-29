package net.faiden.skyrush.listeners.entity;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Golem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.teams.TeamInfo;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.currency.Currency;
import net.golema.database.golemaplayer.game.skyrush.stats.SkyRushStatsMode;
import net.golema.database.golemaplayer.game.skyrush.stats.SkyRushStatsType;
import net.golema.database.support.builder.TitleBuilder;

public class EntityDeathListener implements Listener {

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntity() instanceof Golem) {
			Golem golem = (Golem) event.getEntity();
			for (ItemStack itemStackDrop : event.getDrops()) {
				itemStackDrop.setType(Material.AIR);
			}
			TeamInfo teamInfo;
			if (TeamManager.golemTeamList.get(TeamInfo.RED).getGolem().equals(golem)) {
				teamInfo = TeamInfo.RED;
			} else { 
				teamInfo = TeamInfo.BLEUE;
			}

			// Annonce à tout les joueurs de la mort d'un Golem.
			Bukkit.broadcastMessage(SkyRush.instance.getPrefixGame() + ChatColor.YELLOW + " Le golem de l'équipe "
					+ teamInfo.getChatColor() + teamInfo.getName() + ChatColor.YELLOW + " a succombé...");

			// Détection d'un Killer.
			Player playerKiller = event.getEntity().getKiller();
			if (playerKiller != null) {
				// Envoie des Coins au target.
				int coinsReward = 10;
				GamePlayer gamePlayerShooter = SkyRush.getGamePlayer(playerKiller);
				GolemaPlayer golemaPlayerShooter = GolemaPlayer.getGolemaPlayer(playerKiller);
				gamePlayerShooter.setCoinsWin(gamePlayerShooter.getCoinsWin() + coinsReward);
				golemaPlayerShooter.addCoinsGame(Currency.GCOINS, coinsReward, "Destruction du Golem");
				golemaPlayerShooter.getSkyRushPlayer().addPlayerStats(SkyRushStatsMode.SKYRUSH_2TEAMS, SkyRushStatsType.GOLEMKILLS, 1);
			}
 
			// Envoie du message a l'équipe Adverse.
			for (Player playerOnline : Bukkit.getOnlinePlayers()) {
				playerOnline.playSound(playerOnline.getLocation(), Sound.WITHER_DEATH, 1.0f, 1.0f);
				GamePlayer gamePlayer = SkyRush.getGamePlayer(playerOnline);
				if ((gamePlayer != null) && (!(gamePlayer.isSpectator()))
						&& (TeamManager.isPlayerInTeam(playerOnline, teamInfo))) {
					new TitleBuilder(ChatColor.RED + "│ ATTENTION │",
							ChatColor.YELLOW + "Vous pouvez désormais mourir.").send(playerOnline);
				}
			}

			// Suppression des ArmorStand.
			TeamManager.armorStandTeamList.get("text." + teamInfo.getName()).remove();
			TeamManager.armorStandTeamList.get("live." + teamInfo.getName()).remove();
		}
	}
}
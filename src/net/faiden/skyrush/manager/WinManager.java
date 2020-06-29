package net.faiden.skyrush.manager;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.teams.TeamInfo;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.faiden.skyrush.runnables.GameRunnable;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.currency.Currency;
import net.golema.database.golemaplayer.game.luckbox.LuckBoxType;
import net.golema.database.golemaplayer.game.skyrush.stats.SkyRushStatsMode;
import net.golema.database.golemaplayer.game.skyrush.stats.SkyRushStatsType;
import net.golema.database.golemaplayer.levels.LevelType;
import net.golema.database.support.GameStatus;
import net.golema.database.support.builder.TitleBuilder;
import net.golema.database.support.servers.SwitchServer;

public class WinManager {

	public TeamInfo teamInfo;

	/**
	 * Détection de la Victoire.
	 */
	public WinManager() {
		if (GameStatus.isStatus(GameStatus.GAME)) {

			// Les joueurs se sont tous déconnectés.
			if (Bukkit.getOnlinePlayers().size() == 0) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "restart");
				return;
			}

			// Détection d'une Victoire.
			if ((TeamManager.playerTeamList.get(TeamInfo.RED).isEmpty())
					|| (TeamManager.playerTeamList.get(TeamInfo.BLEUE).isEmpty())) {

				GameStatus.setStatus(GameStatus.FINISH);

				// Détection de la Team gagnante.
				if (!(TeamManager.playerTeamList.get(TeamInfo.RED).isEmpty())) {
					this.teamInfo = TeamInfo.RED;
				} else {
					this.teamInfo = TeamInfo.BLEUE;
				}
				Bukkit.broadcastMessage(SkyRush.instance.getPrefixGame() + ChatColor.GREEN + " Victoire de l'équipe "
						+ teamInfo.getChatColor() + teamInfo.getName() + ChatColor.GREEN + ".");
				new TitleBuilder(ChatColor.AQUA + "Félicitations",
						ChatColor.GOLD + "Victoire de l'équipe " + teamInfo.getChatColor() + teamInfo.getName())
								.broadcast();

				// Envoie du nouveau Scoreboard et message de Récompenses.
				for (Player playerOnline : Bukkit.getOnlinePlayers()) {

					// Récupérer le GamePlayer.
					GamePlayer gamePlayer = SkyRush.getGamePlayer(playerOnline);

					// Gestion des point en plus.
					if (TeamManager.isPlayerInTeam(playerOnline, teamInfo)) {
						playerOnline.playSound(playerOnline.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
						GolemaPlayer golemaPlayerWinner = GolemaPlayer.getGolemaPlayer(playerOnline);
						golemaPlayerWinner.getSkyRushPlayer().addPlayerStats(SkyRushStatsMode.SKYRUSH_2TEAMS,
								SkyRushStatsType.WINS, 1);
						// Envoie des Coins au target.
						int coinsReward = 10;
						gamePlayer.setCoinsWin(gamePlayer.getCoinsWin() + coinsReward);
						golemaPlayerWinner.addCoinsGame(Currency.GCOINS, coinsReward, "Victoire");

						// Envoie des Crédits au target.
						int creditsReward = 1;
						gamePlayer.setCreditsWin(gamePlayer.getCreditsWin() + creditsReward);
						golemaPlayerWinner.addCreditsGame(creditsReward, "Victoire");

						// Envoie de l'Experience au target.
						int experienceReward = 30;
						gamePlayer.setExperience(gamePlayer.getExperience() + experienceReward);
						golemaPlayerWinner.addExperience(LevelType.SKYRUSH_LEVEL, LuckBoxType.LUCKBOX_SKYRUSH,
								experienceReward);
					}

					// Scoreboard
					gamePlayer.getScoreboardSign().setObjectiveName("§6§lSkyRush");
					gamePlayer.getScoreboardSign().setLine(13, "§0");
					gamePlayer.getScoreboardSign().setLine(12, "§7Type : §e2 Teams");
					gamePlayer.getScoreboardSign().setLine(11, "§d");
					gamePlayer.getScoreboardSign().setLine(10, ChatColor.GOLD + "Fin de la partie,");
					gamePlayer.getScoreboardSign().setLine(9, ChatColor.GOLD + "faites " + ChatColor.YELLOW + ""
							+ ChatColor.BOLD + "/lobby" + ChatColor.GOLD + ".");
					gamePlayer.getScoreboardSign().setLine(8, "§c");
					gamePlayer.getScoreboardSign().setLine(7, ChatColor.GRAY + "Gagnant:");
					gamePlayer.getScoreboardSign().setLine(6, teamInfo.getChatColor() + "" + ChatColor.BOLD
							+ "Equipe ".toUpperCase() + teamInfo.getName().toUpperCase());
					gamePlayer.getScoreboardSign().setLine(5, "§b");
					gamePlayer.getScoreboardSign().setLine(4, ChatColor.GRAY + "Durée: " + ChatColor.WHITE
							+ new SimpleDateFormat("mm:ss").format(new Date(GameRunnable.gameTimer * 1000)));
					gamePlayer.getScoreboardSign().setLine(3, "§b");
					gamePlayer.getScoreboardSign().setLine(2, "§7Carte : §b" + SkyRush.instance.mapGameInfos.getMapName());
					gamePlayer.getScoreboardSign().setLine(1, "§b");
					gamePlayer.getScoreboardSign().setLine(0, "§6play.golemamc.net");
					
					// Message de Récompenses.
					gamePlayer.sendPlayerCache();
					gamePlayer.rewardMessage();
				}

				// Fin de Game.
				Bukkit.getScheduler().runTaskLater(SkyRush.instance, new Runnable() {
					@Override
					public void run() {
						for (Player playerOnline : Bukkit.getOnlinePlayers()) {
							SwitchServer.sendPlayerToLobby(playerOnline, true);
						}
						Bukkit.getScheduler().runTaskLater(SkyRush.instance, new Runnable() {
							@Override
							public void run() {
								Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
							}
						}, 20 * 2L);

					}
				}, 20 * 10L);
			}
		}
	}
}
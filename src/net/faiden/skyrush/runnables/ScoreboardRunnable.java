package net.faiden.skyrush.runnables;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.teams.TeamInfo;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.golema.database.support.GameStatus;

public class ScoreboardRunnable extends BukkitRunnable {

	public ScoreboardRunnable() {}

	@Override
	public void run() {
		for (Player playerOnline : Bukkit.getOnlinePlayers()) {
			if (SkyRush.getGamePlayer(playerOnline) != null) {
				GamePlayer gamePlayer = SkyRush.getGamePlayer(playerOnline);
				switch (GameStatus.getStatus()) {
				case LOBBY:
					gamePlayer.getScoreboardSign().setLine(10, "§7Joueurs : §f" + Bukkit.getOnlinePlayers().size()
							+ ChatColor.GRAY + "/" + ChatColor.WHITE + Bukkit.getMaxPlayers());
					gamePlayer.getScoreboardSign().setLine(3, ChatColor.GRAY + "Kit : §e" + gamePlayer.getKitsName());
					break;
				case GAME:

					// ArmorStand
					for (TeamInfo teamInfo : TeamInfo.values()) {
						TeamManager.armorStandTeamList.get("live." + teamInfo.getName())
								.setCustomName(ChatColor.RED + "" + ChatColor.BOLD
										+ ((double) TeamManager.golemTeamList.get(teamInfo).getGolem().getHealth())
										+ " ❤");
					}

					// Spectateur.
					if (gamePlayer.isSpectator()) {

						// Détection du Golem Rouge.
						if (!(TeamManager.golemTeamList.get(TeamInfo.RED).getGolem().isDead())) {
							gamePlayer.getScoreboardSign().setLine(7, TeamInfo.RED.getChatColor() + "■ "
									+ ChatColor.GRAY + "Golem : " + TeamInfo.RED.getChatColor()
									+ ((double) TeamManager.golemTeamList.get(TeamInfo.RED).getGolem().getHealth())
									+ " ❤");
						} else {
							int playerInTeam = TeamManager.playerTeamList.get(TeamInfo.RED).size();
							gamePlayer.getScoreboardSign().setLine(7,
									TeamInfo.RED.getChatColor() + "■ " + ChatColor.GRAY + getPlayerString(playerInTeam)
											+ ": " + TeamInfo.RED.getChatColor() + playerInTeam + " ⚑");
						}

						// Détection du Golem Bleu.
						if (!(TeamManager.golemTeamList.get(TeamInfo.BLEUE).getGolem().isDead())) {
							gamePlayer.getScoreboardSign().setLine(6, TeamInfo.BLEUE.getChatColor() + "■ "
									+ ChatColor.GRAY + "Golem : " + TeamInfo.BLEUE.getChatColor()
									+ ((double) TeamManager.golemTeamList.get(TeamInfo.BLEUE).getGolem().getHealth())
									+ " ❤");
						} else {
							int playerInTeam = TeamManager.playerTeamList.get(TeamInfo.BLEUE).size();
							gamePlayer.getScoreboardSign().setLine(6,
									TeamInfo.BLEUE.getChatColor() + "■ " + ChatColor.GRAY
											+ getPlayerString(playerInTeam) + ": " + TeamInfo.BLEUE.getChatColor()
											+ playerInTeam + " ⚑");
						}

						// Joueur en vie.
					} else {
						
						// Actualisation du Kills/Morts.
						gamePlayer.getScoreboardSign().setLine(11,
								ChatColor.GRAY + "Tués : " + ChatColor.GREEN + gamePlayer.getKills());
						gamePlayer.getScoreboardSign().setLine(10,
								ChatColor.GRAY + "Mort : " + ChatColor.RED + gamePlayer.getDeaths());
						
						// Détection du Golem Rouge.
						if (!(TeamManager.golemTeamList.get(TeamInfo.RED).getGolem().isDead())) {
							gamePlayer.getScoreboardSign().setLine(6, TeamInfo.RED.getChatColor() + "■ "
									+ ChatColor.GRAY + "Golem : " + TeamInfo.RED.getChatColor()
									+ ((double) TeamManager.golemTeamList.get(TeamInfo.RED).getGolem().getHealth())
									+ " ❤");
						} else {
							int playerInTeam = TeamManager.playerTeamList.get(TeamInfo.RED).size();
							gamePlayer.getScoreboardSign().setLine(6,
									TeamInfo.RED.getChatColor() + "■ " + ChatColor.GRAY + getPlayerString(playerInTeam)
											+ ": " + TeamInfo.RED.getChatColor() + playerInTeam + " ⚑");
						}

						// Détection du Golem Bleu.
						if (!(TeamManager.golemTeamList.get(TeamInfo.BLEUE).getGolem().isDead())) {
							gamePlayer.getScoreboardSign().setLine(5, TeamInfo.BLEUE.getChatColor() + "■ "
									+ ChatColor.GRAY + "Golem : " + TeamInfo.BLEUE.getChatColor()
									+ ((double) TeamManager.golemTeamList.get(TeamInfo.BLEUE).getGolem().getHealth())
									+ " ❤");
						} else {
							int playerInTeam = TeamManager.playerTeamList.get(TeamInfo.BLEUE).size();
							gamePlayer.getScoreboardSign().setLine(5,
									TeamInfo.BLEUE.getChatColor() + "■ " + ChatColor.GRAY
											+ getPlayerString(playerInTeam) + ": " + TeamInfo.BLEUE.getChatColor()
											+ playerInTeam + " ⚑");
						}
					}
					break;
				default:
					break;
				}
			}
		}
	}

	/**
	 * Récupérer la bonne String du Joueur.
	 * 
	 * @param countPlayer
	 * @return
	 */
	private String getPlayerString(Integer countPlayer) {
		return countPlayer == 1 ? "Joueur" : "Joueurs";
	}
}
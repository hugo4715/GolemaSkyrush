package net.faiden.skyrush.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.teams.TeamInfo;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.rank.Rank;
import net.golema.database.support.GameStatus;

public class PlayerChatListener implements Listener {

	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent event) {

		// Initialisation des Variables d'un Joueur.
		Player player = event.getPlayer();
		GolemaPlayer golemaPlayer = GolemaPlayer.getGolemaPlayer(player);
		String message = event.getMessage();
		event.setCancelled(true);

		// (t)Chat en fonction du Status.
		switch (GameStatus.getStatus()) { 
		case LOBBY: 
			Bukkit.broadcastMessage(golemaPlayer.getRank().getChatColor() + golemaPlayer.getRank().getPrefix()
					+ getSpaceAllow(golemaPlayer.getRank()) + player.getName() + ChatColor.WHITE + ": " + message);
			break;
		case GAME:

			// Détection si le Joueur est en mode Spectateur.
			if (SkyRush.getGamePlayer(player).isSpectator()) {
				for (Player playerOnline : Bukkit.getOnlinePlayers()) {
					if (SkyRush.getGamePlayer(playerOnline).isSpectator()) {
						playerOnline.sendMessage(ChatColor.GRAY + "[SPEC] " + player.getName() + ": " + message);
					}
				}
				return;
			}

			// Envoie du message d'un Joueur uniquement à tout les Joueurs.
			if ((message.startsWith("@") || (message.startsWith("!")))) {
				Bukkit.broadcastMessage(SkyRush.getGamePlayer(player).getTeamInfo().getChatColor() + "["
						+ SkyRush.getGamePlayer(player).getTeamInfo().getName() + "] " + player.getName()
						+ ChatColor.WHITE + ": " + message.substring(1));

				// Envoie du message d'un Joueur uniquement à sa propre Team.
			} else {
				for (Player playerOnline : Bukkit.getOnlinePlayers()) {
					TeamInfo teamInfo = SkyRush.getGamePlayer(player).getTeamInfo();
					if (TeamManager.isPlayerInTeam(playerOnline, teamInfo)) {
						playerOnline.sendMessage(
								ChatColor.GRAY + "(Privé) " + SkyRush.getGamePlayer(player).getTeamInfo().getChatColor()
										+ "[" + SkyRush.getGamePlayer(player).getTeamInfo().getName() + "] "
										+ player.getName() + ChatColor.WHITE + ": " + message);
					}
				}
			}
			break;
		case FINISH:
			Bukkit.broadcastMessage(golemaPlayer.getRank().getChatColor() + golemaPlayer.getRank().getPrefix()
					+ getSpaceAllow(golemaPlayer.getRank()) + player.getName() + ChatColor.WHITE + ": " + message);
			break;
		default:
			break;
		}
	}

	/**
	 * Espace en fonction du Rank.
	 * 
	 * @param rank
	 * @return
	 */
	private String getSpaceAllow(Rank rank) {
		return rank.getIdentificatorName().equals(Rank.PLAYER.getIdentificatorName()) ? "" : " ";
	}
}
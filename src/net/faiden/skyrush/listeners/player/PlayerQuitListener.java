package net.faiden.skyrush.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.WinManager;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.golema.api.builder.titles.ActionBarBuilder;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.support.GameStatus;
import net.golema.database.support.MessagesUtils;

public class PlayerQuitListener implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		// Initialisation de Variables.
		Player player = event.getPlayer();
		GolemaPlayer golemaPlayer = GolemaPlayer.getGolemaPlayer(player);
		GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
 
		// Paramètres du Left.
		gamePlayer.sendPlayerCache();
		gamePlayer.rewardMessage();
		TeamManager.removePlayerInAllTeam(player);
		SkyRush.getGamePlayersMap().remove(player.getName());
		event.setQuitMessage(null);
 
		// Paramètres suivant le Status.
		switch (GameStatus.getStatus()) {
		case LOBBY:
			Bukkit.getOnlinePlayers().forEach(playerOnline -> {
				new ActionBarBuilder(golemaPlayer.getRank().getChatColor() + golemaPlayer.getRank().getPrefix()
						+ MessagesUtils.getRankSpace(golemaPlayer.getRank()) + player.getName() + ChatColor.YELLOW
						+ " a quitté la partie, " + ChatColor.AQUA + (Bukkit.getOnlinePlayers().size() - 1)
						+ " Joueur(s) " + ChatColor.YELLOW + "en partie.").sendTo(playerOnline);
			});
			break;
		case GAME:
			new WinManager();
			break;
		default:
			break;
		}
	}
}
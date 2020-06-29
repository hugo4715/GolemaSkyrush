package net.faiden.skyrush.runnables;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.golema.database.support.GameStatus;

public class GameRunnable extends BukkitRunnable {

	public static Integer gameTimer = 0;

	public GameRunnable() {
	}

	@Override
	public void run() {

		// Stop le timer � la fin de la Game.
		if (!(GameStatus.isStatus(GameStatus.GAME))) {
			this.cancel();
			return;
		}

		// Gestion du Timer et du Scoreboard.
		for (Player playerOnline : Bukkit.getOnlinePlayers()) {
			GamePlayer gamePlayer = SkyRush.getGamePlayer(playerOnline);
			
			// Joueur est en spectateur.
			if ((gamePlayer != null) && (gamePlayer.isSpectator())) {
				gamePlayer.getScoreboardSign().setLine(4,
						"§7Durée : §b" + new SimpleDateFormat("mm:ss").format(new Date(gameTimer * 1000)));
			}
			
			// Joueur n'est pas en spectateur.
			if ((gamePlayer != null) && (!(gamePlayer.isSpectator()))) {
				gamePlayer.getScoreboardSign().setObjectiveName(
						"§6§lSkyRush§f│ §7" + new SimpleDateFormat("mm:ss").format(new Date(gameTimer * 1000)));
				gamePlayer.setTokens(gamePlayer.getTokens() + 2);
				gamePlayer.getScoreboardSign().setLine(8, ChatColor.WHITE + "Tokens : " + ChatColor.YELLOW + ""
						+ ChatColor.BOLD + gamePlayer.getTokens() + " ✸");
				gamePlayer.getScoreboardSign().setLine(3, ChatColor.GRAY + "Kit : §e" + gamePlayer.getKitsName());
			}
		}
		gameTimer++;

	}
}
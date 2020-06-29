package net.faiden.skyrush.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class PlayerKillsBoard implements ScoreboardManager {

	public static Map<Player, PlayerKillsBoard> scoreboardtabkill = new HashMap<>();
	public Player player;
	public Scoreboard scoreboard;
	public Objective objective;
	private String name = "kill";

	public PlayerKillsBoard(Player player) {
		this.player = player;
		this.scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		if (scoreboardtabkill.containsKey(player)) {
			return;
		}
		scoreboardtabkill.put(player, this);
		Random random = new Random();
		this.name = ("kill." + random.nextInt(1000000000));
		this.objective = this.scoreboard.registerNewObjective(this.name, "playerKillCount");
		this.objective = this.scoreboard.getObjective(this.name);
		this.objective.setDisplaySlot(DisplaySlot.PLAYER_LIST);
	}

	public static PlayerKillsBoard getScoreboard(Player player) {
		return (PlayerKillsBoard) scoreboardtabkill.get(player);
	}

	public Scoreboard getMainScoreboard() {
		return this.scoreboard;
	}

	public Scoreboard getNewScoreboard() {
		return null;
	}
}
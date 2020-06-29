package net.faiden.skyrush.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager.Profession;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.GameUtils;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.teams.TeamInfo;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.faiden.skyrush.runnables.GameRunnable;
import net.faiden.skyrush.utils.PlayerKillsBoard;
import net.golema.api.builder.PNJBuilder;
import net.golema.api.utils.PlayerUtils;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.game.skyrush.stats.SkyRushStatsMode;
import net.golema.database.golemaplayer.game.skyrush.stats.SkyRushStatsType;
import net.golema.database.support.GameStatus;
import net.golema.database.support.boards.TeamsTagsManager;
import net.golema.database.support.builder.TitleBuilder;
import tk.hugo4715.golema.timesaver.TimeSaverAPI;
import tk.hugo4715.golema.timesaver.server.ServerStatus;

public class GameManager {

	/**
	 * Lancement de la partie.
	 */
	public GameManager() {

		// Update de World.
		Bukkit.getWorld("world").setTime(6000);
		Bukkit.getWorld("world").setStorm(false);
		
		// Initialisation de la partie.
		GameStatus.setStatus(GameStatus.GAME);
		TimeSaverAPI.setJoinable(false); 
		TimeSaverAPI.setServerStatus(ServerStatus.INGAME); 

		// Annonce du Lancement de la partie.
		Bukkit.broadcastMessage("");
		Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "?" + ChatColor.WHITE
				+ "] La partie a désormais été démarrée. Détruisez le Golem adverse puis éliminez l'équipe.");

		// Paramètres des Joueurs.
		for (Player playerOnline : Bukkit.getOnlinePlayers()) {

			// Recupération du GamePlayer.
			GamePlayer gamePlayer = SkyRush.getGamePlayer(playerOnline);
			GolemaPlayer.getGolemaPlayer(playerOnline).getSkyRushPlayer()
					.addPlayerStats(SkyRushStatsMode.SKYRUSH_2TEAMS, SkyRushStatsType.GAMEPLAYED, 1);
			gamePlayer.setPlayed(true);

			// Gestion de la Team et de la téléportation du Joueur.
			TeamManager.putInARandomTeam(playerOnline);
			playerOnline.teleport(TeamManager.getTeamLocation(playerOnline));
			TeamInfo teamInfo = TeamManager.getPlayerTeam(playerOnline);

			// Initialisation du Joueur.
			PlayerUtils.clearInventory(playerOnline);
			playerOnline.setMaxHealth(20.0d);
			playerOnline.setHealth(20.0d);
			playerOnline.setFoodLevel(20);
			playerOnline.setFlying(false);
			playerOnline.setAllowFlight(false);
			playerOnline.setGameMode(GameMode.SURVIVAL);
			TeamsTagsManager.setNameTag(playerOnline, teamInfo.getName(), teamInfo.getChatColor() + "");
			new PlayerKillsBoard(playerOnline);

			// Title informatif du démarrage.
			new TitleBuilder(ChatColor.YELLOW + "SkyRush", ChatColor.AQUA + "Téléportation en cours...")
					.send(playerOnline);

			// Inscription de la Team dans une Liste.
			SkyRush.getGamePlayer(playerOnline).setTeamInfo(TeamManager.getPlayerTeam(playerOnline));
			if (!SkyRush.instance.teamInGameList.contains(TeamManager.getPlayerTeam(playerOnline))) {
				SkyRush.instance.teamInGameList.add(TeamManager.getPlayerTeam(playerOnline));
			}

			// Mise en place du nouveau Scoreboard.
			gamePlayer.getScoreboardSign().setObjectiveName("§6§lSkyRush§f│ §700:00");
			gamePlayer.getScoreboardSign().setLine(13, "");
			gamePlayer.getScoreboardSign().setLine(12,
					ChatColor.GRAY + "Alliance : " + teamInfo.getChatColor() + teamInfo.getName());
			gamePlayer.getScoreboardSign().setLine(11,
					ChatColor.GRAY + "Tués : " + ChatColor.GREEN + gamePlayer.getKills());
			gamePlayer.getScoreboardSign().setLine(10,
					ChatColor.GRAY + "Mort : " + ChatColor.RED + gamePlayer.getDeaths());
			gamePlayer.getScoreboardSign().setLine(9, "");
			gamePlayer.getScoreboardSign().setLine(8, ChatColor.WHITE + "Tokens : " + ChatColor.YELLOW + ""
					+ ChatColor.BOLD + gamePlayer.getTokens() + " ✸");
			gamePlayer.getScoreboardSign().setLine(7, "");
			gamePlayer.getScoreboardSign().setLine(6, TeamInfo.RED.getChatColor() + "■ " + ChatColor.GRAY + "Golem: "
					+ TeamInfo.RED.getChatColor() + "800.0 ❤");
			gamePlayer.getScoreboardSign().setLine(5, TeamInfo.BLEUE.getChatColor() + "■ " + ChatColor.GRAY + "Golem: "
					+ TeamInfo.BLEUE.getChatColor() + "800.0 ❤");
			gamePlayer.getScoreboardSign().setLine(4, "§b");

			// Envoie du Kit au Joueur.
			gamePlayer.sendPlayerKit();
		}

		// Initialisation des Golems des Teams.
		SkyRush.loadCuboid();
		TeamManager.golemTeamList.put(TeamInfo.RED,
				new GolemEntity(TeamInfo.RED.getName(), GameUtils.REDGOLEM_LOCATION));
		TeamManager.golemTeamList.put(TeamInfo.BLEUE,
				new GolemEntity(TeamInfo.BLEUE.getName(), GameUtils.BLUEGOLEM_LOCATION));

		// Initialisation des Villager.
		Bukkit.getScheduler().runTaskLater(SkyRush.instance, new Runnable() {
			@Override
			public void run() {
				new PNJBuilder(GameUtils.BLUEPNJ_LOCATION, "§d§l❂ Shop | Tokens ❂", "§7Amélioration", Profession.FARMER,
						Bukkit.getWorld("world")).createVillager();;
				new PNJBuilder(GameUtils.REDPNJ_LOCATION, "§d§l❂ Shop | Tokens ❂", "§7Amélioration", Profession.FARMER,
						Bukkit.getWorld("world")).createVillager();;
			}
		}, 20L);

		// Envoie des ArmorStand.
		for (TeamInfo teamInfo : TeamInfo.values()) {

			// ArmorStand du texte.
			Location locationText = TeamManager.getGolemLocation(teamInfo);
			locationText.setY(locationText.getY() + 1.0);
			ArmorStand armorStandText = (ArmorStand) Bukkit.getWorld("world").spawnEntity(locationText,
					EntityType.ARMOR_STAND);
			armorStandText.setBasePlate(false);
			armorStandText.setVisible(false);
			armorStandText.setGravity(false);
			armorStandText.setCustomNameVisible(true);
			armorStandText.setCustomName(
					ChatColor.YELLOW + "Golem de l'équipe: " + teamInfo.getChatColor() + teamInfo.getName());

			// ArmorStand de la vie.
			Location locationHealth = TeamManager.getGolemLocation(teamInfo);
			locationHealth.setY(locationHealth.getY() - 0.30);
			ArmorStand armorStandHealth = (ArmorStand) Bukkit.getWorld("world").spawnEntity(locationHealth,
					EntityType.ARMOR_STAND);
			armorStandHealth.setBasePlate(false);
			armorStandHealth.setVisible(false);
			armorStandHealth.setGravity(false);
			armorStandHealth.setCustomNameVisible(true);
			armorStandHealth.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + "800.0 ❤");

			// Stockage des ArmorStand
			TeamManager.armorStandTeamList.put("text." + teamInfo.getName(), armorStandText);
			TeamManager.armorStandTeamList.put("live." + teamInfo.getName(), armorStandHealth);
		}

		// Lancement du Timer de la partie.
		new GameRunnable().runTaskTimer(SkyRush.instance, 0L, 20L);
	}
}

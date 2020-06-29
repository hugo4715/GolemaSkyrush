package net.faiden.skyrush.listeners.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.teams.TeamInfo;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.golema.api.utils.PlayerUtils;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.currency.Currency;
import net.golema.database.support.builder.JsonMessageBuilder;
import net.golema.database.support.builder.TitleBuilder;
import net.minecraft.server.v1_8_R3.PacketPlayInClientCommand;

public class PlayerDeathListener implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDeath(PlayerDeathEvent event) {

		// Initilisation des variables et paramètres.
		Player player = event.getEntity();
		GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
		event.setDeathMessage(null); 
 
		// Affichage du message de mort.
		// Mort d'un Joueur par un Joueur. 
		if (SkyRush.instance.playerKillerMap.get(player) != null) {
			Player playerKiller = SkyRush.instance.playerKillerMap.get(player);
			Bukkit.broadcastMessage(SkyRush.getGamePlayer(player).getTeamInfo().getChatColor() + "["
					+ SkyRush.getGamePlayer(player).getTeamInfo().getName() + "] " + player.getName() + ChatColor.YELLOW
					+ " a été tué par " + SkyRush.getGamePlayer(playerKiller).getTeamInfo().getChatColor() + "["
					+ SkyRush.getGamePlayer(playerKiller).getTeamInfo().getName() + "] " + playerKiller.getName()
					+ ChatColor.YELLOW + ".");
			SkyRush.instance.playerKillerMap.remove(player);

			// Envoie des Coins au target.
			GamePlayer gamePlayerShooter = SkyRush.getGamePlayer(playerKiller);
			int coinsReward = 2;
			gamePlayerShooter.setKills(gamePlayerShooter.getKills() + 1);
			gamePlayerShooter.setCoinsWin(gamePlayerShooter.getCoinsWin() + coinsReward);
			GolemaPlayer.getGolemaPlayer(playerKiller).addCoinsGame(Currency.GCOINS, coinsReward, "Kill de " + player.getName());
			
			// Ajout de Tokens.
			gamePlayerShooter.setTokens(gamePlayerShooter.getTokens() + 10);
			new TitleBuilder("§fGain de Tokens : §e§l10 ✸").send(playerKiller);;
			
			// Effect sur le kit Vampire.
			if((gamePlayer != null) && (gamePlayer.getKitAbstract() != null)) {
				if(gamePlayer.getKitAbstract().name.equalsIgnoreCase("Vampire")) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1), true); }
			}
			
		// Mort d'un joueur seul.
		} else {
			Bukkit.broadcastMessage(SkyRush.getGamePlayer(player).getTeamInfo().getChatColor() + "["
					+ SkyRush.getGamePlayer(player).getTeamInfo().getName() + "] " + player.getName() + ChatColor.YELLOW
					+ " est mort.");
		}

		// Paramètres du Spectateur.
		Bukkit.getWorld("world").strikeLightningEffect(player.getLocation());
		event.setKeepInventory(true);
		gamePlayer.setDeaths(gamePlayer.getDeaths() + 1);
		respawnAuto(player, 1);
	}

	/**
	 * Gestion de l'AutoRespawn d'un Joueur.
	 * 
	 * @param player
	 * @param time
	 */
	public void respawnAuto(final Player player, int time) {
		Bukkit.getScheduler().runTaskLater(SkyRush.instance, new Runnable() {
			public void run() {

				// Gérer le Respawn automatique.
				((CraftPlayer) player).getHandle().playerConnection
						.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));

				// Vérifier si un Joueur est définitivement mort.
				TeamInfo teamInfo = TeamManager.getPlayerTeam(player);
				if (TeamManager.isPlayerInTeam(player, teamInfo)
						&& (TeamManager.golemTeamList.get(teamInfo).getGolem().isDead())) {
					GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
					gamePlayer.setSpectator();

					// Scoreboard du Spectateur.
					gamePlayer.getScoreboardSign().setLine(13, "§0");
					gamePlayer.getScoreboardSign().setLine(12, "§7Type : §e2 Teams");
					gamePlayer.getScoreboardSign().setLine(11, "§d");
					gamePlayer.getScoreboardSign().setLine(10, ChatColor.RED + "Vous êtes en mode");
					gamePlayer.getScoreboardSign().setLine(9, ChatColor.RED + "spectateur...");
					gamePlayer.getScoreboardSign().setLine(8, "§c");
					gamePlayer.getScoreboardSign().setLine(7,
							TeamInfo.RED.getChatColor() + "■ " + ChatColor.GRAY + "Golem : " + ChatColor.DARK_GRAY + "load...");
					gamePlayer.getScoreboardSign().setLine(6, TeamInfo.BLEUE.getChatColor() + "■ " + ChatColor.GRAY + "Golem : "
							+ ChatColor.DARK_GRAY + "load...");
					gamePlayer.getScoreboardSign().setLine(5, "§b");
					gamePlayer.getScoreboardSign().setLine(4, "§7Durée : §b00:00");
					gamePlayer.getScoreboardSign().setLine(3,
							ChatColor.GRAY + "Kit : §eDéfaut");
					gamePlayer.getScoreboardSign().setLine(2,
							ChatColor.GRAY + "Carte : " + ChatColor.AQUA + SkyRush.instance.mapGameInfos.getMapName());
					gamePlayer.getScoreboardSign().setLine(1, "§a");
					gamePlayer.getScoreboardSign().setLine(0, ChatColor.GOLD + "play.golemamc.net");

					// Message pour un Spectateur qui est éliminé.
					new TitleBuilder("§4● §cMort §4●", "§7Pouf ! Bon retour parmi nous.").send(player);
					player.sendMessage("");
					player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "✖" + ChatColor.GRAY + "│ "
							+ ChatColor.YELLOW + "Vous avez été éliminé !");
					JsonMessageBuilder jsonMessageBuilder = new JsonMessageBuilder();
					jsonMessageBuilder.newJComp(ChatColor.AQUA + "Souhaitez-vous rejouer ? ").build(jsonMessageBuilder);
					jsonMessageBuilder
							.newJComp(ChatColor.GRAY + "[" + ChatColor.GREEN + "Rejouer - ➲" + ChatColor.GRAY + "]")
							.addCommandExecutor("/lobby")
							.addHoverText(ChatColor.YELLOW + "Rejoindre une nouvelle partie.")
							.build(jsonMessageBuilder);
					jsonMessageBuilder.send(player);
					player.sendMessage("");
					return;
				}

				// AntiSpawn Kill System
				SkyRush.instance.playerSpawnKillList.add(player);
				stopSpawnKill(player);

				// Effectuer le Respaw d'un Joueur.
				player.teleport(TeamManager.getTeamLocation(player));
				PlayerUtils.clearInventory(player);
				PlayerUtils.removeAllPotionEffect(player);
				SkyRush.getGamePlayer(player).sendPlayerKit();
				new TitleBuilder("", ChatColor.GOLD + "Respawn...").send(player);
			}
		}, time);
	}

	/**
	 * Supprimer le bypass du SpawnKill.
	 * 
	 * @param player
	 */
	public void stopSpawnKill(Player player) {
		Bukkit.getScheduler().runTaskLater(SkyRush.instance, new Runnable() {
			@Override
			public void run() {
				SkyRush.instance.playerSpawnKillList.remove(player);
			}
		}, 80L);
	}
}
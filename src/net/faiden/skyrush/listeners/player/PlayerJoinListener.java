package net.faiden.skyrush.listeners.player;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.GameUtils;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.runnables.LobbyRunnable;
import net.golema.api.builder.titles.ActionBarBuilder;
import net.golema.api.utils.PlayerUtils;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.levels.LevelMechanic;
import net.golema.database.golemaplayer.levels.LevelType;
import net.golema.database.support.GameStatus;
import net.golema.database.support.MessagesUtils;
import net.golema.database.support.boards.TeamsTagsManager;
import net.golema.database.support.builder.TitleBuilder;
import net.golema.database.support.builder.items.ItemBuilder;

public class PlayerJoinListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {

		// Initialisation des Variables.
		Player player = event.getPlayer();
		GolemaPlayer golemaPlayer = GolemaPlayer.getGolemaPlayer(player);
		GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
		int playerExperience = golemaPlayer.getExperienceLevel(LevelType.SKYRUSH_LEVEL);
		event.setJoinMessage(null); 

		// Vérifier si la partie n'a pas déjà démarrée.
		if (!(GameStatus.isStatus(GameStatus.LOBBY))) {
			gamePlayer.setSpectator(); 

			// Message pour un Spectateur qui rejoins.
			player.sendMessage("");
			player.sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "ATTENTION" + ChatColor.GRAY + "│ "
					+ ChatColor.YELLOW + "Il vous est impossible de jouer !");
			player.sendMessage(ChatColor.AQUA + "Vous avez rejoint la partie en mode spectateur.");
			player.sendMessage("");
			return;
		}
		
		// Update de World.
		Bukkit.getWorld("world").setTime(6000);
		Bukkit.getWorld("world").setStorm(false);
		
		// Informations message sur les Statistiques.
		player.sendMessage(" ");
		golemaPlayer.sendCenteredMessage(
				ChatColor.WHITE + "[" + ChatColor.AQUA + "?" + ChatColor.WHITE + "] Informations sur vos statistiques");
		player.sendMessage(" ");
		golemaPlayer.sendCenteredMessage("§eVous possèdez " + ChatColor.GOLD + "" + ChatColor.BOLD
				+ LevelMechanic.getPlayerLevel(playerExperience) + "✯" + ChatColor.YELLOW + " sur le jeu SkyRush.");
		golemaPlayer.sendCenteredMessage(ChatColor.GRAY + "Classement : " + ChatColor.AQUA
				+ "https://stats.golemamc.net/player/" + player.getName() + "/");
		player.sendMessage(" ");

		// Paramètres liés aux Designs de la partie.
		new TitleBuilder(ChatColor.GOLD + "SkyRush", ChatColor.YELLOW + "Une idée de @GolemaMC.").send(player);
		new ActionBarBuilder(ChatColor.GRAY + "Développé par : " + ChatColor.DARK_GREEN + "Faiden").withStay(4)
				.sendTo(player);
		Bukkit.broadcastMessage(SkyRush.instance.getPrefixGame() + " " + golemaPlayer.getRank().getChatColor()
				+ golemaPlayer.getRank().getPrefix() + MessagesUtils.getRankSpace(golemaPlayer.getRank())
				+ player.getName() + ChatColor.YELLOW + " a rejoint la partie ! " + ChatColor.GREEN + "("
				+ Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ")");

		// Gestion du design du TABLIST.
		TeamsTagsManager.setNameTag(player,
				LevelMechanic.getPlayerLevel(playerExperience) + new Random().nextInt(48545215) + "",
				ChatColor.GRAY + "" + LevelMechanic.getPlayerLevel(playerExperience) + "✯" + ChatColor.WHITE + "│ "
						+ golemaPlayer.getRank().getChatColor());

		// Paramètres de connexions à la partie du Joueur.
		player.setMaxHealth(20.0d);
		player.setHealth(20.0d);
		player.setFoodLevel(20);
		player.setWalkSpeed(0.2f);
		player.setLevel(LobbyRunnable.lobbyTimer);
		player.setGameMode(GameMode.ADVENTURE);
		player.teleport(GameUtils.LOBBY_LOCATION);
		player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0f, 1.0f);

		// Paramètres liés au Joueur lui-même.
		PlayerUtils.removeAllPotionEffect(player);
		PlayerUtils.clearInventory(player);
		player.getInventory().setHelmet(new ItemStack(Material.AIR));
		player.getInventory().setChestplate(new ItemStack(Material.AIR));
		player.getInventory().setLeggings(new ItemStack(Material.AIR));
		player.getInventory().setBoots(new ItemStack(Material.AIR));
		player.getInventory()
				.setItem(0,
						new ItemBuilder().type(Material.NAME_TAG).name(ChatColor.GREEN + "" + ChatColor.BOLD
								+ "Sélecteur de kits " + ChatColor.DARK_GRAY + " ▏ " + ChatColor.GRAY + " Clic-droit")
								.build());
		player.getInventory().setItem(4, GameUtils.itemTeamSelector());
		player.getInventory()
				.setItem(8,
						new ItemBuilder().type(Material.BED).name(ChatColor.RED + "" + ChatColor.BOLD
								+ "Retourner au hub " + ChatColor.DARK_GRAY + " ▏ " + ChatColor.GRAY + " Clic-droit")
								.build());

		// Lancement du Timer pour le démarrage du Jeu.
		if ((Bukkit.getOnlinePlayers().size() >= SkyRush.instance.getMinPlayers()) && (!LobbyRunnable.isStarted)) {
			new LobbyRunnable().runTaskTimer(SkyRush.instance, 0L, 20L);
			LobbyRunnable.isStarted = true;
		}
	}
}
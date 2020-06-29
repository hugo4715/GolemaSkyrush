package net.faiden.skyrush;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import net.faiden.skyrush.manager.WinManager;
import net.faiden.skyrush.manager.kits.KitAbstract;
import net.faiden.skyrush.manager.teams.TeamInfo;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.faiden.skyrush.runnables.LobbyRunnable;
import net.golema.api.builder.board.ScoreboardSign;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.game.skyrush.stats.SkyRushStatsMode;
import net.golema.database.golemaplayer.game.skyrush.stats.SkyRushStatsType;
import net.golema.database.golemaplayer.levels.LevelMechanic;
import net.golema.database.golemaplayer.levels.LevelType;
import net.golema.database.support.GameStatus;
import net.golema.database.support.boards.TeamsTagsManager;
import net.golema.database.support.builder.items.ItemBuilder;
import net.golema.database.support.utils.GolemaLogger;

public class GamePlayer {

	public Player player;
	private GolemaPlayer golemaPlayer;

	private int coinsWin;
	private int creditsWin;
	private int experience;

	private int kills;
	private int assists;
	private int timePlayer;
	private int deaths;
	private int blocPlaces;
	private int blocBreak;

	private int tokens;
	
	private TeamInfo teamInfo = null;
	private KitAbstract kitAbstract = null;

	private boolean played;
	private boolean isSpectator;
	private ScoreboardSign scoreboardSign;
 
	/**
	 * Constructeur du GamePlayer.
	 * 
	 * @param player
	 */
	public GamePlayer(Player player) {

		// Variables liés au Joueur.
		this.player = player;
		this.golemaPlayer = GolemaPlayer.getGolemaPlayer(player);

		// Statistiques de la partie.
		this.coinsWin = 0;
		this.creditsWin = 0;
		this.experience = 0;

		this.kills = 0;
		this.assists = 0;
		this.timePlayer = 0;
		this.deaths = 0;
		this.blocPlaces = 0;
		this.blocBreak = 0;
		this.played = false;
		
		// Settings de parties.
		this.tokens = 200;

		// Définir le mode spectateur d'un Joueur.
		if (GameStatus.isStatus(GameStatus.LOBBY)) {
			this.isSpectator = false;
		} else {
			this.isSpectator = true;
		}

		// Mise en place du Scoreboard.
		this.scoreboardSign = new ScoreboardSign(player, player.getName());
		this.makeScoreboard();

		// Log de création.
		GolemaLogger.logDebug("[GamePlayer] Created " + player.getName() + " succes.");
	}
	
	/**
	 * Faire un échange avec le PNJ.
	 * 
	 * @param price
	 * @param itemStack
	 */
	public void makeTrade(int price, ItemStack itemStack) {
		
		// Vérification du prix.
		if(price > tokens) {
			player.sendMessage("§d§lShop §8§l» §cVous n'avez pas assez de Tokens.");
			return;
		}
		
		// Envoyer le produit.
		
		this.tokens = tokens - price;
		player.getInventory().addItem(itemStack);
		player.sendMessage("§d§lShop §8§l» §aAchat réussi : §e" + itemStack.getType().name());
	}
	
	/**
	 * Créer le scoreboard du Joueur.
	 */
	private void makeScoreboard() {
		this.scoreboardSign
				.setObjectiveName(ChatColor.GOLD + "" + ChatColor.BOLD + "SkyRush");
		this.scoreboardSign.create();

		// Spectator
		if (isSpectator) {
			this.scoreboardSign.setLine(13, "§0");
			this.scoreboardSign.setLine(12, "§7Type : §e2 Teams");
			this.scoreboardSign.setLine(11, "§d");
			this.scoreboardSign.setLine(10, ChatColor.RED + "Vous êtes en mode");
			this.scoreboardSign.setLine(9, ChatColor.RED + "spectateur...");
			this.scoreboardSign.setLine(8, "§c");
			this.scoreboardSign.setLine(7,
					TeamInfo.RED.getChatColor() + "■ " + ChatColor.GRAY + "Golem : " + ChatColor.DARK_GRAY + "load...");
			this.scoreboardSign.setLine(6, TeamInfo.BLEUE.getChatColor() + "■ " + ChatColor.GRAY + "Golem : "
					+ ChatColor.DARK_GRAY + "load...");
			this.scoreboardSign.setLine(5, "§b");
			this.scoreboardSign.setLine(4, "§7Durée : §b00:00");
			this.scoreboardSign.setLine(3,
					ChatColor.GRAY + "Kit : §eDéfaut");
			this.scoreboardSign.setLine(2,
					ChatColor.GRAY + "Carte : " + ChatColor.AQUA + SkyRush.instance.mapGameInfos.getMapName());
			this.scoreboardSign.setLine(1, "§a");
			this.scoreboardSign.setLine(0, ChatColor.GOLD + "play.golemamc.net");

			// Player
		} else {
			this.scoreboardSign.setLine(11, "§d");
			this.scoreboardSign.setLine(10,
					ChatColor.GRAY + "Joueurs: " + ChatColor.WHITE + Bukkit.getOnlinePlayers().size() + ChatColor.GRAY
							+ "/" + ChatColor.WHITE + Bukkit.getMaxPlayers());
			this.scoreboardSign.setLine(9, "§c");
			this.scoreboardSign.setLine(8, ChatColor.RED + "En attente de");
			this.scoreboardSign.setLine(7, ChatColor.RED + "joueurs...");
			this.scoreboardSign.setLine(6, "§b");
			this.scoreboardSign.setLine(5, ChatColor.GRAY + "Lancement : " + ChatColor.WHITE
					+ new SimpleDateFormat("mm:ss").format(new Date(LobbyRunnable.lobbyTimer * 1000)));
			this.scoreboardSign.setLine(4, "§b");
			this.scoreboardSign.setLine(3,
					ChatColor.GRAY + "Kit : §e" + getKitsName());
			this.scoreboardSign.setLine(2,
					ChatColor.GRAY + "Carte : " + ChatColor.AQUA + SkyRush.instance.mapGameInfos.getMapName());
			this.scoreboardSign.setLine(1, "§a");
			this.scoreboardSign.setLine(0, ChatColor.GOLD + "play.golemamc.net");
		}
	}

	/**
	 * Définir le joueur au status de Spectateur.
	 */
	public void setSpectator() {
		this.isSpectator = true;
		TeamManager.removePlayerInAllTeam(player);
		TeamsTagsManager.setNameTag(player, "§z§7SPECTATOR", ChatColor.GRAY + "[SPEC] ");
		player.setGameMode(GameMode.SPECTATOR);
		
		// Téléporter le Joueur.
		List<Player> playerList = new ArrayList<Player>();
		for(Player playerOnline : Bukkit.getOnlinePlayers()) {
			if(!(SkyRush.getGamePlayer(playerOnline).isSpectator)) {
				playerList.add(playerOnline);
			}
		}
		player.teleport(playerList.get(new Random().nextInt(playerList.size())).getLocation());
		
		// Détection de la Win.
		new WinManager();
	}

	/**
	 * Envoyer l'équipement par défaut au Joueur.
	 */
	public void sendPlayerKit() {
		player.getInventory().setItem(8, new ItemBuilder().type(Material.COMPASS)
				.name(ChatColor.GOLD + "■" + ChatColor.YELLOW + " Tracker de joueurs " + ChatColor.GOLD + "■").build());
		if (kitAbstract == null) {
			// Stuff par défaut.
			player.getInventory().setHelmet(getItemStackArmorLeather(Material.LEATHER_HELMET, teamInfo.getColor()));
			player.getInventory()
					.setChestplate(getItemStackArmorLeather(Material.LEATHER_CHESTPLATE, teamInfo.getColor()));
			player.getInventory().setLeggings(getItemStackArmorLeather(Material.LEATHER_LEGGINGS, teamInfo.getColor()));
			player.getInventory().setBoots(getItemStackArmorLeather(Material.LEATHER_BOOTS, teamInfo.getColor()));
			player.getInventory().addItem(new ItemBuilder().type(Material.STONE_SWORD).build());
			player.getInventory().addItem(new ItemBuilder().type(Material.STONE_PICKAXE).build());
			player.getInventory().addItem(new ItemBuilder().type(Material.STAINED_CLAY)
					.data((byte) teamInfo.getDataClay()).amount(96).build());
			player.getInventory().addItem(new ItemBuilder().type(Material.COOKED_BEEF).amount(6).build());
		} else {
			kitAbstract.sendKits(player);
		}
	}
	
	/**
	 * Récupérer le nom du Kit du Joueur.
	 * 
	 * @return
	 */
	public String getKitsName() {
		if(kitAbstract == null) return "Défaut";
		return kitAbstract.name;
	}

	/*
	 * Getter du GamePlayer.
	 */
	public GolemaPlayer getGolemaPlayer() {
		return golemaPlayer;
	}

	public int getCoinsWin() {
		return coinsWin;
	}

	public int getCreditsWin() {
		return creditsWin;
	}

	public int getExperience() {
		return experience;
	}

	public int getKills() {
		return kills;
	}

	public int getAssists() {
		return assists;
	}

	public int getTimePlayer() {
		return timePlayer;
	}

	public int getDeaths() {
		return deaths;
	}

	public int getBlocPlaces() {
		return blocPlaces;
	}

	public int getBlocBreak() {
		return blocBreak;
	}

	public int getTokens() {
		return tokens;
	}
	
	public boolean isPlayed() {
		return played;
	}

	public TeamInfo getTeamInfo() {
		return teamInfo;
	}

	public KitAbstract getKitAbstract() {
		return kitAbstract;
	}

	public boolean isSpectator() {
		return isSpectator;
	}

	public ScoreboardSign getScoreboardSign() {
		return scoreboardSign;
	}

	/**
	 * Setter du GamePlayer.
	 */
	public void setGolemaPlayer(GolemaPlayer golemaPlayer) {
		this.golemaPlayer = golemaPlayer;
	}

	public void setCoinsWin(int coinsWin) {
		this.coinsWin = coinsWin;
	}

	public void setCreditsWin(int creditsWin) {
		this.creditsWin = creditsWin;
	}

	public void setExperience(int experience) {
		this.experience = experience;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public void setAssists(int assists) {
		this.assists = assists;
	}

	public void setTimePlayer(int timePlayer) {
		this.timePlayer = timePlayer;
	}

	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}

	public void setBlocPlaces(int blocPlaces) {
		this.blocPlaces = blocPlaces;
	}

	public void setBlocBreak(int blocBreak) {
		this.blocBreak = blocBreak;
	}

	public void setTokens(int tokens) {
		this.tokens = tokens;
	}
	
	public void setPlayed(boolean played) {
		this.played = played;
	}

	public void setTeamInfo(TeamInfo teamInfo) {
		this.teamInfo = teamInfo;
	}

	public void setKitAbstract(KitAbstract kitAbstract) {
		this.kitAbstract = kitAbstract;
	}

	public void setScoreboardSign(ScoreboardSign scoreboardSign) {
		this.scoreboardSign = scoreboardSign;
	}

	/**
	 * Envoyer le cache final du Joueur.
	 */
	public void sendPlayerCache() {
		if (played) {
			golemaPlayer.getSkyRushPlayer().addPlayerStats(SkyRushStatsMode.SKYRUSH_2TEAMS, SkyRushStatsType.KILLS,
					kills);
			golemaPlayer.getSkyRushPlayer().addPlayerStats(SkyRushStatsMode.SKYRUSH_2TEAMS, SkyRushStatsType.DEATHS,
					deaths);
			golemaPlayer.getSkyRushPlayer().addPlayerStats(SkyRushStatsMode.SKYRUSH_2TEAMS,
					SkyRushStatsType.BLOCKSPLACES, blocPlaces);
			golemaPlayer.getSkyRushPlayer().addPlayerStats(SkyRushStatsMode.SKYRUSH_2TEAMS,
					SkyRushStatsType.BLOCKSBREAKS, blocBreak);
		}
	}

	/**
	 * Créer une armure en cuir de couleur custom.
	 * 
	 * @param material
	 * @param color
	 * @return
	 */
	public ItemStack getItemStackArmorLeather(Material material, Color color) {
		ItemStack item = new ItemStack(material);
		LeatherArmorMeta itemm = (LeatherArmorMeta) item.getItemMeta();
		itemm.setColor(color);
		item.setItemMeta(itemm);
		return item;
	}

	/**
	 * Envoyer le message de récompenses.
	 */
	public void rewardMessage() {
		if (played) {
			player.sendMessage("");
			player.sendMessage("§6§lRécapitulatif§f│ §aTout ce que vous devez savoir:");
			player.sendMessage("");
			player.sendMessage("§f● Statistiques:");
			player.sendMessage("§7■ §bKills §7» §f" + kills);
			player.sendMessage("§7■ §bMorts §7» §f" + deaths);
			player.sendMessage("");
			player.sendMessage("§f● Gains:");
			player.sendMessage("§7■ §e" + coinsWin + " Coins");
			player.sendMessage("§7■ §b" + creditsWin + " GolemaCrédits");
			player.sendMessage("§7■ §d" + experience + " Expériences");
			player.sendMessage("§7■ §7Progression de l'expérience ["
					+ LevelMechanic.getPlayerProgressBarLevel(golemaPlayer.getExperienceLevel(LevelType.SKYRUSH_LEVEL),
							"●", net.md_5.bungee.api.ChatColor.LIGHT_PURPLE, net.md_5.bungee.api.ChatColor.WHITE)
					+ "§7]");
			player.sendMessage("");
		}
	}
}
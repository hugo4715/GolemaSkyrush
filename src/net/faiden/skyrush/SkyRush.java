package net.faiden.skyrush;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.faiden.skyrush.listeners.ListenerManager;
import net.faiden.skyrush.manager.kits.type.KitAlchimiste;
import net.faiden.skyrush.manager.kits.type.KitDefenseur;
import net.faiden.skyrush.manager.kits.type.KitGladiator;
import net.faiden.skyrush.manager.kits.type.KitPyroman;
import net.faiden.skyrush.manager.kits.type.KitRunner;
import net.faiden.skyrush.manager.kits.type.KitRusheur;
import net.faiden.skyrush.manager.kits.type.KitTank;
import net.faiden.skyrush.manager.kits.type.KitVampire;
import net.faiden.skyrush.manager.teams.TeamInfo;
import net.faiden.skyrush.manager.teams.TeamManager;
import net.faiden.skyrush.runnables.LobbyRunnable;
import net.faiden.skyrush.runnables.ScoreboardRunnable;
import net.faiden.skyrush.runnables.TrackerRunnable;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.rank.Rank;
import net.golema.database.support.GameStatus;
import net.golema.database.support.configs.FileManager;
import net.golema.database.support.configs.FileManager.Config;
import net.golema.database.support.locations.Cuboid;
import net.golema.database.support.world.WorldManager;
import tk.hugo4715.golema.timesaver.TimeSaverAPI;
import tk.hugo4715.golema.timesaver.server.GameInfos;
import tk.hugo4715.golema.timesaver.server.ServerStatus;
import tk.hugo4715.golema.timesaver.server.ServerType;

public class SkyRush extends JavaPlugin {
	
	public static List<Cuboid> cuboidLockBlockList = new ArrayList<Cuboid>();
	public List<MapGameInfos> mapInfosList = new ArrayList<MapGameInfos>();
	public List<TeamInfo> teamInGameList = new ArrayList<TeamInfo>();
	public List<Player> playerSpawnKillList = new ArrayList<Player>();
	
	public static Map<String, GamePlayer> gamePlayersMap = new HashMap<String, GamePlayer>();
	public Map<Player, Player> playerKillerMap = new HashMap<Player, Player>();
	public Map<Block, Location> blockPlayerMap = new HashMap<Block, Location>();
	
	private String prefixGame = ChatColor.GOLD + "" + ChatColor.BOLD + "SkyRush" + ChatColor.WHITE + "│";
	private Integer maxPlayerPerTeam = 12;
	private Integer minPlayers = 8;
	public boolean forceStart = false;
	
	public FileManager fileManager;
	public Config mapLocationsConfig;
	public MapGameInfos mapGameInfos;
	
	public static SkyRush instance;
	
	@Override
	public void onLoad() {
		
		// Définition de l'instance du Plugin.
		instance = this;
		
		// Gestion de la Map et des Configurations.
		Bukkit.unloadWorld("world", false);
		for(MapGameInfos mapInfos : MapGameInfos.values()) { mapInfosList.add(mapInfos); }
		this.mapGameInfos = mapInfosList.get(new Random().nextInt(mapInfosList.size()));
		fileManager = new FileManager(instance);
		mapLocationsConfig = fileManager.getConfig("maps/" + mapGameInfos.getConfigName());
		mapLocationsConfig.copyDefaults(true).save();
		WorldManager.deleteWorld(new File("world"));
		File from = new File("maps/" + mapGameInfos.getMapName());
		File to = new File("world");
		try {
			WorldManager.copyFolder(from, to); 
		} catch (Exception e) {
			System.err.println("Erreur: Le serveur n'arrive pas à copier la Map : " + mapGameInfos.getMapName()); 
		}

		super.onLoad();
	}
	
	@Override
	public void onEnable() {
		// Initialisation des param�tres de la partie.
		TeamManager.fullAndChargeList();
		new ListenerManager(instance).registerListeners();
		new ScoreboardRunnable().runTaskTimer(instance, 0L, 20L);
		GameStatus.setStatus(GameStatus.LOBBY);
		
		// Initialisation du TimeSaver.
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			@Override
			public void run() {
				TimeSaverAPI.setServerMap(mapGameInfos.getMapName());
				TimeSaverAPI.setServerStatus(ServerStatus.ALLOW);
				TimeSaverAPI.setServerGame(GameInfos.SKYRUSH);
				TimeSaverAPI.setServerType(ServerType.GAME);
				TimeSaverAPI.setJoinable(true);
			}
		}, 40L);
		
		// Ajout des kits.
		new KitAlchimiste();
		new KitDefenseur();
		new KitGladiator();
		new KitPyroman();
		new KitTank();
		new KitRunner();
		new KitRusheur();
		new KitVampire();
		
		new TrackerRunnable().runTaskTimer(this, 20, 20);
	}
	
	@Override
	public void onDisable() { 	
		super.onDisable(); 
	}
	
	/**
	 * Récupérer un GamePlayer.
	 * 
	 * @param player
	 * @return
	 */
	public static GamePlayer getGamePlayer(Player player) {
		if(gamePlayersMap.get(player.getName()) == null) { gamePlayersMap.put(player.getName(), new GamePlayer(player)); }
		return gamePlayersMap.get(player.getName());
	}
	
	/**
	 * Récupérer la map des GamePlyers.
	 * 
	 * @return
	 */
	public static Map<String, GamePlayer> getGamePlayersMap() {
		return gamePlayersMap;
	}
	
	/**
	 * Enregistrer les Cuboid.
	 * 
	 */
	public static void loadCuboid() {
		for (int i = 1; i <= 4; i++) {
			Location locationMinimum = new Location(Bukkit.getWorld("world"), 
					SkyRush.instance.mapLocationsConfig.get().getDouble("Cuboid." + i + ".A.x"), 
					SkyRush.instance.mapLocationsConfig.get().getDouble("Cuboid." + i + ".A.y"), 
					SkyRush.instance.mapLocationsConfig.get().getDouble("Cuboid." + i + ".A.z"));
			Location locationMaximum = new Location(Bukkit.getWorld("world"), 
					SkyRush.instance.mapLocationsConfig.get().getDouble("Cuboid." + i + ".B.x"), 
					SkyRush.instance.mapLocationsConfig.get().getDouble("Cuboid." + i + ".B.y"), 
					SkyRush.instance.mapLocationsConfig.get().getDouble("Cuboid." + i + ".B.z"));
			cuboidLockBlockList.add(new Cuboid(locationMinimum, locationMaximum));
			System.out.println("Location " + i + " >> " + locationMinimum.getX() + ", " + locationMinimum.getY() + ", " + locationMaximum.getZ() + ".");
			System.out.println("Location " + i + " >> " + locationMaximum.getX() + ", " + locationMaximum.getY() + ", " + locationMaximum.getZ() + ".");
		}
	}
	
	public String getPrefixGame() {
		return prefixGame;
	}
	
	public Integer getMaxPlayerPerTeam() {
		return maxPlayerPerTeam;
	}
	
	public Integer getMinPlayers() {
		return minPlayers;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		// Vérifiez qui effectu� la commande.
		if(!(sender instanceof Player)) {
			System.out.println("Vous devez �tre un joueur pour utiliser cette commande.");
			return false;
		}
		
		// Mise en oeuvre de la commande '/start'.
		if(label.equalsIgnoreCase("start")) {
			Player player = (Player) sender;
			GolemaPlayer golemaPlayer = GolemaPlayer.getGolemaPlayer(player);
			if(golemaPlayer.getRankPower() >= Rank.YOUTUBER.getPower()) {
				if(!(LobbyRunnable.isStarted)) {
					if(Bukkit.getOnlinePlayers().size() == 1) {
						player.sendMessage(ChatColor.RED + "Erreur: Vous ne pouvez pas jouer seul.");
						return false;
					}
					this.forceStart = true;
					new LobbyRunnable().runTaskTimer(SkyRush.instance, 0L, 20L);
					LobbyRunnable.isStarted = true;
					Bukkit.broadcastMessage(prefixGame + ChatColor.YELLOW + " La partie vient d'être démarrée.");
					return true;
				} else {
					player.sendMessage(ChatColor.RED + "Erreur: La partie à déjà été lancée.");
					return false;
				}
			} else {
				golemaPlayer.sendMessageNoPermission();
				return false;
			}
		}
		return false;
	}
}
package net.faiden.skyrush;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class GameUtils {

	/**
	 * Coordonnées du Lobby.
	 */
	public static final Location LOBBY_LOCATION = new Location(Bukkit.getWorld("world"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("LobbyLocation.x"), 
			SkyRush.instance.mapLocationsConfig.get().getDouble("LobbyLocation.y"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("LobbyLocation.z"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("LobbyLocation.yaw"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("LobbyLocation.pitch"));
	
	/**
	 * Coordonées du spawn de l'équipe Rouge.
	 */
	public static final Location REDTEAM_LOCATION = new Location(Bukkit.getWorld("world"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.red.x"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.red.y"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.red.z"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.red.yaw"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.red.pitch"));
	
	/**
	 * Coordonées du spawn de l'équipe Bleu.
	 */
	public static final Location BLUETEAM_LOCATION = new Location(Bukkit.getWorld("world"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.blue.x"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.blue.y"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.blue.z"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.blue.yaw"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("TeamLocation.blue.pitch"));
	
	/**
	 * Coordonées du golem de l'équipe Rouge.
	 */
	public static final Location REDGOLEM_LOCATION = new Location(Bukkit.getWorld("world"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.red.x"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.red.y"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.red.z"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.red.yaw"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.red.pitch"));
	
	/**
	 * Coordonées du golem de l'équipe Bleu.
	 */
	public static final Location BLUEGOLEM_LOCATION = new Location(Bukkit.getWorld("world"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.blue.x"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.blue.y"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.blue.z"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.blue.yaw"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("GolemLocation.blue.pitch"));
	
	/**
	 * Coordonées du PNJ de l'équipe Bleu.
	 */
	public static final Location BLUEPNJ_LOCATION = new Location(Bukkit.getWorld("world"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.blue.x"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.blue.y"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.blue.z"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.blue.yaw"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.blue.pitch"));
	
	/**
	 * Coordonées du PNJ de l'équipe Red.
	 */
	public static final Location REDPNJ_LOCATION = new Location(Bukkit.getWorld("world"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.red.x"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.red.y"),
			SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.red.z"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.red.yaw"),
			(float) SkyRush.instance.mapLocationsConfig.get().getDouble("VillagerLocation.red.pitch"));
	
	/**
	 * Récupérer l'item du sélecteur de Teams.
	 * 
	 * @return
	 */
	public static ItemStack itemTeamSelector() {
		ItemStack itemStack = new ItemStack(Material.BANNER);
		BannerMeta iBannerMeta = (BannerMeta) itemStack.getItemMeta();
		iBannerMeta.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Sélecteur d'Équipe " + ChatColor.DARK_GRAY + " ▏ " + ChatColor.GRAY + " Clic-droit");
		iBannerMeta.setBaseColor(DyeColor.WHITE);
		List<Pattern> patterns = new ArrayList<Pattern>();
        patterns.add(new Pattern(DyeColor.RED, PatternType.CROSS));
        iBannerMeta.setPatterns(patterns);
		itemStack.setItemMeta(iBannerMeta);
		return itemStack;
	}
}
package net.faiden.skyrush.listeners.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.faiden.skyrush.GameUtils;
import net.faiden.skyrush.SkyRush;
import net.golema.database.support.GameStatus;
import net.golema.database.support.builder.TitleBuilder;

public class PlayerMoveListener implements Listener {
	
	public List<Player> playerInWater = new ArrayList<Player>();
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		switch (GameStatus.getStatus()) {
		case LOBBY: 
			// Téléportation au Hub si le Joueur s'éloigne.
			if ((player.getLocation().getBlockY() < 0) || (((player.getLocation().getBlock().isLiquid()) || (player.getLocation().getBlock().getType().equals(Material.WATER))
					|| (player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)))) && (SkyRush.instance.mapGameInfos.isWater())) {
				player.teleport(GameUtils.LOBBY_LOCATION);
				player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0f, 1.0f);
				new TitleBuilder(ChatColor.GOLD + "SkyRush", ChatColor.RED + "Pourquoi veux-tu t'enfuir ?").send(player);
				return;
			} 
			break;
		case GAME:
			
			// Gestion de l'exception de la mort dans l'eau.
			if((player.getLocation().getBlock().isLiquid()) || (player.getLocation().getBlock().getType().equals(Material.WATER))
					|| (player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER))) {
				if(SkyRush.instance.mapGameInfos.isWater()) {
					if(!(SkyRush.getGamePlayer(player).isSpectator())) {
						if(playerInWater.contains(player)) return;
						player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 3, 1), true);
						player.damage(3.0d);
						playerInWater.add(player);
						new TitleBuilder("§6§lSkyRush", "§cSortez de l'eau pour pas mourrir !");
						player.playSound(player.getLocation(), Sound.WITHER_SPAWN, 1.0f, 1.0f);
						Bukkit.getScheduler().runTaskLater(SkyRush.instance, new Runnable() {
							@Override
							public void run() {
								playerInWater.remove(player);
							}
						}, 15L);
					}
				}
			}
			
			// Gestion d'un Joueur qui va dans le vide en état Spectateur.
			if (player.getLocation().getBlockY() < 0) {
				if (SkyRush.getGamePlayer(player).isSpectator()) {
					List<Player> gamePlayerList = new ArrayList<Player>();
					for (Player playerOnline : Bukkit.getOnlinePlayers()) {
						if (!(SkyRush.getGamePlayer(playerOnline).isSpectator())) {
							gamePlayerList.add(playerOnline);
						}
					}
					player.teleport(gamePlayerList.get(new Random().nextInt(gamePlayerList.size())).getLocation());

				// Accélération de la mort d'un Joueur qui saute dans le vide.
				} else {
					player.setHealth(0.0d);
				}
			}
			break;
		case FINISH:
			break;
		default:
			break;
		}
	}
}
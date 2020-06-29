package net.faiden.skyrush.manager.kits.type;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.kits.KitAbstract;
import net.golema.database.golemaplayer.GolemaPlayer;
import net.golema.database.golemaplayer.game.skyrush.kits.SkyRushKitsMode;
import net.golema.database.golemaplayer.game.skyrush.kits.SkyRushKitsType;
import net.golema.database.support.builder.items.ItemBuilder;

public class KitRunner extends KitAbstract {

	public KitRunner() {
		this.name = "Runner";
		this.slot = 21;
		this.register(this);
	}

	@Override
	public void sendKits(Player player) {
		GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
		player.getInventory().setHelmet(
				gamePlayer.getItemStackArmorLeather(Material.LEATHER_HELMET, gamePlayer.getTeamInfo().getColor()));
		player.getInventory().setChestplate(
				gamePlayer.getItemStackArmorLeather(Material.LEATHER_CHESTPLATE, gamePlayer.getTeamInfo().getColor()));
		player.getInventory().setLeggings(
				gamePlayer.getItemStackArmorLeather(Material.LEATHER_LEGGINGS, gamePlayer.getTeamInfo().getColor()));
		player.getInventory().setBoots(
				gamePlayer.getItemStackArmorLeather(Material.LEATHER_BOOTS, gamePlayer.getTeamInfo().getColor()));
		player.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
		player.getInventory().addItem(new ItemStack(Material.WOOD_PICKAXE));
		player.getInventory().addItem(new ItemBuilder().type(Material.STAINED_CLAY)
				.data((byte) gamePlayer.getTeamInfo().getDataClay()).amount(48).build());
		player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 12));
		player.setWalkSpeed(0.3f);
	}

	@Override
	public ItemStack getItemIcons(Player player) {
		return new ItemBuilder()
				.type(Material.GOLD_BOOTS)
				.name(ChatColor.GOLD + name)
				.lore("", "§7Description :", " §7● §f1 Armure en cuir", " §7● §f1 Epée en pierre", " §7● §f1 Pioche en bois", " §7● §f48 Blocs",
						" §7● §f12 Steaks", "", "§7Avantages", "§dVitesse améliorée", "", "§6» §eCliquez pour utiliser.")
				.amount(GolemaPlayer.getGolemaPlayer(player).getSkyRushPlayer().getPlayerKits(SkyRushKitsMode.SKYRUSH_2TEAMS, SkyRushKitsType.RUNNER))
				.build();
	}

}

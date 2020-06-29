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

public class KitDefenseur extends KitAbstract {

	public KitDefenseur() {
		this.name = "Defenseur";
		this.slot = 12;
		this.register(this);
	}

	@Override
	public void sendKits(Player player) {
		GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
		player.getInventory().setHelmet(
				gamePlayer.getItemStackArmorLeather(Material.LEATHER_HELMET, gamePlayer.getTeamInfo().getColor()));
		player.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
		player.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
		player.getInventory().setBoots(
				gamePlayer.getItemStackArmorLeather(Material.LEATHER_BOOTS, gamePlayer.getTeamInfo().getColor()));
		player.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
		player.getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
		player.getInventory().addItem(new ItemBuilder().type(Material.STAINED_CLAY)
				.data((byte) gamePlayer.getTeamInfo().getDataClay()).amount(64).build());
		player.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 8));
	}

	@Override
	public ItemStack getItemIcons(Player player) {
		return new ItemBuilder()
				.type(Material.IRON_BLOCK)
				.name(ChatColor.GOLD + name)
				.lore("", "§7Description :", " §7● §f1 Casque en cuir", " §7● §f1 Chestplate en maille", " §7● §f1 Pantalon en maille" , " §7● §f1 Bottes en cuir",
						" §7● §f1 Epée en fer", " §7● §f1 Pioche en pierre", " §7● §f64 Blocs",
						" §7● §f8 Steaks", "", "§6» §eCliquez pour utiliser.")
				.amount(GolemaPlayer.getGolemaPlayer(player).getSkyRushPlayer().getPlayerKits(SkyRushKitsMode.SKYRUSH_2TEAMS, SkyRushKitsType.DEFENSEUR))
				.build();
	}
}
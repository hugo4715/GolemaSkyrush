package net.faiden.skyrush.manager.trade;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import net.faiden.skyrush.GamePlayer;
import net.faiden.skyrush.SkyRush;
import net.golema.database.support.builder.InventoryBuilder;
import net.golema.database.support.builder.items.ItemBuilder;

public class TradeStuffMenu implements Listener {

	public String invName = "Trade » Stuffs";
	public Map<Player, Inventory> menuInventoryMap = new HashMap<Player, Inventory>();

	/**
	 * Cr�ation du Trade.
	 * 
	 * @param player
	 */
	public TradeStuffMenu(Player player) {
		Bukkit.getPluginManager().registerEvents(this, SkyRush.instance);
		Inventory inventory = new InventoryBuilder(invName).addLine(new String[] { "", "", "", "", "", "", "", "", "" })
				.addLine(new String[] { "", "mail_chestplate", "iron_chestplate", "diamond_chestplate", "", "", "", "", "" })
				.addLine(new String[] { "", "", "", "", "", "", "", "", "return" })
				.setItem("return", new ItemBuilder().type(Material.ARROW).name("§cRetour").build())
				.setItem("mail_chestplate", new ItemBuilder().type(Material.CHAINMAIL_CHESTPLATE).name("§bPlastron en maille").lore("§7Quantitée : §ax1", "§7Prix : §e§l100 ✸").build())
				.setItem("iron_chestplate", new ItemBuilder().type(Material.IRON_CHESTPLATE).name("§bPlastron en fer").lore("§7Quantitée : §ax1", "§7Prix : §e§l175 ✸").build())
				.setItem("diamond_chestplate", new ItemBuilder().type(Material.DIAMOND_CHESTPLATE).name("§bPlastron en diamant").lore("§7Quantitée : §ax1", "§7Prix : §e§l220 ✸").build())
				.build(player);
				
		this.menuInventoryMap.put(player, inventory);
		player.openInventory(menuInventoryMap.get(player));
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (event.getInventory() == null)
			return;
		if (menuInventoryMap.get(player) == null)
			return;
		if (menuInventoryMap.get(player).equals(event.getInventory()))
			menuInventoryMap.remove(player);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		GamePlayer gamePlayer = SkyRush.getGamePlayer(player);
		if (event.getInventory() == null)
			return;
		if (event.getCurrentItem() == null)
			return;
		if (event.getCurrentItem().getType().equals(Material.AIR))
			return;
		if (!(event.getInventory().getName().equalsIgnoreCase(invName)))
			return;
		if (!(event.getInventory().equals(menuInventoryMap.get(player))))
			return;
		event.setCancelled(true);
		switch (event.getCurrentItem().getType()) {
		case CHAINMAIL_CHESTPLATE:
			gamePlayer.makeTrade(100, new ItemBuilder().type(event.getCurrentItem().getType()).amount(event.getCurrentItem().getAmount())
					.data((byte) event.getCurrentItem().getDurability()).build());
			break;
		case IRON_CHESTPLATE:
			gamePlayer.makeTrade(175, new ItemBuilder().type(event.getCurrentItem().getType()).amount(event.getCurrentItem().getAmount())
					.data((byte) event.getCurrentItem().getDurability()).build());
			break;
		case DIAMOND_CHESTPLATE:
			gamePlayer.makeTrade(220, new ItemBuilder().type(event.getCurrentItem().getType()).amount(event.getCurrentItem().getAmount())
					.data((byte) event.getCurrentItem().getDurability()).build());
			break;
		case ARROW:
			new TradeMainMenu(player);
			break;
		default:
			break;
		}
	}
}
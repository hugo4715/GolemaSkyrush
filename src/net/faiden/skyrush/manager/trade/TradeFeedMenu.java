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

public class TradeFeedMenu implements Listener {

	public String invName = "Trade » Feeds";
	public Map<Player, Inventory> menuInventoryMap = new HashMap<Player, Inventory>();

	/**
	 * Cr�ation du Trade.
	 * 
	 * @param player
	 */
	public TradeFeedMenu(Player player) {
		Bukkit.getPluginManager().registerEvents(this, SkyRush.instance);
		Inventory inventory = new InventoryBuilder(invName).addLine(new String[] { "", "", "", "", "", "", "", "", "" })
				.addLine(new String[] { "", "steak", "apple", "", "", "", "", "", "" })
				.addLine(new String[] { "", "", "", "", "", "", "", "", "return" })
				.setItem("return", new ItemBuilder().type(Material.ARROW).name("§cRetour").build())
				.setItem("steak", new ItemBuilder().type(Material.COOKED_BEEF).amount(16).name("§bSteak").lore("§7Quantitée : §ax16", "§7Prix : §e§l10 ✸").build())
				.setItem("apple", new ItemBuilder().type(Material.APPLE).amount(4).name("§bPommes").lore("§7Quantitée : §ax4", "§7Prix : §e§l50 ✸").build())
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
		case COOKED_BEEF:
			gamePlayer.makeTrade(10, new ItemBuilder().type(event.getCurrentItem().getType()).amount(event.getCurrentItem().getAmount())
					.data((byte) event.getCurrentItem().getDurability()).build());
			break;
		case APPLE:
			gamePlayer.makeTrade(50, new ItemBuilder().type(event.getCurrentItem().getType()).amount(event.getCurrentItem().getAmount())
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
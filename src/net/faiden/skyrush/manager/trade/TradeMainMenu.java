package net.faiden.skyrush.manager.trade;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import net.faiden.skyrush.SkyRush;
import net.golema.database.support.builder.InventoryBuilder;
import net.golema.database.support.builder.items.ItemBuilder;

public class TradeMainMenu implements Listener {

	public String invName = "Trade » Main";
	public Map<Player, Inventory> menuInventoryMap = new HashMap<Player, Inventory>();

	/**
	 * Cr�ation du Trade Main.
	 * 
	 * @param player
	 */
	public TradeMainMenu(Player player) {
		Bukkit.getPluginManager().registerEvents(this, SkyRush.instance);
		Inventory inventory = new InventoryBuilder(invName).addLine(new String[] { "", "", "", "", "", "", "", "", "" })
				.addLine(new String[] { "", "blocks_menu", "epee_menu", "stuff_menu", "tools_menu", "feed_menu", "potion_menu", "divers_menu", "" })
				.addLine(new String[] { "", "", "", "", "", "", "", "", "return" })
				.setItem("return", new ItemBuilder().type(Material.ARROW).name("§cFermer").build())
				
				/** Blocs */
				.setItem("blocks_menu", new ItemBuilder().type(Material.SANDSTONE).name(ChatColor.GREEN + "Blocs")
						.lore("", "§8Disponible:", " §7▪ §fSandstone", " §7▪ §fLaine", " §7▪ §fQuartz", " §7▪ §fObsidien", "", "§6§l➸ §eCliquer pour accéder.").build())
				
				/** Epee */
				.setItem("epee_menu", new ItemBuilder().type(Material.GOLD_SWORD).name(ChatColor.GREEN + "Epées")
						.lore("", "§8Disponible:", " §7▪ §fEpée en pierre", " §7▪ §fEpée en fer", " §7▪ §fEpée en diamant", "", "§6§l➸ §eCliquer pour accéder.").build())
				
				/** Stuff */
				.setItem("stuff_menu", new ItemBuilder().type(Material.GOLD_CHESTPLATE).name(ChatColor.GREEN + "Armures")
						.lore("", "§8Disponible:", " §7▪ §fPlastron en maille", " §7▪ §fPlastron en fer", " §7▪ §fPlastron en diamant", "", "§6§l➸ §eCliquer pour accéder.").build())
				
				/** Tools */
				.setItem("tools_menu", new ItemBuilder().type(Material.GOLD_PICKAXE).name(ChatColor.GREEN + "Outils")
						.lore("", "§8Disponible:", " §7▪ §fPioche en pierre", " §7▪ §fPioche en fer", " §7▪ §fPioche en diamant", "", "§6§l➸ §eCliquer pour accéder.").build())

				/** Feed */
				.setItem("feed_menu", new ItemBuilder().type(Material.COOKED_BEEF).name(ChatColor.GREEN + "Nourritures")
						.lore("", "§8Disponible:", " §7▪ §fSteak", " §7▪ §fPomme", "", "§6§l➸ §eCliquer pour accéder.").build())

				/** Potion */
				.setItem("potion_menu", new ItemBuilder().type(Material.POTION).name(ChatColor.GREEN + "Potions")
						.lore("", "§8Disponible:", " §7▪ §fPotion de régénération", " §7▪ §fPotion de vitesse", " §7▪ §fPotion de soin",
								"", "§6§l➸ §eCliquer pour accéder.").build())
				
				/** Divers */
				.setItem("divers_menu", new ItemBuilder().type(Material.TNT).name(ChatColor.GREEN + "Divers")
						.lore("", "§8Disponible:", " §7▪ §fEnderPearl", " §7▪ §fTNT", " §7▪ §fFirecharge", " §7▪ §fSeau de eau",
								" §7▪ §fPommes d'or", " §7▪ §fCanne à pêche", "", "§6§l➸ §eCliquer pour accéder.").build())
				
				.build(player);
				
		this.menuInventoryMap.put(player, inventory);
		player.openInventory(menuInventoryMap.get(player));
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
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
		case SANDSTONE:
			new TradeBlocksMenu(player);
			break;
		case GOLD_SWORD:
			new TradeSwordMenu(player);
			break;
		case GOLD_CHESTPLATE:
			new TradeStuffMenu(player);
			break;
		case GOLD_PICKAXE:
			new TradeToolsMenu(player);
			break;
		case COOKED_BEEF:
			new TradeFeedMenu(player);
			break;
		case POTION:
			new TradePotionMenu(player);
			break;
		case TNT:
			new TradeMiscMenu(player);
			break;
		case ARROW:
			player.closeInventory();
			break;
		default:
			break;
		}
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
}
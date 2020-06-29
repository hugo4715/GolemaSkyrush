package net.faiden.skyrush.manager.kits;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import net.faiden.skyrush.SkyRush;
import net.golema.api.builder.virtual.VirtualMenu;
import net.golema.database.support.builder.items.ItemBuilder;

public class KitMenu extends VirtualMenu {

	private static Map<Player, Inventory> playerInventoryMap = new HashMap<Player, Inventory>();
	
	/**
	 * Constructeur du Menu de Kit.
	 * 
	 * @param player
	 */
	public KitMenu(Player player) {
		super(player, "Kits", 4);
		
		// Ajout des IteStack de Kits dans le Menu.
		for(KitAbstract kitAbstract : KitAbstract.kitAbstractsList) {
			this.menuInventory.setItem(kitAbstract.slot, kitAbstract.getItemIcons(player));
		}
		
		// Ajout de derniers ItemStack manuellement.
		this.menuInventory.setItem(35, new ItemBuilder().type(Material.WOOD_DOOR).name(ChatColor.RED + "Fermer").build());
		
		// Param�tres du Menu et Ouverture.
		playerInventoryMap.put(player, menuInventory);
		open();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory() == null)
			return;
		if (event.getClickedInventory() == null)
			return;
		if (event.getCurrentItem() == null)
			return;
		if (event.getCurrentItem().getType().equals(Material.AIR))
			return;
		if (!(event.getInventory().equals(playerInventoryMap.get(player))))
			return;
		if (event.getInventory().equals(playerInventoryMap.get((Player) event.getWhoClicked()))) {
			event.setCancelled(true);
			Player player = (Player) event.getWhoClicked();

			// Interaction avec un Item Random.
			if (event.getCurrentItem().getType().equals(Material.STAINED_GLASS_PANE)) {
				return;
			}
			if (event.getCurrentItem().getType().equals(Material.WOOD_DOOR)) {
				player.closeInventory();
				return;
			}

			// Interaction sur un Kit que le Joueur ne posséde pas.
			if (event.getCurrentItem().getAmount() == 0) {
				player.sendMessage(SkyRush.instance.getPrefixGame() + ChatColor.RED + " Vous devez acheter ce kit sur le hub.");
				player.closeInventory();
				return;
			}

			// Selection de kits.
			KitAbstract kitSelect = KitAbstract.getKitAbstractByName(
					event.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.GOLD + "", ""));
			if (kitSelect != null) {
				SkyRush.getGamePlayer(player).setKitAbstract(kitSelect);
				player.sendMessage(SkyRush.instance.getPrefixGame() + ChatColor.GREEN
						+ " Vous avez sélectionné le kit: " + ChatColor.AQUA + kitSelect.name + ChatColor.GREEN + ".");
			}
			player.closeInventory();
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onInventoryClose(InventoryCloseEvent event) {
		Player player = (Player) event.getPlayer();
		if (event.getInventory() == null)
			return;
		if (playerInventoryMap.get(player) == null)
			return;
		if (playerInventoryMap.get(player).equals(event.getInventory()))
			playerInventoryMap.remove(player);
	}
}
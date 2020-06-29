package net.faiden.skyrush.manager.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.faiden.skyrush.SkyRush;
import net.golema.api.builder.virtual.VirtualMenu;
import net.golema.database.support.builder.items.ItemBuilder;
import net.md_5.bungee.api.ChatColor;

public class TeamMenu extends VirtualMenu {

	private static Map<Player, Inventory> playerInventoryMap = new HashMap<Player, Inventory>();

	/**
	 * Constructeur du Menu de Team.
	 * 
	 * @param player
	 */
	public TeamMenu(Player player) {
		super(player, "Choisir d'équipes", 2);

		// Ajout des IteStack de Teams dans le Menu.
		for (TeamInfo teamInfos : TeamInfo.values()) {
			ItemStack itemStack = new ItemStack(Material.BANNER, 1, (short) teamInfos.getData());
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(teamInfos.getCharString() + "Equipe " + teamInfos.getName());
			List<String> lores = new ArrayList<>();
			lores.add("");

			// Description des Joueurs dans la Team.
			int playerInTeam = 0;
			for (Player playerTeam : TeamManager.playerTeamList.get(teamInfos)) {
				lores.add(ChatColor.GRAY + "■ " + teamInfos.getChatColor() + playerTeam.getName());
				playerInTeam++;
			}
			for (int x = playerInTeam; x != SkyRush.instance.getMaxPlayerPerTeam(); x++) {
				lores.add(ChatColor.DARK_GRAY + "[Emplacement Vide]");
			}

			// Description des disponibilités de la Team.
			lores.add("");
			if (TeamManager.playerTeamList.get(teamInfos).size() >= SkyRush.instance.getMaxPlayerPerTeam()) {
				lores.add(ChatColor.RED + "Cette équipe est pleine.");
			} else {
				lores.add(ChatColor.YELLOW + "Cliquer pour rejoindre.");
			}

			itemMeta.setLore(lores);
			itemStack.setItemMeta(itemMeta);
			this.menuInventory.addItem(itemStack);
		}

		// Ajout de derniers ItemStack manuellement.
		this.menuInventory.setItem(13,
				new ItemBuilder().type(Material.ARMOR_STAND).name(ChatColor.GOLD + "Team aléatoire").lore("",
						"§7Rejoindre une équipe,", "§7aléatoirement.", "", ChatColor.YELLOW + "Cliquer pour rejoindre.")
						.build());
		this.menuInventory.setItem(17, new ItemBuilder().type(Material.WOOD_DOOR).name("§cFermer").build());

		// Paramètres du Menu et Ouverture.
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
			switch (event.getCurrentItem().getType()) {
			case WOOD_DOOR:
				player.closeInventory();
				break;
			case ARMOR_STAND:
				if (TeamManager.getPlayerTeam(player) == null) {
					TeamManager.putInARandomTeam(player);
					TeamInfo teamInfo = TeamManager.getPlayerTeam(player);
					player.sendMessage(SkyRush.instance.getPrefixGame() + ChatColor.WHITE
							+ " Vous avez rejoins l'équipe: " + teamInfo.getChatColor() + teamInfo.getName());
					player.setItemInHand(new ItemBuilder().type(Material.BANNER)
							.name(player.getItemInHand().getItemMeta().getDisplayName())
							.data((byte) event.getCurrentItem().getDurability()).build());
					player.closeInventory();
				} else {
					player.sendMessage(
							SkyRush.instance.getPrefixGame() + ChatColor.RED + " Vous êtes déjà dans une équipe.");
					player.closeInventory();
					return;
				}
				break;
			case BANNER:

				// Paramètres des Teams.
				String teamName = event.getCurrentItem().getItemMeta().getDisplayName().substring(9);
				TeamInfo teamInfo = TeamInfo.geTeamInfoByName(teamName);

				// Détection si la Team est plein.
				if (TeamManager.playerTeamList.get(teamInfo).size() >= SkyRush.instance.getMaxPlayerPerTeam()) {
					player.sendMessage(
							SkyRush.instance.getPrefixGame() + ChatColor.RED + " Cette équipe est déjà pleine.");
					player.closeInventory();
					return;
				}
				
				// Balancer.
				if(teamInfo.equals(TeamInfo.RED)) {
					if (TeamManager.playerTeamList.get(TeamInfo.RED).size() > TeamManager.playerTeamList.get(TeamInfo.BLEUE).size()) {
						player.sendMessage(
								SkyRush.instance.getPrefixGame() + ChatColor.RED + " Vous devez équilibrer les équipes.");
						player.closeInventory();
						return;
					}
				} else {
					if (TeamManager.playerTeamList.get(TeamInfo.BLEUE).size() > TeamManager.playerTeamList.get(TeamInfo.RED).size()) {
						player.sendMessage(
								SkyRush.instance.getPrefixGame() + ChatColor.RED + " Vous devez équilibrer les équipes.");
						player.closeInventory();
						return;
					}
				}
					

				// Ajout du Joueur dans la Team.
				TeamManager.removePlayerInAllTeam(player);
				TeamManager.addPlayerInTeam(player, teamInfo);
				player.sendMessage(SkyRush.instance.getPrefixGame() + ChatColor.WHITE + " Vous avez rejoins l'équipe: "
						+ teamInfo.getChatColor() + teamName);
				player.setItemInHand(new ItemBuilder().type(Material.BANNER)
						.name(player.getItemInHand().getItemMeta().getDisplayName())
						.data((byte) event.getCurrentItem().getDurability()).build());
				player.closeInventory();
				break;
			default:
				break;
			}
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
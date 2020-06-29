package net.faiden.skyrush.manager.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

import net.faiden.skyrush.GameUtils;
import net.faiden.skyrush.SkyRush;
import net.faiden.skyrush.manager.GolemEntity;

public class TeamManager {

	public static List<TeamInfo> teamList = new ArrayList<TeamInfo>();

	public static Map<TeamInfo, List<Player>> playerTeamList = new HashMap<TeamInfo, List<Player>>();
	public static Map<TeamInfo, GolemEntity> golemTeamList = new HashMap<TeamInfo, GolemEntity>();
	public static Map<String, ArmorStand> armorStandTeamList = new HashMap<String, ArmorStand>();

	/**
	 * Constructeur du TeamManager.
	 */
	public TeamManager() {
	}

	/**
	 * Ajouter un Joueur � une Team.
	 * 
	 * @param player
	 * @param teamInfo
	 */
	public static void addPlayerInTeam(Player player, TeamInfo teamInfo) {
		if (playerTeamList.get(teamInfo) == null) {
			playerTeamList.put(teamInfo, new ArrayList<Player>());
		}
		playerTeamList.get(teamInfo).add(player);
	}

	/**
	 * Enlever un Joueur d'une Team.
	 * 
	 * @param player
	 * @param teamInfo
	 */
	public static void removePlayerInTeam(Player player, TeamInfo teamInfo) {
		playerTeamList.get(teamInfo).remove(player);
	}

	/**
	 * Supprimer le Joueur de toutes les Teams.
	 * 
	 * @param player
	 */
	public static void removePlayerInAllTeam(Player player) {
		for (TeamInfo teamInfo : TeamInfo.values()) {
			playerTeamList.get(teamInfo).remove(player);
		}
	}

	/**
	 * Savoir si une Team est plein.
	 * 
	 * @param teamInfo
	 * @return
	 */
	public boolean teamIsFull(TeamInfo teamInfo) {
		if (playerTeamList.get(teamInfo).size() >= SkyRush.instance.getMaxPlayerPerTeam()) {
			return true;
		}
		return false;
	}

	/**
	 * Ajouter un Joueur � une Team Al�atoire.
	 * 
	 * @param player
	 */
	public static void putInARandomTeam(Player player) {
		if (!(hasPlayerTeam(player))) {
			if (playerTeamList.get(TeamInfo.BLEUE).size() <= playerTeamList.get(TeamInfo.RED).size()) {
				playerTeamList.get(TeamInfo.BLEUE).add(player);
			} else {
				playerTeamList.get(TeamInfo.RED).add(player);
			}
		}
	}

	/**
	 * R�cup�rer la Team d'un Joueur.
	 * 
	 * @param player
	 * @return
	 */
	public static TeamInfo getPlayerTeam(Player player) {
		for (TeamInfo teamInfo : teamList) {
			if (playerTeamList.get(teamInfo).contains(player)) {
				return teamInfo;
			}
		}
		return null;
	}

	/**
	 * R�cup�rer la Location d'une Team.
	 * 
	 * @param player
	 * @return
	 */
	public static Location getTeamLocation(Player player) {
		if (isPlayerInTeam(player, TeamInfo.RED)) {
			return GameUtils.REDTEAM_LOCATION;
		} else {
			return GameUtils.BLUETEAM_LOCATION;
		}
	}

	/**
	 * R�cup�rer la Location d'un Golem.
	 * 
	 * @param teamInfo
	 * @return
	 */
	public static Location getGolemLocation(TeamInfo teamInfo) {
		if (teamInfo.getName().equalsIgnoreCase(TeamInfo.RED.getName())) {
			return GameUtils.REDGOLEM_LOCATION;
		} else {
			return GameUtils.BLUEGOLEM_LOCATION;
		}
	}

	/**
	 * R�cup�rer si le Joueur est dans une Team.
	 * 
	 * @param player
	 * @param teamInfo
	 * @return
	 */
	public static boolean isPlayerInTeam(Player player, TeamInfo teamInfo) {
		return playerTeamList.get(teamInfo).contains(player);
	}

	/**
	 * R�cup�rer si le Joueur poss�de une Team.
	 * 
	 * @param player
	 * @return
	 */
	public static boolean hasPlayerTeam(Player player) {
		for (TeamInfo teamInfo : TeamInfo.values()) {
			if (playerTeamList.get(teamInfo).contains(player)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * R�cup�rer la List des Teams.
	 * 
	 * @return
	 */
	public static List<TeamInfo> getTeamList() {
		return teamList;
	}

	/**
	 * R�cup�rer la Map des Player & Team.
	 * 
	 * @return
	 */
	public static Map<TeamInfo, List<Player>> getPlayerTeamList() {
		return playerTeamList;
	}

	/**
	 * Charger les Teams au lancement du Serveur
	 */
	public static void fullAndChargeList() {
		for (TeamInfo teamInfo : TeamInfo.values()) {
			teamList.add(teamInfo);
			playerTeamList.put(teamInfo, new ArrayList<Player>());
		}
	}
}
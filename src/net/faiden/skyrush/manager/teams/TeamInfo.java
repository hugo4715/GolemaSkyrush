package net.faiden.skyrush.manager.teams;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum TeamInfo {
	
	RED("Rouge", (short) 1, ChatColor.RED, "§c", Color.RED, (short) 14),
	BLEUE("Bleue", (short) 6, ChatColor.DARK_AQUA, "§3", Color.AQUA, (short) 3);
	
	private String name;
	private short data;
	private ChatColor chatColor;
	private String charString;
	private Color color;
	private short dataClay;
	
	/**
	 * Constructeur de l'�num�ration TeamInfo.
	 * 
	 * @param name
	 * @param data
	 * @param chatColor
	 * @param charString
	 */
	private TeamInfo(String name, short data, ChatColor chatColor, String charString, Color color, short dataClay) {
		this.name = name;
		this.data = data;
		this.chatColor = chatColor;
		this.charString = charString;
		this.color = color;
		this.dataClay = dataClay;
	}
	
	public String getName() {
		return name;
	}
	
	public short getData() {
		return data;
	}
	
	public ChatColor getChatColor() {
		return chatColor;
	}
	
	public String getCharString() {
		return charString;
	}
	
	public Color getColor() {
		return color;
	}
	
	public short getDataClay() {
		return dataClay;
	}
	
	/**
	 * S�lectionner une Team par rapport � son Nom.
	 * 
	 * @param teamName
	 * @return
	 */
	public static TeamInfo geTeamInfoByName(String teamName) {
		for(TeamInfo teamInfo : TeamInfo.values()) {
			if(teamInfo.getName().equalsIgnoreCase(teamName)) {
				return teamInfo;
			}
		}
		return null;
	}
}
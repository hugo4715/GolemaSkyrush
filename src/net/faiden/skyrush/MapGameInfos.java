package net.faiden.skyrush;

public enum MapGameInfos {
	
	ANHERIA("Anheria", "anheria.yml", false),
	PIRATES("Pirates", "pirates.yml", true),
	//FARWEST("Farwest", "farwest.yml", false),
	SRINO("Srino", "srino.yml", true);

	public String mapName;
	public String configName;
	public boolean water;

	/**
	 * Constructeur du MapInfos.
	 * 
	 * @param mapName
	 * @param configName
	 */
	private MapGameInfos(String mapName, String configName, boolean water) {
		this.mapName = mapName;
		this.configName = configName;
		this.water = water;
	}

	/**
	 * Récupérer le nom de la Map.
	 * 
	 * @return
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * Récupérer le nom de la Config.
	 * 
	 * @return
	 */
	public String getConfigName() {
		return configName;
	}
	
	/**
	 * Récupérer si la map ce joue dans l'eau.
	 * 
	 * @return
	 */
	public boolean isWater() {
		return water;
	}
}
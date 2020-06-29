package net.faiden.skyrush.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Golem;

import net.minecraft.server.v1_8_R3.Entity;
import net.minecraft.server.v1_8_R3.NBTTagCompound;

public class GolemEntity {

	public String name;
	public Location location;
	public Golem golem;
	public Double live;

	/**
	 * Constructeur du GolemEntity.
	 * 
	 * @param name
	 * @param location
	 */
	public GolemEntity(String name, Location location) {
		this.name = name;
		this.location = location;
		this.golem = ((Golem) Bukkit.getWorld("world").spawnEntity(location, EntityType.IRON_GOLEM));
		this.live = Double.valueOf(800.0D);
		this.golem.setMaxHealth(live);
		this.golem.setHealth(live);
		this.golem.setCustomNameVisible(false);

		Entity nmsEntity = ((CraftEntity) this.golem).getHandle();
		NBTTagCompound tag = nmsEntity.getNBTTag();
		if (tag == null) {
			tag = new NBTTagCompound();
		}
		nmsEntity.c(tag);
		tag.setInt("NoAI", 1);
		nmsEntity.f(tag);
	}

	public String getName() {
		return this.name;
	}

	public Location getLocation() {
		return this.location;
	}

	public Golem getGolem() {
		return this.golem;
	}

	public Double getLive() {
		return this.live;
	}
}
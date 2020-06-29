package net.faiden.skyrush.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TrackerRunnable extends BukkitRunnable {

	@Override
	public void run() {
		for(Player p : Bukkit.getOnlinePlayers()) {
			Player target = closest(p);
			
			if(target != null) {
				p.setCompassTarget(target.getLocation());
			}
		}
	}

	private Player closest(Player p) {
		Player closest = null;
		double dist = Double.MAX_VALUE;
		
		for(Player i : p.getWorld().getPlayers()) {
			double d = i.getLocation().distanceSquared(p.getLocation());
			if(d < dist) {
				closest = i;
				dist = d;
			}
		}
		
		return closest;
	}

}

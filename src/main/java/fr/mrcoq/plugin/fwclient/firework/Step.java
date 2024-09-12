package fr.mrcoq.plugin.fwclient.firework;

import org.bukkit.Location;

public class Step {
    private String firework;
    private int timing;
    private Location location;

    public String getFirework() {
        return firework;
    }

    public void setFirework(String firework) {
        this.firework = firework;
    }

    public int getTiming() {
        return timing;
    }

    public void setTiming(int timing) {
        this.timing = timing;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}

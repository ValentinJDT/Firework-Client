package fr.mrcoq.plugin.fwclient.firework.ig;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.List;

public class FireworkInstance {

    private long timing = 0L;
    private int power = 1;
    private Location location;
    private List<FireworkEffect> fwe = new ArrayList<>();

    public void setTiming(long timing) {
        this.timing = timing;
    }

    public long getTiming() {
        return timing;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return location;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }

    public void addEffect(FireworkEffect effect) {
        this.fwe.add(effect);
    }

    public Firework spawn() {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK_ROCKET);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(power);
        fwe.forEach(fwm::addEffect);
        fw.setFireworkMeta(fwm);
        return fw;
    }
}
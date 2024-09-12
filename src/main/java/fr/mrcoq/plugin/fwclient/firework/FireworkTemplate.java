package fr.mrcoq.plugin.fwclient.firework;

import java.util.ArrayList;
import java.util.List;

public class FireworkTemplate {

    private String id;
    private int power;
    private List<FireworkEffect> fireworkEffects = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public List<FireworkEffect> getFireworkEffects() {
        return fireworkEffects;
    }

    public void setFireworkEffects(List<FireworkEffect> fireworkEffects) {
        this.fireworkEffects = fireworkEffects;
    }
}

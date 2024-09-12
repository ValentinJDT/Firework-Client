package fr.mrcoq.plugin.fwclient.firework;

import java.util.List;

public class FireworkEffect {

    private String type;
    private List<String> colors;
    private boolean flicker;
    private boolean trail;
    private List<String> fadeColors;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public boolean getFlicker() {
        return flicker;
    }

    public void setFlicker(boolean flicker) {
        this.flicker = flicker;
    }

    public boolean getTrail() {
        return trail;
    }

    public void setTrail(boolean trail) {
        this.trail = trail;
    }

    public List<String> getFadeColors() {
        return fadeColors;
    }

    public void setFadeColors(List<String> fadeColors) {
        this.fadeColors = fadeColors;
    }
}

package fr.mrcoq.plugin.fwclient.firework;

import java.util.ArrayList;
import java.util.List;

public class Show {
    private List<FireworkTemplate> fireworks = new ArrayList<>();
    private List<Step> steps = new ArrayList<>();

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setFireworks(List<FireworkTemplate> fireworks) {
        this.fireworks = fireworks;
    }

    public List<FireworkTemplate> getFireworks() {
        return fireworks;
    }
}

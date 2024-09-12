package fr.mrcoq.plugin.fwclient.manager;

import fr.mrcoq.plugin.fwclient.FWClient;
import fr.mrcoq.plugin.fwclient.firework.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import fr.mrcoq.plugin.fwclient.firework.ig.FireworkInstance;
import fr.mrcoq.plugin.fwclient.firework.ig.ShowInstance;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.json.JSONArray;
import org.json.JSONObject;

public class ShowManager {

    private FWClient plugin;
    private List<ShowFile> showFiles = new ArrayList<>();

    public ShowManager(FWClient plugin) {
        this.plugin = plugin;
    }

    public List<ShowFile> getShowFiles() {
        return showFiles;
    }

    private File checkShowDirectory() {
        File dir = new File(plugin.getDataFolder(), "/shows/");
        dir.mkdirs();
        return dir;
    }

    private File checkMinecraftDirectory() {
        File dirA = new File(plugin.getDataFolder(), "/pack/assets/minecraft/");
        dirA.mkdirs();
        return dirA;
    }

    private File checkSoundsDirectory() {
        File dirB = new File(plugin.getDataFolder(), "/pack/assets/minecraft/sounds/show/");
        dirB.mkdirs();
        return dirB;
    }

    public void reloadShows() throws IOException {
        File dir = checkShowDirectory();
        showFiles.clear();

        File[] files = Arrays.stream(dir.listFiles()).filter(f -> f.isFile() && f.getName().endsWith(".fcs")).toArray(File[]::new);

        for(File file : files) {
            ShowFile showFile = loadShowFile(file);

            if(showFile != null) {
                showFiles.add(showFile);
            }
        }

        plugin.getLogger().info(showFiles.size() + " show files loaded.");
    }

    public File generateSoundsFile() throws IOException {
        JSONObject obj = new JSONObject();
        for(ShowFile showFile : showFiles) {
            JSONObject objt = new JSONObject();

            objt.put("sounds", List.of("show/" + showFile.getId()));
            obj.put("show." + showFile.getId(), objt);

        }

        File file = new File(checkMinecraftDirectory(), "sounds.json");
        Files.write(Path.of(file.getPath()), obj.toString().getBytes());

        return file;
    }

    public void extractAllSounds() throws IOException {
        File dir = checkShowDirectory();

        File[] files = Arrays.stream(dir.listFiles()).filter(f -> f.isFile() && f.getName().endsWith(".fcs")).toArray(File[]::new);

        for(File file : files) {
            extractSound(file);
        }
    }

    private void extractSound(File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        String fileId = null;
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if(!entry.getName().equalsIgnoreCase("show.json")) continue;
            fileId = new JSONObject(toString(zipFile.getInputStream(entry))).getString("id");
        }

        entries = zipFile.entries();
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            if(!entry.getName().equalsIgnoreCase("song.ogg")) continue;

            File newFile = new File(checkSoundsDirectory(), fileId + ".ogg");
            FileOutputStream outputStream = new FileOutputStream(newFile);
            outputStream.write(zipFile.getInputStream(entry).readAllBytes());
            outputStream.close();
        }
    }

    public ShowFile getShowFile(String id) {
        return this.showFiles.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
    }

    public ShowInstance createShowInstance(ShowFile showFile) {
        ShowInstance showInstance = new ShowInstance(plugin, showFile.getId());

        for(Step step : showFile.getShow().getSteps()) {

            Optional<FireworkTemplate> finded = showFile.getShow().getFireworks().stream().filter(ft -> ft.getId().equalsIgnoreCase(step.getFirework())).findFirst();

            finded.ifPresent(ft -> {
                FireworkInstance fireworkInstance = new FireworkInstance();

                for(FireworkEffect fireworkEffect : ft.getFireworkEffects()) {

                    org.bukkit.FireworkEffect.Builder builder = org.bukkit.FireworkEffect.builder();

                    builder.with(org.bukkit.FireworkEffect.Type.valueOf(fireworkEffect.getType()));

                    builder.flicker(fireworkEffect.getFlicker());
                    builder.trail(fireworkEffect.getTrail());

                    List<Color> colors = fireworkEffect.getColors().stream().map(this::fromString).toList();
                    builder.withColor(colors);

                    List<Color> fadeColors = fireworkEffect.getFadeColors().stream().map(this::fromString).toList();
                    builder.withFade(fadeColors);

                    fireworkInstance.addEffect(builder.build());
                }

                fireworkInstance.setPower(ft.getPower());
                fireworkInstance.setLocation(step.getLocation());
                fireworkInstance.setTiming(step.getTiming());

                showInstance.addFireworkInstance(fireworkInstance);
            });
        }

        return showInstance;
    }

    public ShowFile loadShowFile(File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        String content = null;
        while(entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            if(!entry.getName().equalsIgnoreCase("show.json")) continue;

            content = toString(zipFile.getInputStream(entry));
        }

        if(content == null) {
            Bukkit.getLogger().severe("Can't load file content : " + file.getName());
            return null;
        }

        return toShowFile(content);
    }

    private ShowFile toShowFile(String content) {
        JSONObject showFileObject = new JSONObject(content);

        ShowFile showFile = new ShowFile();

        showFile.setId(showFileObject.getString("id"));
        showFile.setDisplayName(showFileObject.getString("display_name"));
        showFile.setAuthors(showFileObject.getJSONArray("authors").toList().stream().map(a -> (String) a).collect(Collectors.toList()));
        showFile.setVersion(showFileObject.getString("version"));
        showFile.setLink(showFileObject.getString("link"));
        showFile.setSong(showFileObject.getString("song"));

        JSONObject showObject = showFileObject.getJSONObject("show");
        JSONArray fireworksObject = showObject.getJSONArray("fireworks");
        JSONArray stepsObject = showObject.getJSONArray("steps");

        Show show = new Show();

        for(int i = 0; i < fireworksObject.length(); i++) {
            JSONObject fireworkObject = fireworksObject.getJSONObject(i);

            FireworkTemplate fireworkTemplate = new FireworkTemplate();
            fireworkTemplate.setId(fireworkObject.getString("id"));
            fireworkTemplate.setPower(fireworkObject.getInt("power"));

            JSONArray effects = fireworkObject.getJSONArray("effects");

            for(int j = 0; j < effects.length(); j++) {
                JSONObject effectObject = effects.getJSONObject(j);

                FireworkEffect fireworkEffect = new FireworkEffect();
                fireworkEffect.setType(effectObject.getString("type"));
                fireworkEffect.setColors(effectObject.getJSONArray("colors").toList().stream().map(a -> (String) a).collect(Collectors.toList()));
                fireworkEffect.setFlicker(effectObject.getBoolean("flicker"));
                fireworkEffect.setTrail(effectObject.getBoolean("trail"));
                fireworkEffect.setFadeColors(effectObject.getJSONArray("fade_colors").toList().stream().map(a -> (String) a).collect(Collectors.toList()));

                fireworkTemplate.getFireworkEffects().add(fireworkEffect);
            }

            show.getFireworks().add(fireworkTemplate);
        }

        for(int i = 0; i < stepsObject.length(); i++) {
            JSONObject stepObject = stepsObject.getJSONObject(i);

            Step step = new Step();
            step.setFirework(stepObject.getString("firework"));
            step.setTiming(stepObject.getInt("timing"));

            JSONObject locationObject = stepObject.getJSONObject("location");
            step.setLocation(new Location(
                            Bukkit.getWorld(locationObject.getString("world")),
                            locationObject.getDouble("x"),
                            locationObject.getDouble("y"),
                            locationObject.getDouble("z")
                    )
            );

            show.getSteps().add(step);
        }

        showFile.setShow(show);

        return showFile;
    }

    private String toString(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
    }

    private Color fromString(String color) {
        return switch(color.toUpperCase()) {
            case "WHITE" -> Color.WHITE;
            case "SILVER" -> Color.SILVER;
            case "GRAY" -> Color.GRAY;
            case "BLACK" -> Color.BLACK;
            case "RED" -> Color.RED;
            case "MAROON" -> Color.MAROON;
            case "YELLOW" -> Color.YELLOW;
            case "OLIVE" -> Color.OLIVE;
            case "LIME" -> Color.LIME;
            case "GREEN" -> Color.GREEN;
            case "AQUA" -> Color.AQUA;
            case "TEAL" -> Color.TEAL;
            case "BLUE" -> Color.BLUE;
            case "NAVY" -> Color.NAVY;
            case "FUCHSIA" -> Color.FUCHSIA;
            case "PURPLE" -> Color.PURPLE;
            case "ORANGE" -> Color.ORANGE;
            default -> Color.BLACK;
        };
    }

}

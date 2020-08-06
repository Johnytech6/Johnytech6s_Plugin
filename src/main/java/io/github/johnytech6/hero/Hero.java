package io.github.johnytech6.hero;

import io.github.johnytech6.DndPlayer;
import io.github.johnytech6.JohnytechPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Hero implements DndPlayer {

    private static Plugin plugin = JohnytechPlugin.getPlugin();

    private Player playerRef;

    private Location checkpoint;
    private Location chairPosition;

    public Hero(Player p) {
        this.playerRef = p;
    }

    @Override
    public void loadConfig() {
        setCheckpoint(plugin.getConfig().getLocation("Heros."+playerRef.getName()+".checkpoint"));
        setChairPosition(plugin.getConfig().getLocation("Heros."+playerRef.getName()+".chair_position"));
    }

    @Override
    public Location getCheckpoint() {
        return checkpoint;
    }

    @Override
    public void setCheckpoint(Location checkpoint) {
        this.checkpoint = checkpoint;
        plugin.getConfig().set("Heros."+playerRef.getName()+".checkpoint", checkpoint);
        plugin.saveConfig();
    }

    @Override
    public boolean hasCheckpoint() {
        if (checkpoint != null) {
            return true;
        }
        return false;
    }

    @Override
    public Location getChairPosition() {
        return chairPosition;
    }

    @Override
    public void setChairPosition(Location chairPosition) {
        this.chairPosition = chairPosition;
        plugin.getConfig().set("Heros."+playerRef.getName()+".chair_position", chairPosition);
        plugin.saveConfig();
    }

    @Override
    public boolean hasChair() {
        if (chairPosition != null) {
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return playerRef.getName();
    }

    @Override
    public Player getPlayer() {
        return this.playerRef;
    }

    @Override
    public UUID getUniqueId() {
        return playerRef.getUniqueId();
    }

    @Override
    public void sendTitle(String title, String s, int i, int i1, int i2) {
        playerRef.sendTitle(title, s, i, i1, i2);
    }

    @Override
    public void sendMessage(String s) {
        playerRef.sendMessage(s);
    }

    @Override
    public void teleport(Location targetLocation) {
        playerRef.teleport(targetLocation);
    }

    @Override
    public Location getLocation() {
        return playerRef.getLocation();
    }

    @Override
    public void addPotionEffect(PotionEffect potionEffect) {
        playerRef.addPotionEffect(potionEffect);
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffect) {
        playerRef.removePotionEffect(potionEffect);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffect) {
        return playerRef.hasPotionEffect(potionEffect);
    }

    public float getWalkSpeed() {
        return playerRef.getWalkSpeed();
    }

    public void setWalkSpeed(float f) {
        playerRef.setWalkSpeed(f);
    }
}
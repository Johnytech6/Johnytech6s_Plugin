package io.github.johnytech6.Handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import io.github.johnytech6.DndPlayer;
import io.github.johnytech6.dm.Dm;
import io.github.johnytech6.dm.puppeter.Puppet;
import io.github.johnytech6.dm.puppeter.Puppeter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.johnytech6.JohnytechPlugin;

public class PuppeterHandler {

    private DMHandler dmh;
    private PluginHandler pluginHandler;

    // list of morphed puppeter
    private HashMap<UUID, Puppeter> puppeters;
    // list of puppeter
    private HashMap<UUID, Puppeter> morphedPuppeters;

    public PuppeterHandler(PluginHandler pluginHandler) {
        this.pluginHandler = pluginHandler;
        dmh = pluginHandler.getDmHandler();
        puppeters = new HashMap<>();
        morphedPuppeters = new HashMap<>();
    }

    /**
     * @param ep
     */
    public void Unmorph(Player ep) {
        Puppet pu = GetMorphPuppeter(ep.getUniqueId()).getPuppet();
        Entity e = pu.getEntity();
        if (e instanceof ArmorStand) {
            UnmorphOfArmorStand(ep);
        } else {
            UnmorphMob(ep);
        }
    }

    /**
     * UnMorph the puppeter
     *
     * @param ep
     */
    public void UnmorphMob(Player ep) {

        Puppeter Morphedpuppeter = GetMorphPuppeter(ep.getUniqueId());
        Player p = Morphedpuppeter.getPlayer();
        Puppet pu = Morphedpuppeter.getPuppet();
        Entity e = pu.getEntity();

        p.chat("/unmorph");

        // remove item of entity of the player
        p.getInventory().setItemInMainHand(((LivingEntity) e).getEquipment().getItemInMainHand());
        p.getInventory().setItemInOffHand((((LivingEntity) e).getEquipment().getItemInOffHand()));
        p.getInventory().setHelmet(((LivingEntity) e).getEquipment().getHelmet());
        p.getInventory().setChestplate(((LivingEntity) e).getEquipment().getChestplate());
        p.getInventory().setLeggings(((LivingEntity) e).getEquipment().getLeggings());
        p.getInventory().setBoots(((LivingEntity) e).getEquipment().getBoots());

        // give item to the entity
        ((LivingEntity) e).getEquipment().setItemInMainHand(pu.getItemInMainHand());
        ((LivingEntity) e).getEquipment().setItemInOffHand(pu.getItemInOffHand());
        ((LivingEntity) e).getEquipment().setHelmet(pu.getHelmet());
        ((LivingEntity) e).getEquipment().setChestplate(pu.getChestplate());
        ((LivingEntity) e).getEquipment().setLeggings(pu.getLeggings());
        ((LivingEntity) e).getEquipment().setBoots(pu.getBoots());

        e.teleport(p.getLocation());
        e.setInvulnerable(false);
        ((LivingEntity) e).removePotionEffect(PotionEffectType.INVISIBILITY);
        e.setSilent(false);

        p.removePassenger(e);

        UUID playerID = p.getUniqueId();
        Dm dm = dmh.getDm(playerID);

        Boolean invisibleState = Morphedpuppeter.wasInvisible();
        pluginHandler.saveInvisbility(dm, invisibleState);
        dm.setInvisibility(invisibleState);

        Boolean nightVisionState = Morphedpuppeter.hadNightVision();
        pluginHandler.saveNightVision(dm, nightVisionState);
        dm.setNightVision(nightVisionState);

        RemoveMorphPlayer(Morphedpuppeter);
    }

    public void UnmorphOfArmorStand(Player ep) {

        Puppeter morphedpuppeter = GetMorphPuppeter(ep.getUniqueId());
        Puppet pu = morphedpuppeter.getPuppet();
        Entity e = pu.getEntity();
        EntityEquipment armorStandEquipement = ((ArmorStand) e).getEquipment();

        // remove item of entity off the player
        ep.getInventory().setItemInMainHand(armorStandEquipement.getItemInMainHand());
        ep.getInventory().setItemInOffHand(armorStandEquipement.getItemInOffHand());
        ep.getInventory().setHelmet(armorStandEquipement.getHelmet());
        ep.getInventory().setChestplate(armorStandEquipement.getChestplate());
        ep.getInventory().setLeggings(armorStandEquipement.getLeggings());
        ep.getInventory().setBoots(armorStandEquipement.getBoots());

        if (morphedpuppeter.wasInvisible()) {
            Dm dm = dmh.getDm(ep.getUniqueId());
            pluginHandler.saveInvisbility(dm, true);
            dm.setInvisibility(true);
        }

        // give item to the entity
        armorStandEquipement.setItemInMainHand(pu.getItemInMainHand());
        armorStandEquipement.setItemInOffHand(pu.getItemInOffHand());
        armorStandEquipement.setHelmet(pu.getHelmet());
        armorStandEquipement.setChestplate(pu.getChestplate());
        armorStandEquipement.setLeggings(pu.getLeggings());
        armorStandEquipement.setBoots(pu.getBoots());

        e.teleport(ep.getLocation());
        ((ArmorStand) e).setCollidable(true);
        e.setInvulnerable(false);
        ((ArmorStand) e).setVisible(true);

        morphedpuppeter.setPuppet(null);

        RemoveMorphPlayer(morphedpuppeter);
    }

    /**
     * Take the inventory of ArmorStand and make this armorStand disapear
     *
     * @param p
     * @param armorStand
     */
    public void MorphInArmorStand(final Player p, ArmorStand armorStand, EquipmentSlot selectedEquipment,
                                  ItemStack itemHold) {

        EntityEquipment armorStandEquipement = armorStand.getEquipment();
        final ItemStack EEitemInMainHand = armorStandEquipement.getItemInMainHand();

        Puppeter puppeter = getPuppeter(p.getUniqueId());
        puppeter.setPuppet(new Puppet(armorStand));

        // Puppeter is in list of MorphedPlayer
        AddMorphPuppeter(puppeter);

        // teleport player to entity last location
        p.teleport(armorStand.getLocation());

        puppeter.setInvisibilityState(dmh.getDm(p.getUniqueId()).isInvisible());
        if (puppeter.wasInvisible()) {
            Dm dm = dmh.getDm(p.getUniqueId());
            pluginHandler.saveInvisbility(dm, false);
            dm.setInvisibility(false);
        }

        // Set armor stand invisible incollidable and invulnerable
        armorStand.setVisible(false);
        armorStand.setCollidable(false);
        armorStand.setInvulnerable(true);

        storeArmorStandEquipment(puppeter.getPuppet(), armorStandEquipement, selectedEquipment, itemHold);

        giveArmorOfAT(p, armorStandEquipement, selectedEquipment, itemHold);

        removeEquipmentFromAS(armorStandEquipement);

        Bukkit.getScheduler().scheduleSyncDelayedTask(pluginHandler.getPlugin(), new Runnable() {
            public void run() {
                itemInHand(p, EEitemInMainHand);
            }
        }, 2L);

    }

    /**
     * Give items of entity to player of ArmorStand
     *
     * @param p
     * @param ee
     * @param selectedEquipment
     */
    private void giveArmorOfAT(Player p, EntityEquipment ee, EquipmentSlot selectedEquipment, ItemStack itemHold) {

        if (selectedEquipment == EquipmentSlot.HAND) {
            p.getInventory().setItemInOffHand(ee.getItemInOffHand());
            p.getInventory().setHelmet(ee.getHelmet());
            p.getInventory().setChestplate(ee.getChestplate());
            p.getInventory().setLeggings(ee.getLeggings());
            p.getInventory().setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.OFF_HAND) {
            p.getInventory().setItemInOffHand(itemHold);
            p.getInventory().setHelmet(ee.getHelmet());
            p.getInventory().setChestplate(ee.getChestplate());
            p.getInventory().setLeggings(ee.getLeggings());
            p.getInventory().setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.HEAD) {
            p.getInventory().setItemInOffHand(ee.getItemInOffHand());
            p.getInventory().setHelmet(itemHold);
            p.getInventory().setChestplate(ee.getChestplate());
            p.getInventory().setLeggings(ee.getLeggings());
            p.getInventory().setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.CHEST) {
            p.getInventory().setItemInOffHand(ee.getItemInOffHand());
            p.getInventory().setHelmet(ee.getHelmet());
            p.getInventory().setChestplate(itemHold);
            p.getInventory().setLeggings(ee.getLeggings());
            p.getInventory().setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.LEGS) {
            p.getInventory().setItemInOffHand(ee.getItemInOffHand());
            p.getInventory().setHelmet(ee.getHelmet());
            p.getInventory().setChestplate(ee.getChestplate());
            p.getInventory().setLeggings(itemHold);
            p.getInventory().setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.FEET) {
            p.getInventory().setItemInOffHand(ee.getItemInOffHand());
            p.getInventory().setHelmet(ee.getHelmet());
            p.getInventory().setChestplate(ee.getChestplate());
            p.getInventory().setLeggings(ee.getLeggings());
            p.getInventory().setBoots(itemHold);
        }
    }

    /**
     * Store items of ArmorStand in Puppet
     *
     * @param pu
     * @param ee
     */
    private void storeArmorStandEquipment(Puppet pu, EntityEquipment ee, EquipmentSlot selectedEquipment,
                                          ItemStack itemhold) {

        if (selectedEquipment == EquipmentSlot.HAND) {
            pu.setItemInMainHand(itemhold);
            pu.setItemInOffHand(ee.getItemInOffHand());
            pu.setHelmet(ee.getHelmet());
            pu.setChestplate(ee.getChestplate());
            pu.setLeggings(ee.getLeggings());
            pu.setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.OFF_HAND) {
            pu.setItemInMainHand(ee.getItemInMainHand());
            pu.setItemInOffHand(itemhold);
            pu.setHelmet(ee.getHelmet());
            pu.setChestplate(ee.getChestplate());
            pu.setLeggings(ee.getLeggings());
            pu.setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.HEAD) {
            pu.setItemInMainHand(ee.getItemInMainHand());
            pu.setItemInOffHand(ee.getItemInOffHand());
            pu.setHelmet(itemhold);
            pu.setChestplate(ee.getChestplate());
            pu.setLeggings(ee.getLeggings());
            pu.setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.CHEST) {
            pu.setItemInMainHand(ee.getItemInMainHand());
            pu.setItemInOffHand(ee.getItemInOffHand());
            pu.setHelmet(ee.getHelmet());
            pu.setChestplate(itemhold);
            pu.setLeggings(ee.getLeggings());
            pu.setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.LEGS) {
            pu.setItemInMainHand(ee.getItemInMainHand());
            pu.setItemInOffHand(ee.getItemInOffHand());
            pu.setHelmet(ee.getHelmet());
            pu.setChestplate(ee.getChestplate());
            pu.setLeggings(itemhold);
            pu.setBoots(ee.getBoots());
        } else if (selectedEquipment == EquipmentSlot.FEET) {
            pu.setItemInMainHand(ee.getItemInMainHand());
            pu.setItemInOffHand(ee.getItemInOffHand());
            pu.setHelmet(ee.getHelmet());
            pu.setChestplate(ee.getChestplate());
            pu.setLeggings(ee.getLeggings());
            pu.setBoots(itemhold);
        }

    }

    /**
     * Delete all items of entity
     *
     * @param ee
     */
    private void removeEquipmentFromAS(EntityEquipment ee) {
        ee.setItemInMainHand(null);
        ee.setItemInOffHand(null);
        ee.setHelmet(null);
        ee.setChestplate(null);
        ee.setLeggings(null);
        ee.setBoots(null);
    }

    /**
     * Set the armor selected onto you
     *
     * @param p
     * @param itemInHand
     */
    private void itemInHand(Player p, ItemStack itemInHand) {
        p.getInventory().setItemInMainHand(itemInHand);
    }

    /*
     * private boolean isItemArmor( ItemStack itemStack) { if (itemStack == null)
     * return false; final String typeNameString = itemStack.getType().name(); if
     * (typeNameString.endsWith("_HELMET") || typeNameString.endsWith("_CHESTPLATE")
     * || typeNameString.endsWith("_LEGGINGS") || typeNameString.endsWith("_BOOTS"))
     * { return true; }
     *
     * return false; }
     */

    /**
     * Transform dm Puppeter into the entity right clicked
     *
     * @param p
     * @param e
     */
    public void Morph(Player p, Entity e) {

        if(!(e instanceof LivingEntity) && !(e instanceof ArmorStand)){

            p.sendMessage("This " + e.getType().toString() + " is not morphable");
            return;
        }

        Puppeter puppeter = getPuppeter(p.getUniqueId());
        puppeter.setPuppet(new Puppet(e));
        Puppet pu = puppeter.getPuppet();

        // Puppeter is in list of MorphedPlayer
        AddMorphPuppeter(puppeter);

        // teleport player to entity last location
        p.teleport(e.getLocation());

        puppeter.setInvisibilityState(dmh.getDm(p.getUniqueId()).isInvisible());
        puppeter.setNightVisionState(dmh.getDm(p.getUniqueId()).hasNightVision());

        // make entity invinsible, silent and invisible
        ((LivingEntity) e)
                .addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 10, false, false));
        e.setInvulnerable(true);
        e.setSilent(true);

        p.addPassenger(e);

        // give items of entity to player
        p.getInventory().setItemInMainHand(((LivingEntity) e).getEquipment().getItemInMainHand());
        p.getInventory().setItemInOffHand((((LivingEntity) e).getEquipment().getItemInOffHand()));
        p.getInventory().setHelmet(((LivingEntity) e).getEquipment().getHelmet());
        p.getInventory().setChestplate(((LivingEntity) e).getEquipment().getChestplate());
        p.getInventory().setLeggings(((LivingEntity) e).getEquipment().getLeggings());
        p.getInventory().setBoots(((LivingEntity) e).getEquipment().getBoots());

        // store items of entity
        pu.setItemInMainHand(((LivingEntity) e).getEquipment().getItemInMainHand());
        pu.setItemInOffHand((((LivingEntity) e).getEquipment().getItemInOffHand()));
        pu.setHelmet(((LivingEntity) e).getEquipment().getHelmet());
        pu.setChestplate(((LivingEntity) e).getEquipment().getChestplate());
        pu.setLeggings(((LivingEntity) e).getEquipment().getLeggings());
        pu.setBoots(((LivingEntity) e).getEquipment().getBoots());

        // Delete all items of entity
        ((LivingEntity) e).getEquipment().setItemInMainHand(null);
        ((LivingEntity) e).getEquipment().setItemInOffHand(null);
        ((LivingEntity) e).getEquipment().setHelmet(null);
        ((LivingEntity) e).getEquipment().setChestplate(null);
        ((LivingEntity) e).getEquipment().setLeggings(null);
        ((LivingEntity) e).getEquipment().setBoots(null);

        // -----Morphy plugin handling-----
        String entityName = e.toString();
        entityName = entityName.replace("Craft", "");
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        entityName = entityName.replaceAll(regex, replacement).toLowerCase();
        p.chat("/morph " + entityName); // MORPH
        // -----Morph plugin handling-----
    }

    /**
     * Toggle Puppeter Mode(call by dm only)
     *
     * @param dm
     * @return
     */
    public void TogglePuppeterMode(Dm dm, boolean verbose) {
        dm.setPuppeterPower(!dm.havePuppeterPower());
        setPuppeterMode(dm.getPlayer(), dm.havePuppeterPower(), verbose);

        pluginHandler.savePuppeterPower(dm, dm.havePuppeterPower());
    }

    public boolean setPuppeterMode(Player p, boolean hasPuppeterPower, boolean verbose) {

        if (hasPuppeterPower) {
            AddPuppeter(new Puppeter(p));
            if (verbose) {
                p.sendMessage("You have puppeter power.");
            }
        } else {
            if (isPlayerMorph(p.getUniqueId())) {
                Unmorph(p);
            }

            RemovePuppeter(getPuppeter(p.getUniqueId()));
            if (verbose) {
                p.sendMessage("You dont have puppeter power anymore");
            }
        }
        return true;
    }

    /**
     * Add morphing puppeter
     *
     * @param puppeter
     */
    public void AddMorphPuppeter(Puppeter puppeter) {

        if (!(isPlayerMorph(puppeter.getUniqueId()))) {
            morphedPuppeters.put(puppeter.getUniqueId(), puppeter);
        }
    }

    /*
     * Remove morphed puppeter
     */
    public void RemoveMorphPlayer(Puppeter pp) {
        morphedPuppeters.remove(pp.getUniqueId());
    }

    /*
     * Check if puppeter already morph
     */
    public boolean isPlayerMorph(UUID id) {
        if (morphedPuppeters.containsKey(id)) {
            return true;
        }
        return false;
    }

    /*
     * Get morphed puppeter reference with his id
     */
    public Puppeter GetMorphPuppeter(UUID id) {
        return morphedPuppeters.get(id);
    }

    /*
     * Add a puppeter
     */
    public void AddPuppeter(Puppeter pp) {
        if (!(isPlayerPuppeter(pp.getUniqueId()))) {
            puppeters.put(pp.getUniqueId(), pp);
        }
    }

    /*
     * Remove puppeter
     */
    public void RemovePuppeter(Puppeter pp) {
        puppeters.remove(pp.getUniqueId());
    }

    /*
     * Check if the player is in the list of all the puppeters
     */
    public boolean isPlayerPuppeter(UUID id) {
        if (puppeters.containsKey(id)) {
            return true;
        }
        return false;
    }

    /*
     * Get puppeter reference with his UUID
     */
    public Puppeter getPuppeter(UUID id) {
        return puppeters.get(id);
    }

    /*
     * Get reference of the list of all morphed puppeter
     */
    public HashMap<UUID, Puppeter> getMorphPlayers() {
        return morphedPuppeters;
    }

    /*
     * Get reference of the list of all puppeters
     */
    public HashMap<UUID, Puppeter> getPuppeters() {
        return puppeters;
    }
}

package com.kingssmp.rituals;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemManager {

    public enum KSItem {
        DRAGON_BONE_BLADE("Dragon Bone Blade", Material.NETHERITE_SWORD),
        LIFE_STEALER("Life Stealer", Material.DIAMOND_SWORD),
        AVIAN_TRIDENT("Avian Trident", Material.TRIDENT),
        VOID_BOW("Void Bow", Material.BOW),
        KINGS_CROWN("King's Crown", Material.GOLDEN_HELMET),
        BUCKET_OF_MULK("Bucket of Mulk", Material.MILK_BUCKET);

        public final String display;
        public final Material mat;
        KSItem(String display, Material mat) { this.display = display; this.mat = mat; }
    }

    private final KingsSMPPlugin plugin;
    private final Map<KSItem, ItemStack> prototypes = new EnumMap<>(KSItem.class);

    public ItemManager(KingsSMPPlugin plugin) {
        this.plugin = plugin;
        buildPrototypes();
    }

    public ItemStack get(KSItem type) {
        return prototypes.get(type).clone();
    }

    public boolean is(ItemStack stack, KSItem type) {
        if (stack == null) return false;
        if (!stack.hasItemMeta()) return false;
        var meta = stack.getItemMeta();
        var key = plugin.key("ks_item");
        var val = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        return type.name().equals(val);
    }

    public Optional<KSItem> which(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return Optional.empty();
        var val = stack.getItemMeta().getPersistentDataContainer().get(plugin.key("ks_item"), PersistentDataType.STRING);
        if (val == null) return Optional.empty();
        try { return Optional.of(KSItem.valueOf(val)); } catch (Exception e) { return Optional.empty(); }
    }

    private void tag(ItemMeta meta, KSItem type) {
        meta.getPersistentDataContainer().set(plugin.key("ks_item"), PersistentDataType.STRING, type.name());
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
    }

    private void addDmgSpeed(ItemMeta meta, double damage, double attackSpeed) {
        // Set base modifiers (override)
        var dmg = new AttributeModifier(UUID.randomUUID(), "ks_dmg", damage, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND);
        var spd = new AttributeModifier(UUID.randomUUID(), "ks_spd", attackSpeed - 4.0, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, dmg);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, spd);
    }

    private void buildPrototypes() {
        // DRAGON_BONE_BLADE
        prototypes.put(KSItem.DRAGON_BONE_BLADE, createWeapon(KSItem.DRAGON_BONE_BLADE, ChatColor.DARK_PURPLE, 10, 1.6, true));

        // LIFE_STEALER
        prototypes.put(KSItem.LIFE_STEALER, createWeapon(KSItem.LIFE_STEALER, ChatColor.RED, 8, 1.6, true));

        // AVIAN_TRIDENT
        prototypes.put(KSItem.AVIAN_TRIDENT, createWeapon(KSItem.AVIAN_TRIDENT, ChatColor.AQUA, 12, 1.2, true));

        // VOID_BOW
        ItemStack vb = new ItemStack(KSItem.VOID_BOW.mat);
        ItemMeta vbMeta = vb.getItemMeta();
        vbMeta.setDisplayName(ChatColor.DARK_AQUA + KSItem.VOID_BOW.display);
        tag(vbMeta, KSItem.VOID_BOW);
        vb.setItemMeta(vbMeta);
        prototypes.put(KSItem.VOID_BOW, vb);

        // KING'S CROWN
        ItemStack crown = new ItemStack(KSItem.KINGS_CROWN.mat);
        ItemMeta cMeta = crown.getItemMeta();
        cMeta.setDisplayName(ChatColor.GOLD + KSItem.KINGS_CROWN.display);
        tag(cMeta, KSItem.KINGS_CROWN);
        cMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
        crown.setItemMeta(cMeta);
        prototypes.put(KSItem.KINGS_CROWN, crown);

        // BUCKET OF MULK
        ItemStack mulk = new ItemStack(KSItem.BUCKET_OF_MULK.mat);
        ItemMeta mMeta = mulk.getItemMeta();
        mMeta.setDisplayName(ChatColor.WHITE + KSItem.BUCKET_OF_MULK.display);
        tag(mMeta, KSItem.BUCKET_OF_MULK);
        mulk.setItemMeta(mMeta);
        prototypes.put(KSItem.BUCKET_OF_MULK, mulk);
    }

    private ItemStack createWeapon(KSItem type, ChatColor color, double damage, double speed, boolean enchantable) {
        ItemStack it = new ItemStack(type.mat);
        ItemMeta meta = it.getItemMeta();
        meta.setDisplayName(color + type.display);
        tag(meta, type);
        addDmgSpeed(meta, damage, speed);
        it.setItemMeta(meta);
        return it;
    }
}

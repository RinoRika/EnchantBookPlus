package cn.stars.enchantbookplus.listener;

import cn.stars.enchantbookplus.ConfigEnchantmentEntry;
import cn.stars.enchantbookplus.EnchantBookPlus;
import cn.stars.enchantbookplus.Permissions;
import cn.stars.enchantbookplus.util.PlayerUtil;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.view.AnvilView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.bukkit.Bukkit.getServer;

public final class PrepareAnvilListener implements Listener {
    @EventHandler
    public void onPrepareAnvil(final PrepareAnvilEvent event) {
        final Optional<ItemStack> result = Optional.ofNullable(event.getResult());
        if (result.isEmpty()) return;

        final AnvilInventory inventory = event.getInventory();
        final AnvilView view = event.getView();

        final ItemStack item = inventory.getItem(0);
        if (item == null) return;

        final ItemStack upgrade = inventory.getItem(1);
        if (upgrade == null) return;

        final Map<Enchantment, Integer> itemEnchants =
                item.getType() == Material.ENCHANTED_BOOK && item.getItemMeta() instanceof final EnchantmentStorageMeta itemMeta ?
                        itemMeta.getStoredEnchants() : item.getEnchantments();
        final Map<Enchantment, Integer> upgradeEnchants =
                upgrade.getType() == Material.ENCHANTED_BOOK && upgrade.getItemMeta() instanceof final EnchantmentStorageMeta upgradeMeta ?
                        upgradeMeta.getStoredEnchants() : upgrade.getEnchantments();

        if (upgradeEnchants.isEmpty()) return;
        final HashMap<Enchantment, Integer> upgrades = new HashMap<>();
        int cost = 0;
        for (final Map.Entry<Enchantment, Integer> entry : upgradeEnchants.entrySet()) {
            final Enchantment enchantment = entry.getKey();
            if (!view.getPlayer().hasPermission(Permissions.enchant(enchantment))) continue;
            if (enchantment.getMaxLevel() == 1) continue;
            final Optional<ConfigEnchantmentEntry> configEnchantment = EnchantBookPlus.getInstance().getConfigEnchantment(enchantment);
            if (configEnchantment.isEmpty()) continue;
            final int upgradeLevel = entry.getValue();
            final int finalLevel;
            if (itemEnchants.containsKey(enchantment)) {
                final int itemLevel = itemEnchants.get(enchantment);
                if (itemLevel > upgradeLevel) finalLevel = itemLevel;
                else if (itemLevel == upgradeLevel) finalLevel = upgradeLevel + 1;
                else finalLevel = upgradeLevel;
            }
            else finalLevel = upgradeLevel;
            if (finalLevel <= enchantment.getMaxLevel()) continue;
            if (configEnchantment.get().getMaxLevel().isPresent() && finalLevel > configEnchantment.get().getMaxLevel().get()) continue;
            if (finalLevel > upgradeLevel) cost += configEnchantment.get().getMultiplyCostByLevel() ? configEnchantment.get().getCost() * (finalLevel - enchantment.getMaxLevel()) : configEnchantment.get().getCost();
            upgrades.put(enchantment, finalLevel);
        }

        if (upgrades.isEmpty()) return;

        final int vanillaCost = view.getRepairCost();
        view.setRepairCost(vanillaCost + cost);
        for (final Map.Entry<Enchantment, Integer> entry : upgrades.entrySet()) {
            if (result.get().getItemMeta() instanceof final EnchantmentStorageMeta resultMeta) {
                if (!resultMeta.getStoredEnchants().containsKey(entry.getKey())) continue;
                resultMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
                result.get().setItemMeta(resultMeta);
            }
            else {
                if (!result.get().getEnchantments().containsKey(entry.getKey())) continue;
                result.get().addUnsafeEnchantment(entry.getKey(), entry.getValue());
            }
        }

        if (!(view.getPlayer() instanceof Player)
                || view.getPlayer().getGameMode() == GameMode.CREATIVE) {
            return;
        }

        getServer().getScheduler().runTask(EnchantBookPlus.getInstance(), () -> {
            ItemStack input2 = inventory.getItem(1);
            PlayerUtil.setInstantBuild(
                    (Player) view.getPlayer(),
                    // Prevent "Too Expensive!" with no secondary input.
                    input2 == null || input2.getType() == Material.AIR
                            // Display "Too Expensive!" if cost meets or exceeds maximum.
                            || view.getRepairCost() < view.getMaximumRepairCost());
        });
    }
}

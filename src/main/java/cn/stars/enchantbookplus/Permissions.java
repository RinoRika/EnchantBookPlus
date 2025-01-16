package cn.stars.enchantbookplus;

import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

public final class Permissions {
    public static @NotNull String enchant(final @NotNull Enchantment enchantment) {
        return "enchantbookplus.enchant." + enchantment.getKey().getKey();
    }

    public static @NotNull String RELOAD = "enchantbookplus.reload";
}

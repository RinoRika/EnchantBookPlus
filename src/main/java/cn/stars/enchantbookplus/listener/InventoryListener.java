package cn.stars.enchantbookplus.listener;

import cn.stars.enchantbookplus.util.PlayerUtil;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.view.AnvilView;

public class InventoryListener implements Listener {
    @EventHandler
    private void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory)) {
            return;
        }

        ((AnvilView) event.getView()).setMaximumRepairCost(32767);

        if (event.getPlayer() instanceof Player
                && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            PlayerUtil.setInstantBuild((Player) event.getPlayer(), true);
        }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() instanceof AnvilInventory
                && event.getPlayer() instanceof Player
                && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            PlayerUtil.setInstantBuild((Player) event.getPlayer(), false);
        }
    }
}

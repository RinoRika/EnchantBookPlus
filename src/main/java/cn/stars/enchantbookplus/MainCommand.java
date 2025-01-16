package cn.stars.enchantbookplus;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("all")
public final class MainCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) reload(sender, command);
        return overview(sender);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        return null;
    }

    /**
     * Plugin overview
     */
    public static boolean overview(final @NotNull CommandSender sender) {
        final @NotNull EnchantBookPlus plugin = EnchantBookPlus.getInstance();
        sender.sendMessage(MiniMessage.miniMessage()
                .deserialize("<green><name></green> <white>v<version> by</white> <gray><author></gray>.", Placeholder.unparsed("name", plugin
                        .getPluginMeta().getName()), Placeholder.unparsed("version", plugin.getPluginMeta()
                        .getVersion()), Placeholder.unparsed("author", String.join(", ", plugin.getPluginMeta()
                        .getAuthors()))));
        sender.sendMessage(MiniMessage.miniMessage().deserialize(" <gray>> /enchantbookplus reload -</gray> <white>重载配置.</white>"));
        return true;
    }

    /**
     * Reload plugin configuration
     */
    public static boolean reload(final @NotNull CommandSender sender, final @NotNull Command command) {
        if (!sender.hasPermission(Permissions.RELOAD)) {
            sender.sendMessage(Optional.ofNullable(command.permissionMessage()).orElse(sender.getServer().permissionMessage()));
            return true;
        }
        EnchantBookPlus.getInstance().reload();
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>(!) 插件配置已重载."));
        return true;
    }
}

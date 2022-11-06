package com.enjin.kingdomdarkages.chromatica;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

import static org.bukkit.permissions.PermissionDefault.FALSE;

public final class Chromatica extends JavaPlugin implements Listener {

    private int messageIndex;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getConfig().getConfigurationSection("groups").getKeys(false).forEach((group) -> {
            getServer().getPluginManager().addPermission(new Permission(
                    "chromatica.groups." + group,
                    "Applies formatting for group " + group,
                    FALSE
            ));
        });
        getServer().getPluginManager().registerEvents(this, this);
        messageIndex = 0;
        if (getConfig().getStringList("headers").size() > 1 || getConfig().getStringList("footers").size() > 1) {
            getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
                messageIndex++;
                getServer().getOnlinePlayers().forEach(this::updateHeaderAndFooter);
            }, getConfig().getLong("update-frequency"), getConfig().getLong("update-frequency"));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getConfig().getConfigurationSection("groups").getKeys(false).stream()
                .filter(groupName -> player.hasPermission("chromatica.groups." + groupName))
                .findFirst()
                .ifPresent(group -> player.setPlayerListName(
                        PlaceholderAPI.setPlaceholders(
                                player,
                                ChatColor.translateAlternateColorCodes('&',
                                        getConfig().getString("groups." + group))
                        )
                ));

        updateHeaderAndFooter(player);
    }

    private void updateHeaderAndFooter(Player player) {
        List<String> headers = getConfig().getStringList("headers");
        String header = headers.get(messageIndex % headers.size());
        player.setPlayerListHeader(PlaceholderAPI.setPlaceholders(
                player,
                PlaceholderAPI.setPlaceholders(
                        player,
                        ChatColor.translateAlternateColorCodes('&', header)
                )
        ));
        List<String> footers = getConfig().getStringList("footers");
        String footer = footers.get(messageIndex % footers.size());
        player.setPlayerListFooter(PlaceholderAPI.setPlaceholders(
                player,
                PlaceholderAPI.setPlaceholders(
                        player,
                        ChatColor.translateAlternateColorCodes('&',
                                footer)
                )
        ));
    }

}

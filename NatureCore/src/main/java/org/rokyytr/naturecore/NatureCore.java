package org.rokyytr.naturecore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class NatureCore extends JavaPlugin {

    private FileConfiguration config;
    private Location spawnLocation;
    private Set<UUID> whitelist;
    private boolean whitelistEnabled;

    @Override
    public void onEnable() {
        getLogger().info("NatureCore has been enabled!");

        config = getConfig();
        saveDefaultConfig();
        spawnLocation = loadSpawnLocation();

        whitelist = new HashSet<>();
        whitelistEnabled = config.getBoolean("whitelist-enabled");
        if (whitelistEnabled) {
            loadWhitelist();
        }

        getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("NatureCore has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Only players can execute this command!");
            return true;
        }

        Player player = (Player) sender;

        if (label.equalsIgnoreCase("gmc")) {
            if (args.length == 0) {
                player.setGameMode(GameMode.CREATIVE);
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Your gamemode has been updated to Creative.");
                return true;
            }
        } else if (label.equalsIgnoreCase("gms")) {
            if (args.length == 0) {
                player.setGameMode(GameMode.SURVIVAL);
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Your gamemode has been updated to Survival.");
                return true;
            }
        } else if (label.equalsIgnoreCase("gma")) {
            if (args.length == 0) {
                player.setGameMode(GameMode.ADVENTURE);
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Your gamemode has been updated to Adventure.");
                return true;
            }
        } else if (label.equalsIgnoreCase("gmsp")) {
            if (args.length == 0) {
                player.setGameMode(GameMode.SPECTATOR);
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Your gamemode has been updated to Spectator.");
                return true;
            }
        } else if (label.equalsIgnoreCase("sun")) {
            if (args.length == 0) {
                setWeather(player.getWorld(), true);
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "The weather has been set to sun.");
                return true;
            }
        } else if (label.equalsIgnoreCase("rain")) {
            if (args.length == 0) {
                setWeather(player.getWorld(), false);
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "The weather has been set to rain.");
                return true;
            }
        } else if (label.equalsIgnoreCase("tpcoords")) {
            if (args.length == 3) {
                try {
                    double x = Double.parseDouble(args[0]);
                    double y = Double.parseDouble(args[1]);
                    double z = Double.parseDouble(args[2]);
                    Location location = new Location(player.getWorld(), x, y, z);
                    player.teleport(location);
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "You have been teleported to the specified coordinates.");
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Invalid coordinates! Please provide numeric values for x, y, and z.");
                    return false;
                }
            } else {
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Usage: /tpcoords <x> <y> <z>");
                return false;
            }
        } else if (label.equalsIgnoreCase("tp")) {
            if (args.length == 1) {
                Player target = getServer().getPlayer(args[0]);
                if (target != null) {
                    player.teleport(target.getLocation());
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "You have been teleported to " + target.getName() + ".");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Player not found!");
                }
                return true;
            }
        } else if (label.equalsIgnoreCase("tpall")) {
            if (args.length == 0) {
                for (Player onlinePlayer : getServer().getOnlinePlayers()) {
                    onlinePlayer.teleport(player.getLocation());
                }
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "All players have been teleported to you.");
                return true;
            }
        } else if (label.equalsIgnoreCase("broadcast")) {
            String message = ChatColor.translateAlternateColorCodes('&', String.join(" ", args));

            Bukkit.broadcastMessage(ChatColor.GREEN + "Broadcast" + ChatColor.GRAY + " | " + ChatColor.WHITE + message);
            return true;
        } else if (label.equalsIgnoreCase("anvil")) {
            if (args.length == 0) {
                player.openInventory(Bukkit.createInventory(player, InventoryType.ANVIL));
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "You have opened anvil gui.");
                return true;
            }
        } else if (label.equalsIgnoreCase("craft")) {
            if (args.length == 0) {
                player.openWorkbench(null, true);
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "You have opened crafting gui.");
                return true;
            }
        } else if (label.equalsIgnoreCase("smite")) {
            if (args.length == 1) {
                Player target = getServer().getPlayer(args[0]);
                if (target != null) {
                    target.getWorld().strikeLightning(target.getLocation());
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "You have smitten " + target.getName() + ".");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Player not found!");
                }
                return true;
            }
        } else if (label.equalsIgnoreCase("fly")) {
            if (args.length == 0) {
                player.setAllowFlight(!player.getAllowFlight());
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Flight mode toggled.");
                return true;
            }
        } else if (label.equalsIgnoreCase("speed")) {
            if (args.length == 1) {
                try {
                    float speed = Float.parseFloat(args[0]);
                    player.setWalkSpeed(speed);
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Speed set to " + speed);
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Invalid speed value!");
                    return false;
                }
            }
        } else if (label.equalsIgnoreCase("flyspeed")) {
            if (args.length == 1) {
                try {
                    float speed = Float.parseFloat(args[0]);
                    player.setFlySpeed(speed);
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Speed set to " + speed);
                    return true;
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Invalid speed value!");
                    return false;
                }
            }
        } else if (label.equalsIgnoreCase("clearinventory")) {
            if (args.length == 0) {
                player.getInventory().clear();
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Inventory cleared.");
                return true;
            }
        } else if (label.equalsIgnoreCase("kill")) {
            if (args.length == 1) {
                Player target = getServer().getPlayer(args[0]);
                if (target != null) {
                    target.setHealth(0);
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Player " + target.getName() + " has been killed.");
                    return true;
                } else {
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Player not found!");
                    return false;
                }
            }
        } else if (label.equalsIgnoreCase("heal")) {
            if (args.length == 0) {
                player.setHealth(20);
                player.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "You have been healed!");
                return true;
            }
        } else if (label.equalsIgnoreCase("feed")) {
            if (args.length == 0) {
                player.setFoodLevel(20);
                player.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "You have been fed!");
                return true;
            }
        } else if (label.equalsIgnoreCase("day")) {
            player.getWorld().setTime(0);
            player.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Time set to day.");
            return true;
        } else if (label.equalsIgnoreCase("night")) {
            player.getWorld().setTime(13000);
            player.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Time set to night.");
            return true;
        } else if (label.equalsIgnoreCase("whitelist")) {
            return handleWhitelistCommand(sender, args);
        } else if (label.equalsIgnoreCase("setspawn")) {
            if (args.length == 0) {
                Location spawnLocation = player.getLocation();
                config.set("spawn-coords", locationToString(spawnLocation));
                saveConfig();
                this.spawnLocation = spawnLocation;
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Spawn point has been set.");
                return true;
            }
        } else if (label.equalsIgnoreCase("spawn")) {
            if (args.length == 0) {
                if (spawnLocation != null) {
                    player.teleport(spawnLocation);
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "You have been teleported to spawn.");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Spawn point has not been set!");
                }
                return true;
            }
        } else if (label.equalsIgnoreCase("naturecore")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Usage: /naturecore <help|reload>");
                return true;
            }
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    reloadPluginConfig(sender);
                    return true;
                } else if (args[0].equalsIgnoreCase("help")) {
                    displayHelp(sender);
                    return true;
                }
            }
        }
        return false;
    }

    private class PlayerJoinQuitListener implements Listener {

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();

            if (player.isOp()) {
                player.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Thank you for using NatureCore Version: 2.0");
            }

            String welcomeMessage = getConfig().getString("welcome-message").replace("%player%", player.getName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', welcomeMessage));

            if (whitelistEnabled) {
                if (!whitelist.contains(player.getUniqueId())) {
                    player.kickPlayer(ChatColor.WHITE + "You are not whitelisted on this server.");
                    return;
                }
            }

            if (config.getBoolean("spawn-on-join")) {
                if (spawnLocation != null) {
                    player.teleport(spawnLocation);
                }
            }
        }

        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            Player player = event.getPlayer();
            String quitMessage = getConfig().getString("quit-message").replace("%player%", player.getName());
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', quitMessage));
        }

        @EventHandler
        public void onPlayerChat(AsyncPlayerChatEvent event) {
            if (!getConfig().getBoolean("chat-message-enable")) {
                return;
            }

            String playerName = event.getPlayer().getName();
            String message = event.getMessage();

            String chatMessageFormat = getConfig().getString("chat-message");

            // Format the message
            String formattedMessage = chatMessageFormat
                    .replace("%player%", playerName)
                    .replace("%message%", message);

            // Broadcast the formatted message
            event.setCancelled(true);
            getServer().broadcastMessage(formattedMessage);
        }
    }

    private void displayHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "NatureCore Help:");
        sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "/naturecore help - Displays this help message.");
        sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "/naturecore reload - Reloads the plugin configuration.");
    }
    private void reloadPluginConfig(CommandSender sender) {
        reloadConfig();
        config = getConfig();
        sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Configuration reloaded.");
    }
    private Location loadSpawnLocation() {
        String spawnString = config.getString("spawn-coords");
        if (spawnString != null) {
            String[] parts = spawnString.split(",");
            if (parts.length == 6) {
                String worldName = parts[0];
                double x = Double.parseDouble(parts[1]);
                double y = Double.parseDouble(parts[2]);
                double z = Double.parseDouble(parts[3]);
                float yaw = Float.parseFloat(parts[4]);
                float pitch = Float.parseFloat(parts[5]);
                World world = Bukkit.getWorld(worldName);
                if (world != null) {
                    return new Location(world, x, y, z, yaw, pitch);
                }
            }
        }
        return null;
    }
    private String locationToString(Location location) {
        return String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f",
                location.getWorld().getName(),
                location.getX(), location.getY(), location.getZ(),
                location.getYaw(), location.getPitch());
    }
    private void loadWhitelist() {
        whitelist.clear();
        List<String> uuidStrings = config.getStringList("whitelist");
        for (String uuidString : uuidStrings) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                whitelist.add(uuid);
            } catch (IllegalArgumentException e) {
                getLogger().warning("Invalid UUID found in whitelist: " + uuidString);
            }
        }
    }

    private boolean handleWhitelistCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Usage: /whitelist <add|remove|on|off> [player]");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add":
                return handleWhitelistAdd(sender, args);
            case "remove":
                return handleWhitelistRemove(sender, args);
            case "on":
                whitelistEnabled = true;
                config.set("whitelist-enabled", whitelistEnabled);
                saveConfig();
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Whitelist is now enabled.");
                return true;
            case "off":
                whitelistEnabled = false;
                config.set("whitelist-enabled", whitelistEnabled);
                saveConfig();
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Whitelist is now disabled.");
                return true;
            default:
                sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Invalid subcommand! Usage: /whitelist <add|remove|on|off> [player]");
                return false;
        }
    }

    private boolean handleWhitelistAdd(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Usage: /whitelist add [player]");
            return true;
        }

        String playerName = args[1];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer.hasPlayedBefore()) {
            UUID playerUUID = offlinePlayer.getUniqueId();
            whitelist.add(playerUUID);
            config.set("whitelist", whitelist.stream().map(UUID::toString).toList());
            saveConfig();
            sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + playerName + " has been added to the whitelist.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Player not found or has never joined the server.");
        }
        return true;
    }

    private boolean handleWhitelistRemove(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Usage: /whitelist remove [player]");
            return true;
        }

        String playerName = args[1];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        if (offlinePlayer.hasPlayedBefore()) {
            UUID playerUUID = offlinePlayer.getUniqueId();
            whitelist.remove(playerUUID);
            config.set("whitelist", whitelist.stream().map(UUID::toString).toList());
            saveConfig();
            sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + playerName + " has been removed from the whitelist.");
        } else {
            sender.sendMessage(ChatColor.GREEN + "NatureCore" + ChatColor.DARK_GRAY + " | " + ChatColor.WHITE + "Player not found or has never joined the server.");
        }
        return true;
    }
    private void setWeather(World world, boolean clear) {
        if (clear) {
            world.setStorm(false);
            world.setThundering(false);
        } else {
            world.setStorm(true);
            world.setThundering(true);
        }
    }
}
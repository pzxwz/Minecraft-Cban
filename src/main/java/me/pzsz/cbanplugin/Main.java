package me.pzsz.cbanplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin implements CommandExecutor, Listener {
    private final HashMap<UUID, String> bans = new HashMap<>();

    @Override
    public void onEnable() {
        getCommand("cban").setExecutor(this);
        getCommand("unban").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("cban")) {
            return CommandBan(sender, args);
        } else if (command.getName().equalsIgnoreCase("unban")) {
            return CommandUnban(sender, args);
        }
        return false;
    }

    private boolean CommandBan(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cbanplugin.cban")) {
            sender.sendMessage("§cVocê não possui permissão para isso.");
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage("§cUso correto: /cban <jogador> <motivo>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getName() == null) {
            sender.sendMessage("§cJogador não encontrado!");
            return true;
        }

        UUID playerUUID = target.getUniqueId();
        String motivo = "Uso de trapaças";
        bans.put(playerUUID, motivo);

        if (target.isOnline()) {
            ((Player) target).kickPlayer(formatBanMessage(motivo));
        }
        sender.sendMessage("§aJogador " + target.getName() + " foi banido permanentemente por: " + motivo);
        return true;
    }

    private boolean CommandUnban(CommandSender sender, String[] args) {
        if (!sender.hasPermission("cbanplugin.unban")) {
            sender.sendMessage("§cVocê não possui permissão para isso.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage("§cUso correto: /unban <jogador>");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
        if (target.getName() == null) {
            sender.sendMessage("§cJogador não encontrado!");
            return true;
        }

        UUID playerUUID = target.getUniqueId();
        if (bans.remove(playerUUID) != null) {
            sender.sendMessage("§aJogador " + target.getName() + " foi desbanido com sucesso!");
        } else {
            sender.sendMessage("§cEsse jogador não está banido!");
        }
        return true;
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        if (bans.containsKey(playerUUID)) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, formatBanMessage(bans.get(playerUUID)));
        }
    }

    private String formatBanMessage(String motivo) {
        return "§cVocê está banido permanentemente.\n\n" +
                "§cMotivo: " + motivo + "\n" +
                "§cTem direito à comprar unban? Sim.\n\n" +
                "§cPunido injustamente? Acesse: discord.gg/servidor";
    }
}
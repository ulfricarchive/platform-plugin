package com.ulfric.platform.andrew;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.ulfric.andrew.Sender;
import com.ulfric.i18n.content.Details;
import com.ulfric.servix.services.locale.TellService;

import java.util.UUID;

public final class BukkitSender implements Sender {

	public static CommandSender getHandle(Sender sender) {
		if (sender instanceof BukkitSender) {
			return ((BukkitSender) sender).sender;
		}

		return null;
	}

	private final CommandSender sender;

	public BukkitSender(CommandSender sender) {
		this.sender = sender;
	}

	@Override
	public UUID getUniqueId() {
		return sender instanceof Player ? ((Player) sender).getUniqueId() : null;
	}

	@Override
	public boolean hasPermission(String permission) {
		return sender.hasPermission(permission);
	}

	@Override
	public void sendMessage(String message) {
		TellService.sendMessage(sender, message);
	}

	@Override
	public void sendMessage(String message, Details context) {
		TellService.sendMessage(sender, message, context);
	}

	@Override
	public Object handle() {
		return sender;
	}

}
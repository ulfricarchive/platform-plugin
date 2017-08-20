package com.ulfric.platform.andrew;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.ulfric.andrew.argument.Exact;
import com.ulfric.andrew.argument.ResolutionRequest;
import com.ulfric.andrew.argument.Resolver;
import com.ulfric.commons.spigot.player.PlayerHelper;
import com.ulfric.commons.value.UniqueIdHelper;

import java.util.UUID;

public class PlayerResolver extends Resolver<Player> {

	public PlayerResolver() {
		super(Player.class);
	}

	@Override
	public Player apply(ResolutionRequest request) {
		String argument = request.getArgument();
		UUID uniqueId = UniqueIdHelper.parseUniqueId(argument);

		Player player;
		if (uniqueId == null) {
			if (request.getDefinition().getField().isAnnotationPresent(Exact.class)) {
				player = Bukkit.getPlayerExact(argument);
			} else {
				player = Bukkit.getPlayer(argument);
			}
		} else {
			player = Bukkit.getPlayer(uniqueId);
		}

		if (player == null) {
			if (PlayerHelper.isAskingForSelf(argument)) {
				player = self(request);
			}
		} else {
			Player self = self(request);

			if (self != null && !self.canSee(player)) {
				player = null;
			}
		}

		return player;
	}

	private Player self(ResolutionRequest request) {
		Object handle = request.getContext().getSender().handle();

		return handle instanceof Player ? (Player) handle : null;
	}

}

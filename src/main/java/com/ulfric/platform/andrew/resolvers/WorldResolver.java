package com.ulfric.platform.andrew.resolvers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import com.ulfric.andrew.argument.ResolutionRequest;
import com.ulfric.andrew.argument.Resolver;
import com.ulfric.commons.math.NumberHelper;
import com.ulfric.commons.spigot.player.PlayerHelper;
import com.ulfric.commons.value.UniqueIdHelper;

import java.util.List;
import java.util.OptionalInt;
import java.util.UUID;

public class WorldResolver extends Resolver<World> {

	public WorldResolver() {
		super(World.class);
	}

	@Override
	public World apply(ResolutionRequest request) { // TODO cleanup method
		String argument = request.getArgument();

		World world = Bukkit.getWorld(argument);
		if (world != null) {
			return world;
		}

		UUID uniqueId = UniqueIdHelper.parseUniqueId(argument);
		if (uniqueId == null) {
			OptionalInt integer = NumberHelper.parseInt(argument);

			if (integer.isPresent()) {
				int integerValue = integer.getAsInt();
				List<World> worlds = Bukkit.getWorlds();
				if (worlds.size() < integerValue) {
					return null;
				}

				return worlds.get(integerValue);
			}
		} else {
			return Bukkit.getWorld(uniqueId);
		}

		return PlayerHelper.isAskingForSelf(argument) ? self(request) : null;
	}

	private World self(ResolutionRequest request) {
		CommandSender sender = request.getContext().getSender();

		return sender instanceof Entity ? ((Entity) sender).getWorld() : null;
	}

}

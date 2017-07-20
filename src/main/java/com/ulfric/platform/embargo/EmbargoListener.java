package com.ulfric.platform.embargo;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.ulfric.commons.reflect.FieldHelper;
import com.ulfric.data.database.Data;
import com.ulfric.data.database.Database;
import com.ulfric.data.database.Store;
import com.ulfric.dragoon.reflect.Handles;
import com.ulfric.embargo.User;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

public class EmbargoListener implements Listener {

	private static MethodHandle setPermissibleBase;

	@Database // TODO stereotype for common permissions database annotation
	private Store users;

	@EventHandler
	public void on(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
			return;
		}

		preloadPermissionsData(event.getName(), event.getUniqueId());
	}

	@EventHandler
	public void on(PlayerLoginEvent event) throws Throwable {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
			return;
		}

		Player player = event.getPlayer();
		MethodHandle setter = setter(player);
		if (setter == null) {
			return;
		}
		User user = getPermissionsData(player.getName(), player.getUniqueId());
		if (user == null) {
			return;
		}
		BukkitPermissible permissible = new BukkitPermissible(player, user);
		setter.invokeExact((Object) player, (Object) permissible);
	}

	private void preloadPermissionsData(String name, UUID uniqueId) {
		getPermissionsData(name, uniqueId);
	}

	private User getPermissionsData(String name, UUID uniqueId) {
		return PersistentUser.getUser(uniqueId, () -> {
			Data data = users.getData(uniqueId.toString());
			return new PersistentUser(data, name, uniqueId);
		});
	}

	private MethodHandle setter(Player player) {
		if (setPermissibleBase != null) {
			return setPermissibleBase;
		}

		Optional<Field> permField = FieldHelper.getDeclaredField(player.getClass().getSuperclass(), "perm");
		if (!permField.isPresent()) {
			return null;
		}
		Field field = permField.get();
		setPermissibleBase = Handles.setter(field);

		return setPermissibleBase;
	}

}
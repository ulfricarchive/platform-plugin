package com.ulfric.platform.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.platform.Platform;
import com.ulfric.platform.Plugin;

import java.util.Objects;

public class ListenerApplication extends Application {

	private final Listener listener;

	@Inject
	private PluginManager manager;

	public ListenerApplication(Listener listener) {
		Objects.requireNonNull(listener, "listener");

		this.listener = listener;

		addBootHook(this::register);
		addShutdownHook(this::unregister);
	}

	private void register() {
		Plugin plugin = Plugin.getProvidingPlugin(listener.getClass());
		if (plugin == null) {
			plugin = Plugin.getPluginInstance(Platform.class);
		}
		Bukkit.getPluginManager().registerEvents(listener, plugin);
	}

	private void unregister() {
		HandlerList.unregisterAll(listener);
	}

}
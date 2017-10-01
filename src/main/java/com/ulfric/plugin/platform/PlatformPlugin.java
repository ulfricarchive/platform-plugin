package com.ulfric.plugin.platform;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scoreboard.ScoreboardManager;

import com.ulfric.commons.bukkit.plugin.PluginHelper;
import com.ulfric.dragoon.logging.DefaultLoggerBinding;
import com.ulfric.plugin.Plugin;
import com.ulfric.plugin.platform.listener.ListenerFeature;

import java.util.logging.Logger;

public final class PlatformPlugin extends Plugin {

	public PlatformPlugin() {
		setupFactory();

		install(ListenerFeature.class);
	}

	private void setupFactory() {
		bindBukkitManagers();
		bindBukkitPlugin();
		bindLogger();
	}

	private void bindBukkitManagers() {
		FACTORY.bind(PluginManager.class).toSupplier(Bukkit::getPluginManager);
		FACTORY.bind(ScoreboardManager.class).toSupplier(Bukkit::getScoreboardManager);
	}

	private void bindBukkitPlugin() {
		FACTORY.bind(org.bukkit.plugin.Plugin.class).toFunction(parameters ->
			PluginHelper.getProvidingPlugin(parameters.getHolder()));
	}

	private void bindLogger() {
		FACTORY.bind(Logger.class).toFunction(parameters -> {
			try {
				org.bukkit.plugin.Plugin plugin = FACTORY.request(org.bukkit.plugin.Plugin.class, parameters);

				if (plugin != null) {
					return plugin.getLogger();
				}
			} catch (IllegalStateException thatsOk) {
			}

			return DefaultLoggerBinding.INSTANCE.apply(parameters);
		});
	}

}
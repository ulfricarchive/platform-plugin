package com.ulfric.plugin.platform;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import com.ulfric.plugin.Plugin;
import com.ulfric.plugin.platform.listener.ListenerFeature;
import com.ulfric.plugin.platform.logging.LoggerBinding;

import java.nio.file.Path;
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
		bindDataFolder();
	}

	private void bindBukkitManagers() {
		FACTORY.bind(PluginManager.class).toSupplier(Bukkit::getPluginManager);
		FACTORY.bind(ScoreboardManager.class).toSupplier(Bukkit::getScoreboardManager);
	}

	private void bindBukkitPlugin() {
		bindBukkitPlugin(org.bukkit.plugin.Plugin.class);
		bindBukkitPlugin(JavaPlugin.class);
		bindBukkitPlugin(Plugin.class);
	}

	private void bindBukkitPlugin(Class<? extends org.bukkit.plugin.Plugin> type) {
		FACTORY.bind(type).toFunction(parameters -> Plugin.getProvidingPlugin(parameters.getHolder()));
	}

	private void bindLogger() {
		LoggerBinding binding = FACTORY.request(LoggerBinding.class);
		FACTORY.bind(Logger.class).toFunction(binding);
	}

	private void bindDataFolder() {
		FACTORY.bind(Path.class).toFunction(parameters -> Plugin.getProvidingPlugin(parameters.getHolder()).getDataFolder());
	}

}
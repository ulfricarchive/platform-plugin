package com.ulfric.platform;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.ulfric.andrew.Registry;
import com.ulfric.data.config.SettingsExtension;
import com.ulfric.data.database.Database;
import com.ulfric.data.database.DatabaseExtension;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Feature;
import com.ulfric.platform.andrew.CommandFeature;
import com.ulfric.platform.andrew.CommandRegistry;
import com.ulfric.platform.embargo.EmbargoContainer;
import com.ulfric.platform.listener.ListenerFeature;

import java.util.Arrays;
import java.util.List;

public final class Platform extends Plugin {

	private static Plugin getProvidingPlugin(Object[] parameters) {
		for (Object parameter : parameters) {
			Plugin plugin = getProvidingPlugin(parameter.getClass());
			if (plugin != null) {
				return plugin;
			}
		}
		throw new IllegalStateException("Plugin could not be injected from: " + Arrays.toString(parameters));
	}

	private Feature command;
	private Feature listener;

	public Platform() {
		ObjectFactory factory = Plugin.FACTORY;
		factory.bind(Plugin.class).toFunction(Platform::getProvidingPlugin);
		factory.bind(org.bukkit.plugin.Plugin.class).toFunction(Platform::getProvidingPlugin);
		factory.bind(JavaPlugin.class).toFunction(Platform::getProvidingPlugin);
		factory.bind(Registry.class).toValue(factory.request(CommandRegistry.class));
		factory.bind(PluginManager.class).toFunction(ignore -> Bukkit.getPluginManager());
		factory.install(SettingsExtension.class);
		factory.install(DatabaseExtension.class);

		addBootHook(this::registerFeatures);
		addShutdownHook(this::unregisterFeatures);
		addShutdownHook(this::saveDatabases);

		install(EmbargoContainer.class);
	}

	private void registerFeatures() {
		if (command == null) {
			command = Plugin.FACTORY.request(CommandFeature.class);
		}
		if (listener == null) {
			listener = Plugin.FACTORY.request(ListenerFeature.class);
		}
		Feature.register(command);
		Feature.register(listener);
	}

	private void unregisterFeatures() {
		Feature.disable(command);
		Feature.disable(listener);
	}

	private void saveDatabases() {
		List<Database> databases = Database.getDatabases();
		log("Shutting down " + databases.size() + " databases");
		long start = System.currentTimeMillis();
		databases.forEach(Database::save);
		long end = System.currentTimeMillis();
		log("Databases shut down. Took " + (end - start) + "ms");
	}

}
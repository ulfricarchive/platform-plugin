package com.ulfric.platform;

import org.bukkit.plugin.java.JavaPlugin;

import com.ulfric.andrew.Registry;
import com.ulfric.data.config.SettingsExtension;
import com.ulfric.data.database.Database;
import com.ulfric.data.database.DatabaseExtension;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Feature;
import com.ulfric.platform.andrew.CommandFeature;
import com.ulfric.platform.andrew.CommandRegistry;

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

	public Platform() {
		ObjectFactory factory = Plugin.FACTORY;
		factory.bind(Plugin.class).toFunction(Platform::getProvidingPlugin);
		factory.bind(org.bukkit.plugin.Plugin.class).toFunction(Platform::getProvidingPlugin);
		factory.bind(JavaPlugin.class).toFunction(Platform::getProvidingPlugin);
		factory.bind(Registry.class).toValue(factory.request(CommandRegistry.class));
		factory.install(SettingsExtension.class);
		factory.install(DatabaseExtension.class);

		Feature.register(factory.request(CommandFeature.class));

		addShutdownHook(this::saveDatabases);
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
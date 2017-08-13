package com.ulfric.platform;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.ulfric.andrew.Registry;
import com.ulfric.data.config.SettingsExtension;
import com.ulfric.data.database.DatabaseExtension;
import com.ulfric.data.database.Store;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Feature;
import com.ulfric.dragoon.logging.DefaultLoggerBinding;
import com.ulfric.etruscans.locale.LocaleContainer;
import com.ulfric.etruscans.placeholder.PlaceholderFeature;
import com.ulfric.palpatine.Scheduler;
import com.ulfric.platform.andrew.CommandFeature;
import com.ulfric.platform.andrew.CommandRegistry;
import com.ulfric.platform.andrew.PlayerResolver;
import com.ulfric.platform.andrew.ResolverFeature;
import com.ulfric.platform.listener.ListenerFeature;
import com.ulfric.plugin.FeatureFeature;
import com.ulfric.plugin.Plugin;
import com.ulfric.servix.ServiceFeature;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

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
		factory.bind(PluginManager.class).toFunction(ignore -> Bukkit.getPluginManager());
		factory.bind(Scheduler.class).toFunction(parameters -> new Scheduler(getProvidingPlugin(parameters)));
		factory.bind(Logger.class).toFunction(parameters -> {
			try {
				return getProvidingPlugin(parameters).getLogger();
			} catch (IllegalStateException thatsOk) {
				return DefaultLoggerBinding.INSTANCE.apply(parameters);
			}
		});

		factory.install(SettingsExtension.class);
		factory.install(DatabaseExtension.class);

		install(LocaleContainer.class);
		install(PlayerResolver.class);

		Feature.register(new FeatureFeature()); // TODO unregister
		install(CommandFeature.class);
		install(ListenerFeature.class);
		install(ServiceFeature.class);
		install(ResolverFeature.class);
		install(PlaceholderFeature.class);

		addShutdownHook(this::saveDatabases);
	}

	private void saveDatabases() {
		List<Store> databases = Store.getDatabases();
		if (databases.isEmpty()) {
			return;
		}

		log("Shutting down " + databases.size() + " databases");
		long start = System.currentTimeMillis();
		databases.forEach(Store::save);
		long end = System.currentTimeMillis();
		log("Databases shut down. Took " + (end - start) + "ms");
	}

}
package com.ulfric.platform.service;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;

import java.util.ServiceLoader;

public class Services { // TODO thread safety

	public static <S> S get(Class<S> service) { // TODO cleanup method
		ServicesManager manager = Bukkit.getServicesManager();
		S provider = Bukkit.getServicesManager().load(service);

		if (provider == null) {
			for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
				if (!plugin.isEnabled()) {
					continue;
				}

				ServiceLoader.load(service, plugin.getClass().getClassLoader())
					.forEach(implementation -> register(service, implementation, plugin));
			}

			provider = manager.load(service);

			if (provider == null) {
				return null;
			}
		}

		return provider;
	}

	static void load(Plugin plugin) { // TODO cleanup method
		ClassLoader loader = plugin.getClass().getClassLoader();
		for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
			ServiceLoader.load(service, loader).forEach(implementation -> register(service, implementation, plugin));
		}
	}

	private static <T> void register(Class<?> service, Object implementation, Plugin plugin) {
		@SuppressWarnings("unchecked")
		Class<T> useService = (Class<T>) service;
		@SuppressWarnings("unchecked")
		T useImplementation = (T) implementation;

		Priority priority = implementation.getClass().getAnnotation(Priority.class);
		ServicePriority usePriority = priority == null ? ServicePriority.Normal : priority.value();

		Bukkit.getServicesManager().register(useService, useImplementation, plugin, usePriority);
	}

	private Services() {
	}

}
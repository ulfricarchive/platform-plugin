package com.ulfric.platform;

import org.bukkit.plugin.java.JavaPlugin;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Container;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.value.Result;

import java.util.Arrays;

public abstract class Plugin extends JavaPlugin implements Extensible<Class<? extends Application>> {

	private static final ObjectFactory FACTORY = new ObjectFactory();

	static {
		FACTORY.bind(Plugin.class).toFunction(Plugin::getProvidingPlugin);
		FACTORY.bind(org.bukkit.plugin.Plugin.class).toFunction(Plugin::getProvidingPlugin);
		FACTORY.bind(JavaPlugin.class).toFunction(Plugin::getProvidingPlugin);
	}

	private static Plugin getProvidingPlugin(Object[] parameters) {
		for (Object parameter : parameters) {
			JavaPlugin plugin = JavaPlugin.getProvidingPlugin(parameter.getClass());
			if (plugin instanceof Plugin) {
				return (Plugin) plugin;
			}
		}
		throw new IllegalStateException("Plugin could not be injected from: " + Arrays.toString(parameters));
	}

	public static Plugin getProvidingPlugin(Class<?> type) {
		JavaPlugin plugin = JavaPlugin.getProvidingPlugin(type);
		return plugin instanceof Plugin ? (Plugin) plugin : null;
	}

	public static <T extends Plugin> T getPluginInstance(Class<T> type) {
		JavaPlugin plugin = JavaPlugin.getPlugin(type);
		return type.isInstance(plugin) ? type.cast(plugin) : null;
	}

	private final Container container;

	public Plugin() {
		container = FACTORY.request(PluginContainer.class, this);
	}

	@Override
	public final Result install(Class<? extends Application> application) {
		return container.install(application);
	}

	@Override
	public final void onLoad() {
	}

	@Override
	public final void onEnable() {
		container.boot();
	}

	@Override
	public final void onDisable() {
		container.shutdown();
	}

}
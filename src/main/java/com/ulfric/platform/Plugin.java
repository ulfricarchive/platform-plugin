package com.ulfric.platform;

import org.bukkit.plugin.java.JavaPlugin;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Container;
import com.ulfric.dragoon.application.Hookable;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.value.Result;

public abstract class Plugin extends JavaPlugin implements Extensible<Class<? extends Application>>, Hookable {

	static final ObjectFactory FACTORY = new ObjectFactory();

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
	public final void addBootHook(Runnable hook) {
		container.addBootHook(hook);
	}

	@Override
	public final void addShutdownHook(Runnable hook) {
		container.addShutdownHook(hook);
	}

	public final void log(String message) {
		getLogger().info(message);
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
package com.ulfric.platform;

import org.bukkit.plugin.java.JavaPlugin;

import com.ulfric.commons.reflect.FieldHelper;
import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Container;
import com.ulfric.dragoon.application.Hookable;
import com.ulfric.dragoon.extension.Extensible;
import com.ulfric.dragoon.value.Result;
import com.ulfric.tryto.Try;

import java.lang.reflect.Field;

public abstract class Plugin extends JavaPlugin implements Extensible<Class<? extends Application>>, Hookable {

	static final Class<?> LOADER_TYPE = Plugin.class.getClassLoader().getClass();
	static final Field PLUGIN_FIELD = FieldHelper.getDeclaredField(LOADER_TYPE, "plugin")
			.orElse(null);

	static final ObjectFactory FACTORY = new ObjectFactory();

	static {
		if (PLUGIN_FIELD != null) {
			PLUGIN_FIELD.setAccessible(true);
		}
	}

	public static Plugin getProvidingPlugin(Class<?> type) {
		return getProvidedPlugin(type.getClassLoader());
	}

	private static Plugin getProvidedPlugin(ClassLoader loader) {
		if (loader == null) {
			return null;
		}

		if (PLUGIN_FIELD == null) {
			return null;
		}

		if (LOADER_TYPE.isInstance(loader)) {
			Object plugin = Try.toGet(() -> PLUGIN_FIELD.get(loader));
			if (plugin instanceof Plugin) {
				return (Plugin) plugin;
			}
			return null;
		}

		return getProvidedPlugin(loader.getParent());
	}

	public static <T extends Plugin> T getPluginInstance(Class<T> type) {
		JavaPlugin plugin = JavaPlugin.getPlugin(type);
		return type.isInstance(plugin) ? type.cast(plugin) : null;
	}

	private final Container container = FACTORY.request(PluginContainer.class, this);

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
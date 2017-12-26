package com.ulfric.plugin.platform.logging;

import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.Parameters;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.logging.DefaultLoggerBinding;
import com.ulfric.dragoon.logging.Log;

public class LoggerBinding implements Function<Parameters, Logger> {

	@Inject
	private Log formerLogger;

	@Inject
	private ObjectFactory factory;

	@Override
	public Logger apply(Parameters parameters) {
		try {
			org.bukkit.plugin.Plugin plugin = factory.request(org.bukkit.plugin.Plugin.class, parameters);

			if (plugin != null) {
				return plugin.getLogger();
			}
		} catch (IllegalArgumentException thatsOk) {
			formerLogger.log(Level.SEVERE, "Falling back to default logger binding", thatsOk);
		}

		return DefaultLoggerBinding.INSTANCE.apply(parameters);
	}

}

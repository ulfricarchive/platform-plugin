package com.ulfric.platform.listener;

import org.bukkit.event.Listener;

import com.ulfric.dragoon.ObjectFactory;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.application.Feature;
import com.ulfric.dragoon.extension.inject.Inject;

public class ListenerFeature extends Feature {

	@Inject
	private ObjectFactory factory;

	@Override
	public Application apply(Object object) {
		if (object instanceof Listener) {
			return factory.request(ListenerApplication.class, object);
		}
		return null;
	}

}

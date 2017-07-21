package com.ulfric.platform.service;

import org.apache.commons.collections4.CollectionUtils;

import com.ulfric.commons.service.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Services { // TODO thread safety

	private static final Map<Class<? extends Service>, List<Service>> SERVICES = new HashMap<>();

	public static <S extends Service> S get(Class<S> service) { // TODO cleanup method
		List<Service> services = SERVICES.get(service);
		return CollectionUtils.isEmpty(services) ? null : service.cast(services.get(0));
	}

	public static void register(Service service) {
		Objects.requireNonNull(service, "service");

		SERVICES.computeIfAbsent(service.getService(), ignore -> new ArrayList<>()) // TODO validate service type
		.add(service); // TODO contains to prevent duplicates
	}

	public static void unregister(Service service) {
		Objects.requireNonNull(service, "service");

		SERVICES.values().forEach(services -> services.remove(service));
	}

	private Services() {
	}

}
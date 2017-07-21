package com.ulfric.platform.service;

import com.ulfric.commons.service.Service;
import com.ulfric.dragoon.application.Application;

import java.util.Objects;

public class ServiceApplication extends Application {

	private final Service service;

	public ServiceApplication(Service service) {
		Objects.requireNonNull(service, "service");

		this.service = service;

		addBootHook(this::register);
		addShutdownHook(this::unregister);
	}

	private void register() {
		Services.register(service);
	}

	private void unregister() {
		Services.unregister(service);
	}

}
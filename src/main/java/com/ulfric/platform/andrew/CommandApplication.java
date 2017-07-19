package com.ulfric.platform.andrew;

import com.ulfric.andrew.Command;
import com.ulfric.andrew.Invoker;
import com.ulfric.andrew.Registry;
import com.ulfric.dragoon.application.Application;
import com.ulfric.dragoon.extension.inject.Inject;
import com.ulfric.dragoon.reflect.Classes;

public class CommandApplication extends Application {

	private final Command command;

	@Inject
	private Registry registry;

	public CommandApplication(Command command) {
		@SuppressWarnings("unchecked")
		Class<? extends Command> commandType = (Class<? extends Command>) Classes.getNonDynamic(command.getClass());
		this.command = Invoker.of(commandType);

		addBootHook(this::register);
		addShutdownHook(this::unregister);
	}

	private void register() {
		registry.register(command);
	}

	private void unregister() {
		registry.unregister(command);
	}

}
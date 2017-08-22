package com.ulfric.platform.andrew;

import org.bukkit.command.CommandSender;

import com.ulfric.andrew.Command;
import com.ulfric.andrew.Context;
import com.ulfric.andrew.Invoker;
import com.ulfric.andrew.MissingPermissionException;
import com.ulfric.andrew.argument.MissingArgumentException;
import com.ulfric.commons.collection.MapHelper;
import com.ulfric.commons.spigot.command.CommandSenderHelper;
import com.ulfric.i18n.content.Details;
import com.ulfric.servix.services.locale.TellService;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

final class Dispatcher extends org.bukkit.command.Command {

	private static final Executor EXECUTOR = Executors.newFixedThreadPool(3);
	private static final ConcurrentMap<UUID, Context> CURRENTLY_EXECUTING = MapHelper.newConcurrentMap(4);

	private final CommandRegistry registry;
	final Invoker command;

	Dispatcher(CommandRegistry registry, Invoker command) {
		super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
		this.registry = registry;
		this.command = command;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] arguments) {
		UUID uniqueId = CommandSenderHelper.getUniqueId(sender);
		if (uniqueId != null) {
			Context existingExecution = CURRENTLY_EXECUTING.get(uniqueId);
			if (existingExecution != null) {
				TellService.sendMessage(sender, "command-already-running"); // TODO qualify message with context label
				return true; // TODO permission bypass
			}
		}

		Context context = new Context();
		Map<Class<? extends Command>, List<String>> contextArgumens = new IdentityHashMap<>();
		List<String> enteredArguments = Arrays.stream(arguments).collect(Collectors.toList()); // TODO handle "quoted arguments"
		contextArgumens.put(Command.class, enteredArguments);
		context.setArguments(contextArgumens);
		context.setSender(sender);
		context.setLabel(label);

		if (command.shouldRunOnMainThread()) {
			this.run(context);
		} else {
			this.runAsync(context); // TODO timeouts, handle InterruptedException
		}

		return true;
	}

	private void runAsync(Context context) {
		EXECUTOR.execute(() -> this.run(context));
	}

	private void run(Context context) {
		try {
			registry.dispatch(context);
		} catch (MissingPermissionException permissionCheck) {
			TellService.sendMessage(context.getSender(), "command-no-permission",
					Details.of("node", permissionCheck.getMessage()));
		} catch (MissingArgumentException requiredArgument) {
			TellService.sendMessage(context.getSender(), "command-missing-argument",
					Details.of("argument", requiredArgument.getMessage()));
		} catch (Exception exception) {
			// TODO auto report this to admins
			exception.printStackTrace(); // TODO improve logging
			TellService.sendMessage(context.getSender(), "command-failed-execution");
		}
	}

}

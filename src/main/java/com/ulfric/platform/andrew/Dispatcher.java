package com.ulfric.platform.andrew;

import org.bukkit.command.CommandSender;

import com.ulfric.andrew.Command;
import com.ulfric.andrew.CommandException;
import com.ulfric.andrew.Context;
import com.ulfric.andrew.Invoker;
import com.ulfric.andrew.Sender;

import java.util.Arrays;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

final class Dispatcher extends org.bukkit.command.Command {

	private static final Executor EXECUTOR = Executors.newFixedThreadPool(2);
	private static final ConcurrentMap<UUID, Context> CURRENTLY_EXECUTING = new ConcurrentHashMap<>();

	final Invoker command;

	Dispatcher(Invoker command) {
		super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
		this.command = command;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] arguments) {
		Sender andrewSender = new BukkitSender(sender);
		UUID uniqueId = andrewSender.getUniqueId();
		if (uniqueId != null) {
			Context existingExecution = CURRENTLY_EXECUTING.get(uniqueId);
			if (existingExecution != null) {
				andrewSender.sendMessage("command-already-running"); // TODO locale, qualify message with context label
				return true; // TODO permission bypass
			}
		}

		Context context = new Context();
		context.setLabel(label);
		Map<Class<? extends Command>, List<String>> contextArgumens = new IdentityHashMap<>();
		List<String> enteredArguments = Arrays.stream(arguments).collect(Collectors.toList()); // TODO handle "quoted arguments"
		contextArgumens.put(Command.class, enteredArguments);
		context.setArguments(contextArgumens);
		context.setSender(new BukkitSender(sender));

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
			command.run(context);
		} catch (CommandException thatsOk) {
			// TODO handle
		} catch (Exception exception) {
			// TODO auto report this to admins
			exception.printStackTrace(); // TODO improve logging
			context.getSender().sendMessage("command-failed-execution"); // TODO locale
		}
	}

}

package com.ulfric.platform.andrew;

import org.bukkit.command.CommandSender;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import com.ulfric.andrew.Arguments;
import com.ulfric.andrew.Context;
import com.ulfric.andrew.Invoker;
import com.ulfric.andrew.Labels;
import com.ulfric.andrew.MissingPermissionException;
import com.ulfric.andrew.argument.MissingArgumentException;
import com.ulfric.commons.collection.MapHelper;
import com.ulfric.commons.spigot.command.CommandSenderHelper;
import com.ulfric.commons.time.TemporalHelper;
import com.ulfric.i18n.content.Details;
import com.ulfric.servix.services.locale.TellService;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

final class Dispatcher extends org.bukkit.command.Command {

	private static final Executor EXECUTOR =
			Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("command-%d").build()); // TODO Fibers with Quasar
	private static final ConcurrentMap<UUID, Context> CURRENTLY_EXECUTING = MapHelper.newConcurrentMap(3);

	private final CommandRegistry registry;
	final Invoker command;

	Dispatcher(CommandRegistry registry, Invoker command) {
		super(command.getName(), command.getDescription(), command.getUsage(), command.getAliases());
		this.registry = registry;
		this.command = command;
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] arguments) {
		if (!command.shouldBypassRunningCommand()) {
			Context existingExecution = getRunningCommand(sender);
			if (existingExecution != null) {
				TellService.sendMessage(sender, "command-already-running", Details.of("running", existingExecution));
				return true;
			}
		}

		Context context = createContext(sender, label, arguments);

		if (command.shouldRunOnMainThread()) {
			this.run(context);
		} else {
			this.runAsync(context);
		}

		return true;
	}

	private Context getRunningCommand(CommandSender sender) {
		UUID uniqueId = CommandSenderHelper.getUniqueId(sender);
		if (uniqueId != null) {
			return CURRENTLY_EXECUTING.get(uniqueId);
		}
		return null;
	}

	private Context createContext(CommandSender sender, String label, String[] arguments) {
		Context context = new Context();
		context.setCreation(TemporalHelper.instantNow());
		context.setSender(sender);
		addArguments(context, arguments);
		addLabel(context, label);
		return context;
	}

	private void addArguments(Context context, String[] enteredArguments) {
		Arguments arguments = new Arguments();
		arguments.setAllArguments(Arrays.stream(enteredArguments).collect(Collectors.toList())); // TODO handle "quoted arguments"
		context.setArguments(arguments);
	}

	private void addLabel(Context context, String label) {
		Labels labels = new Labels();
		labels.setRoot(label);
		context.setLabels(labels);
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
			TellService.sendMessage(context.getSender(), "command-failed-execution");
			throw new CommandExecutionException(exception);
		}
	}

}

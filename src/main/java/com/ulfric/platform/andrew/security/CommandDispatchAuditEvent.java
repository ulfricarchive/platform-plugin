package com.ulfric.platform.andrew.security;

import com.google.gson.JsonObject;

import com.ulfric.andrew.Context;
import com.ulfric.servix.services.security.AuditEvent;

public class CommandDispatchAuditEvent extends AuditEvent {

	private final Context context;

	public CommandDispatchAuditEvent(Context context) {
		super(context.getSender());

		this.context = context;
	}

	@Override
	public JsonObject toJson() {
		JsonObject json = super.toJson();

		json.addProperty("command", context.getCommandLine());

		return json;
	}

}
package net.whydah.sso.commands.extensions.statistics;

import java.net.URI;
import java.time.Instant;

public class CommandGetUserLogonStats extends CommandGetActivityStats{

	public CommandGetUserLogonStats(URI statisticsServiceUri, String userId, Instant startTime, Instant endTime) {
		super(statisticsServiceUri, "whydah", "userlogon", userId, startTime, endTime);
	}

}

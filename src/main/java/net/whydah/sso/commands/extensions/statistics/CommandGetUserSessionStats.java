package net.whydah.sso.commands.extensions.statistics;

import java.net.URI;
import java.time.Instant;

public class CommandGetUserSessionStats extends CommandGetActivityStats {

	public CommandGetUserSessionStats(URI statisticsServiceUri, String userId,
			Instant startTime, Instant endTime) {
		super(statisticsServiceUri, "whydah", "usersession", userId, startTime, endTime);
	}

}

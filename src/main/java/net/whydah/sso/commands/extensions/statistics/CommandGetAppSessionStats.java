package net.whydah.sso.commands.extensions.statistics;

import java.net.URI;
import java.time.Instant;

public class CommandGetAppSessionStats extends CommandGetActivityStats{

	public CommandGetAppSessionStats(URI statisticsServiceUri, String appId, Instant startTime, Instant endTime) {
		super(statisticsServiceUri, "whydah", "appsession", appId, startTime, endTime);
	}

}

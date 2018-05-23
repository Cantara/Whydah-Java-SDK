package net.whydah.sso.commands.extensions.statistics;

import java.net.URI;
import java.time.Instant;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

class CommandGetActivityStats extends BaseHttpGetHystrixCommand<String> {
	
	private final String prefix;
	private Instant startTime = null;
	private Instant endTime = null;
	private final String activityName;
	private final String userId;
	public static int DEFAULT_TIMEOUT = 6000;
	
	public CommandGetActivityStats(URI statisticsServiceUri, String prefix, String activityName, String userId, Instant startTime, Instant endTime) {
		super(statisticsServiceUri, null, null, "StatisticsExtensionGroup", DEFAULT_TIMEOUT);
		this.startTime = startTime;
		this.endTime = endTime;
		this.prefix = prefix;
		this.activityName = activityName;
		this.userId = userId;
		if (statisticsServiceUri == null) {
			log.error("CommandGetUsersStats initialized with null-values - will fail");
		}
	}
	
	public CommandGetActivityStats(URI statisticsServiceUri, String prefix, String activityName, String userId, Instant startTime, Instant endTime, int timeout) {
		super(statisticsServiceUri, null, null, "StatisticsExtensionGroup", timeout);
		this.startTime = startTime;
		this.endTime = endTime;
		this.prefix = prefix;
		this.activityName = activityName;
		this.userId = userId;
		if (statisticsServiceUri == null) {
			log.error("CommandGetUsersStats initialized with null-values - will fail");
		}
	}

	@Override
	protected Object[] getQueryParameters() {
		String[] arr1 = new String[0];
		String[] arr2 = new String[0];
		if (endTime != null) {
			arr1 = new String[]{"endTime", String.valueOf(endTime.toEpochMilli())};
		} 
		if (startTime != null) {
			arr2 = new String[]{"startTime", String.valueOf(startTime.toEpochMilli())};
		}
		return join(arr2, arr1);

	}

	String[] join(String[]... arrays) {
	
		int size = 0;
		for (String[] array : arrays) {
			size += array.length;
		}

		java.util.List<String> list = new java.util.ArrayList<String>(size);

		for (String[] array : arrays) {
			list.addAll(java.util.Arrays.asList(array));
		}
		
		return list.toArray(new String[size]);
	}

	@Override
	protected String getTargetPath() {
		return "observe/statistics/" + prefix + "/" + activityName + ((userId!=null && userId.trim().length()>0)? "/" + userId : "") ;
	}


}

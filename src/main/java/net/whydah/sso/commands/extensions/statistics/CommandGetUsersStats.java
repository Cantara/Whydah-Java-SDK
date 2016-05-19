package net.whydah.sso.commands.extensions.statistics;

import net.whydah.sso.commands.baseclasses.BaseHttpGetHystrixCommand;

import java.net.URI;
import java.time.Instant;

public class CommandGetUsersStats extends BaseHttpGetHystrixCommand<String> {
	
	private final String prefix;

	private String adminUserTokenId;
	private Instant startTime = null;
	private Instant endTime = null;


	public CommandGetUsersStats(URI statisticsServiceUri, String myAppTokenId, String adminUserTokenId, Instant startTime, Instant endTime) {
		super(statisticsServiceUri, "", myAppTokenId, "StatisticsExtensionGroup", 3000);


		this.adminUserTokenId = adminUserTokenId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.prefix = "All";
		//        if (statisticsServiceUri == null || myAppTokenId == null || adminUserTokenId == null || userid == null) {
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
		return "observe/statistics/" + prefix + "/userlogon";
	}


}

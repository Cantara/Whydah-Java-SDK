package net.whydah.sso.commands.extras;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import net.minidev.json.JSONArray;
import net.whydah.sso.basehelpers.JsonPathHelper;
import net.whydah.sso.commands.baseclasses.BaseHttpPostHystrixCommand;

public class CommandReceiveASmsDotComResponseParser extends BaseHttpPostHystrixCommand<String> {

	private String searchText="";
	public static int DEFAULT_TIMEOUT = 6000;
	
	public CommandReceiveASmsDotComResponseParser(String searchText) {
		super(URI.create("https://receive-a-sms.com/"), null, null, "SMSReceiver", DEFAULT_TIMEOUT);
		this.searchText = searchText;
	}
	
	public CommandReceiveASmsDotComResponseParser(String searchText, int timeout) {
		super(URI.create("https://receive-a-sms.com/"), null, null, "SMSReceiver", timeout);
		this.searchText = searchText;
	}


	@Override
	protected String dealWithFailedResponse(String responseBody, int statusCode) {
		return null;
	}

	@Override
	protected String dealWithResponse(String response) {
		JSONArray data = JsonPathHelper.getJsonArrayFromJsonpathExpression(response, "$.data");
		for(Object obj : data.toArray()){
			JSONArray arr = (JSONArray) obj;
			String from = arr.get(1).toString();
			String msg = arr.get(2).toString();
			if(from.equals("INN")&&msg.length()==4){
				return msg;
			}
		}
		return null;
	}

	@Override
	protected Map<String, String> getFormParameters() {
		Map<String, String> data = new HashMap<String, String>();
		data.put("draw", "1");
		data.put("columns[0][data]","0");
		data.put("columns[0][name]","");
		data.put("columns[0][searchable]", "true");
		data.put("columns[0][orderable]", "true");
		data.put("columns[0][search][value]","");
		data.put("columns[0][search][regex]","false");
		data.put("columns[1][data]","1");
		data.put("columns[1][name]","");
		data.put("columns[1][searchable]","true");
		data.put("columns[1][orderable]","true");
		data.put("columns[1][search][value]","");
		data.put("columns[1][search][regex]", "false");
		data.put("columns[2][data]","2");
		data.put("columns[2][name]","");
		data.put("columns[2][searchable]","true");
		data.put("columns[2][orderable]","true");
		data.put("columns[2][search][value]","");
		data.put("columns[2][search][regex]","false");
		data.put("order[0][column]","1");
		data.put("order[0][dir]","desc");
		data.put("start","0");
		data.put("length","25");
		data.put("search[value]", searchText);
		data.put("search[regex]", "false");
		return data;
	}


	@Override
	protected String getTargetPath() {
		return "scripts/employee-grid-dataNORWAY1.php";
	}
}
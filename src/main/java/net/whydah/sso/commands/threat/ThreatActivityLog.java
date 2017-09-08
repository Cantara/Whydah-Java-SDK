package net.whydah.sso.commands.threat;

import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "endpoint",
        "ip-address",
        "request-time",
        "user-token-id",
        "app-token-id",
        "username"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreatActivityLog implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty("id")
	private String id="";
	
	@JsonProperty("endpoint")
	private String endPoint="";
	
	@JsonProperty("ip-address")
	private String ipAddress="";
	
	@JsonProperty("request-time")
	private String requestTime="";
	
	@JsonProperty("user-token-id")
	private String userTokenId=""; //if any
	
	@JsonProperty("app-token-id")
	private String appTokenId=""; //if any
	
	@JsonProperty("username")
	private String userName=""; //if any
	
	public ThreatActivityLog(){
		id = UUID.randomUUID().toString();
	}
	
	public ThreatActivityLog(String endPoint, String ipAddress, String requestTime, String userTokenId, String appTokenId, String userName){
		this(UUID.randomUUID().toString(), endPoint, ipAddress, requestTime, userTokenId, appTokenId, userName);
	}
	
	public ThreatActivityLog(String id, String endPoint, String ipAddress, String requestTime, String userTokenId, String appTokenId, String userName){
		this.id = id;
		this.endPoint = endPoint;
		this.ipAddress = ipAddress;
		this.requestTime = requestTime;
		this.userTokenId = userTokenId;
		this.appTokenId = appTokenId;
		this.userName = userName;
	}
	
	@JsonProperty("id")
	public String getId() {
		return id;
	}
	
	@JsonProperty("id")
	public ThreatActivityLog setId(String id) {
		this.id = id;
		return this;
	}
	
	@JsonProperty("endpoint")
	public String getEndPoint() {
		return endPoint;
	}
	
	@JsonProperty("endpoint")
	public ThreatActivityLog setEndPoint(String endPoint) {
		this.endPoint = endPoint;
		return this;
	}
	
	@JsonProperty("ip-address")
	public String getIpAddress() {
		return ipAddress;
	}
	
	@JsonProperty("ip-address")
	public ThreatActivityLog setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
		return this;
	}
	
	@JsonProperty("request-time")
	public String getRequestTime() {
		return requestTime;
	}
	
	@JsonProperty("request-time")
	public ThreatActivityLog setRequestTime(String requestTime) {
		this.requestTime = requestTime;
		return this;
	}
	
	@JsonProperty("user-token-id")
	public String getUserTokenId() {
		return userTokenId;
	}
	
	@JsonProperty("user-token-id")
	public ThreatActivityLog setUserTokenId(String userTokenId) {
		this.userTokenId = userTokenId;
		return this;
	}
	
	@JsonProperty("app-token-id")
	public String getAppTokenId() {
		return appTokenId;
	}
	
	@JsonProperty("app-token-id")
	public ThreatActivityLog setAppTokenId(String appTokenId) {
		this.appTokenId = appTokenId;
		return this;
	}
	
	@JsonProperty("username")
	public String getUserName() {
		return userName;
	}
	
	@JsonProperty("username")
	public ThreatActivityLog setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	
}

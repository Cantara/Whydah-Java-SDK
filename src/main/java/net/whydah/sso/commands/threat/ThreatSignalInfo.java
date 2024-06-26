package net.whydah.sso.commands.threat;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "def-code",
        "suspect",
        "appId",
        "detail",
        "activity-log"
        
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreatSignalInfo implements Serializable  {
	
	@JsonProperty("def-code")
	private
	int threatDefinitionCode;
	
	@JsonProperty("suspect") //if any
	private
	String suspect="";
	
	@JsonProperty("detail") //if any
	private
	String suspiciousDetail="";
	
	@JsonProperty("appId") //if any
	private
	String appId="";
	
	@JsonProperty("activity-log") //if any
	private
	List<ThreatActivityLog> activityLogList=new ArrayList<ThreatActivityLog>();
	
	public ThreatSignalInfo(){}
	public ThreatSignalInfo(int code, String responsibleIpAddress, String suspiciousDetail, List<ThreatActivityLog> activityLogList){
		this.setThreatDefinitionCode(code);
		this.setSuspiciousDetail(suspiciousDetail);
		this.setSuspect(responsibleIpAddress);
		this.activityLogList = activityLogList;
	}

	private static final ObjectMapper deserializeMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public static ThreatSignalInfo fromJson(String json) {
        if (json == null) {
            return null;
        }
        try {
            return deserializeMapper.readValue(json, ThreatSignalInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

	private static final ObjectMapper serializeMapper = new ObjectMapper();

    public static String toJson(ThreatSignalInfo threatSignalInfo) {
        if (threatSignalInfo == null) {
            return "{}";
        }
        try {
            return serializeMapper.writeValueAsString(threatSignalInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
	/**
	 * @return the threatDefinitionCode
	 */
    @JsonProperty("def-code")
	public int getThreatDefinitionCode() {
		return threatDefinitionCode;
	}
	/**
	 * @param threatDefinitionCode the threatDefinitionCode to set
	 */
    @JsonProperty("def-code")
	public void setThreatDefinitionCode(int threatDefinitionCode) {
		this.threatDefinitionCode = threatDefinitionCode;
	}
	/**
	 * @return the responsibleIdentities
	 */
    @JsonProperty("suspect") 
	public String getSuspect() {
		return suspect;
	}
	/**
	 * @param suspect the responsibleIdentities to set
	 */
    @JsonProperty("suspect") 
	public void setSuspect(String suspect) {
		this.suspect = suspect;
	}
	/**
	 * @return the activityLogList
	 */
    @JsonProperty("activity-log")
	public List<ThreatActivityLog> getActivityLogList() {
		return activityLogList;
	}
	/**
	 * @param activityLogList the activityLogList to set
	 */
	@JsonProperty("activity-log")
	public void setActivityLogList(List<ThreatActivityLog> activityLogList) {
		this.activityLogList = activityLogList;
	}
	/**
	 * @return the suspiciousDetail
	 */
	@JsonProperty("detail")
	public String getSuspiciousDetail() {
		return suspiciousDetail;
	}
	/**
	 * @param suspiciousDetail the suspiciousDetail to set
	 */
	@JsonProperty("detail")
	public void setSuspiciousDetail(String suspiciousDetail) {
		this.suspiciousDetail = suspiciousDetail;
	}
	/**
	 * @return the appId
	 */
	@JsonProperty("appId")
	public String getAppId() {
		return appId;
	}
	/**
	 * @param appId the appId to set
	 */
	@JsonProperty("appId")
	public void setAppId(String appId) {
		this.appId = appId;
	}
	
}

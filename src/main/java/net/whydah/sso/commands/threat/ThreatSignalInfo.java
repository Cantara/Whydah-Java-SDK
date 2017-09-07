package net.whydah.sso.commands.threat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.whydah.sso.extensions.crmcustomer.types.Customer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "definition-code",
        "responsible-ipaddress",
        "activity-log",
       
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreatSignalInfo {
	
	@JsonProperty("definition-code")
	private
	int threatDefinitionCode;
	
	@JsonProperty("responsible-ipaddress") //if any
	private
	String responsibleIPAddress="";
	
	@JsonProperty("activity-log") //if any
	private
	List<ThreatActivityLog> activityLogList=new ArrayList<ThreatActivityLog>();
	
	public ThreatSignalInfo(){}
	public ThreatSignalInfo(int code, String responsibleIpAddress, List<ThreatActivityLog> activityLogList){
		
	}
	
	public static ThreatSignalInfo fromJson(String json) {
        if (json == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            return mapper.readValue(json, ThreatSignalInfo.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toJson(ThreatSignalInfo threatSignalInfo) {
        if (threatSignalInfo == null) {
            return "{}";
        }
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(threatSignalInfo);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
	/**
	 * @return the threatDefinitionCode
	 */
    @JsonProperty("definition-code")
	public int getThreatDefinitionCode() {
		return threatDefinitionCode;
	}
	/**
	 * @param threatDefinitionCode the threatDefinitionCode to set
	 */
    @JsonProperty("definition-code")
	public void setThreatDefinitionCode(int threatDefinitionCode) {
		this.threatDefinitionCode = threatDefinitionCode;
	}
	/**
	 * @return the responsibleIdentities
	 */
    @JsonProperty("responsible-ipaddress") 
	public String getResponsibleIPAddress() {
		return responsibleIPAddress;
	}
	/**
	 * @param responsibleIdentities the responsibleIdentities to set
	 */
    @JsonProperty("responsible-ipaddress") 
	public void setResponsibleIPAddress(String responsibleIdentities) {
		this.responsibleIPAddress = responsibleIdentities;
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
	
}

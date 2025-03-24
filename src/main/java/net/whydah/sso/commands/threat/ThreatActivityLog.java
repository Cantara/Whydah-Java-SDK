package net.whydah.sso.commands.threat;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "endpoint",
        "ip-address",
        "request-time",
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ThreatActivityLog implements Serializable  {

    @Serial
    private static final long serialVersionUID = 1L;
	
	@JsonProperty("id")
	private String id="";
	
	@JsonProperty("endpoint")
	private String endPoint="";
	
	@JsonProperty("ip-address")
	private String ipAddress="";
	
	@JsonProperty("request-time")
	private String requestTime="";
	
	@JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();
	
	
	public ThreatActivityLog(){
		id = UUID.randomUUID().toString();
	}
	
	public ThreatActivityLog(String endPoint, String ipAddress, String requestTime){
		this(UUID.randomUUID().toString(), endPoint, ipAddress, requestTime, null);
	}
	
	public ThreatActivityLog(String endPoint, String ipAddress, String requestTime, Object[] details){
		this(UUID.randomUUID().toString(), endPoint, ipAddress, requestTime, details);
		
	}
	
	public ThreatActivityLog(String id, String endPoint, String ipAddress, String requestTime, Object[] details){
		this.id = id;
		this.endPoint = endPoint;
		this.ipAddress = ipAddress;
		this.requestTime = requestTime;
		
		if(details!=null){
        	for(int i=0; i<details.length;i=i+2){
        		String key=details[i].toString();
        		Object value = (i==details.length -1)?"":details[i+1];
        		setAdditionalProperty(key, value);
        	}
        }
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
	
	@JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public ThreatActivityLog setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

	private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(ThreatActivityLog log) {
        if (log == null) {
            return "{}";
        }
        try {
            return mapper.writeValueAsString(log);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
	
}

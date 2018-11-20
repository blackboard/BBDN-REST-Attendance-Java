package bbdn.rest.attendance;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import bbdn.rest.BaseObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Attendance extends BaseObject {

	@JsonProperty("id")
	private String id;

	@JsonProperty("meetingId")
	private String meetingId;

	@JsonProperty("userId")
	private String userId;

	@JsonProperty("status")
	private String status;

	public Attendance() {
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Attendance [id=" + id + ", meetingId=" + meetingId + ", userId=" + userId + ", status=" + status
				 + super.toString() + "]";
	}
}
package bbdn.rest.meeting;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import bbdn.rest.BaseObject;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Meeting extends BaseObject {

	@JsonProperty("id")
	private String id;

	@JsonProperty("courseId")
	private String courseId;

	@JsonProperty("title")
	private String title;

	@JsonProperty("description")
	private String description;

	@JsonProperty("start")
	private String start;

	@JsonProperty("end")
	private String end;

	@JsonProperty("externalLink")
	private String externalLink;


	public Meeting() {
	}

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getExternalLink() {
		return externalLink;
	}

	public void setExternalLink(String externalLink) {
		this.externalLink = externalLink;
	}

	@Override
	public String toString() {
		return "Meeting [id=" + id + ", courseId=" + courseId + ", title=" + title
				+ ", description=" + description + ", start=" + start + ", end=" + end
				+ ", externalLink=" + externalLink + ", " + super.toString() + "]";
	}
}
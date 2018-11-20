package bbdn.rest.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import bbdn.rest.common.Duration;

/**
 * Availability of Learn objects as specified in the REST APIs
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Availability {

	/**
	 * Available setting. This can be set to 'Yes' or 'No' in all cases. Some objects allow for different settings like 'Disabled' or 'Term'
	 */
	@JsonProperty("available")
	@JsonInclude(Include.NON_NULL)
  private String available;

	/**
	 * Duration setting. @see Duration.
	 */
	@JsonProperty("duration")
	@JsonInclude(Include.NON_NULL)
  private Duration duration;

	

	/**
	 * Empty constructor
	 */
	public Availability() {

	}

	public Availability(Boolean duration) {
		if(duration) {
			this.duration = new Duration();
		}
	}

	/**
	 * Get the current availability settings
	 * @return available
	 */
  public String getAvailable() {
		return available;
	}

	/**
	 * Set availability settings
	 * @param available: The availability setting
	 */
	public void setAvailable(String available) {
		this.available = available;
	}

	/**
	 * Get the current duration settings
	 * @return duration
	 */
  public Duration getDuration() {
		return duration;
	}

	/**
	 * Set duration settings
	 * @param duration: The duration setting
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}


	@Override
	public String toString() {
		String dur = duration != null ? duration.toString() : "null";
		return "Availability [available=" + available +
					 ", duration=" + dur  + "]";
	}

}

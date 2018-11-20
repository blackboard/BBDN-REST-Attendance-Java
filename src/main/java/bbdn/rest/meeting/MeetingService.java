package bbdn.rest.meeting;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import bbdn.caching.CacheUtil;
import bbdn.rest.RestConstants;
import bbdn.rest.RestConfig;
import bbdn.rest.common.Availability;
import bbdn.rest.meeting.Meeting;
import bbdn.rest.meeting.Meetings;
import bbdn.unsecurity.UnSecurityUtil;

public class MeetingService {

	private Logger log = null;

	/**
	 * This is the constuctor, which essentially just sets up logging.
	 */
	public MeetingService () {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO");
		log = LoggerFactory.getLogger(MeetingService.class);
	}

	public Meeting createMeeting(String crsId, Meeting meeting) {
		log.debug("READALL");

		String endpoint = RestConstants.MEETING_BASE_PATH + "/" + crsId + "/meetings";

		List<Meeting> meetings = sendRequest(endpoint, HttpMethod.POST, meeting, false);

		log.debug("Size of READALL meetings: " + String.valueOf(meetings.size()));
		log.debug("First READALL Result: " + meetings.get(0));
		log.debug("Last READALL Result: " + meetings.get(meetings.size()-1));

		return(meetings.get(0));
	}

	private List<Meeting> sendRequest(String sUri, HttpMethod method, Meeting body, boolean isCollection) {

		List<Meeting> meetingList = new ArrayList();
		RestTemplate restTemplate = null;

		try {

			restTemplate = UnSecurityUtil.getRestTemplate();

		URI uri = null;
			try {
				uri = new URI(RestConfig.host + sUri);
				log.debug("URI is " + uri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			HttpHeaders headers = new HttpHeaders();

			headers.add("Authorization", "Bearer " + CacheUtil.getValidToken());
			headers.setContentType(MediaType.APPLICATION_JSON);
			log.debug("Request Headers: " + headers.toString());

			HttpEntity<Meeting> request = new HttpEntity<Meeting>(body, headers);
			log.debug("Request Body: " + request.getBody());

			if(isCollection) {
				Meeting lastMeeting = new Meeting();
				log.debug("in isCollection, URI is " +uri.toString());

				while(uri != null) {
					log.debug("getting meetings");
					ResponseEntity<Meetings> response = restTemplate.exchange(uri, method, request, Meetings.class );

					log.debug("setting tempMeetings");
					Meetings tempMeetings = response.getBody();

					log.debug("getting results");
					Meeting[] results = tempMeetings.getResults();

					log.debug("if");
					if(lastMeeting != null && results.length > 0 ) {
						log.debug("nextIf");
						if(results[results.length-1].getId() != lastMeeting.getId()) {
							log.debug("startFor");
						for(int i = 0; i < results.length; i++) {
								log.debug("forLastMeeting");
						lastMeeting = results[i];
								log.debug("forMeetingAdd");
								meetingList.add(results[i]);
						}
								try {
								uri = new URI(RestConfig.host + tempMeetings.getPaging().getNextPage());
								log.debug("NewURI is " + uri);
							} catch (URISyntaxException e) {
								// TODO Auto-generated catch block
								log.debug("setNewURIEx");
								e.printStackTrace();
							} catch (NullPointerException npe) {
								log.debug("Next Page is null, that means we are done!");
								uri = null;
							}
						} else {
							log.debug("nextIfFalse");
						uri = null;
					}
				} else {
						log.debug("ifFalse");
					uri = null;
				}
				}
				log.debug("exit while");

			} else {
				ResponseEntity<Meeting> response = restTemplate.exchange(uri, method, request, Meeting.class );
				log.debug("Response: " + response);

				Meeting meeting = response.getBody();
			log.debug("Term: " + meeting.toString());

				meetingList.add(meeting);
			}
		}
		catch (Exception e) {
			if( method.equals(HttpMethod.DELETE )) {

			} else {
				log.error("Exception encountered");
				e.printStackTrace();
			}
		}

	return (meetingList);
	}
}

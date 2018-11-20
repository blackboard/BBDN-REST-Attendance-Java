package bbdn.rest.attendance;

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
import bbdn.rest.attendance.Attendance;
import bbdn.rest.attendance.Attendances;
import bbdn.unsecurity.UnSecurityUtil;

public class AttendanceService {

	private Logger log = null;

	/**
	 * This is the constuctor, which essentially just sets up logging.
	 */
	public AttendanceService () {
		System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "DEBUG");
		log = LoggerFactory.getLogger(AttendanceService.class);
	}

	public List<Attendance> generateReport(String crsId, String meetingId) {
		log.debug("READ");

		String endpoint = RestConstants.ATTENDANCE_BASE_PATH + "/" + crsId + "/meetings/" + meetingId + "/users";

		List<Attendance> attendances = sendRequest(endpoint, HttpMethod.GET, new Attendance(), true);

		return attendances;
	}


	public Attendance createAttendance(String crsId, String meetingId, Attendance attendance, boolean bulk) {
		log.debug("READALL");

		String endpoint = RestConstants.ATTENDANCE_BASE_PATH + "/" + crsId + "/meetings/" + meetingId + "/users";

		if(bulk) {
			endpoint += "/bulk";
		}

		List<Attendance> attendances = sendRequest(endpoint, HttpMethod.POST, attendance, false);

		if(bulk) {
			return null;
		} else {
			return(attendances.get(0));
		}
	}


	private List<Attendance> sendRequest(String sUri, HttpMethod method, Attendance body, boolean isCollection) {

		List<Attendance> attendanceList = new ArrayList();
		RestTemplate restTemplate = null;
		boolean bulk = false;

		try {

			restTemplate = UnSecurityUtil.getRestTemplate();

		URI uri = null;
			try {
				uri = new URI(RestConfig.host + sUri);
				log.info("URI is " + uri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String path = uri.getPath();
			bulk = path.substring(path.lastIndexOf('/') + 1).equals("bulk");

			HttpHeaders headers = new HttpHeaders();

			headers.add("Authorization", "Bearer " + CacheUtil.getValidToken());
			headers.setContentType(MediaType.APPLICATION_JSON);
			log.info("Request Headers: " + headers.toString());

			HttpEntity<Attendance> request = new HttpEntity<Attendance>(body, headers);
			log.info("Request Body: " + request.getBody());

			if(isCollection) {
				Attendance lastAttendance = new Attendance();
				log.info("in isCollection, URI is " +uri.toString());

				while(uri != null) {
					log.info("getting attendances");
					ResponseEntity<Attendances> response = restTemplate.exchange(uri, method, request, Attendances.class );

					log.info("setting tempAttendances: " + response.getStatusCode() );
					Attendances tempAttendances = response.getBody();

					log.info("getting results");
					Attendance[] results = tempAttendances.getResults();


					log.debug("if");
					if(lastAttendance != null && results.length > 0 ) {
						log.debug("nextIf");
						if(results[results.length-1].getMeetingId() != lastAttendance.getMeetingId()) {
							log.debug("startFor");
						for(int i = 0; i < results.length; i++) {
								log.debug("forLastAttendance");
						lastAttendance = results[i];
								log.debug("forAttendanceAdd");
								attendanceList.add(results[i]);
						}
								try {
								uri = new URI(RestConfig.host + tempAttendances.getPaging().getNextPage());
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
				ResponseEntity<Attendance> response = restTemplate.exchange(uri, method, request, Attendance.class );
				log.debug("Response: " + response);

				Attendance attendance = response.getBody();
			log.debug("Term: " + attendance.toString());

				attendanceList.add(attendance);
			}
		}
		catch (Exception e) {
			if( method.equals(HttpMethod.DELETE) || bulk) {

			} else {
				log.error("Exception encountered");
				e.printStackTrace();
			}
		}

	return (attendanceList);
	}
}

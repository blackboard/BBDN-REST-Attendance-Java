package bbdn.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

import bbdn.caching.CacheUtil;

import bbdn.rest.attendance.*;
import bbdn.rest.common.*;
import bbdn.rest.course.*;
import bbdn.rest.meeting.*;
import bbdn.rest.membership.*;
import bbdn.rest.oauth.*;
import bbdn.rest.user.*;

import bbdn.rest.RestConfig;
import bbdn.rest.RestConstants;

public class TakeAttendance {

	private static final Logger log = LoggerFactory.getLogger(Attendance.class);

	public static boolean DEVMODE = false;

	public static void main(String[] args) {

		String userName = "";
		String user = "";
		String status = "";
		String course = "";
		String meeting = "";

		boolean GET_COURSES = false;
		boolean GET_MEMBERSHIPS = false;
		boolean MARK_ATTENDANCE = false;
		boolean MARK_ALL_ATTENDANCE = false;
		boolean GET_REPORT = false;
		
		boolean nextUserName = false;
		boolean nextUser = false;
		boolean nextStatus = false;
		boolean nextCourse = false;
		boolean nextMeeting = false;
		
		if(args.length > 0) {
			for(int i = 0; i < args.length; i++ ) {
				log.info("args[" + i + "]: " + args[i]);
				
				if (args[i].equalsIgnoreCase("-c")) {
					GET_COURSES = true;
				} 
				else if (args[i].equalsIgnoreCase("-m")) {
					GET_MEMBERSHIPS = true;
					nextCourse = true;
				}
				else if (args[i].equalsIgnoreCase("-s")) {
					MARK_ATTENDANCE = true;
					nextUser = true;
				}
				else if (args[i].equalsIgnoreCase("-b")) {
					MARK_ALL_ATTENDANCE = true;
					nextStatus = true;
				}
				else if (args[i].equalsIgnoreCase("-r")) {
					GET_REPORT = true;
					nextMeeting = true;
				}
				else if (args[i].equalsIgnoreCase("-u")) {
					nextUserName = true;
				}
				else if (nextUserName) {
					userName = args[i];
					nextUserName = false;
				}
				else if (nextUser) {
					user = args[i];
					nextUser = false;
					nextStatus = true;
				}
				else if (nextStatus) {
					status = args[i];
					nextStatus = false;
					nextMeeting = true;
				}
				else if (nextCourse) {
					course = args[i];
					nextCourse = false;
				}
				else if (nextMeeting) {
					meeting = args[i];
					nextMeeting = false;
					nextCourse = true;
				}
			}
			
			log.info(" UserName: " + userName +
					 " User: " + user +
					 " Status: " + status + 
					 " Meeting: " + meeting + 
					 " Course: " + course + 
			 		 " GET_COURSES: " + GET_COURSES +
					 " GET_MEMBERSHIPS: " + GET_MEMBERSHIPS +
					 " MARK_ATTENDANCE: " + MARK_ATTENDANCE +
					 " MARK_ALL_ATTENDANCE: " + MARK_ALL_ATTENDANCE +
					 " GET_REPORT: " + GET_REPORT);
		}

		// Instantiate the authorizer class
		Authorizer authorizer = new Authorizer();

		// Request a token and cache it for further use
	  	authorizer.authorize();

		log.info("Token: " + CacheUtil.getValidToken());

		// Instantiate services. These objects are responsible for making REST calls
		AttendanceService attendanceService = new AttendanceService();
		MeetingService meetingService = new MeetingService();
		MembershipService membershipService = new MembershipService();

		// gradle run -Dexec.args="-u <username> -c"
		// This returns the coursename and the course pk1. Make note of the pk1.
		if(GET_COURSES) {
			
			// Get all of the users course memberships to see what courses they are enrolled in. 
			// In real life, we should use Three legged oauth or LTI to ensure it is an Instructor and
			// also filter this list to only list the courses this user is an instructor in.
			List<Membership> myCourses = membershipService.readAllByUserId("userName:" + userName, true);

			// Iterate through the course list and print out relevent details
			ListIterator<Membership> courseCursor = myCourses.listIterator();
			while(courseCursor.hasNext()) {
				Course curCourse = courseCursor.next().getCourse();
				String courseId = curCourse.getId();
				String courseName = curCourse.getName();
				
				log.info("Current Course:" + courseName + " (" + courseId + ")");
			}
		// gradle run -Dexec.args="-m <course pk1>"
		// This creates a meeting, which is the holder of the attendance records. This would be a class or office hours
		// It prings out the course roster by username and pk1. Make note of the user's pk1 if you plan to create an 
		// individual attendance record.
		} else if (GET_MEMBERSHIPS) {
			
			LocalDateTime start = LocalDateTime.now();
	  		LocalDateTime end = start.plusHours(1);
			
			Meeting newMeeting = new Meeting();

			newMeeting.setCourseId(course);
			newMeeting.setTitle("Today's Meeting");
			newMeeting.setDescription("A meeting representing today's attendance records.");
			newMeeting.setStart(start.toString() + "Z");
			newMeeting.setEnd(end.toString() + "Z");
			newMeeting.setExternalLink("https://developer.blackboard.com");

			// Create the meeting so you have a container to associate the attendance records with.
			// Make note of the meeting Id.
			Meeting myMeeting = meetingService.createMeeting(course, newMeeting);
			log.info("Meeting: " + myMeeting.getId());

			//Get all of the users for this course. In the real world we would filter out non-students
			List<Membership> roster = membershipService.readAllByCourseId(course, true);

			// Iterate over the users, print out the username and pk1.
			ListIterator<Membership> rosterCursor = roster.listIterator();
			while(rosterCursor.hasNext()) {
				User curUser = rosterCursor.next().getUser();
				String courseUserName = curUser.getUserName();
				String courseUserId = curUser.getId();
				
				log.info("Current User:" + courseUserName + " (" + courseUserId + ")");
			}
		// gradle run -Dexec.args="-s <user pk1> <Present|Late|Absent|Excused> <meetingId> <course pk1>"
		// This creates an individual attendance record for the specified user in the specified course
		// for the specified meeting. 
		} else if (MARK_ATTENDANCE) {

			Attendance attendance = new Attendance();
			attendance.setMeetingId(meeting);
			attendance.setUserId(user);
			attendance.setStatus(status);

			Attendance record = attendanceService.createAttendance(course, meeting, attendance, false);

			log.info("Attendance: " + record.toString());
		// gradle run -Dexec.args="-b <Present|Late|Absent|Excused> <meetingId> <course pk1>"
		// This creates attendance records for all of the users in the specified course for the specified meeting.
		// This is one status for all	
		} else if (MARK_ALL_ATTENDANCE) {

			Attendance attendance = new Attendance();
			attendance.setMeetingId(meeting);
			attendance.setStatus(status);

			Attendance record = attendanceService.createAttendance(course, meeting, attendance, true);

			log.info("Attendance: BULK");
		// gradle run -Dexec.args="-r <meetingId> <course pk1>"
		// Generate a report of the attendance records for the given meeting in the given course.
		} else if (GET_REPORT) {

			List<Attendance> attendanceReport = attendanceService.generateReport(course, meeting);

			log.info("Attendance Report - Course: " + course + " Meeting: " + meeting);

			ListIterator<Attendance> reportCursor = attendanceReport.listIterator();
			while(reportCursor.hasNext()) {
				Attendance curRecord = reportCursor.next();
				String curId = curRecord.getId();
				String curMeetingId = curRecord.getMeetingId();
				String curUserId = curRecord.getUserId();
				String curStatus = curRecord.getStatus();
				
				log.info("Record " + curId + ": UserId - " + curUserId + ", Status: " + status);
			}
		}

	}
}

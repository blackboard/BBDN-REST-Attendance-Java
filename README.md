## BBDN-REST-Attendance-Java

The purpose of this project is to illustrate the usage of the Blackboard Attendance APIs. These APIs, though serviced by a MicroService, are surfaced via the Blackboard Learn REST Framework for the time being. 

This means you, the developer, can use the same key, secret, and application ID that you use with all Blackboard Learn REST applications.

### Prerequisites
* Java 8
* Application ID, Key, and Secret from developer.blackboard.com.
* Application ID registered in Blackboard Learn instance with an associated non-Admin user with the appropriate rights
* Gradle (Built and tested with Gradle 4.3)
* A Blackboard Learn instance with the attendance APIs available and the service enabled

### Running the Application
This application is a java command line application built with the gradle Application plugin. This plugin allows you to build and run the java code with gradle run. 

In order to give the user control over the action, the application uses command line switches and arguments to know what actions to take and with what data.

In order to pass the command line options to the gradle application plugin, you simply append -Dexec.args="\<your command line args\>"

This project is intended to show you how to use the APIs and not how to build a Java application.

#### Available Commands
##### Get a course list for a specific user:
```bash
gradle run -Dexec.args="-u <username> -c"
```

##### Create a meeting for a course and get the roster
```bash
gradle run -Dexec.args="-m <course pk1>"
```

##### Create an individual user attendance record
```bash
gradle run -Dexec.args="-s <user pk1> <Present|Late|Absent|Excused> <meetingId> <course pk1>"
```

##### Batch create all user attendance records
```bash
gradle run -Dexec.args="-b <Present|Late|Absent|Excused> <meetingId> <course pk1>"
```

##### Generate a meeting attendance report
```bash
gradle run -Dexec.args="-r <meetingId> <course pk1>"
```

### Licensing
```
Copyright © 2018 Blackboard Developer Community. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

-- Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

-- Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.

-- Neither the name of Blackboard Inc. nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY BLACKBOARD INC ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL BLACKBOARD INC. BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
```


package com.wakanda.soapapi.soapdemoappwakanda;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import com.wakanda.courses.CourseDetails;
import com.wakanda.courses.DeleteCourseDetailsRequest;
import com.wakanda.courses.DeleteCourseDetailsResponse;
import com.wakanda.courses.GetAllCourseDetailsRequest;
import com.wakanda.courses.GetAllCourseDetailsResponse;
import com.wakanda.courses.GetCourseDetailsRequest;
import com.wakanda.courses.GetCourseDetailsResponse;
import com.wakanda.soapapi.soapdemoappwakanda.CourseDetailsService.Status;

@Endpoint
public class CourseDetailsEndpoint {
//localPart - name of the request that needs to be handled
	@Autowired
	CourseDetailsService service;
	
	//Method 1 - that takes a request and returns a response
			@PayloadRoot(namespace="http://wakanda.com/courses", localPart="GetCourseDetailsRequest") 
			@ResponsePayload 
			public GetCourseDetailsResponse processCourseDetailsRequest(@RequestPayload GetCourseDetailsRequest request) {  //convert from Request.xml to java
				Course course = service.findById(request.getId()); 	//use the service
				if(course == null) {
					throw new CourseNotFoundException("Invalid course Id " + request.getId());
				}
				System.out.println("Test updated for github");
				return mapCourseDetails(course);
			}
			
	//Get All 
			@PayloadRoot(namespace="http://wakanda.com/courses", localPart="GetAllCourseDetailsRequest") 
			@ResponsePayload 
			public GetAllCourseDetailsResponse processAllCourseDetailsRequest(@RequestPayload GetAllCourseDetailsRequest request) {  //convert from Request.xml to java
				//use the service
				 List<Course>courses = service.findAll();
				return mapAllCourseDetails(courses);
			}
			
			
			
			//Method to Get all course details response
			private GetAllCourseDetailsResponse mapAllCourseDetails(List<Course> courses) {
				GetAllCourseDetailsResponse response = new GetAllCourseDetailsResponse();
				for(Course course: courses) {
					CourseDetails mapCourse = mapCourse(course);  //map each course 
					response.getCourseDetails().add(mapCourse);  //add it to the list of courseDetails
				}
				return response;
			}
			
			
			@PayloadRoot(namespace="http://wakanda.com/courses", localPart="DeleteCourseDetailsRequest") 
			@ResponsePayload 
			public DeleteCourseDetailsResponse deleteCourseDetailsRequest(@RequestPayload DeleteCourseDetailsRequest request) {  //convert from Request.xml to java
				// Status status -> from course details service
				Status status = service.deleteById(request.getId());  //Status from CourseDetails service enum
				 DeleteCourseDetailsResponse response = new DeleteCourseDetailsResponse();  //create an instance
				 response.setStatus(mapStatus(status)); //map the two Status's 
				 //- setStatus comes from xsd Java Object
				return response;
			}
			
			
			
			//Method to Get a single Course Response
			private GetCourseDetailsResponse mapCourseDetails(Course course) {
				GetCourseDetailsResponse response = new GetCourseDetailsResponse();
				response.setCourseDetails(mapCourse(course));
				return response;
			}
			
			private CourseDetails mapCourse(Course course) {
				CourseDetails courseDetails = new CourseDetails();
				courseDetails.setId(course.getId());
				courseDetails.setName(course.getName());
				courseDetails.setDescription(course.getDescription());
				return courseDetails;
			}
			
			//Method for Status enum
			private com.wakanda.courses.Status mapStatus(Status status) {  //parameter is service status
				if(status == Status.FAILURE) {
					return com.wakanda.courses.Status.FAILURE; //mapping the service Status to the Status we defined in the xsd bean
				}
				return com.wakanda.courses.Status.SUCCESS;
			}

}

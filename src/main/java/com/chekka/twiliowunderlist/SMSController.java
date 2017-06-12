package com.chekka.twiliowunderlist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;





@RestController
public class SMSController{

	
	@RequestMapping("/sms")
	public String greeting(@RequestParam(value="Body", defaultValue="World") String message)  {
        String CALENDAR_USER = "sumedh.chekka@gmail.com";
		
        String events = quickAddtoCalendar(CALENDAR_USER, message);
        return events;
    }


	private String quickAddtoCalendar(String calendarUser, String message) {
		
		try{
			com.google.api.services.calendar.Calendar service = GoogleCalendarUtil.getCalendarService();
			if (!"todo".equals(message.toLowerCase()))
				service.events().quickAdd(calendarUser, message).execute();
			return getEvents(calendarUser);
		}
		catch (Exception e){
			e.printStackTrace();
			return "Oops...something went wrong !!";
		}
	}
	
	private String getEvents(String calendarUser) {
		StringBuilder eventsSB = new StringBuilder();
		try{
			com.google.api.services.calendar.Calendar service = GoogleCalendarUtil.getCalendarService();
			
			
			// List the next 10 events from the primary calendar.
	        DateTime now = new DateTime(System.currentTimeMillis());
	        System.out.println("will get 10 events from: " + now.toString());
	       
	        //com.google.api.services.calendar.model.Calendar calendar = service.calendars().get(calendarUser).execute();
	        //System.out.println("Calendar summary: " + calendar.getSummary());
	    
	        Events events = service.events().list(calendarUser)
	            .setMaxResults(10)
	            .setTimeMin(now)
	            .setOrderBy("startTime")
	            .setSingleEvents(true)
	            .execute();
	        
	        SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyy HH:mm");
	        String formattedStartDate = "";
	        DateTime eventDate;
	        
	        List<Event> items = events.getItems();
	        if (items.size() == 0) {
	            System.out.println("No upcoming events found.");
	        } else {
	            System.out.println("Upcoming events");
	            for (Event event : items) {
	            	
	            	eventDate = event.getStart().getDateTime();
	            	
	            	if (eventDate != null)
	            		formattedStartDate = sdfDate.format(new Date(eventDate.getValue()));
	            	else{
	            		formattedStartDate = event.getStart().toString();
	            	}
	            	
	            	
	                eventsSB.append(event.getSummary() + " : " + formattedStartDate + "\n");
	                System.out.printf("%s (%s)\n", event.getSummary(), formattedStartDate);
	                
	            }
	        }
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return eventsSB.toString();
	}
	
	
}

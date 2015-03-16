package aTryout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;


public class Parser {
	
	
	
	static List<StopEntry> loadStops(String fileName){
		List<StopEntry> dataEntriesList = new ArrayList<StopEntry>();
		
		File input = new File(fileName);
        Scanner sc;
		try {
			sc = new Scanner(input);
			
			sc.nextLine();
			
	        while(sc.hasNextLine()){
	        	String line = sc.nextLine();
	        	
	        	List<String> lineList = Arrays.asList(line.split(","));
	        	
	        	StopEntry dataEntry = new StopEntry();
	        	dataEntry.stop_id = Float.parseFloat(lineList.get(0));
	        	dataEntry.stop_name = lineList.get(1);
	        	dataEntry.stop_lat = Float.parseFloat(lineList.get(2));
	        	dataEntry.stop_lon = Float.parseFloat(lineList.get(3));
	        	dataEntry.stop_code = Float.parseFloat(lineList.get(4));
	        	
	        	dataEntriesList.add(dataEntry);
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    //Reads from input
        
		return dataEntriesList;
	}
	
	static List<CalendarDates> loadCalendarDates(String fileName){
		List<CalendarDates> dataEntriesList = new ArrayList<CalendarDates>();
		
		File input = new File(fileName);
        Scanner sc;
		try {
			sc = new Scanner(input);
			
			sc.nextLine();
			
	        while(sc.hasNextLine()){
	        	String line = sc.nextLine();
	        	
	        	List<String> lineList = Arrays.asList(line.split(","));
	        	
	        	CalendarDates dataEntry = new CalendarDates();
	        	dataEntry.service_id = Float.parseFloat(lineList.get(0));
	        	dataEntry.date = Float.parseFloat(lineList.get(1));
	        	dataEntry.exception_type = Float.parseFloat(lineList.get(2));
	        	
	        	dataEntriesList.add(dataEntry);
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    //Reads from input
        
		return dataEntriesList;
	}
	
	static List<Routes> loadRoutes(String fileName){
		List<Routes> dataEntriesList = new ArrayList<Routes>();
		
		File input = new File(fileName);
        Scanner sc;
		try {
			sc = new Scanner(input);
			
			sc.nextLine();
			
	        while(sc.hasNextLine()){
	        	String line = sc.nextLine();
	        	
	        	List<String> lineList = Arrays.asList(line.split(","));
	        	
	        	Routes dataEntry = new Routes();
	        	dataEntry.agency_id = Float.parseFloat(lineList.get(0));
	        	dataEntry.route_id = Float.parseFloat(lineList.get(1));
	        	dataEntry.route_short_name = lineList.get(2);
	        	dataEntry.route_long_name = lineList.get(3);
	        	dataEntry.route_type = Float.parseFloat(lineList.get(4));
	        	
	        	dataEntriesList.add(dataEntry);
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    //Reads from input
        
		return dataEntriesList;
	}

	static class StopEntry {
		Float stop_id;
		String stop_name;
		Float stop_lat;
		Float stop_lon;
		Float stop_code;
	}
	
	static class CalendarDates {
		Float service_id;
		Float date;
		Float exception_type;
	}
	
	static class Routes {
		Float agency_id;
		Float route_id;
		String route_short_name;
		String route_long_name;
		Float route_type;
	}
	
	static List<StopTimes> loadStopTimes(String fileName){
		List<StopTimes> dataEntriesList = new ArrayList<StopTimes>();
		
		File input = new File(fileName);
        Scanner sc;
		try {
			sc = new Scanner(input);
			
			sc.nextLine();
			
	        while(sc.hasNextLine()){
	        	String line = sc.nextLine();
	        	
	        	List<String> lineList = Arrays.asList(line.split(","));
	        	
	        	StopTimes dataEntry = new StopTimes();
	        	dataEntry.trip_id = lineList.get(0);
	        	dataEntry.stop_id = Float.parseFloat(lineList.get(1));
	        	dataEntry.arrival_time = lineList.get(2);
	        	dataEntry.departure_time = lineList.get(3);
	        	dataEntry.stop_sequence = Integer.parseInt(lineList.get(4));
	        	
	        	dataEntriesList.add(dataEntry);
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    //Reads from input
        
		return dataEntriesList;
	}
	
	static class StopTimes {
		String trip_id;
		Float stop_id;
		String arrival_time;
		String departure_time;
		Integer stop_sequence;
	}
	
	static List<Trips> loadTrips(String fileName){
		List<Trips> dataEntriesList = new ArrayList<Trips>();
		
		File input = new File(fileName);
        Scanner sc;
		try {
			sc = new Scanner(input);
			
			sc.nextLine();
			
	        while(sc.hasNextLine()){
	        	String line = sc.nextLine();
	        	
	        	List<String> lineList = Arrays.asList(line.split(","));
	        	
	        	Trips dataEntry = new Trips();
	        	dataEntry.route_id = Float.parseFloat(lineList.get(0));
	        	dataEntry.trip_id = lineList.get(1);
	        	dataEntry.service_id = Float.parseFloat(lineList.get(2));
	        	dataEntry.trip_short_name = Float.parseFloat(lineList.get(3));
	        	dataEntry.trip_headsign = lineList.get(4);
	        	
	        	dataEntriesList.add(dataEntry);
	        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    //Reads from input
        
		return dataEntriesList;
	}
	
	static class Trips {
		Float route_id;
		String trip_id;
		Float service_id;
		Float trip_short_name;
		String trip_headsign;
	}

}

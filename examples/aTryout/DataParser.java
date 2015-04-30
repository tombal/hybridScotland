package aTryout;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class DataParser {
	
	/**
	 * 
	 * @param fileName
	 * @return Map <stopId, DataStop>
	 */
	static Map<Float,DataStop> getStops(String fileName) {
		Map<Float,DataStop> result = new HashMap<Float, DataParser.DataStop>();

		File input = new File(fileName);
		Scanner sc;
		try {
			sc = new Scanner(input);

			sc.nextLine();

			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				List<String> lineList = Arrays.asList(line.split(","));

				DataStop dataEntry = new DataStop();
				dataEntry.stop_id = Float.parseFloat(lineList.get(0));
				dataEntry.stop_name = lineList.get(1);
				dataEntry.stop_lat = Float.parseFloat(lineList.get(2));
				dataEntry.stop_lon = Float.parseFloat(lineList.get(3));
				dataEntry.stop_code = Float.parseFloat(lineList.get(4));
				

				result.put(dataEntry.stop_id, dataEntry);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Reads from input


		
		return result;
	}
	
	static class DataStop {
		Float stop_id;
		String stop_name;
		Float stop_lat;
		Float stop_lon;
		Float stop_code;
		
		public String[] getArray(){
			//return null;
			String[] result = {stop_id.toString(),stop_name, stop_lat.toString(), stop_lon.toString(), stop_code.toString()};
			return result;
		}
		
		public String toString(){
			return stop_id.toString() + " , " + stop_name + " , " + stop_lat.toString() + " , " + stop_lon.toString() + " , " + stop_code.toString();
		}
	}
	
	
	/**
	 * 
	 * @param fileName
	 * @return map <calendardate, list of all serviceid's for this calendardate>
	 */
	static Map<Float,List<Float>> gatDates(String fileName) {
		Map<Float, List<Float>> result = new HashMap<Float, List<Float>>();
		
		File input = new File(fileName);
		Scanner sc;
		try {
			sc = new Scanner(input);

			sc.nextLine();

			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				List<String> lineList = Arrays.asList(line.split(","));
				
				

				Float service_id = Float.parseFloat(lineList.get(0));
				Float date = Float.parseFloat(lineList.get(1));
				
				List<Float> serviceIds = new ArrayList<Float>();
				if(result.containsKey(date)){
					serviceIds = result.get(date);
				}
				serviceIds.add(service_id);

				result.put(date, serviceIds);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Reads from input

		return result;
	}
	
	/**
	 * 
	 * @param fileName
	 * @return map <routeID, DataRoute>
	 */
	static Map<Float, DataRoute> getRoutes(String fileName) {
		Map<Float, DataRoute> result = new HashMap<Float, DataRoute>();
		
		File input = new File(fileName);
		Scanner sc;
		try {
			sc = new Scanner(input);

			sc.nextLine();

			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				List<String> lineList = Arrays.asList(line.split(","));

				DataRoute dataEntry = new DataRoute();
				dataEntry.agency_id = Float.parseFloat(lineList.get(0));
				dataEntry.route_id = Float.parseFloat(lineList.get(1));
				dataEntry.route_short_name = lineList.get(2);
				dataEntry.route_long_name = lineList.get(3);
				dataEntry.route_type = Float.parseFloat(lineList.get(4));

				result.put(dataEntry.route_id, dataEntry);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Reads from input

		return result;
	}
	
	static class DataRoute {
		Float agency_id;
		Float route_id;
		String route_short_name;
		String route_long_name;
		Float route_type;
	}
	
	
	/**
	 * 
	 * @param fileName
	 * @return map <trip_id, list of all DataStopTimes with this id>
	 */
	static Map<String, List<DataStopTime>> getStopTimes(String fileName) {
		
		Map<String, List<DataStopTime>> result = new HashMap<String, List<DataStopTime>>();

		File input = new File(fileName);
		Scanner sc;
		try {
			sc = new Scanner(input);

			sc.nextLine();

			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				List<String> lineList = Arrays.asList(line.split(","));

				DataStopTime dataEntry = new DataStopTime();
				dataEntry.trip_id = lineList.get(0);
				dataEntry.stop_id = Float.parseFloat(lineList.get(1));
				dataEntry.arrival_time = lineList.get(2);
				dataEntry.departure_time = lineList.get(3);
				dataEntry.stop_sequence = Integer.parseInt(lineList.get(4));
				
				List<DataStopTime> stopTimes = result.get(dataEntry.trip_id);
				
				if(stopTimes==null){
					stopTimes = new ArrayList<DataParser.DataStopTime>();
				}
				
				stopTimes.add(dataEntry);
				

				result.put(dataEntry.trip_id, stopTimes);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Reads from input

		return result;
	}

	static class DataStopTime {
		String trip_id;
		Float stop_id;
		String arrival_time;
		String departure_time;
		Integer stop_sequence;
	}
	
	static List<DataTrips> getTrips(String fileName) {
		List<DataTrips> dataEntriesList = new ArrayList<DataTrips>();

		File input = new File(fileName);
		Scanner sc;
		try {
			sc = new Scanner(input);

			sc.nextLine();

			while (sc.hasNextLine()) {
				String line = sc.nextLine();

				List<String> lineList = Arrays.asList(line.split(","));

				DataTrips dataEntry = new DataTrips();
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
		} // Reads from input

		return dataEntriesList;
	}

	static class DataTrips {
		Float route_id;
		String trip_id;
		Float service_id;
		Float trip_short_name;
		String trip_headsign;
	}

}

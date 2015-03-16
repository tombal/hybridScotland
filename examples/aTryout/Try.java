package aTryout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.naming.spi.DirStateFactory.Result;

import aTryout.Parser.CalendarDates;
import aTryout.Parser.Routes;
import aTryout.Parser.StopEntry;
import aTryout.Parser.StopTimes;
import aTryout.Parser.Trips;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.examples.marker.labelmarker.LabeledMarker;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import processing.core.PApplet;
import processing.core.PFont;


public class Try extends PApplet {
	
	
	UnfoldingMap map;
	PFont font;
	
	LabeledMarker train;
	int i = 0;
	
	List<StopEntry> data = Parser.loadStops("nmbs/stops.txt");

	public void setup() {		
		
		size(800, 600, OPENGL);

		map = new UnfoldingMap(this, "map", 50, 50, 700, 500);
		map.zoomToLevel(4);
		MapUtils.createDefaultEventDispatcher(this, map);

		List<Marker> markers = this.createMarkers();
		// Add markers to map
		map.addMarkers(markers);
		
		
		
		map.panTo(markers.get(0).getLocation());
		
		initTrain();
		
	}
	
	public void initTrain(){
		font = loadFont("ui/OpenSans-18.vlw");
		train = new LabeledMarker(map.getMarkers().get(i).getLocation(), "test", font, 10);
		train.setColor(color(255, 0, 0, 100));
		map.addMarker(train);
		i++;
		
	}
	
	public List<Marker> createMarkers(){
		List<Marker> markers = new ArrayList<Marker>();
		
		
		font = loadFont("ui/OpenSans-12.vlw");
		
		for(StopEntry entry:data){
			Location location = new Location(entry.stop_lat, entry.stop_lon);
			LabeledMarker marker = new LabeledMarker(location, entry.stop_name, font, 5);
			markers.add(marker);
		}
		return markers;
		
	}

	public void draw() {
		background(240);
		map.draw();
	}

	public void mouseMoved() {
		// Deselect all marker
		for (Marker marker : map.getMarkers()) {
			marker.setSelected(false);
		}

		// Select hit marker
		// Note: Use getHitMarkers(x, y) if you want to allow multiple selection.
		Marker marker = map.getFirstHitMarker(mouseX, mouseY);
		if (marker != null) {
			marker.setSelected(true);
		}
	}
	
	public void keyPressed() {
		if (key == ' ') {
			initTrain();
		}
	}
	
	public List<List<float[]>> createTrainsArray(){
		List<List<float[]>> result = new ArrayList<List<float[]>>();
		int i = 0;
		while(i<24*60){
			result.add(new ArrayList<float[]>());
			i++;
		}
		return result;
	}
	
	public float[] getCoord(float stop_id){
		
		for(StopEntry stopentry: data){
			if(stopentry.stop_id==stop_id){
				float result[] = new float[2];
				result[0]= stopentry.stop_lat;
				result[1]= stopentry.stop_lon;
				return result;
			}
		}
		return null;
	}
	
	public void getAllTrainsDay(float day){
		List<List<float[]>> trainsPerMinute = this.createTrainsArray();
		
		
		
	}
	
	public List<StopTimes> getStopTimesTripId(String trip_Id, List<StopTimes> stoptimes){
		 List<StopTimes> result = new ArrayList<StopTimes>();
		 for(StopTimes stops:stoptimes){
			 if(stops.trip_id==trip_Id){
				 result.add(stops);
			 }
		 }
		 return result;
	}
	
	public HashMap<Float, float[]> getTrainForStopTimes(List<StopTimes> stopTimes){
		HashMap<Float, float[]> result = new HashMap<Float, float[]>();
		
		float lastTime = Float.NaN;
		
		for(StopTimes stop: stopTimes){
			if(lastTime!=Float.NaN){
				result.putAll(this.getTrainBetweenStops(lastTime, this.timeToFloat(stop.arrival_time), null, null));//TODO coord
			}
			result.putAll(this.getSingleStop(this.timeToFloat(stop.arrival_time), this.timeToFloat(stop.departure_time), null)); //TODO coord
			lastTime = this.timeToFloat(stop.departure_time);
		}
		
		return result;
		
		
	}
	
	public Map<Float, float[]> getTrainBetweenStops(float departure, float arrival, float[] arrivalCoord, float[] departureCoord){
		HashMap<Float, float[]> result = new HashMap<Float, float[]>();
		float diff = arrival-departure;
		float x = departureCoord[0]-arrivalCoord[0];
		float y = departureCoord[1]-arrivalCoord[1];
		x = x/diff;
		y = y/diff;
		departure++;
		while(departure<arrival){
			arrivalCoord[0] = arrivalCoord[0] + x;
			arrivalCoord[1] = arrivalCoord[1] + y;
			result.put(departure, arrivalCoord);
			departure++;
		}
		return result;	
	}
	
	public Map<Float, float[]> getSingleStop(float arrival, float departure, float[] coord){
		HashMap<Float, float[]> result = new HashMap<Float, float[]>();
		
		while(arrival<=departure){
			result.put(arrival, coord);
			arrival++;
		}
		return result;
		
	}
	
	public Float timeToFloat(String time){
		Float hours = Float.parseFloat(time.substring(0, 2));
		Float minutes = Float.parseFloat(time.substring(2, 4));
		return hours*60+minutes;
	}
	
	public List<Float> getService_IdForDay(float day){
		List<CalendarDates> calendarDates = Parser.loadCalendarDates("nmbs/calendar_dates.txt");
		
		List<Float> service_ids = new ArrayList<Float>();
		for(CalendarDates calendar: calendarDates){
			if(calendar.date==day){
				service_ids.add(calendar.service_id);
			}
		}
		return service_ids;
	}
	
	public List<String> getTrip_IdForServiceId(float serviceId){
		List<Trips> trips = Parser.loadTrips("nmbs/trips.txt");
		
		List<String> result = new ArrayList<String>();
		for(Trips trip: trips){
			if(trip.service_id==serviceId){
				result.add(trip.trip_id);
			}
		}
		return result;
	}
	
    
    
    

}

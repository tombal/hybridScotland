package aTryout;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import processing.core.PApplet;
import processing.core.PFont;
import aTryout.DataParser.DataRoute;
import aTryout.DataParser.DataStop;
import aTryout.DataParser.DataStopTime;
import aTryout.DataParser.DataTrips;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.examples.marker.labelmarker.LabeledMarker;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.MapUtils;
public class Test extends PApplet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6393266518120392076L;
	UnfoldingMap map;
	
	//maps with data
	Map<Float,DataStop> stops;
	Map<Float,List<Float>> dates;
	Map<Float, DataRoute> routes;
	Map<String, List<DataStopTime>> stoptimes;
	Map<Float, List<DataStopTime>> stoptimesByStopId;
	List<DataTrips> trips;
	Player player;
	
	List<Player> players;
	
	//list of all trains
	List<Train> allTrainsForDay;
	
	//Settings
	private static final int TIMEWINDOW = 10;
	
	PFont font = loadFont("ui/OpenSans-18.vlw");
	
	public float askStation(String title){
		final float[] result = {0};
		result[0]=-1;
		
		
		Vector rowData = new Vector();
		Collection<DataStop> st = stops.values();
	    for (DataStop stop : st) {
	      Vector colData = new Vector(Arrays.asList(stop.getArray()));
	      rowData.add(colData);
	    }
	    
	    String[] columnNames = {"stop_id", "stop_name", "stop_lat", "stop_lon", "stop_code"};
	    
	    Vector columnNamesV = new Vector(Arrays.asList(columnNames));
	    final JTable table = new JTable(rowData, columnNamesV);
	    table.setRowSelectionAllowed(true);

	    ListSelectionModel rowSelection = table.getSelectionModel();
	    rowSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
	    JDialog fAskStation = new JDialog();
	    fAskStation.setTitle(title);
	    fAskStation.setSize(300, 300);
	    fAskStation.add(new JScrollPane(table));
	    fAskStation.setVisible(true);
	    
	    

	    
	    rowSelection.addListSelectionListener(new ListSelectionListener() {
	        public void valueChanged(ListSelectionEvent e) {	          
	          int row = table.getSelectedRow();

	          String stop_ID = (String) table.getModel().getValueAt(row, 0);
	          float stop_Id = Float.parseFloat(stop_ID);
	          System.out.println("Selected: " + stop_Id);
	          result[0] = stop_Id;
	        }

	      });
	   
		
		
		while(result[0]==-1){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//do nothing
		}
		
		fAskStation.dispose();
		
		
		return result[0];
	}
	
	boolean run = true;
	public void initPlayers(){
		if(run){
			run = false;
		players = new ArrayList<Test.Player>();
		System.out.println("staaaaa");
		for(int i=0; i<3;i++){
			System.out.println("iiiii");
			float id = this.askStation("select a station for player "+(i+1));
			Float[] coord = this.getCoord(id);
			Player player = new Player(coord[0], coord[1], id);
			players.add(player);
		}
		
		
		}
		
	}
	
	
	public void setup() {
		
		
		System.out.println("0");

		//load all the data from the nmbs 
		this.loadData();
		
		
		this.initPlayers();



		
		
		Float[] coord = this.getCoord(32737);
		player = new Player(coord[0], coord[1], 32737);
		

		
		player.getOn("4979:0", 32737, 32482);
		
		Location belgiumLocation = new Location(50.833333f, 4f);
		map = new UnfoldingMap(this, "map", 52f, 4f, 750, 650);
		
		this.size(800, 600, OPENGL);

		map.zoomToLevel(8);
		MapUtils.createDefaultEventDispatcher(this, map);
		map.panTo(belgiumLocation);
		


		
		//this.showStops();
		
		//List<Marker> markers = this.createMarkers();
		//Add markers to map
		//map.addMarkers(markers);
		//map.panTo(markers.get(0).getLocation());
		// initTrain();
		//this.tryoutOneDay();
		
		
		//this.showStops();
		
		this.allTrainsForDay = this.getTrainsForDay(20131215);
		initMarker();
		//Test jochen
		if(NotRundepartinTest){
			NotRundepartinTest = false;
		//	System.out.println("DepartingTrainTest");
			showDepartingTrainsInStation((float) 32335,"18:04:00");
		}
		
		
	}
	private boolean NotRundepartinTest = true;
	
	
	public void draw() {
		background(240);
		
		map.draw();
		textSize(20);
		text(floatToTime(time), 50, 50);
		fill(0, 102, 153);
		
	}
	
	
	
	//real time (increases every space)
	private float time = -1;
	//index of list of trains
	private int index = 0; 
	
	public void keyPressed() {
		if (key == ' ') {
			initMarker();
			// initTrain();
		}
	}
	
	
	//get all trains from day from index 
	private List<Train> getTrainsTimeIndex(){
		int endIndex = index;
		while(allTrainsForDay.get(endIndex).time==time){
			endIndex++;
		}
		
		List<Train> result = allTrainsForDay.subList(index, endIndex);
		index = endIndex;
		return result;		
	}
	
	//create markers for every train
	private void initMarker() {
		map.getDefaultMarkerManager().clearMarkers();
		
		if(time<0){
			time = allTrainsForDay.get(0).time;
		}
		
		
		//textSize(32);
		//text("word", 10, 30); 
		
		List<Train> trains = this.getTrainsTimeIndex();
		
		for(Train train: trains){
			Location location = new Location(train.coordX, train.coordY);
			float stopId = train.stop_id;
			LabeledMarker marker = new LabeledMarker(location,
					"test", font, 6);
			if(stopId>=0){
				marker.setColor(color(255, 0, 0, 100));
			}else{
		
				marker.setColor(color(0, 0, 255, 100));
			}
			map.addMarker(marker);
		}
		
		
		for(Player play: players){
			if(!play.trains.isEmpty()){
				Train train = play.trains.get(0);
				if(train.time==this.time){
					play.trains.remove(0);
				}
				Location location = new Location(train.coordX, train.coordY);
				float stopId = train.stop_id;
				LabeledMarker marker = new LabeledMarker(location, "player", font, 10);
				if(stopId>=0){
					marker.setColor(color(0, 255, 255, 100));
				}else{
			
					marker.setColor(color(0, 255, 255, 50));
				}
				map.addMarker(marker);
			}else{
				//station
				Float[] coord = this.getCoord(play.stop_id);
				Location location = new Location(coord[0],coord[1]);
				LabeledMarker marker = new LabeledMarker(location, "player", font, 10);
				marker.setColor(color(0, 255, 255, 100));
				map.addMarker(marker);
			}
		}
		
		
		if(!player.trains.isEmpty()){
			Train train = player.trains.get(0);
			if(train.time==this.time){
				player.trains.remove(0);
			}
			Location location = new Location(train.coordX, train.coordY);
			float stopId = train.stop_id;
			LabeledMarker marker = new LabeledMarker(location, "player", font, 10);
			if(stopId>=0){
				marker.setColor(color(0, 255, 0, 100));
			}else{
		
				marker.setColor(color(0, 255, 0, 50));
			}
			map.addMarker(marker);
		}else{
			//station
			Float[] coord = this.getCoord(player.stop_id);
			Location location = new Location(coord[0],coord[1]);
			LabeledMarker marker = new LabeledMarker(location, "player", font, 10);
			marker.setColor(color(0, 255, 0, 100));
			map.addMarker(marker);
		}
		
		//TODO: player
		time++;
		
	}
	private void loadData(){
		this.stops = DataParser.getStops("nmbs/stops.txt");
		this.dates = DataParser.gatDates("nmbs/calendar_dates.txt");
		this.routes = DataParser.getRoutes("nmbs/routes.txt");
		this.stoptimes = DataParser.getStopTimes("nmbs/stop_times.txt");
		this.stoptimesByStopId = DataParser.getStopTimesByStopId("nmbs/stop_times_stopId.txt");
		this.trips = DataParser.getTrips("nmbs/trips.txt");
		
//		this.stops = DataParser.getStops("./data/nmbs/stops.txt");
//		this.dates = DataParser.gatDates("./data/nmbs/calendar_dates.txt");
//		this.routes = DataParser.getRoutes("./data/nmbs/routes.txt");
//		this.stoptimes = DataParser.getStopTimes("./data/nmbs/stop_times.txt");
//		this.stoptimesByStopId = DataParser.getStopTimesByStopId("./data/nmbs/stop_times_stopId.txt");
//		this.trips = DataParser.getTrips("./data/nmbs/trips.txt");
		
	}
	
	//show all stops in table (using JTable) 
	private void showStops(){
		Vector rowData = new Vector();
		Collection<DataStop> st = stops.values();
	    for (DataStop stop : st) {
	      Vector colData = new Vector(Arrays.asList(stop.getArray()));
	      rowData.add(colData);
	    }
	    
	    String[] columnNames = {"stop_id", "stop_name", "stop_lat", "stop_lon", "stop_code"};
	    
	    Vector columnNamesV = new Vector(Arrays.asList(columnNames));
	    final JTable table = new JTable(rowData, columnNamesV);
	    
	    JDialog f = new JDialog();
	    f.setSize(300, 300);
	    f.add(new JScrollPane(table));
	    f.setVisible(true);
	    
	    table.setRowSelectionAllowed(true);
	    
	    ListSelectionModel rowSelection = table.getSelectionModel();
	    rowSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	    
	    
	    rowSelection.addListSelectionListener(new ListSelectionListener() {
	        public void valueChanged(ListSelectionEvent e) {
	          String selectedData = null;
	          
	          int row = table.getSelectedRow();

	          String stop_ID = (String) table.getModel().getValueAt(row, 0);
	          float stop_Id = Float.parseFloat(stop_ID);
	          System.out.println("Selected: " + stop_Id);
	        }

	      });
	   
	}
	
	
	private List<Float> getSeviceIdForDay(float day){
		return this.dates.get(day);
	}
	
	private Float timeToFloat(String time) {
		Float hours = Float.parseFloat(time.substring(0, 2));
		Float minutes = Float.parseFloat(time.substring(3, 5));
		Float result = hours * 60 + minutes;
		return result;
	}
	
	private String floatToTime(Float time) {
		System.out.println("time: "+String.valueOf(time));
		
		//float h = time/60;
		
		Integer hoursI = (int) Math.floor(time/60);
		
		String hoursS = String.valueOf(hoursI);
		System.out.println("uur: "+hoursS);
		String minutesS = String.valueOf(time - hoursI*60);
		System.out.println("minutes: "+minutesS);
		String result = hoursS +":"+ minutesS;
		
		return result;
	}
	
	private Float[] getCoord(float stop_id) {
		DataStop stop = stops.get(stop_id);
		return new Float[] {stop.stop_lat, stop.stop_lon};
	}
	
	private List<DataTrips> getTripIdForServiceIds(List<Float> serviceIds){
		List<DataTrips> result = new ArrayList<DataTrips>();
		for(DataTrips trip : trips){
			if(serviceIds.contains(trip.service_id)){
				result.add(trip);
			}
		}
		return result;
	}
	
	private List<Train> getTrainsForDay(float date){
		List<Float> serviceIds = this.getSeviceIdForDay(date);
		List<DataTrips> tripIds = this.getTripIdForServiceIds(serviceIds);
		
		List<Train> result = new ArrayList<Test.Train>();
		
		for(DataTrips trip: tripIds){
			Train train = new Train(date, trip.service_id, trip.route_id, trip.trip_id, trip.trip_short_name, trip.trip_headsign);
			result.addAll(getTrainsforTripId(trip.trip_id, train));
		}
		Collections.sort(result);
		return result;
 	}
	
	private List<DataStopTime> getStopTimesBetweenStations(String trip_id, float stop_idFirst, float stop_idSecond){
		List<DataStopTime> stoptimes = this.stoptimes.get(trip_id);
		if(stop_idFirst==-1){
			return stoptimes;
		}
		int i1 = 0;
		int i2 = 0;
		for(DataStopTime stopTime: stoptimes){
			if(stopTime.stop_id.equals(stop_idFirst)){
				i1 = stoptimes.indexOf(stopTime);
			} else if(stopTime.stop_id.equals(stop_idSecond)){
				i2 = stoptimes.indexOf(stopTime);
				break;
			}
		}
		return stoptimes.subList(i1, i2+1);
	}
	
	private List<Train> getTrainsforTripId(String trip_id, Train train){
		return this.getTrainsforTripId(trip_id, train, -1, -1);
	}
	
	private List<Train> getTrainsforTripId(String trip_id, Train train, float stop_idFirst, float stop_idSecond){
		List<DataStopTime> stoptimes = this.getStopTimesBetweenStations(trip_id, stop_idFirst, stop_idSecond);
		
		List<Train> result = new ArrayList<Test.Train>();
		
		Train lastTrain = null;
		for (DataStopTime stop : stoptimes) {
			
			Train newTrain = train.addcoordTime(stop.stop_id, stops.get(stop.stop_id).stop_lat, stops.get(stop.stop_id).stop_lon, this.timeToFloat(stop.arrival_time));
			if(lastTrain!=null){
				result.addAll(this.getTrainsBetween(lastTrain, newTrain));
			}
			List<Train> trains = new ArrayList<Test.Train>();
			trains.add(newTrain);
			result.addAll(this.getSingleStop(
					trains,
					this.timeToFloat(stop.departure_time))); 
			lastTrain = result.get(result.size()-1);
		}	
		return result;
	}
	
	private List<Train> getTrainsBetween(Train lastTrain, Train newTrain) {
		List<Train> result = new ArrayList<Test.Train>();
		Train last = lastTrain;
		float i = newTrain.time - lastTrain.time;
		float coordX = (newTrain.coordX - lastTrain.coordX)/i;
		float coordY = (newTrain.coordY - lastTrain.coordY)/i;
		while(i>1){
			Train train = last.addCoordIncreaseTime(coordX, coordY);
			result.add(train);
			i--;
			last = train;
		}
		return result;
	}
	private List<Train> getSingleStop(List<Train> trains, Float departure) {
		Train last = trains.get(trains.size()-1);
		while (last.time < departure) {
			Train train = last.addTime();
			trains.add(train);
			last = train;
		}
		return trains;
	}
	
	private DateFormat hourFormat = new SimpleDateFormat( "HH:mm:ss");
	
	private List<DataStopTime> getStopTimesFromStation(Float stop_id, String timenow) throws ParseException{
		List<DataStopTime> stoptimesbyStopId = this.stoptimesByStopId.get(stop_id);
				
		List<DataStopTime> result = new ArrayList<DataStopTime>();
		
		
		Float timeNowFloat = timeToFloat(timenow);
		Float timePlusWindowFloat = timeNowFloat + TIMEWINDOW;
		Float timeMinWindowFloat = timeNowFloat - TIMEWINDOW;
		//String timePlusWindow = floatToTime(timePlusWindowFloat);
		
		
		HashSet<String> tripIDs = new HashSet<String>();
		for (DataStopTime stop : stoptimesbyStopId) {
			if ( timeToFloat(stop.departure_time) <= timePlusWindowFloat && timeMinWindowFloat <= timeToFloat(stop.departure_time))
				result.add(stop);
		}		
		return result;
	}
	
	private List<DataStopTime> getStopTimesFromDeparingTrain(DataStopTime departingTrain){
		List<DataStopTime> result = new ArrayList<DataStopTime>();
		for(DataStopTime stopTime : this.stoptimes.get(departingTrain.trip_id) ){
			if(stopTime.stop_sequence >= departingTrain.stop_sequence)
				result.add(stopTime);
		}
		return result;
	}

		
	
	
	// alle trains vanaf NU (time) tot time X vanuit station stop_id
	// set van alle trip_id (geen dubbelen) vanaf bepaalde tijd (zoeken in lijst van alle trains vanaf index)
	// trip_id via stop_times vertrektijd en lijst met alle stopplaatsen + tijd
	

	
	
	//show all stops in table (using JTable) 
			private void showDepartingTrainsInStation(Float stop_id, String timenow){
				//final String[] resultingSelection = {null};
				//resultingSelection[0]=null;

				try{
					List<DataStopTime> departingTrains = getStopTimesFromStation(stop_id, timenow);
					
					
					String stopName = this.stops.get(stop_id).stop_name;
					
					Vector rowData = new Vector();
					for (DataStopTime stop : departingTrains) {
						//String tripId = stop.trip_id;
						String tripname = "HeadSign not available";
						for(DataTrips trip : this.trips){
							if(trip.trip_id.equals(stop.trip_id)){
								tripname = trip.trip_headsign;
								break;
							}
						}
					      Vector colData = new Vector( Arrays.asList(stop.arrival_time, stop.departure_time, tripname, stop.trip_id));
					      //Vector colData = new Vector( Arrays.asList(stop.trip_id, this.stops.get(stop.stop_id).stop_name,stop.arrival_time,stop.departure_time,stop.stop_sequence.toString()));
					      rowData.add(colData);
				    }
					
				System.out.println("Setting up departingTable");
				//Setup table
			    String[] columnNames = {"Arrival Time", "Departure Time", "Name route", "trip ID"};
			    
			    Vector columnNamesV = new Vector(Arrays.asList(columnNames));
			    final JTable table = new JTable(rowData, columnNamesV);
			    
			    JDialog fdeparting = new JDialog();
			    fdeparting.setTitle("Current Station: "+ stopName);
			    //f.setTitle("Departing Trains from " + this.stops.get(stop_id) );
			    fdeparting.setSize(600,600);
			    JScrollPane scrollPane = new JScrollPane(table); 
			    fdeparting.add(scrollPane);
			    fdeparting.setVisible(true);

			    
			    table.setRowSelectionAllowed(true);
				System.out.println("done departingtapble");


			    ListSelectionModel rowSelection = table.getSelectionModel();
			    rowSelection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	
			    
			    System.out.println("selected mode");
					
			    rowSelection.addListSelectionListener(new ListSelectionListener() {
			        public void valueChanged(ListSelectionEvent e) {	          
			          int row = table.getSelectedRow();

			          String trip_id = (String) table.getModel().getValueAt(row, 3);
			          System.out.println("Selected: " + trip_id);
			          //resultingSelection[0] = trip_id;
			          //System.out.println("Selected array: " + resultingSelection[0]);
			        }

			      });
			    


			   
			    System.out.println("going in while loop");
		          String nullStr = null;
					//while(resultingSelection[0].equals(nullStr)){
		          System.out.println(table.getSelectedRow());
					while(table.getSelectedRow() == -1){
				          try {
				        	  System.out.println(table.getSelectedRow());
							Thread.sleep(500);
				          	} catch (InterruptedException e1) {
				          			// TODO Auto-generated catch block
				          			e1.printStackTrace();
				          	}
					}
			    //System.out.println("After Selected array: " + resultingSelection[0]);

				
			    System.out.println("coming out while loop");

				
		          int row = table.getSelectedRow();
			    String trip_id = (String) table.getModel().getValueAt(row, 3);
			    
			   List< DataStopTime> trainList = this.stoptimes.get(trip_id);
			   DataStopTime train = null;
			   for(DataStopTime i : trainList){
					if(i.stop_id.equals(stop_id)){
						train = i;
						break;
					}
				}

				fdeparting.dispose();			    
		
			   List<DataStopTime> listNextTrains = getStopTimesFromDeparingTrain(train);
			    System.out.println("Going for routeTrains: " + listNextTrains.toString());
			    showRestingTrainsInRoute(listNextTrains, departingRouteName);
			   			    
			    
				}catch (ParseException e){
					e.printStackTrace();
				}
			}
			
			private Float chosenDepartingTrain;
			private void setChosenDepartingTrain(Float chosenDepartingTrain){
				this.chosenDepartingTrain = chosenDepartingTrain;
			}
			private Float getChosenDepartingTrain(){
				return chosenDepartingTrain;
			}
			private String departingRouteName;
			private void setDepartingRouteName(String departingRouteName){
				this.departingRouteName = departingRouteName;
			}
			private String getDepartingRouteName(){
				return departingRouteName;
			}
			
			
			

			
			private void showRestingTrainsInRoute(List<DataStopTime> stopList, String routeName){
				Vector rowData = new Vector();
				for (DataStopTime stop : stopList) {
					//String tripId = stop.trip_id;
				      Vector colData = new Vector( Arrays.asList(stop.trip_id, this.stops.get(stop.stop_id).stop_name,stop.arrival_time,stop.departure_time,stop.stop_sequence.toString()));
				      rowData.add(colData);
			    }
				
				//Setup table
			    String[] columnNames = {"Trip ID", "Stop Name", "Arrival Time", "Departure Time", "Stop Sequence"};
			    
			    Vector columnNamesV = new Vector(Arrays.asList(columnNames));
			    final JTable table = new JTable(rowData, columnNamesV);
			    
			    JDialog fRestingInRoute = new JDialog();
			    fRestingInRoute.setTitle("Current Route: "+ routeName);
			    //f.setTitle("Departing Trains from " + this.stops.get(stop_id) );
			    fRestingInRoute.setSize(600,600);
			    fRestingInRoute.add(new JScrollPane(table));
			    fRestingInRoute.setVisible(true);
			    
			    
			    table.setRowSelectionAllowed(true);
				
			}
	
	
	
	private class Train implements Comparable<Train>{
		
		public Train(float date, float service_id, float route_id, String trip_id, float trip_short_name,  String trip_headsign){
			this.date=date;
			this.service_id= service_id;
			this.route_id= route_id;
			this.trip_id= trip_id;
			this.trip_short_name = trip_short_name;
			this.trip_headsign= trip_headsign;
		}
		
		public Train addCoordIncreaseTime(float coordX, float coordY) {
			return new Train(date, service_id, route_id, trip_id, trip_short_name, trip_headsign, -1, this.coordX + coordX, this.coordY + coordY, this.time+1);
		}
		public Train(float date, float service_id, float route_id, String trip_id, float trip_short_name, String trip_headsign, float stop_id, float coordX, float coordY, float time){
			this.date=date;
			this.service_id= service_id;
			this.route_id= route_id;
			this.trip_id= trip_id;
			this.trip_short_name = trip_short_name;
			this.stop_id = stop_id;
			this.coordX= coordX;
			this.coordY= coordY;
			this.time= time;
			
		}
		
		public Train addcoordTime(float stop_id, float coordX, float coordY, float time){
			return new Train(date, service_id, route_id, trip_id, trip_short_name, trip_headsign, stop_id, coordX, coordY, time);
		}
		
		public Train addTime(){
			return new Train(date, service_id, route_id, trip_id, trip_short_name, trip_headsign, stop_id, coordX, coordY, this.time+1);
		}
		
		 float date;
		 float service_id;
		 float route_id;
		 String trip_id;
		 float trip_short_name;
		 String trip_headsign;
		 float stop_id = -1;
		 float coordX;
		 float coordY;
		 float time;
		 
		@Override
		public String toString(){
			return "date: "+String.valueOf(date)+ ", service_id: "+String.valueOf(service_id)+ ", route_id: "+String.valueOf(route_id)+ ", trip_id: "+(trip_id)+ ", trip_short_name: "+String.valueOf(trip_short_name)+ ", trip_headsign: "+(trip_headsign)+ ", stop_id: "+String.valueOf(stop_id)+  ", coordX: "+String.valueOf(coordX)+ ", coordY: "+String.valueOf(coordY)+ ", time: "+String.valueOf(time);
		}
		 
		@Override
		public int compareTo(Train train) {
			return (int) -(train.time - this.time);
		}
		 
	}
	
private class Player{
		
		//coord van station waar hij zal afstappen!!!
		private float coordX;
		private float coordY;
		// -1 if not at stop;
		private float stop_id;
		
		public Player(float coordX, float coordY, float stop_id){
			this.setCoord(coordX, coordY, stop_id);
		}
		
		public void setCoord(float coordX, float coordY, float stop_id){
			this.coordX=coordX;
			this.coordY=coordY;
			this.stop_id=stop_id;
		}
		
		private List<Train> trains = new ArrayList<Test.Train>();
		
		private boolean canGetOnTrain(Train train){
			return this.stop_id==train.stop_id && time<train.time;
		}
		
		public void getOn(String trip_id, float Start_id, float stop_id){
			//TODO informatie van trein juist invullen
			Train train = new Train(20131215, 0, 0, trip_id, 0, "0");
			//alle vorige treinen gaan verloren, pas opstappen op andere trein als je bent afgestapt
			this.trains = getTrainsforTripId(trip_id, train, Start_id, stop_id);
			
			this.stop_id=stop_id;
		}
	}
	
	
	
	
	
}
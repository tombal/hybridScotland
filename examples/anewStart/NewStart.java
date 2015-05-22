package anewStart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;
import anewStart.DataParser.DataRoute;
import anewStart.DataParser.DataStop;
import anewStart.DataParser.DataStopTime;
import anewStart.DataParser.DataTrips;



public class NewStart{
	
	
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
		
		
		
		public NewStart(){
			this.loadData();
			
			ShowTable t = new ShowTable(stoptimesByStopId, trips, stops, stoptimes);
			t.showDepartingTrainsInStation((float) 32335,"18:04:00");
			//this.initPlayers();
			
			//new Mapp(allTrainsForDay, stops);
			
			 //create an instance of your processing applet
            //final Mapp applet = new Mapp(allTrainsForDay, stops, players);

            //start the applet
            //System.out.println("applet");
			//Mapp applet = new Mapp();
            //applet.setup();
            
            
			
		}
		
		public void initPlayers(){
			players = new ArrayList<Player>();
			//System.out.println("staaaaa");
			for(int i=0; i<3;i++){
				//System.out.println("iiiii");
				float id = AskStation.getStation(("select a station for player "+(i+1)), stops);
				//float id = this.askStation("select a station for player "+(i+1));
				Float[] coord = this.getCoord(id);
				Player player = new Player(coord[0], coord[1], id);
				players.add(player);
			}

		}
		
		private Float[] getCoord(float stop_id) {
			DataStop stop = stops.get(stop_id);
			return new Float[] {stop.stop_lat, stop.stop_lon};
		}
		
	
		private void loadData(){
			this.stops = DataParser.getStops("./data/nmbs/stops.txt");
			this.dates = DataParser.gatDates("./data/nmbs/calendar_dates.txt");
			this.routes = DataParser.getRoutes("./data/nmbs/routes.txt");
			this.stoptimes = DataParser.getStopTimes("./data/nmbs/stop_times.txt");
			this.stoptimesByStopId = DataParser.getStopTimesByStopId("./data/nmbs/stop_times_stopId.txt");
			this.trips = DataParser.getTrips("./data/nmbs/trips.txt");
			
//			this.stops = DataParser.getStops("nmbs/stops.txt");
//			this.dates = DataParser.gatDates("nmbs/calendar_dates.txt");
//			this.routes = DataParser.getRoutes("nmbs/routes.txt");
//			this.stoptimes = DataParser.getStopTimes("nmbs/stop_times.txt");
//			this.stoptimesByStopId = DataParser.getStopTimesByStopId("nmbs/stop_times_stopId.txt");
//			this.trips = DataParser.getTrips("nmbs/trips.txt");
			
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
		
		
		private List<Train> getTrainsBetween(Train lastTrain, Train newTrain) {
			List<Train> result = new ArrayList<Train>();
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
		
		private List<Train> getTrainsforTripId(String trip_id, Train train){
			return this.getTrainsforTripId(trip_id, train, -1, -1);
		}
		
		private Float timeToFloat(String time) {
			Float hours = Float.parseFloat(time.substring(0, 2));
			Float minutes = Float.parseFloat(time.substring(3, 5));
			Float result = hours * 60 + minutes;
			return result;
		}
		
		//TODO deze methode oproepen om trains voor player te creeren
		
		private List<Train> getTrainsforTripId(String trip_id, Train train, float stop_idFirst, float stop_idSecond){
			List<DataStopTime> stoptimes = this.getStopTimesBetweenStations(trip_id, stop_idFirst, stop_idSecond);
			
			List<Train> result = new ArrayList<Train>();
			
			Train lastTrain = null;
			for (DataStopTime stop : stoptimes) {
				
				Train newTrain = train.addcoordTime(stop.stop_id, stops.get(stop.stop_id).stop_lat, stops.get(stop.stop_id).stop_lon, this.timeToFloat(stop.arrival_time));
				if(lastTrain!=null){
					result.addAll(this.getTrainsBetween(lastTrain, newTrain));
				}
				List<Train> trains = new ArrayList<Train>();
				trains.add(newTrain);
				result.addAll(this.getSingleStop(
						trains,
						this.timeToFloat(stop.departure_time))); 
				lastTrain = result.get(result.size()-1);
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
		
		
		
		
		
		


}



package anewStart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
			this.initPlayers();
			
		}
		
		public void initPlayers(){
			players = new ArrayList<Player>();
			System.out.println("staaaaa");
			for(int i=0; i<3;i++){
				System.out.println("iiiii");
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
			
			private List<Train> trains = new ArrayList<Train>();
			
			
			public void getOn(String trip_id, float Start_id, float stop_id){
				//TODO informatie van trein juist invullen
				Train train = new Train(20131215, 0, 0, trip_id, 0, "0");
				//alle vorige treinen gaan verloren, pas opstappen op andere trein als je bent afgestapt
				this.trains = getTrainsforTripId(trip_id, train, Start_id, stop_id);
				
				this.stop_id=stop_id;
			}
		}
		

}



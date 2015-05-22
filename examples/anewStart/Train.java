package anewStart;


public class Train implements Comparable<Train>{
	
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
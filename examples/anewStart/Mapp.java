package anewStart;

import java.util.List;
import java.util.Map;

import anewStart.DataParser.DataStop;
import processing.core.PApplet;
import processing.core.PFont;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.examples.marker.labelmarker.LabeledMarker;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class Mapp extends PApplet{
	
	private static final long serialVersionUID = -6393266518120392076L;
	UnfoldingMap map;
	PFont font = loadFont("ui/OpenSans-18.vlw");
	
	//real time (increases every space)
	private float time = -1;
	//index of list of trains
	private int index = 0; 
	
	List<Train> allTrainsForDay;
	Map<Float,DataStop> stops;
	List<Player> players;
	
	public Mapp(){
		
	}
//	
//	public Mapp(List<Train> allTrainsForDay,Map<Float,DataStop> stops,List<Player> players){
//		this.allTrainsForDay=allTrainsForDay;
//		this.stops=stops;
//		this.players=players;
//		System.out.println("construc");
//		this.setup();
//		System.out.println("init");
//	}
	
	

	public void setup() {
		
//		NewStart n = new NewStart();
//		
//		this.allTrainsForDay=n.allTrainsForDay;
//		this.stops=n.stops;
//		this.players=n.players;
//		
		System.out.println("mapp setup1");

		this.size(800, 600, OPENGL);
		
		
		System.out.println("mapp setup");
		map = new UnfoldingMap(this, "map", 52f, 4f, 750, 650);
		map.zoomToLevel(8);
		
		MapUtils.createDefaultEventDispatcher(this, map);
		
		Location belgiumLocation = new Location(50.833333f, 4f);
		
		System.out.println("mapp setup");
		
		
		//this.size(800, 600, P2D);
		
		map.panTo(belgiumLocation);

		//initMarker();
	}
	
	public void draw() {
		System.out.println("draw");
		background(240);
		map.draw();
	}
	
	public void keyPressed() {
		if (key == ' ') {
			initMarker();
			// initTrain();
		}
	}
	
	
	private Float[] getCoord(float stop_id) {
		DataStop stop = stops.get(stop_id);
		return new Float[] {stop.stop_lat, stop.stop_lon};
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
			
			
//			if(!player.trains.isEmpty()){
//				Train train = player.trains.get(0);
//				if(train.time==this.time){
//					player.trains.remove(0);
//				}
//				Location location = new Location(train.coordX, train.coordY);
//				float stopId = train.stop_id;
//				LabeledMarker marker = new LabeledMarker(location, "player", font, 10);
//				if(stopId>=0){
//					marker.setColor(color(0, 255, 0, 100));
//				}else{
//			
//					marker.setColor(color(0, 255, 0, 50));
//				}
//				map.addMarker(marker);
//			}else{
//				//station
//				Float[] coord = this.getCoord(player.stop_id);
//				Location location = new Location(coord[0],coord[1]);
//				LabeledMarker marker = new LabeledMarker(location, "player", font, 10);
//				marker.setColor(color(0, 255, 0, 100));
//				map.addMarker(marker);
//			}
//			
			//TODO: player
			time++;
			
		}
	
	
	
	

}

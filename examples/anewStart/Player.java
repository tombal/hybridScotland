package anewStart;

import java.util.ArrayList;
import java.util.List;


public class Player{
	
	//coord van station waar hij zal afstappen!!!
	 float coordX;
	 float coordY;
	// -1 if not at stop;
	 float stop_id;
	
	public Player(float coordX, float coordY, float stop_id){
		this.setCoord(coordX, coordY, stop_id);
	}
	
	public void setCoord(float coordX, float coordY, float stop_id){
		this.coordX=coordX;
		this.coordY=coordY;
		this.stop_id=stop_id;
	}
	
	List<Train> trains = new ArrayList<Train>();
	
	public void getOnTrains(List<Train> trains){
		this.trains=trains;
	}
	
}
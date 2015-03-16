import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import org.json.XML;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoDataReader;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.examples.data.countrydata.CountryBubbleMapApp.DataEntry;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PApplet;


public class SimpleMapApp extends PApplet {
	
	
	UnfoldingMap map;
	 
    public void setup() {
        size(800, 600);
        map = new UnfoldingMap(this);
        try {
			HashMap<String, DataEntry> data = loadStops("nmbs/stops.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
 
    public void draw() {
        map.draw();
    }
    
    public HashMap<String, DataEntry> loadStops(String fileName) throws FileNotFoundException {
		HashMap<String, DataEntry> dataEntriesMap = new HashMap<String, DataEntry>();
		
		File input = new File(fileName);
        Scanner sc = new Scanner(input);    //Reads from input
        
        sc.nextLine();
        
        while(sc.hasNextLine()){
        	String line = sc.nextLine();
        	
        	List<String> lineList = Arrays.asList(line.split(","));
        	
        	DataEntry dataEntry = new DataEntry();
        	dataEntry.stop_id = Float.parseFloat(lineList.get(0));
        	dataEntry.stop_name = lineList.get(1);
        	dataEntry.stop_lat = Float.parseFloat(lineList.get(2));
        	dataEntry.stop_lon = Float.parseFloat(lineList.get(3));
        	dataEntry.stop_code = Float.parseFloat(lineList.get(4));
        	
        	dataEntriesMap.put(dataEntry.stop_name, dataEntry);
        }

		return dataEntriesMap;
	}

	public class DataEntry {
		Float stop_id;
		String stop_name;
		Float stop_lat;
		Float stop_lon;
		Float stop_code;
	}

    
    

}

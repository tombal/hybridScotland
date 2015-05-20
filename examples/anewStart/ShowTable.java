package anewStart;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import anewStart.DataParser.*;
import anewStart.DataParser.DataStopTime;
import anewStart.DataParser.DataTrips;


public class ShowTable {

	
	Map<Float, List<DataStopTime>> stoptimesByStopId;
	
	Map<Float,DataStop> stops;
	
	List<DataTrips> trips;
	
	Map<String, List<DataStopTime>> stoptimes;
	
	public ShowTable(Map<Float, List<DataStopTime>> stoptimesByStopId, List<DataTrips> trips, Map<Float,DataStop> stops, Map<String, List<DataStopTime>> stoptimes){
		this.stoptimesByStopId = stoptimesByStopId;
		this.stoptimes = stoptimes;
		this.stops=stops;
		this.trips = trips;
	}
	
	private Float timeToFloat(String time) {
		Float hours = Float.parseFloat(time.substring(0, 2));
		Float minutes = Float.parseFloat(time.substring(3, 5));
		Float result = hours * 60 + minutes;
		return result;
	}
	
	//Settings
		private static final int TIMEWINDOW = 10;

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
			public void showDepartingTrainsInStation(Float stop_id, String timenow){
				final String[] resultingSelection = {null};
				resultingSelection[0]=null;

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
			    fdeparting.add(new JScrollPane(table));
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
			          resultingSelection[0] = trip_id;
			          System.out.println("Selected array: " + resultingSelection[0]);

			        }

			      });
			   
			    System.out.println("going in while loop");
		          System.out.println("After Selected array: " + resultingSelection[0]);
		          String nullStr = null;
		          try {
					Thread.sleep(500);
					while(resultingSelection[0]==nullStr)
							Thread.sleep(500);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				
			    System.out.println("coming out while loop");

				
				fdeparting.dispose();			    
			    
			    String trip_id = resultingSelection[0];
			    
			   List< DataStopTime> trainList = this.stoptimes.get(trip_id);
			   DataStopTime train = null;
			   for(DataStopTime i : trainList){
					if(i.stop_id.equals(stop_id)){
						train = i;
						break;
					}
				}

			    		
			    
			   List<DataStopTime> listNextTrains = getStopTimesFromDeparingTrain(train);
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
	
}

package anewStart;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import processing.core.PApplet;
import anewStart.DataParser.DataStop;

public class AskStation {
	
	public static float getStation(String title, Map<Float,DataStop> stops){
		
		System.out.println("start");
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
	
	
	
	

}

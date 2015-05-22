package aTryout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleReader {
	

	public static void main(String[] args){
		
		while(true){
			System.out.println("User : ");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try{
			    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
			    String s = bufferRead.readLine();
			    
		 
			    System.out.println(s);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		 
		  }
		}
}
	
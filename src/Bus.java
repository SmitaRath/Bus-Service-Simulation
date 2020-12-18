// This class is to store the bus details
public class Bus {
  
	//this will store the bus no
	int busNo;
	//it will store the stop at which bus arrives or leaving
	int lastStop;
	//it will store the next bus stop where bus will reach
	int nextStop;
	//it will store the arrival time of the last stop where the bus was arrived
	double lastStopArrivalTime;
	//Bus at Stop - Y, Bus not at stop - N
	char busAtStop; 
	//Bus in sync - Y, Bus not in sync - N added to keep distance between the busses uniform
	char allBusesSync; 
	
	
	//constructor for initialization, we will initialize in BusSimulation constructor
	public Bus(int busNo, int lastStop, int nextStop, double lastStopArrivalTime) {
		super();
		this.busNo = busNo;
		this.lastStop = lastStop;
		this.nextStop = nextStop;
		this.lastStopArrivalTime = lastStopArrivalTime;
		this.busAtStop = 'N';
		this.allBusesSync='Y';
	}
	
	//overrding toString() method to print in log file
	@Override
	public String toString() {	
		return String.format(" Bus number " + busNo + "********************" + "%n"
				+ " Last Stop              = " + (lastStop+1) + "%n"
				+ " Next Stop              = " + (nextStop+1) + "%n" +
				  " Last Stop Arrival time = " + lastStopArrivalTime + "%n" 
				+ " Bus At Stop            = " + busAtStop
				  );
		
	} 
	
}


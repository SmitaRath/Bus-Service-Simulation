//this class is to store the bus stop details
public class BusStop {

	//this will store the information for n no of stops: 0 - 1st stop, n-1 for last stop
	int stopNo;
	//it will calculate the total number of people for every hour to calculate the average
	int totalPeople;
	//it will store the minimum number of people
	int minimumPeople;
	//it will store the maximum number of people
	int maximumPeople;
	//it will store the number of times bus arrived at stop to calculate the average
	int noOfTimesBusArrives;
	//it will store the average no of people
	int avgNoOfPeople;
	
	//constructor for initialization, we will initialize in BusSimulation constructor
	public BusStop(int stopNo, int totalPeople, int minimumPeople, int maximumPeople, int noOfTimesBusArrives) {
		this.stopNo = stopNo;
		this.totalPeople = totalPeople;
		this.minimumPeople = minimumPeople;
		this.maximumPeople = maximumPeople;
		this.noOfTimesBusArrives = noOfTimesBusArrives;
		this.avgNoOfPeople = 0;
	}  
	
	//calculating average
	public void calulateAverage() {

		//if bus arrived at stop
		if (noOfTimesBusArrives!=0)
		this.avgNoOfPeople = totalPeople/noOfTimesBusArrives;
		//if bus did not arrive at stop
		else {
		this.avgNoOfPeople=(maximumPeople+minimumPeople)/2;
		}
		
	}

	//overrding toString() method to print in log file
	@Override
	public String toString() {
		return String.format(" Stop No " + stopNo + "********************" + "%n"
				+ " Min People= " + minimumPeople + "%n"
				+  " Max People= " + maximumPeople + "%n" +
				" Avg No Of People= " + avgNoOfPeople);
	}
}

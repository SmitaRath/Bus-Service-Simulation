
public class Main {

		public static void main(String args[]) {
			int i=0;
		
			/*
			 * all time is in seconds
			 * No Of Stops =  15
			 * No Of Busses = 5 
			 * Time between different stops = 300
			 * Boarding time = 2
			 * Simulation time = 8 hours = 3600*8 seconds
			 * Mean Arrival Rate = 5 person/minute
			 * InterArrival rate = 12 seconds
			 */
			
			BusSimulation b1 = new BusSimulation(15, 5, 300, 2, 3600*8 ,5);
			b1.busSimulationMethod();
			
	}
}

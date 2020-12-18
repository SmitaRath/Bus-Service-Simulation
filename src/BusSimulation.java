
import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.io.IOException;
import java.io.PrintStream;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;




public class BusSimulation {

	//ArrayList for storing all the event details as mentioned in event details class
	private ArrayList<EventDetails> eventDetails = new ArrayList<>();
	//ArrayList for storing bus data(busno, laststop, nextstop) as mentioned in Bus class)
	private ArrayList<Bus> busRecord = new ArrayList<>();
	//ArrayList for storing stop data(stopno, min people, max people) as mentioned in BusStop class)
	private ArrayList<BusStop> stopRecord = new ArrayList<>();
	//variable for the no of stops which user will provide
	private int noOfStops;
	//variable for the no of busses which user will provide
	private int noOfBusses;
	//variable for the time interval between stops which user will provide
	private double timeIntervalbetweenStops;
	//variable for the boarding time for the passenger which user will provide
	private double boardingTime;
	//total simulation time
	private double totalTime;
	//array for the no of stops which will store the no of people in each queue, 0 index for 1st stop
	private int[] busStopQueue;
	private double arrivalRate;
	private double interArrivalRate;

	//constructor for the initialization of the all events,stops,queues

	public BusSimulation(int noOfStops, int noOfBusses, double timeIntervalbetweenStops, double boardingTime,
			double totalTime,double arrivalRate) {
		super();
		this.noOfStops = noOfStops;
		this.noOfBusses = noOfBusses;
		this.timeIntervalbetweenStops = timeIntervalbetweenStops;
		this.boardingTime = boardingTime;
		this.totalTime = totalTime;
		this.interArrivalRate = (1/arrivalRate)*60;
		//initializing the no of stops, creating an array equal to size of no of stops.
		busStopQueue = new int[noOfStops];
		//at the beginning no one is at the stop queues so initializing with zero
		Arrays.fill(busStopQueue, 0);

		int no;
		//Generating Arrival events for all the busses at time zero by uniformly distributing them along the route
		//0 is for 1st stop
		for (int i = 0; i < noOfBusses; i++) {
			no = noOfStops / noOfBusses;
			eventDetails.add(new EventDetails(0, "Arrival", i + 1, i * no));
			//initializing busRecord for all the busses, 99999 refers to invalid stop at present
			busRecord.add(new Bus(i + 1, 99999, 99999, 0));
		}

		//Generating Person event for each stop at time zero
		for (int i = 0; i < noOfStops; i++) {
			eventDetails.add(new EventDetails(0, "Person", 99999, i));
			//initializing stop record for all the stops.
			stopRecord.add(new BusStop(i, 0, Integer.MAX_VALUE, Integer.MIN_VALUE, 0));
		}
		//Sorting the ArrayList for the events according to time.
		Collections.sort(eventDetails);
	}

	//Method for exponentially distribution number generation
	public double randomGeneration()
	{
		return (-interArrivalRate * (Math.log(((Math.random()*65536 + 1)/65536))));

	}

	//Method for simulation
	public void busSimulationMethod() {
		try {
			//Variable for the Event Type
			String eventTypeRec;
			//Variable for exponentially distribution time
			double newExpTime;
			//Variable for loop counter for the eventDetails
			int count = 0;
			//Variable for current clock time
			double clock;
			int busNo;
			int stopNo;
			int nextStopNo;
			int noOfPeopleAtStop;
			BusStop oldRecordBusStop;
			Bus oldRecordBus;
			int counter = 1;
			int index;
			//Added this part of code to keep the distance between the busses uniform.--start
			int sum = 0;
			int avgQueue = 0;
			double remainingWaitingTime = 0;
			boolean flag = true;
			double waitingTime = 0;
			double busLeavingTime = 0;
			//Added this part of code to keep the distance between the busses uniform.--end
			int peopleServiced = 0;
			int person = 0;
			int arrival = 0;
			//Added this part for logging output to file and excel report
			PrintStream fileOut = new PrintStream("./out.txt");
			PrintStream originalOut = System.out;	
			BufferedWriter writer = Files.newBufferedWriter(Paths.get("Output_Simulation_File.csv"));
			DecimalFormat df = new DecimalFormat("#.#####");
			df.setRoundingMode(RoundingMode.CEILING);

			//loop for checking the event details

			do {

				//printing to out file
				fileOut.println(eventDetails.get(count).toString());

				//getting the time of the event in the clock variable
				clock = eventDetails.get(count).time;

				//This part of code is for logging the output for every hour --start
				//This part of code is exporting the data to excel file for periodic snapshot.--start
				if (clock >= counter * 3600) {

					//for logging to the out file
					fileOut.println("For the " + counter + ":00:00 hour  Minimum Maximum and Average No Of People at each stop");
					//Logging the busStop record into the excel file
					writer.write("For the " + counter + ":00:00 hour  Minimum Maximum and Average No Of People at each stop");
					writer.newLine();
					writer.newLine();
					for (BusStop rec : stopRecord) {

						rec.calulateAverage();
						writer.write("Stop Number,Average People,Maximum People,Minimum People");
						writer.newLine();
						writer.append(String.valueOf(rec.stopNo));
						writer.append(',');
						writer.append(String.valueOf(rec.avgNoOfPeople));
						writer.append(',');
						writer.append(String.valueOf(rec.maximumPeople));
						writer.append(',');
						writer.append(String.valueOf(rec.minimumPeople));
						writer.newLine();
						System.out.println(rec.toString());
						fileOut.println(rec.toString());
						//resetting the min, avg and max no of people, no of time bus arrives after every hour
						index = rec.stopNo;
						rec.noOfTimesBusArrives = 0;
						rec.totalPeople = 0;
						rec.maximumPeople = busStopQueue[index];
						rec.minimumPeople = busStopQueue[index];
						stopRecord.set(index, rec);
					}

					//for logging to the out file
					fileOut.println("For the " + counter + ":00:00 hour  snapshots of busses at their respective stops");


					writer.write("For the " + counter + ":00:00 hour snapshots of busses at their respective stops");
					writer.newLine();
					writer.newLine();
					writer.write("Bus No,Last Stop,Next Stop,Last Stop Arrival time,Bus At Stop, Bus Beetween Stops");
					writer.newLine();

					for (Bus rec : busRecord) {

						//if there are 15 stops then it is 0 to 14 so adding 1 to the stop for excel snapshot
						writer.append(String.valueOf(rec.busNo));
						writer.append(',');
						writer.append(String.valueOf(rec.lastStop+1));
						writer.append(',');
						writer.append(String.valueOf(rec.nextStop+1));
						writer.append(',');
						writer.append(String.valueOf(rec.lastStopArrivalTime));
						writer.append(',');
						writer.append(String.valueOf(rec.busAtStop));
						writer.append(',');
						//this part of code added to check if busses are between stops so adding .5 to show busses are between two stops
						if (rec.busAtStop=='N') {
							if (rec.lastStop == (noOfStops -1))
								writer.append(String.valueOf(0.5));
							else
								writer.append(String.valueOf(rec.lastStop+1.5));
						}
						writer.newLine();
						fileOut.println(rec.toString());
						System.out.println(rec.toString());
					}
					counter++;
				}
				//This part of code is exporting the data to excel file for periodic snapshot.--end
				//This part of code is for logging the output for every hour --end

				//getting the event type into the variable
				eventTypeRec = eventDetails.get(count).eventType;
				//setting the status of the event to Y as it is in the process to distinguish between other events which are not yet processed
				eventDetails.get(count).setStatus('Y');
				//getting the bus no from record
				busNo = eventDetails.get(count).busNo;
				//getting the stop no from the record
				stopNo = eventDetails.get(count).StopNo;
				//setting the next stop no,if the current stop is last stop then setting the next stop to 0 which is our first stop.
				nextStopNo = (stopNo + 1 > (noOfStops - 1) ? 0 : stopNo + 1);
				//getting the number of people which are in the bus stop queue for that stop
				noOfPeopleAtStop = busStopQueue[eventDetails.get(count).StopNo];

				System.out.println("Event = " + eventTypeRec + " Clock = " + clock + " Stop No= " + stopNo);
				//depend on the event type(Arrival, boarder, Person) the actions will be performed 
				switch (eventTypeRec) {
				//For Arrival event
				case "Arrival":
					//Added this part of code to keep the distance between the busses uniform.--start	
					flag = true;
					//checking for all the busses if they are in sync, if yes calculating the average queue length for all the bus stops
					//and on basis of that all busses will board that passengers, otherwise will wait for tht waiting time
					for (Bus bus : busRecord) {
						if (bus.allBusesSync != 'Y') {
							flag = false;
							break;
						}
					}

					//buses are in sync calculating average length for all the queues
					if (flag == true) {
						//resetting all the variables to zero 
						sum = 0;
						busLeavingTime = 0;
						waitingTime = 0;
						avgQueue = 0;
						remainingWaitingTime = 0;

						//calculating average length

						for (int i = 0; i < busStopQueue.length; i++) {
							sum = sum + busStopQueue[i];
						}
						avgQueue = sum / busStopQueue.length;
						//calculating waiting time for all the busses depend on the avg length of the queue and boarding time
						waitingTime = avgQueue * boardingTime;
						//calculate the time at which all the busses should leave the stop by adding the waiting time to the clock
						busLeavingTime = clock + waitingTime;
						//setting the flag to N for all the busses as they are not in sync now.
						for (Bus bus : busRecord) {
							bus.allBusesSync = 'N';
						}

					}
					//Added this part of code to keep the distance between the busses uniform.--end

					//updating the record of the bus which arrived at stop
					oldRecordBus = busRecord.get(busNo - 1);
					oldRecordBus.lastStop = stopNo;
					oldRecordBus.nextStop = nextStopNo;
					oldRecordBus.lastStopArrivalTime = clock;

					//updating the record of the bus stop for min, max and no of time the bus arrives and total people
					oldRecordBusStop = stopRecord.get(stopNo);
					oldRecordBusStop.maximumPeople = oldRecordBusStop.maximumPeople < noOfPeopleAtStop
							? noOfPeopleAtStop
									: oldRecordBusStop.maximumPeople;
					oldRecordBusStop.minimumPeople = oldRecordBusStop.minimumPeople > noOfPeopleAtStop
							? noOfPeopleAtStop
									: oldRecordBusStop.minimumPeople;
					oldRecordBusStop.noOfTimesBusArrives++;
					oldRecordBusStop.totalPeople += noOfPeopleAtStop;

					//checking the no of people at stop if they are greater than zero generating the boarder event
					if (noOfPeopleAtStop > 0) {

						//setting flag of that bus which arrived to Y that it as stop now 
						oldRecordBus.busAtStop = 'Y';
						busRecord.set(busNo - 1, oldRecordBus);
						stopRecord.set(stopNo, oldRecordBusStop);
						//generating boarder event at the time of clock for the first boarder.
						eventDetails.add(new EventDetails(clock, "Boarder", busNo, stopNo));
					}
					//if no one is bus stop queue
					else {

						//setting flag of that bus which arrived to N that it is leaving the stop now  
						oldRecordBus.busAtStop = 'N';
						//setting flag of that bus which arrived to Y now the bus is in sync now because added the waiting time for the arrival at next stop
						oldRecordBus.allBusesSync = 'Y';//Added this part of code to keep the distance between the busses uniform start end
						busRecord.set(busNo - 1, oldRecordBus);
						stopRecord.set(stopNo, oldRecordBusStop);
						//generating arrival event at next stop
						//Added waiting time to keep the distance between the busses uniform - start end
						eventDetails.add(new EventDetails(clock + timeIntervalbetweenStops + waitingTime, "Arrival",
								busNo, nextStopNo));
					}

					break;

					//if the event type is boarder
				case "Boarder":
					//decrementing the queue length by 1 as the person is boarding the bus
					busStopQueue[eventDetails.get(count).StopNo]--;
					//adding the boarding time to the clock
					clock = clock + boardingTime;
					//retrieving the no of people at stop
					noOfPeopleAtStop = busStopQueue[eventDetails.get(count).StopNo];

					//checking if no of people at stop is greater than zero
					if (noOfPeopleAtStop > 0) {

						//Added this part of code to keep the distance between the busses uniform start
						//checking if the clock is equal to bus leaving time which was calculated earlier
						if (clock != busLeavingTime) {
							// if not then bus will board more passengers by generating the boarder event
							eventDetails.add(new EventDetails(clock, "Boarder", busNo, stopNo));
						} else {
							// if clock is equal to bus leaving time then bus has to leave the stop generating the arrival event
							//calculating time remaining waiting time if there is any
							remainingWaitingTime = busLeavingTime - clock;
							oldRecordBus = busRecord.get(busNo - 1);
							//setting flag of that bus which arrived to N that it is leaving the stop now  
							oldRecordBus.busAtStop = 'N';
							//setting flag of that bus which arrived to Y the bus is in sync now because added the waiting time for the arrival at next stop
							oldRecordBus.allBusesSync = 'Y';
							busRecord.set(busNo - 1, oldRecordBus);
							//generating the arrival event for next bus stop
							eventDetails.add(new EventDetails((clock + timeIntervalbetweenStops + remainingWaitingTime),
									"Arrival", busNo, nextStopNo));
							//Added this part of code to keep the distance between the busses uniform end
						}
					} else {

						//Added this part of code to keep the distance between the busses uniform start
						//calculating time remaining waiting time if there is any
						remainingWaitingTime = busLeavingTime - clock;
						oldRecordBus = busRecord.get(busNo - 1);
						//setting flag of that bus which arrived to N that it is leaving the stop now  
						oldRecordBus.busAtStop = 'N';
						//setting flag of that bus which arrived to Y now the bus is in sync now because added the waiting time for the arrival at next stop
						oldRecordBus.allBusesSync = 'Y';
						busRecord.set(busNo - 1, oldRecordBus);

						//generating the arrival event for next bus stop by adding the remaining waiting time for the next bus stop
						eventDetails.add(new EventDetails((clock + timeIntervalbetweenStops + remainingWaitingTime),
								"Arrival", busNo, nextStopNo));

						//Added this part of code to keep the distance between the busses uniform end
					}
					break;
					//if the event type is person
				case "Person":
					//incrementing the queue length for that stop
					busStopQueue[eventDetails.get(count).StopNo]++;
					//calculating the exponentially distribution for the next person arrival
					newExpTime = randomGeneration();
					//generating the nextStopNo person arrival event by adding the clock totalTime exponentially Distribution Point time for the same stop and as bus no can be changed so it is set to 99999
					eventDetails.add(new EventDetails(Double.parseDouble(df.format(newExpTime+clock)), "Person", 99999, stopNo));
					break;
				}

				//sorting the array list of our event details according to event time
				//incrementing the counter
				Collections.sort(eventDetails);
				count++;
				//checking whether the clock is equal to the sumulation time, if it is stop the loop.
			} while (clock <= totalTime);



			//checking the count for the no of people boarded the bus, no of person arrived at bus stops and no of times bus arrives.
			for (EventDetails rec : eventDetails) {
				if (rec.status == 'Y' && rec.eventType == "Boarder")
					peopleServiced++;
				if (rec.status == 'Y' && rec.eventType == "Person")
					person++;
				if (rec.status == 'Y' && rec.eventType == "Arrival")
					arrival++;

			}

			//exprting the data to excel file and out file
			writer.newLine();
			writer.newLine();
			writer.append("No Of People Bus Serviced, No Of People Arrived at bus stops, No of Times Bus Arrives");
			writer.newLine();
			writer.append(String.valueOf(peopleServiced));
			writer.append(",");
			writer.append(String.valueOf(person));
			writer.append(",");
			writer.append(String.valueOf(arrival));
			fileOut.println("People Serviced = " + peopleServiced + " Person Arrived = " + person + " Bus Arrived = " + arrival);
			System.out.println("People Serviced = " + peopleServiced + " Person Arrived = " + person + " Bus Arrived = " + arrival);


			writer.close();


		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
	}

}

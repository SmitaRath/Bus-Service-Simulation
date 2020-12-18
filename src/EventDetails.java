//This class is to store the event details
public class EventDetails implements Comparable<EventDetails> {
	     //it will store the time of the event
		 double time;
		 //it will store the type of event
		 String eventType;
		 //it will store the bus no for which the event should happen - not valid for person event
		 int busNo;
		 //it will store the stop no of the event 
		 int StopNo;
		//added to distinguish between events which are already processed and yet to process Y-processed N - not processed
		 char status; 

		 //constructor for initialization, we will initialize in BusSimulation constructor
		public EventDetails(double time, String eventType, int busNo, int stopNo) {
			this.time = time;
			this.eventType = eventType;
			this.busNo = busNo;
			StopNo = stopNo;
			status = 'N';

		}

		public void setStatus(char status) {
			this.status = status;
		}

		//overriding compareTo method to sort the arrayList based on time.
		@Override
		public int compareTo(EventDetails eventDetailsparam) {

			return this.time > eventDetailsparam.time ? 1 : this.time < eventDetailsparam.time ? -1 : 0;
		}

		//overrding toString() method to print in log file
		@Override
		public String toString() {
			
			return String.format("Event Type = "+ eventType + " Clock = " + time + " Bus No = " + busNo + " Stop No = " + StopNo);
			
		}

	}
	
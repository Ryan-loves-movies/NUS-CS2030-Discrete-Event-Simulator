import java.util.function.Supplier;

class Simulator {
    private static final int FIRSTCUST = 1;
    private final int numOfServers;
    private final int numOfSelfCheckOut;
    private final int qmax;
    private final ServerFleet serverFleet;
    private final ImList<Pair<Double, Supplier<Double>>> inputTimes;
    private final Supplier<Double> restTimes;
    
    Simulator(int numOfServers,
              int numOfSelfCheckOut,
              int qmax, 
              ImList<Pair<Double, Supplier<Double>>> inputTimes,
              Supplier<Double> restTimes) {
        this.numOfServers = numOfServers;
        this.numOfSelfCheckOut = numOfSelfCheckOut;
        this.qmax = qmax;
        this.serverFleet = new ServerFleet(numOfServers,
                                           numOfSelfCheckOut,
                                           qmax, 
                                           restTimes);
        this.inputTimes = inputTimes;
        this.restTimes = restTimes;
    }

    public Pair<Pair<String, Double>, ServerFleet> serveAllCustBef(ServerFleet serverFleet,
                                                                   Event arriveEvent) {
        String eventString = "";
        double waitTime = 0;
        // while a queue exists with earliest time before the next arriveEvent,
        // keep getting nextEvent
        while (serverFleet.getEarliestTime() <= arriveEvent.getTime() &&
               !serverFleet.isEmpty()) {
            int earliestTimeInd = serverFleet.getEarliestTimeInd();
            Pair<Pair<String, Double>, ServerFleet> strNServer = 
                                            serverFleet.nextEvent(earliestTimeInd);
            String currEvent = strNServer.first().first();
            eventString += currEvent + (currEvent.equals("") ? ""
                                                             : "\n");
            waitTime += strNServer.first().second();
            serverFleet = strNServer.second();
        }            
        return new Pair<Pair<String, Double>, ServerFleet>(new Pair<String, Double>(
                                                               eventString, waitTime),
                                                          serverFleet);
    }

    public String simulate() {
        int numOfCust = 0;
        double totWaitTime = 0;
        int numOfServed = 0;
        int numOfLeft = 0;

        String eventOutline = "";
        ServerFleet serverFleet = new ServerFleet(this.numOfServers, 
                                                  this.numOfSelfCheckOut,
                                                  this.qmax, 
                                                  this.restTimes);
        ImList<Pair<Double, Supplier<Double>>> workingInputTimes = this.inputTimes;

        int custNum = this.FIRSTCUST;
        while (workingInputTimes.size() > 0) {
            // Create new customer and arriveEvent
            Pair<Double, Supplier<Double>> frontCust = workingInputTimes.get(0);
            Customer currCust = new Customer(custNum, 
                                             frontCust.first(), 
                                             frontCust.second());
            Event newArrival = new ArriveEvent(currCust);

            // Serve all customers before the arrival time of the new Customer
            Pair<Pair<String, Double>, ServerFleet> strNserverFleet = 
                                    this.serveAllCustBef(serverFleet, newArrival);
            eventOutline += strNserverFleet.first().first();
            totWaitTime += strNserverFleet.first().second();
            serverFleet = strNserverFleet.second();

            // Add arriveEvent to the queue if possible
            // else customer leaves
            Pair<Pair<String, Double>, ServerFleet> eventStatsNserverFleet = 
                                                    serverFleet.addArriveEvent(newArrival);
            Pair<String, Double> eventStats = eventStatsNserverFleet.first();
            
            eventOutline += newArrival.toString() + "\n";
            if (!eventStats.first().equals("")) {
                numOfLeft++;
                eventOutline += eventStats.first() + "\n";
            }
            totWaitTime += eventStats.second();
            serverFleet = eventStatsNserverFleet.second();

            workingInputTimes = workingInputTimes.remove(0);
            custNum++;
            numOfCust++;
        }

        // Keep going through the customers until they are done
        while (!serverFleet.isEmpty()) {
            int nextEventInd = serverFleet.getEarliestTimeInd();
            Pair<Pair<String, Double>, ServerFleet> eventStrNserverFleet = 
                                        serverFleet.nextEvent(nextEventInd);
            String currEvent = eventStrNserverFleet.first().first();
            String eventString = currEvent + (currEvent.equals("") ? ""
                                                                   : "\n");
            eventOutline += eventString;
            totWaitTime += eventStrNserverFleet.first().second();
            serverFleet = eventStrNserverFleet.second();
        }
        
        // Add the final statistics to keep track of
        numOfServed = numOfCust - numOfLeft;
        double avgWaitTime = totWaitTime / numOfServed;
        eventOutline += String.format("[%.3f %d %d]", 
                                      avgWaitTime, 
                                      numOfServed, 
                                      numOfLeft);

        return eventOutline;
    }
}

import java.util.List;
import java.util.function.Supplier;

class ServerFleet {
    private static final double EPSILON = 1e-15;
    private static final double DEFEVENTTIME = -1;
    private static final int FIRSTSERVERNAME = 1;
    private static final int SERVERNAMEINC = 1;
    private static final int DIFFQMAX = 1;
    private final int numOfServers;
    private final int numOfSelfCheckOut;
    private final int qmax;
    private final ImList<PQ<Event>> serverQueues;
    private final ImList<Server> servers;

    ServerFleet(int numOfServers,
                int numOfSelfCheckOut,
                int qmax, 
                Supplier<Double> randRestTimes) {
        this.numOfServers = numOfServers; // + ServerFleet.SERVERNAMEINC; Additional server queue
        this.numOfSelfCheckOut = numOfSelfCheckOut;
        this.qmax = qmax;

        ImList<PQ<Event>> serverQueues = new ImList<PQ<Event>>(
                                             List.of(new PQ<Event>(new EventComparator())));
        ImList<Server> servers = new ImList<Server>(List.of(new Server(this.FIRSTSERVERNAME, 
                                                                       0.0, 
                                                                       randRestTimes)));
        // Add normal servers
        for (int i = this.FIRSTSERVERNAME + this.SERVERNAMEINC; i <= numOfServers; i++) {
            serverQueues = serverQueues.add(new PQ<Event>(new EventComparator()));
            servers = servers.add(new Server(i, 0.0, randRestTimes));
        }
        // Add self check-out machines
        if (numOfSelfCheckOut > 0) {
            serverQueues = serverQueues.add(new PQ<Event>(new EventComparator()));
            SelfCheckOut machines = new SelfCheckOut(numOfServers + this.SERVERNAMEINC, 0.0);
            for (int i = numOfServers + this.SERVERNAMEINC + this.SERVERNAMEINC; i <= numOfServers + numOfSelfCheckOut; i++) {
                machines = machines.addSelfCheckOut(new Server(i, 0.0, () -> 0.0));
            }
            servers = servers.add(machines);
        }
        System.out.println(serverQueues.get(numOfServers));
        this.serverQueues = serverQueues;
        this.servers = servers;
    }

    ServerFleet(int numOfServers,
                int numOfSelfCheckOut,
                int qmax, 
                ImList<PQ<Event>> serverQueues,
                ImList<Server> servers) {
        this.numOfServers = numOfServers;
        this.numOfSelfCheckOut = numOfSelfCheckOut;
        this.qmax = qmax;
        this.serverQueues = serverQueues;
        this.servers = servers;
    }

    @Override
    public String toString() {
        return this.serverQueues.toString();
    }

    // Get earliest event time of serverNum
    // OR freeAtTime of server if no event
    public double getEventTimeAt(int serverNum) {
        PQ<Event> queue = this.serverQueues.get(serverNum);

        if (queue.isEmpty()) {
            return this.servers.get(serverNum).getFreeAtTime();   
        } else {
            Pair<Event, PQ<Event>> eventNPQ = queue.poll();
            return eventNPQ.first().getTime();
        }
    }

    // Get index of earliest time
    public int getEarliestTimeInd() {
        int earliestInd = 0;

        // If all queues are empty, then default to 0
        if (this.isEmpty()) { 
            return 0;
        }

        // Queue all front events into a PQ
        PQ<Event> frontEvents = new PQ<Event>(new EventComparator());
        for (PQ<Event> queue : this.serverQueues) {
            if (queue.isEmpty()) {
                continue;
            }
            Pair<Event, PQ<Event>> frontNqueue = queue.poll();
            frontEvents = frontEvents.add(frontNqueue.first());
        }

        // Get earliest event
        Pair<Event, PQ<Event>> earliestNothers = frontEvents.poll();
        Event earliestEvent = earliestNothers.first();

        // Get index of the earliest event found
        for (int i = 0; i < this.numOfServers + ServerFleet.SERVERNAMEINC; i++) {
            PQ<Event> queue = this.serverQueues.get(i);
            if (queue.isEmpty()) {
                continue;
            }
            Pair<Event, PQ<Event>> frontNqueue = queue.poll();
            if (frontNqueue.first().equals(earliestEvent)) {
                earliestInd = i;
            }
        }
        return earliestInd;
    }

    // Get earliest event time among all queues
    public double getEarliestTime() {
        int earliestTimeInd = this.getEarliestTimeInd();
        return this.getEventTimeAt(earliestTimeInd);
    }

    // Check if all queues are empty
    public boolean isEmpty() {
        boolean isEmpty = true;
        for (PQ<Event> queue : this.serverQueues) {
            isEmpty = (isEmpty && queue.isEmpty());
        }
        return isEmpty;
    }

    // Check if particular server queue is free
    public boolean isEmpty(int serverNum) {
        return this.serverQueues.get(serverNum).isEmpty();
    }

    // Check if all queues are full
    public boolean isFull() {
        int numOfCust = 0;
        for (PQ<Event> queue : this.serverQueues) {
            numOfCust += new PQSize<Event>(queue).size();
        }
        // Maximum number of events
        int maxQueue = this.numOfServers * (this.qmax + ServerFleet.DIFFQMAX);
        maxQueue += this.numOfSelfCheckOut + this.qmax; // Add self check-out counters
        return (numOfCust == maxQueue);
    }   

    public Pair<Pair<String, Double>, ServerFleet> nextEvent(int serverNum) {
        PQ<Event> queue = this.serverQueues.get(serverNum);
        Server server = this.servers.get(serverNum);

        ImList<Server> newServers = this.servers;
        ImList<PQ<Event>> newServerQueues = this.serverQueues;

        String eventStr = "";
        double waitTime = 0;

        if (!queue.isEmpty()) {
            Pair<Event, PQ<Event>> eventNPQ = queue.poll();
            Event currEvent = eventNPQ.first();
            PQ<Event> newPQ = eventNPQ.second();
            eventStr += currEvent.toString();
            
            Pair<Server, Event> nextServerNEvent = currEvent.nextEvent(server);
            Server nextServer = nextServerNEvent.first();
            Event nextEvent = nextServerNEvent.second();
            waitTime += nextEvent.getWaitTime(); // Add statistic
            newServers = this.servers.set(serverNum, nextServer);
            if (nextEvent.equals(currEvent)) {
                newServerQueues = this.serverQueues.set(serverNum, newPQ);
            } else {
                newServerQueues = this.serverQueues.set(serverNum, 
                                                        newPQ.add(nextEvent));
            }
            return new Pair<Pair<String, Double>, ServerFleet>(new Pair<String, Double>(eventStr, 
                                                                                        waitTime),
                                                               new ServerFleet(this.numOfServers,
                                                                               this.numOfSelfCheckOut,
                                                                               this.qmax,
                                                                               newServerQueues,
                                                                               newServers
                                                                               ));
        } else {
            return new Pair<Pair<String, Double>, ServerFleet>(new Pair<String, Double>("", 0.0), 
                                                               this);
        }
    }

    public Pair<Pair<String, Double>, ServerFleet> addArriveEvent(Event arriveEvent) {
        // First, scan thru for idle servers
        for (int i = 0; i < this.numOfServers + ServerFleet.SERVERNAMEINC; i++) {
            PQ<Event> queue = this.serverQueues.get(i);
            Server server = this.servers.get(i);

            if (i == this.numOfServers) {
                System.out.println("Size: " + new PQSize<Event>(queue).size());
                System.out.println("Max: " + this.numOfSelfCheckOut);
                System.out.println("Server: "+ server.getFreeAtTime());
            }
            if ((queue.isEmpty() || // Check if queue is empty
                    (i == this.numOfServers && // Check for self check-out
                    new PQSize<Event>(queue).size() < this.numOfSelfCheckOut)) &&
                server.getFreeAtTime() < arriveEvent.getTime()) { // Modified getRestingUntil() to getFreeAtTime()

                Pair<Server, Event> serverNEvent = arriveEvent.nextEvent(server);
                Server newServer = serverNEvent.first();
                Event nextEvent = serverNEvent.second();
                System.out.println(nextEvent);
                PQ<Event> newQueue = queue.add(nextEvent);
                ImList<PQ<Event>> newServerQueues = this.serverQueues.set(i, newQueue);
                return new Pair<Pair<String, Double>, ServerFleet>(
                       new Pair<String, Double>("", 0.0), 
                       new ServerFleet(this.numOfServers,
                                       this.numOfSelfCheckOut,
                                       this.qmax,
                                       newServerQueues,
                                       this.servers.set(i, newServer)));
            }
        }
        // Second, scan thru for non-filled queues
        for (int i = 0; i < this.numOfServers + ServerFleet.SERVERNAMEINC; i++) {
            PQ<Event> queue = this.serverQueues.get(i);
            Server server = this.servers.get(i);

            if ((new PQSize<Event>(queue).size() < (this.qmax + this.DIFFQMAX)) ||
                     (i == this.numOfServers &&
                     new PQSize<Event>(queue).size() < (this.qmax + this.numOfSelfCheckOut))) {
                Pair<Server, Event> serverNEvent = arriveEvent.nextEvent(server);
                Server newServer = serverNEvent.first();
                Event nextEvent = serverNEvent.second();
                PQ<Event> newQueue = queue.add(nextEvent);
                ImList<PQ<Event>> newServerQueues = this.serverQueues.set(i, newQueue);
                double waitTime = nextEvent.getWaitTime(); // Add statistic
                return new Pair<Pair<String, Double>, ServerFleet>(
                       new Pair<String, Double>("", waitTime),
                       new ServerFleet(this.numOfServers,
                                       this.numOfSelfCheckOut,
                                       this.qmax,
                                       newServerQueues,
                                       this.servers.set(i, newServer)));
            }
        }
        // Customer leaves because no space for him
        Customer customer = arriveEvent.getCustomer();
        return new Pair<Pair<String, Double>, ServerFleet>(
               new Pair<String, Double>(new LeftEvent(customer).toString(), 0.0), 
                                        this);
    }
}

class DoneEvent extends Event {
    private final Server server;
    private final double serveTime;

    DoneEvent(Customer customer, Server server, double serveTime) {
        super(customer);
        this.server = server;
        this.serveTime = serveTime;
    }

    DoneEvent(Customer customer, Server server) {
        this(customer, server, customer.getArrivalTime());
    }

    @Override
    public double getTime() {
        return this.server.getFreeAtTime();
        // return this.serveTime + this.customer.getServiceTime();
    }

    @Override
    public double getWaitTime() {
        return 0.0;
    }

    @Override
    public String toString() {
        return String.format("%.3f %s done serving by %s", 
                             this.getTime(), 
                             this.customer.toString(), 
                             this.server.toString());
    }

    @Override
    public Pair<Server, Event> nextEvent(Server server) {
        Server nextServer = server.randomlyRest();
        return new Pair<Server, Event>(nextServer,
                                       new RestEvent(this.customer,
                                                     nextServer));
    }
}

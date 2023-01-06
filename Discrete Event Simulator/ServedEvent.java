class ServedEvent extends Event {
    private final Server server;
    private final double serveTime;

    ServedEvent(Customer customer, Server server, double serveTime) {
        super(customer);
        this.server = server;
        this.serveTime = serveTime;
    }

    ServedEvent(Customer customer, Server server) {
        this(customer, server, customer.getArrivalTime());
    }

    @Override
    public String toString() {
        return String.format("%.3f %s serves by %s", this.getTime(), customer, server);
    }
    
    @Override
    public double getTime() {
        return this.serveTime;
    }

    @Override
    public double getWaitTime() {
        return 0.0;
    }

    @Override
    public Pair<Server, Event> nextEvent(Server server) {
        return new Pair<Server, Event>(server,
                                       new DoneEvent(this.customer, this.server, this.serveTime));
    }
}

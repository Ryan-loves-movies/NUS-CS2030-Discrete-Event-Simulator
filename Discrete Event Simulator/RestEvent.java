class RestEvent extends Event {
    private final Server server;

    RestEvent(Customer customer, Server server) {
        super(customer);
        this.server = server;
    }

    @Override
    public String toString() {
        return "";
    }

    @Override
    public double getTime() {
        return this.server.getFreeAtTime();
    }

    @Override
    public double getWaitTime() {
        return 0.0;
    }

    @Override
    public Pair<Server, Event> nextEvent(Server server) {
        return new Pair<Server, Event>(this.server, this);
    }
}

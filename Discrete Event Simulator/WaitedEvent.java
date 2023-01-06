class WaitedEvent extends Event {
    private final Server server;
    private final double waitTime;

    WaitedEvent(Customer customer, Server server, double waitTime) {
        super(customer);
        this.server = server;
        this.waitTime = waitTime;
    }

    public Pair<Server, Event> serveCustomer(Server server) {
        if (server.getFreeAtTime() > this.server.getFreeAtTime()) {
            return new Pair<Server, Event>(
                server,
                new WaitedEvent(this.customer, 
                              server,
                              server.getFreeAtTime() - 
                                this.server.getFreeAtTime()));
        } else {
            Server newServer = server.serve(this.customer);
            return new Pair<Server, Event>(
                   newServer,
                   new ServedEvent(this.customer, 
                                   newServer, 
                                   this.server.getFreeAtTime()));
        }
    }


    @Override
    public String toString() {
        return "";
    }

    @Override
    public double getWaitTime() {
        return this.waitTime;
    }

    @Override
    public double getTime() {
        return this.server.getFreeAtTime();
    }

    @Override
    public Pair<Server, Event> nextEvent(Server server) {
        return this.serveCustomer(server);
    }
}

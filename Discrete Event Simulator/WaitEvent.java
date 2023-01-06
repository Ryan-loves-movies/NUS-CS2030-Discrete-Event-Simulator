class WaitEvent extends Event {
    private final Server server;

    WaitEvent(Customer customer, Server server) {
        super(customer);
        this.server = server;
    }

    public Pair<Server, Event> serveCustomer(Server server) {
        // if (server.getFreeAtTime() > this.serveTime) {
        return new Pair<Server, Event>(
            server,
            new WaitedEvent(this.customer, 
                            server,
                            server.getFreeAtTime() - 
                                this.server.getFreeAtTime()));
        /*} else {
            Server newServer = server.serve(this.customer);
            return new Pair<Server, Event>(
                   newServer,
                   new ServedEvent(this.customer, 
                                   newServer, 
                                   this.serveTime));
        }*/
    }

    @Override
    public String toString() {
        return String.format("%.3f %s waits at %s", this.getTime(), 
                                                    this.customer, 
                                                    this.server);
    }

    @Override
    public double getWaitTime() {
        return this.server.getFreeAtTime() - this.getTime();
    }
    
    @Override
    public double getTime() {
        return this.customer.getArrivalTime();
    }

    @Override
    public Pair<Server, Event> nextEvent(Server server) {
        return this.serveCustomer(server);
    }
}

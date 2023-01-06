import java.util.function.Supplier;

class ArriveEvent extends Event {
    
    ArriveEvent(Customer customer) {
        super(customer);
    }

    @Override
    public double getWaitTime() {
        return 0;
    }

    @Override
    public double getTime() {
        return this.customer.getArrivalTime();
    }

    @Override
    public String toString() {
        return String.format("%.3f %s arrives", this.getTime(), this.customer.toString());
    }

    public Pair<Server, Event> serveCustomer(Server server) {
        if (server.getFreeAtTime() <= this.customer.getArrivalTime()) {
            Server newServer = server.serve(this.customer);
            return new Pair<Server, Event>(newServer,
                                           new ServedEvent(this.customer, 
                                                           newServer));
        } else {
            return new Pair<Server, Event>(server,
                                           new WaitEvent(this.customer, 
                                                         server));
        }
    }

    @Override
    public Pair<Server, Event> nextEvent(Server server) {
        return this.serveCustomer(server);
    }  
}

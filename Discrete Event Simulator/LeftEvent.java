class LeftEvent extends Event {
    LeftEvent(Customer customer) {
        super(customer);
    }

    @Override
    public String toString() {
        return String.format("%.3f %s leaves", this.getTime(), this.customer.toString());
    }

    @Override
    public double getWaitTime() {
        return 0.0;
    }

    @Override
    public double getTime() {
        return this.customer.getArrivalTime();
    }
    
    @Override
    public Pair<Server, Event> nextEvent(Server server) {
        return new Pair<Server, Event>(server,
                                       this);
    }
}

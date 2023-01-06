abstract class Event {
    protected final Customer customer;

    Event(Customer customer) {
        this.customer = customer;
    }
    
    @Override
    public String toString() {
        return "Event() Default";
    }

    public Customer getCustomer() {
        return this.customer;
    }
    
    public double getArriveTime() {
        return this.customer.getArrivalTime();
    }

    public int getCustOrder() {
        return Integer.valueOf(this.customer.getCustomerName());
    }

    public abstract double getWaitTime();

    public abstract double getTime();

    public abstract Pair<Server, Event> nextEvent(Server server);
}

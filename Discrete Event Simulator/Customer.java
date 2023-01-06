import java.util.function.Supplier;

class Customer {
    private final String customerName;
    private final double arrivalTime;
    private final Supplier<Double> serviceTime;

    Customer(String customerName, double arrivalTime, Supplier<Double> serviceTime) {
        this.customerName = customerName;
        this.arrivalTime = arrivalTime;
        this.serviceTime = serviceTime;
    }

    Customer(int customerName, double arrivalTime, Supplier<Double> serviceTime) {
        this(String.valueOf(customerName), arrivalTime, serviceTime);
    } 

    public String getCustomerName() {
        return this.customerName;
    }

    public double getArrivalTime() {
        return this.arrivalTime;
    }

    public double getServiceTime() {
        return this.serviceTime.get();
    }

    @Override
    public String toString() {
        return String.format("%s", this.getCustomerName());
    } 
}

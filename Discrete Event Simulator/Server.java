import java.lang.Math;
import java.util.function.Supplier;

class Server {
    protected final String serverName;
    protected final double freeAtTime;
    protected final double restingUntil;
    private final Supplier<Double> randRestTime;

    Server(String serverName, 
           double freeAtTime, 
           double restingUntil,
           Supplier<Double> randRestTime) {
        this.serverName = serverName;
        this.freeAtTime = freeAtTime;
        this.restingUntil = restingUntil;
        this.randRestTime = randRestTime;
    }

    Server(String serverName, 
           double freeAtTime, 
           Supplier<Double> randRestTime) {
        this(serverName, freeAtTime, freeAtTime, randRestTime);
    }

    Server(int serverName, 
           double freeAtTime,
           Supplier<Double> randRestTime) {
        this(String.valueOf(serverName), freeAtTime, randRestTime);
    }
    
    Server(String serverName, Supplier<Double> randRestTime) {
        this(serverName, 0.0, randRestTime);
    }

    Server(int serverName, Supplier<Double> randRestTime) {
        this(serverName, 0.0, randRestTime);
    }

    public double getFreeAtTime() {
        // return this.freeAtTime;
        return this.restingUntil;
    }

    public double getRestingUntil() {
        return this.restingUntil;
    }

    public Server randomlyRest() {
        return new Server(serverName,
                          this.getFreeAtTime(),
                          this.getRestingUntil() + 
                            this.randRestTime.get(),
                          this.randRestTime);
    }

    protected String getServerName() {
        return this.serverName;
    }

    @Override
    public String toString() {
        return this.getServerName();
    }

    public Server serve(Customer customer) {
        return new Server(this.getServerName(), 
                          Math.max(customer.getArrivalTime(), this.getFreeAtTime()) + 
                            customer.getServiceTime(),
                          this.randRestTime);
    }
}

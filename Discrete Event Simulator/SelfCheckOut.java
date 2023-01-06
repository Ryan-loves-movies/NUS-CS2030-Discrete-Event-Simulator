import java.util.List;

class SelfCheckOut extends Server {
    private final ImList<Server> servers;

    SelfCheckOut(String serverName, 
                 double freeAtTime,
                 ImList<Server> servers) {
        super(serverName, freeAtTime, () -> 0.0);
        this.servers = servers;
    }
    
    SelfCheckOut(int serverName,
                 double freeAtTime,
                 ImList<Server> servers) {
        super(serverName, freeAtTime, () -> 0.0);
        this.servers = servers;
    }

    SelfCheckOut(int serverName,
                 double freeAtTime) {
        this(serverName, 
             freeAtTime,
             new ImList<Server>(
                 List.<Server>of(new Server(serverName, 
                                            freeAtTime, 
                                            () -> 0.0))));
    }

    @Override
    public String toString() {
        return "self-check " + this.serverName;
    }

    public SelfCheckOut addSelfCheckOut(Server server) {
        return new SelfCheckOut(this.serverName,
                                this.getFreeAtTime(),
                                this.servers.add(server));
    }

    @Override
    public double getFreeAtTime() {
        double freeAtTime = this.servers.get(0).freeAtTime;
        for (Server server : this.servers) {
            freeAtTime = Math.min(server.getFreeAtTime(), freeAtTime);
        }
        return freeAtTime;
    }

    private int getFreeAtTimeInd() {
        int i = 0;
        for (Server server : this.servers) {
            if (server.getFreeAtTime() == this.getFreeAtTime()) {
                break;
            }
            i++;
        }
        return i;
    }

    @Override
    public SelfCheckOut randomlyRest() {
        return this;
    }

    @Override
    public SelfCheckOut serve(Customer customer) {
        Server serverServing = this.servers.get(this.getFreeAtTimeInd());
        return new SelfCheckOut(serverServing.getServerName(), 
                                this.getFreeAtTime(), // Should not mean anything
                                this.servers.set(this.getFreeAtTimeInd(),
                                                 serverServing.serve(customer)));
    }
}

# NUS-CS2030-Discrete-Event-Simulator
Discrete Event Simulator built and refined throughout Y1S1 of NUS CS2030 to schedule customers with waiters and self-service kiosks based on service times and random rest times of servers.

### Background
- A sample test file is presented in the repository under "sampleTestFile.in"
- User input starts with values representing the number of servers, the number of self-check counters, the maximum queue length, the number of customers and the probability of a server resting. This is followed by the arrival times of the customers. Lastly, a number of service times (could be more than necessary) are provided.
- In other words, 
```
(no. of servers) (no. of self-check counters) (max queue length) (no. of customers) (prob of server resting)
(arrival times of customers -- based on number of customers specified already)
(service times of customers)
```

### Trying it out
- To try out the simulator, ensure at least java 17 is installed
- a simple "java Main < sampleTestFile.in" in jshell would suffice to present the schedule

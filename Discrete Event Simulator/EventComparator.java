import java.util.Comparator;

class EventComparator implements Comparator<Event> {
    public int compare(Event event1, Event event2) {
        double diff = event1.getTime() - event2.getTime();
        
        if (diff > 0.0) {
            return 1;
        } else if (diff < 0.0) {
            return -1;
        } else {
            double arriveDiff = event1.getArriveTime() - event2.getArriveTime();
            if (arriveDiff > 0.0) {
                return 1;
            } else if (arriveDiff < 0.0) {
                return -1;
            } else {
                double custDiff = event1.getCustOrder() - event2.getCustOrder();
                if (custDiff > 0.0) {
                    return 1;
                } else if (custDiff < 0.0) {
                    return -1;
                } else { 
                    return 0;
                }
            }
        }
    }
}

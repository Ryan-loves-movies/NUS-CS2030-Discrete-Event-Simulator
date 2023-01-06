class PQSize<E> {
    private final PQ<E> queue;

    PQSize(PQ<E> queue) {
        this.queue = queue;
    }

    public int size() {
        int size = 0;
        PQ<E> queueCopy = this.queue;
        while (!queueCopy.isEmpty()) {
            Pair<E, PQ<E>> headNQ = queueCopy.poll();
            queueCopy = headNQ.second();
            size++;
        }
        return size;
    }
}

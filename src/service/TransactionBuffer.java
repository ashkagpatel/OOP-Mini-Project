
package service;

public class TransactionBuffer {

    private final int[] buffer = new int[5];
    private int count = 0;
    private int in = 0;
    private int out = 0;

    public synchronized void put(int transaction)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[in] = transaction;
        in = (in + 1) % buffer.length;
        count++;

        System.out.println("Added transaction: " + transaction);

        notify();
    }

    public synchronized int take()
            throws InterruptedException {

        while (count == 0) {
            wait();
        }

        int transaction = buffer[out];
        out = (out + 1) % buffer.length;
        count--;

        System.out.println("Processing transaction: " + transaction);

        notify();
        return transaction;
    }
}

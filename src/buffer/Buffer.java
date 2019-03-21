package buffer;

import errors.BufferError;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A buffer that the threads use to store the largest found number & the number of steps that went with it.
 */
public class Buffer {

    private final Lock lock = new ReentrantLock();
    private final Condition stepUsage = lock.newCondition();

    private boolean bufferInUse = false;

    private int largestSteps = 0;
    private String largestNumber;

    /**
     * Set the new largest amount of steps, and the new largest number
     * @param steps the number of steps this multiplicative persistence had
     * @param number the actual number that was processed
     * @throws BufferError if a thread is interrupted while waiting for usage of the buffer
     */
    public void setNewLargest(int steps, String number) throws BufferError {
        lock.lock();
        try {
            if (bufferInUse) stepUsage.await();
            bufferInUse = true;
            this.largestSteps = steps;
            this.largestNumber = number;
        } catch (InterruptedException e) {
            throw new BufferError(e.getMessage());
        } finally {
            bufferInUse = false;
            stepUsage.signal();
            lock.unlock();
        }
    }

    /**
     * Return the largest amount of steps
     * @return the largest amount of steps taken
     * @throws BufferError if a thread is interrupted while waiting for usage of the buffer
     */
    public int getLargestSteps() throws BufferError {
        lock.lock();
        try {
            if (bufferInUse) stepUsage.await();
            bufferInUse = true;
            return largestSteps;
        } catch (InterruptedException e) {
            throw new BufferError(e.getMessage());
        } finally {
            bufferInUse = false;
            stepUsage.signal();
            lock.unlock();
        }
    }

    /**
     * Get the largest number, it doesn't need to use conditions as it is thread-safe; only the Main class accesses
     * this, and only when all other threads have stopped.
     * @return the largest number
     */
    public String getLargestNumber() {
        lock.lock();
        try {
            return largestNumber;
        } finally {
            lock.unlock();
        }
    }
}

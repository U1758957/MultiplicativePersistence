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
    private final Condition bufferUsage = lock.newCondition();

    private static boolean bufferInUse = false;
    private static boolean finish = false;

    private static int largestSteps = 0;
    private static long largestNumber;

    /**
     * Set the new largest amount of steps, and the new largest number
     * @param steps the number of steps this multiplicative persistence had
     * @param number the actual number that was processed
     * @throws BufferError if a thread is interrupted while waiting for usage of the buffer
     */
    public void setNewLargest(int steps, long number) throws BufferError {
        lock.lock();
        try {
            if (bufferInUse) bufferUsage.await();
            bufferInUse = true;
            largestSteps = steps;
            largestNumber = number;
        } catch (InterruptedException e) {
            throw new BufferError(e.getMessage());
        } finally {
            bufferInUse = false;
            bufferUsage.signal();
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
            if (bufferInUse) bufferUsage.await();
            bufferInUse = true;
            return largestSteps;
        } catch (InterruptedException e) {
            throw new BufferError(e.getMessage());
        } finally {
            bufferInUse = false;
            bufferUsage.signal();
            lock.unlock();
        }
    }

    /**
     * Get the largest number, it doesn't need to use conditions as it is thread-safe; only the Main class accesses
     * this, and only when all other threads have stopped.
     * @return the largest number
     */
    public long getLargestNumber() {
        lock.lock();
        try {
            return largestNumber;
        } finally {
            lock.unlock();
        }
    }

    /**
     * See if the program is finished
     *
     * @return if the program is finished
     */
    public boolean isFinished() {
        return finish;
    }

    /**
     * Set whether to tell the threads to finish up or not
     *
     * @param finish the variable to say if the program is finished
     */
    public void setFinished(boolean finish) {
        Buffer.finish = finish;
    }
}

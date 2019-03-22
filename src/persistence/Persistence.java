package persistence;

import buffer.Buffer;
import errors.BufferError;

/**
 * This class is the checker class
 */
public class Persistence implements Runnable {

    private static Buffer buffer = new Buffer();
    private static final int processedBufferSize = 262144;

    public Thread t;
    private long initialNumber;
    private long offset;

    private int steps = 0;
    private long number;

    private long processed = 0;

    private int[] processBufferStep = new int[processedBufferSize];
    private long[] processBufferNumber = new long[processedBufferSize];
    private int processBufferIndex = 0;

    /**
     * A constructor for Persistence, also declares the thread to be this class
     *
     * @param initialNumber the initial number for this thread to check
     * @param offset        the next number for this number to check, added each time
     */
    public Persistence(long initialNumber, long offset) {
        this.t = new Thread(this);
        this.initialNumber = initialNumber;
        this.offset = offset;
    }

    /**
     * Find whether or not the parameter is the next largest steps involved.
     * <p>
     * >> (int) Math.log10(x) + 1 is a mathematical way to get the amount of digits a number has, faster than string conversions
     * >> x % 10 gets the last digit of a number; modding by 10 then dividing the number by 10 is faster than looping through a string conversion
     *
     * @param x the number to check
     * @throws BufferError if this thread is interrupted while checking
     */
    private void findNextNumber(long x) throws BufferError {
        long newNumber = 1;
        if ((Math.floor(Math.log10(x)) + 1) == 1) {
            processBufferStep[processBufferIndex] = steps - 1;
            processBufferNumber[processBufferIndex] = number;
            processBufferIndex++;
            if (processBufferIndex == processedBufferSize - 1) {
                processBufferIndex = 0;
                int largestStep = buffer.getLargestSteps();
                int currentLargest = 0;
                int numberIndex = 0;
                for (int stepIndex = 0; stepIndex < processedBufferSize; stepIndex++) {
                    if ((processBufferStep[stepIndex] > largestStep) &&
                            (processBufferStep[stepIndex] > currentLargest)) {
                        currentLargest = processBufferStep[stepIndex];
                        numberIndex = stepIndex;
                    }
                }
                if (currentLargest > 0) buffer.setNewLargest(currentLargest, processBufferNumber[numberIndex]);
            }
        } else {
            while (x > 0) {
                newNumber *= (x % 10);
                x /= 10;
            }
            steps++;
            findNextNumber(newNumber);
        }
    }

    public long getProcessed() {
        return processed;
    }

    @Override
    public void run() {
        long x = initialNumber;
        while (!buffer.isFinished()) {
            try {
                steps = 0;
                number = x;
                findNextNumber(x);
                x += offset;
                processed++;
            } catch (BufferError bufferError) {
                bufferError.printStackTrace();
            }
        }
    }
}

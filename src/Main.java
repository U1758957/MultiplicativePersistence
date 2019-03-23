import buffer.Buffer;
import errors.BufferError;
import errors.MainError;
import persistence.Persistence;

public class Main {

    private static Buffer buffer = new Buffer();

    /**
     * The main method
     *
     * @param args args[0] is the time to run in seconds, args[1] is the number to start from
     * @throws MainError   if Main is interrupted
     * @throws BufferError if a thread using Buffer is interrupted
     */
    public static void main(String[] args) throws MainError, BufferError {

        int threadCount = Runtime.getRuntime().availableProcessors();
        int timeToRun;
        long startNumber;

        long totalProcessed = 0;

        switch (args.length) {
            case 1:
                timeToRun = Integer.valueOf(args[0]);
                startNumber = 0L;
                break;
            case 2:
                timeToRun = Integer.valueOf(args[0]);
                startNumber = Long.valueOf(args[1]);
                break;
            default:
                timeToRun = 60;
                startNumber = 0L;
                break;
        }

        try {

            Persistence[] workers = new Persistence[threadCount];

            for (int x = 0; x < threadCount; x++) {
                workers[x] = new Persistence(x + startNumber, threadCount);
            }

            for (Persistence worker : workers) {
                worker.t.start();
            }

            Thread.sleep(timeToRun * 1000);

            buffer.setFinished(true);

            for (Persistence worker : workers) {
                worker.t.join();
            }

            for (Persistence worker : workers) {
                totalProcessed += worker.getProcessed();
            }

        } catch (InterruptedException e) {
            throw new MainError(e.getMessage());
        }

        String plural = timeToRun == 1 ? "second" : "seconds";

        System.out.println("In " + timeToRun + " " + plural + ", " + buffer.getLargestNumber() + " was the number " +
                "with the largest steps, with " + buffer.getLargestSteps() + " steps taken.");

        System.out.println("Total Numbers Processed: " + totalProcessed + ", started from " + startNumber);

    }
}

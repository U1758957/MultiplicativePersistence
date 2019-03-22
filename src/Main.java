import buffer.Buffer;
import errors.BufferError;
import errors.MainError;
import persistence.Persistence;

public class Main {

    private static Buffer buffer = new Buffer();

    public static void main(String[] args) throws MainError, BufferError {

        int threadCount = Runtime.getRuntime().availableProcessors();
        //threadCount = 1;
        int timeToRun;

        long totalProcessed = 0;

        if (args.length > 0) timeToRun = Integer.valueOf(args[0]);
        else timeToRun = 60;

        try {

            Persistence[] workers = new Persistence[threadCount];

            for (int x = 0; x < threadCount; x++) {
                workers[x] = new Persistence(x, threadCount);
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

        System.out.println("Total Numbers Processed: " + totalProcessed);

    }
}

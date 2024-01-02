package org.senergy.ams.app;
import java.util.concurrent.*;

public class TimeoutExample {

    public static void main(String[] args) {
        // Example: Timeout after 5 seconds
        long timeoutMillis = 2000;

        try {
            String result = executeWithTimeout(() -> {
                // Your code that should complete within the timeout and return a result
                System.out.println("Executing task...");
                // Simulate some work
                Thread.sleep(3000);
                System.out.println("Task completed.");
                return "Task result";
            }, timeoutMillis);

            System.out.println("Result: " + result);
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("Task timed out or encountered an exception: " + e.getMessage());
        }
    }

    public static <T> T executeWithTimeout(Callable<T> task, long timeoutMillis)
            throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<T> future = executor.submit(task);

        try {
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (java.util.concurrent.TimeoutException e) {
            throw new RuntimeException(e);
        } finally {
            executor.shutdownNow(); // Shut down the executor service
        }
    }

    static class TimeoutException extends Exception {
        public TimeoutException(String message) {
            super(message);
        }
    }
}

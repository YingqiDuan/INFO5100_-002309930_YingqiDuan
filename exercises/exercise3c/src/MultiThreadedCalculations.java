import java.util.Random;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MultiThreadedCalculations {

    // Formatter for timestamps
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    // Random generator for delays
    private static final Random random = new Random();

    public static void main(String[] args) {
        System.out.println("===== Part A: Using Individual Threads =====");
        executePartA();

        System.out.println("\n===== Part B: Using Thread Pool =====");
        executePartB();
    }

    /**
     * Part A: Uses separate threads to calculate primes, Fibonacci numbers, and factorials.
     */
    private static void executePartA() {
        // Prime numbers thread
        Thread primeThread = new Thread(() -> {
            for (int i = 1; i <= 25; i++) {
                long prime = calculateNthPrime(i);
                logResult("Prime", i, prime);
                sleepRandom();
            }
        }, "Prime-Thread");

        // Fibonacci numbers thread
        Thread fibonacciThread = new Thread(() -> {
            for (int i = 1; i <= 50; i++) {
                long fibonacci = calculateFibonacci(i);
                logResult("Fibonacci", i, fibonacci);
                sleepRandom();
            }
        }, "Fibonacci-Thread");

        // Factorials thread
        Thread factorialThread = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                double factorial = calculateFactorial(i);
                logResult("Factorial", i, factorial);
                sleepRandom();
            }
        }, "Factorial-Thread");

        // Start all threads
        primeThread.start();
        fibonacciThread.start();
        factorialThread.start();

        // Wait for all threads to finish
        try {
            primeThread.join();
            fibonacciThread.join();
            factorialThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Part A threads were interrupted.");
        }
    }

    /**
     * Part B: Uses a thread pool to handle individual calculation tasks.
     */
    private static void executePartB() {
        // Create a fixed thread pool with 5 threads
        ExecutorService executor = Executors.newFixedThreadPool(5);

        // Submit prime number tasks
        for (int i = 1; i <= 25; i++) {
            final int nth = i;
            executor.submit(() -> {
                long prime = calculateNthPrime(nth);
                logResult("Prime", nth, prime);
                sleepRandom();
            });
        }

        // Submit Fibonacci number tasks
        for (int i = 1; i <= 50; i++) {
            final int nth = i;
            executor.submit(() -> {
                long fibonacci = calculateFibonacci(nth);
                logResult("Fibonacci", nth, fibonacci);
                sleepRandom();
            });
        }

        // Submit factorial tasks
        for (int i = 1; i <= 100; i++) {
            final int number = i;
            executor.submit(() -> {
                double factorial = calculateFactorial(number);
                logResult("Factorial", number, factorial);
                sleepRandom();
            });
        }

        // Shutdown the executor and await termination
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.HOURS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("Thread pool was interrupted.");
        }
    }

    /**
     * Logs the calculation result with timestamp and thread information.
     *
     * @param type   The type of calculation (Prime, Fibonacci, Factorial)
     * @param index  The index or number being calculated
     * @param result The result of the calculation
     */
    private static void logResult(String type, int index, Object result) {
        String timestamp = LocalDateTime.now().format(formatter);
        Thread currentThread = Thread.currentThread();
        System.out.printf("[%s] [Thread-%d (%s)] %s #%d: %s%n",
                timestamp,
                currentThread.threadId(),
                currentThread.getName(),
                type,
                index,
                result.toString());
    }

    /**
     * Introduces a random delay between 100 and 500 milliseconds.
     */
    private static void sleepRandom() {
        try {
            Thread.sleep(100 + random.nextInt(401)); // 100 to 500 ms
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Calculates the nth prime number.
     *
     * @param n The position of the prime number to find
     * @return The nth prime number
     */
    private static long calculateNthPrime(int n) {
        int count = 0;
        long num = 1;
        while (count < n) {
            num++;
            if (isPrime(num)) {
                count++;
            }
        }
        return num;
    }

    /**
     * Checks if a number is prime.
     *
     * @param num The number to check
     * @return True if prime, else false
     */
    private static boolean isPrime(long num) {
        if (num <= 1) return false;
        if (num <= 3) return true;
        if (num % 2 == 0 || num % 3 == 0) return false;
        for (long i = 5; i * i <= num; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0)
                return false;
        }
        return true;
    }

    /**
     * Calculates the nth Fibonacci number.
     *
     * @param n The position in the Fibonacci sequence
     * @return The nth Fibonacci number
     */
    private static long calculateFibonacci(int n) {
        if (n == 1) return 0;
        if (n == 2) return 1;
        long a = 0, b = 1, fib = 0;
        for (int i = 3; i <= n; i++) {
            fib = a + b;
            a = b;
            b = fib;
        }
        return fib;
    }

    /**
     * Calculates the factorial of a number.
     *
     * @param n The number to calculate the factorial for
     * @return The factorial of n
     */
    private static double calculateFactorial(int n) {
        double fact = 1.0;
        for (int i = 2; i <= n; i++) {
            fact *= i;
        }
        return fact;
    }
}

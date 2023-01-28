import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.concurrent.*;

public class PrimeFinder
{

    PrimeFinder(){}

    class Counter implements Runnable
    {
        private PrimeFinder instance;
        private int lower, upper;
        long delta;

        Counter(PrimeFinder instance, int lower, int upper)
        {
            this.instance = instance;
            this.lower = lower;
            this.upper = upper;
        }

        @Override
        public void run()
        {
            long time = System.nanoTime();
            eratosthenes(lower, upper);
            delta = System.nanoTime() - time;
        }

        public double getMS()
        {
            return delta / 10e6;
        }

        public void eratosthenes(int lower, int upper)
        {
            for (int i = 2; i <= Math.sqrt(instance.sieve.length) && i <= upper; i++)
            {
                if (instance.sieve[i] == true)
                {
                    for (int j = i * i; j < upper; j += i)
                    {
                        instance.sieve[j] = false;
                    }
                }
            }
        }
    }

    private static final int MAX_VALUE = 100000000;
    private static final int NUM_THREADS = 8;
    public static ArrayList<Thread> counters = new ArrayList<>();;
    public static boolean sieve[] = new boolean[MAX_VALUE];
    public static int upper_bounds[] = new int[NUM_THREADS];
    public static long primesFound = 0;
    public static long totalPrimeSum = 0;

    public static void main(String[] args)
    {
        PrimeFinder instance = new PrimeFinder();
        instance.execute(instance);
    }

    public void execute(PrimeFinder instance)
    {   
        // fill the sieve with values
        Arrays.fill(sieve, Boolean.TRUE);
        sieve[0] = false;
        sieve[1] = false;

        // create the bounds for parallelization
        for (int i = 0; i < upper_bounds.length; i++)
        {
            upper_bounds[i] = MAX_VALUE / (NUM_THREADS) * (i + 1);
        }

        // System.out.println(Arrays.toString(upper_bounds));

        // populate the thread pool
        for (int i = 0; i < NUM_THREADS; i++)
        {
            Counter temp;

            if (i == 0)
            {
                temp = new Counter(instance, 2, upper_bounds[i]);
            }
            else
            {
                temp = new Counter(instance, upper_bounds[i - 1] + 1, upper_bounds[i]);
            }

            Thread t = new Thread(temp);
            counters.add(t);
        }

        long start = System.nanoTime();

        // start prime calculation
        for (int i = 0; i < counters.size(); i++)
        {
            counters.get(i).start();
        }

        // turn off the threads
        for (int i = 0; i < counters.size(); i++)
        {
            try
            {
                counters.get(i).join();
            } 
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        // record the time it took
        long delta = System.nanoTime() - start;
        System.out.println("Execution completed in " + (delta / 1e6) + " milliseconds.");

        for (int i = 0; i < sieve.length; i++)
        {
            if (sieve[i]) // if we have a prime number ...
            {
                primesFound++;
                totalPrimeSum += i; // add the current prime to the running sum
            }
        }

        ArrayList<Integer> topPrimes = new ArrayList<>();
        System.out.println("Total primes found: " + primesFound);
        System.out.println("Total prime sum: " + totalPrimeSum);

        for (int i = sieve.length - 1; topPrimes.size() < 10 && i > 0; i--)
        {
            if (sieve[i])
                topPrimes.add(i);
        }

        Collections.reverse(topPrimes);
        System.out.println("Top primes: " + topPrimes);

        try{
            File f = new File("primes.txt");
            if (!f.exists())
                f.createNewFile();
    
            FileWriter fw = new FileWriter(f);
            fw.write("" + (delta / 1e6) + "ms " + primesFound + " " + totalPrimeSum + " " + topPrimes.toString());
            fw.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}

// ---------------------+
// OS PA1: BarberShop   |
// Author: Jordi Castro |
// ID: 010974536        |
// ---------------------+

// imports:
//import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.List;
import java.util.ArrayList;

/*
 * Creates X customers
 *      - Customers need to enter the waiting room before accessing Barber's room
 */

public class BarberShop 
{
    private static int elapsedTime = 0;
    private static final int NUM_BARBERS = 5; 
    static final int NUM_CHAIRS= NUM_BARBERS * 2;
    //private static final int NUM_CHAIRS= NUM_BARBERS * 2;


    public static void main(String[] args) 
    {
        // Default values
        int sleepTime = 20;
        int numBarber = 5;
        int numCustomer = numBarber+ numBarber*2;


        Semaphore swait = new Semaphore(NUM_CHAIRS);
        Semaphore sbarber = new Semaphore(NUM_BARBERS);
        Semaphore mwait = new Semaphore(1);
        Semaphore mbarber = new Semaphore(1);

        if (args.length == 2) 
        {
            sleepTime = Integer.parseInt(args[0]);
            numBarber = Integer.parseInt(args[1]);
            System.out.printf("Sleep time = %d\t", sleepTime);
            System.out.printf("Number of Barber = %d\t", numBarber);
            System.out.println("");
        } 
        else 
        {
            System.out.println("Usage: <sleep time: 0 > <no. Barber: 5>");
            System.out.println("Using default");
            System.out.printf("Sleep time = %d\t", sleepTime);
            System.out.printf("Number of Barber = %d\t", numBarber);
            System.out.println("");
        }

	    // Initialize/declare variables including object of class type Customer, mutexes, and semaphores

        // Time info
        long startTime = System.currentTimeMillis();


	    // This is one way but there are others.
        List<Thread> customerThreads = new ArrayList<>();

        for (int i = 0; i < numCustomer; i++) 
        {
            if (elapsedTime < sleepTime) 
            {

		        // Instantiate customers and don't forget to pass in mutexes and semaphores
                Customer myCustomer = new Customer(i, swait, sbarber, mwait, mbarber);

		        // Create a thread
                Thread c = new Thread(myCustomer);


		        // Start up the threads that will run code in run()
                c.start();

                myCustomer.setThread(c); // Set the thread reference in Customer class

                customerThreads.add(c); // Add the thread to the list

            } else 
            {
                System.out.println("Shop is closed. No new customers allowed.");
                break;
            }
            elapsedTime = (int) (System.currentTimeMillis() - startTime);
        }

        // Wait for all customer threads to finish
        for (Thread customerThread : customerThreads) 
        {
            try 
            {
                customerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("All customers have been served. Closing the shop.");
    }
}

class Customer implements Runnable 
{
    private int id;
    private static int numInWait, numInBarber;
    //private final Random generator = new Random();
    private static final int NUM_CHAIRS = BarberShop.NUM_CHAIRS;
    
    // define semaphores and mutex variables
    private Semaphore swait, sbarber;
    private Semaphore mwait, mbarber; // mutex
   // private Thread thread; // Store the reference to the thread

    Customer(int id, Semaphore swait, Semaphore sbarber, Semaphore mwait, Semaphore mbarber) 
    {
        this.id = id;
        this.swait = swait;
        this.sbarber = sbarber;
        this.mwait = mwait;
        this.mbarber = mbarber;
        numInBarber = 0;
        numInWait = 0;
    }

    public void setThread(Thread thread) 
    {
        //this.thread = thread;
    }


    /**
     * Customer enters the waiting room
     */
    private void getWait() 
    {
        // Use semaphores and mutexes here
        try {
            // Acquire the semaphore
            mwait.acquire();
            swait.acquire();

            // Increment the number of customers waiting
            numInWait++;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Always release the semaphore in a finally block
            swait.release();
            mwait.release();
        }
    } 


    /**
     * Customer leaves the waiting chair to enter Barber chair
     * the customer then sleeps for 1s (:-P) and leaves chair
     */
    private void getBarberChair() 
    {
        //Use mutexes and semaphores appropriately 
	    try 
        {
            // aquire the semaphore and mutex
            sbarber.acquire();  
            mbarber.acquire();
            mwait.acquire();

            numInBarber++;
            numInWait--;
            
            System.out.printf("\t\tCustomer %d enters Barbers chair. We have %d performing haircut and %d waiting\n", this.id, numInBarber, numInWait);
            
        } catch (InterruptedException e) 
        {
            e.printStackTrace();
        }
        finally
        {
            // release them
            mbarber.release();
            mwait.release(); //barberchair was acquired -> release mwait
            swait.release(); // barberchair was acquired -> release swait
        }


        //Do not forget to signal the appropriate mutexes and semaphores
	    //code ...
    }

    /**
     * Customer exits the shop
     */

    private void exitShop() 
    {
        // Use mutexes and semaphores appropriately
        try 
        {
            mbarber.acquire(); // Acquire the mutex

            numInBarber--;
            System.out.printf("\t\t\tCustomer %d leaves the shop\n", id);
        } 
        catch (InterruptedException e) 
        {
            e.printStackTrace();
        } 
        finally
        {
            // Release the mutex
            mbarber.release();
            sbarber.release();
        }
    }

    @Override
    public void run() 
    {

        //Sleep a random amount of time here
        sleeps();
	    // code ...

	

        System.out.printf("Customer %d enters the shop\n", id);

	    // Call method to get access to Waiting Room
        getWait();

        // Check if the thread is interrupted (customer leaves in frustration)
        if (!Thread.currentThread().isInterrupted()) 
        {

	        // Now compete for a barber chair
            getBarberChair();

	        // Sleep a random amount of time here
            sleeps();
            
	        // Exit
            exitShop();

            //System.out.printf("\t\t\tCustomer %d leaves the shop\n", id);

        } 
        
    }

    public void sleeps()
    {
        try {
        double randSleepTime = Math.random();
        long miliSleepTime = (long) (randSleepTime * 1000);
        Thread.sleep(miliSleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isFull() 
    {
        return numInWait >= NUM_CHAIRS;
    }

}
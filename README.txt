TITLE: BarberShop Semaphores and Mutex Simulation

AUTHOR: JORDI CASTRO 

ID: _________

Description:
Java program simulates the operation of a barber shop with a seating area (protected area) and barber seats (protected area) through the use of semaphores and mutexes
A customer enters the shop, takes a seat in the waiting area (if not full), then waits for a barber seat to open up, gets a haircut, then leaves the shop.
    if the waiting area isFull(), the customer %d leaves in frustration.
Customer threads are stored in an ArrayList to take care of concurrent execution. Once every customer in the ArrayList exits, the shop can close (program terminates).
Random wait times are assigned with the sleeps() method and is used to take care of how long people wait before entering the shop and the duration of their haircut.





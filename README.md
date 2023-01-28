# COP4520_Assignment_1
To run the code, please run the following command in a terminal: javac PrimeFinder.java && java PrimeFinder

Note: Java must be installed! :)

# Summary of Approach

To solve this problem, I used the sieve of erosthenes, which is a very efficient method of computing prime numbers, up until 10^8 or so (how convenient!). To accelerate the process with multithreading, I dynamically split the upper bound for primes we wanted to compute, into n equal partitions, n is the number of threads. 

# Experimental Evaluation

In short, I was able to reduce the multithreaded runtime from 4000ms to approximately 2500ms through various optimizations. However, single-threaded performance is still superior, at around 750ms. I suspect that this is because the thread overhead in Java is inefficient, especially the use of the join() method, which was released in the early days of Java. Additionally, it is possible that the threads are not all doing the same amount of work. I verified this by measuring the amount of time that each thread needed to complete its task, and noticed that while playing around with different values for the partitioning (i.e not setting EQUAL PARTITIONS), that some threads took longer to run than others. I am still not sure about the mathematics behind this, but I suspect that computing multiples for greater numbers, even though these numbers can all fit in a 32-bit integer, are still marginally more expensive compared to smaller numbers, which certainly has the potential to add up.

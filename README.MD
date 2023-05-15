# Description 

This repository contains an implementation of a
CRDT (Causally Replicating Data Type) known as 
"counter". It is based of the implementation 
provided in the following [article](https://inria.hal.science/hal-00932836).

## Replication of the counter

The implementation offers a simple channel
between two threads that allows to test the
concurrency of the solution. The implementation
itself isn't limited by this channel, however,
since the channel is hidden behind an interface.

## The tests

There are two test sets:

* Basic -- these tests compare the CRDT counter 
implementation with int based counter and
also test the implementation for eventual
consistency by emulating a concurrent execution
with one thread
* Concurrent -- these tests test the counter
in a proper multithreaded setting. One test
verifies simple replication, while the other
checks eventual consistency by having **Thread1**
do 100 increments, while **Thread2** does 100
decrements: it is expected to have both counters
eventually reach 0.
# Fair Lock Assignment
This project has been developed for the course of *Concurrent and Distributed Systems*, *Master of Engineering in Embedded Computing Systems* (*University of Pisa and Sant'Anna School of Advanced Studies*).

## Table of Contents
* [Requirements](#requirements)
	* [Assignment 1](#assignment-1)
	* [Assignment 2](#assignment-2)
	* [Assignment 3](#assignment-3)
* [Tools](#tools)
* [Project Structure](#project-structure)
* [License](#license)

## Requirements
### Assignment 1
Implement a synchronization mechanism similar to the mechanism provided by Java within the `java.util.concurrent` package (explicit `Lock` and `Condition` variables) but whose behaviour is in accordance with the semantic *"signal-and-urgent"*.
For the implementation of this mechanism you can use only the built-in synchronization constructs provided by Java (i.e.  `synchronized` blocks or `synchronized` methods ) and the methods `wait()`, `notify()` and `notifyAll()` provided by the class `Object`).
In particular:
* implement the class `FairLock` that provides the two methods `lock()` and `unlock()`, to be used to explicitly guarantee the mutual exclusion of critical sections. Your implementation must guarantee that threads waiting to acquire a `FairLock` are awakened in a **FIFO order**
* implement also the class `Condition` that provides the two methods `await()` and `signal()` that are used, respectively, to block a thread on a `Condition` variable and to awake the first thread (if any) blocked on the `Condition` variable. In other words `Condition` variables must be implemented as **FIFO queues**. The semantics of the signal operation must be *"signal-and-urgent*". Remember that every instance of the class `Condition` must be intrinsically bound to a lock (instance of the class `FairLock`). For this reason, the class `FairLock` provides, in addition to methods `lock()` and `unlock()`, also the method `newCondition()` that returns a new `Condition` instance that is bound to this `FairLock` instance.

### Assignment 2
#### 2.0
As a simple example of the use of the previous mechanism, implement a **manager of a single resource** that dynamically allocates the resource to three client threads: `ClientA1`, `ClientA2` and `ClientB`. If the resource is in use by `ClientA1`  or by `ClientA2`, when it is released and both `ClientB` and the other `ClientA` are waiting for the resource, `ClientB` must be privileged.
#### 2.1
Provide also the implementation of the same manager but now by using the analogous mechanism provided by Java (`Lock` and `Condition` variables whose behaviour is in accordance with the semantics *“signal-and-continue”* and point out the differences, if any, between this implementation and the previous one.

### Assignment 3
#### 3.0
By using the language FSP, provide the design model of the problem described at point 2.0.
#### 3.1
From the design model described at point 3.0, derive the corresponding Java program implemented by using the `Lock`  and `Condition` variables provided by Java and whose behaviour is in accordance with the semantics **“signal-and-continue”**.
#### 3.2
By modeling this implementation with the FSP language, verify that it satisfies the problem’s specification.

## Tools
The tools used for the project were
* [Java JDK 8u111](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) for implementing and testing
* [LTS Analyzer](https://www.doc.ic.ac.uk/ltsa/) for analysis using the FSP language

## Project Structure
The project folder resembles the following structure:

* `/assignment1`:
	* `/locks`: Java files for `FairLock` implementation
	* `/test`: Java files for testing
* `/assignment2`:
	* `/clients`: Java files for clients implementation
	* `/managers`: Java files for managers implementation
	* `/test`: Java files for testing
* `/assignment3`:
	* `/implementation`: Java files for manager implementation
	* `/models`: LTSA models
	* `/test`: Java files for testing

The design workflow can be found in the [report](https://github.com/gabrielebaris/Fair-Lock-Assignment/blob/master/Report.pdf) file.

## License
This project is licensed under the MIT License - see the [LICENSE](https://github.com/gabrielebaris/Fair-Lock-Assignment/blob/master/LICENSE) file for details

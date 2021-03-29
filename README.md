# Task Manager

With Task Manager we refer to a software component that is designed for handling multiple processes inside an operating
system. Each process is identified by 2 fields, a unique unmodifiable **identifier** (PID), and a **priority** (low,
medium, high). The process is immutable, it is generated with a priority and will die with this priority – each process
has a `kill()` method that will destroy it We want the Task Manager to expose the following functionality:

* Add a process
* List running processes
* Kill/KillGroup/KillAll

## Add a process (1/3)

The task manager should have a prefixed maximum capacity, so it can not have more than a certain number of running
processes within itself. This value is defined at build time. The `add(process)` method in TM is used for it. The
default behaviour is that we can accept new processes till when there is capacity inside the Task Manager, otherwise we
won’t accept any new process.

## Add a process – FIFO approach (2/3)

A different customer wants a different behaviour:
he’s asking to accept all new processes through the
`add()` method, killing and removing from the TM list the oldest one (First-In, First-Out) when the max size is reached.

## Add a process – Priority based (3/3)

A new customer is asking something different again, every call to the `add()` method, when the max size is reached,
should result into an evaluation: if the new process passed in the `add()` call has a higher priority compared to any of
the existing one, we remove the lowest priority that is the oldest, otherwise we skip it.

## List running processes

The task manager offers the possibility to `list()` all the running processes, sorting them by time of creation
(implicitly we can consider it the time in which has been added to the TM), priority or id.

## Kill, KillGroup and KillAll

Model one or more methods capable of:

1. killing a specific process
2. killing all processes with a specific priority
3. killing all running processes

## Out of scope

As this is an exercise in clean coding, the following is not covered for the sake of simplicity / time consumption:

* Process stream handling
* Setting OS specific process priority
* Various process creation and execution issues that can arise and be handled on the fly
* Interpreter configuration, '/bin/bash' is hardcoded
* Execution directory configuration, '/tmp' is hardcoded
* Logging
* Security
* Command line tools or wrapping service that would enable usage / integration of the library
* Process termination feedback mechanism, such as the CompletableFuture in the underlying Java implementation
* Terminating long-running processes after TaskManager shut down
* Explore thread safety optimization

## Running the tests

```shell
./gradlew clean test
```

## Usage

```kotlin
// create a manager instance, choose type and process capacity
val myManager = TaskManager.new(TaskManagerType.SIMPLE, 10)
// run your command and get the PID reference
val identifier = taskManager.add("echo 'I'm a process!'", Priority.HIGH)
// get a list of running processes, sorted by desired order
val myProcesses = taskManager.list(SortOrder.ID)
// kill a specific process
val killedSingleCount = taskManager.kill(identifier)
// ...or a group by Priority
val killedGroupCount = taskManager.killGroup(Priority.MEDIUM)
// ...or all managed processes
val killedAllCount = taskManager.killAll()
```

For more info, check out the Kotlin documentation.
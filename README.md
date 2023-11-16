## Task execution toolkit
### Introduction
* A simple library that focus on low code programming paradigm
* It consists of multiple modules:
  * Core
  * Persistence
  * Starter
  * Sample Project

### Core
* Contains basic interfaces and classes that helps defineing tasks
* There are 2 main interfaces: **Task** and **Activity**
  * Task
    * Define the instruction that library's client wants to execute.
    * There are 2 types of tasks: **PluginTask** and **CompositeTask**.
    * PluginTask, when executed, will load invoke a Java code that will perform the job.
    * CompositeTask, on the other hand, contains multiple activities linked together. Its execution begins from a **StartActivity** and terminates when it reaches an **EndActivity**
  * Activity
    * Define each step in a **CompositeTask**.
    * Activity can be a loop, a try/catch block or simply call another task.
    * There are multiple types of activities: ForEach, Group, Trial, TaskBased, etc.
### Persistence
* The main purpose of this module is convert tasks to POJO objects (DTO) so that they can easily be serialized to JSON or be stored into database. The actual persistence implementation is free of choice.
### Starter
[TBD]
### Sample project
* A project for testing and demonstrate various usecases of the library.
## Task execution toolkit
### Introduction
* A simple library that focuses on low code programming paradigm
* It consists of multiple modules:
  * Core
  * Persistence
  * Sample Project
* Some UI (to be developed)

![testGroup.png](sample-project%2Fsrc%2Ftest%2Fjava%2Fdoc-files%2Fimages%2FtestGroup.png)

![testForEachSimple.png](sample-project%2Fsrc%2Ftest%2Fjava%2Fdoc-files%2Fimages%2FtestForEachSimple.png)

![testSimpleGuard.png](sample-project%2Fsrc%2Ftest%2Fjava%2Fdoc-files%2Fimages%2FtestSimpleGuard.png)

![testOtherwise.png](sample-project%2Fsrc%2Ftest%2Fjava%2Fdoc-files%2Fimages%2FtestOtherwise.png)

### Core
* Contains basic interfaces and classes that helps defineing tasks
* Main components:
  * EventSource:
    * Define a place where the event got fired up. The event will then be handled by **Handler** (see next section).
    * Some examples: HttpEventSource, TimerEventSource, CronEventSource, ActiveMQEventSource, etc.
  * Task
    * Define the instruction that library's client wants to execute.
    * There are 2 types of tasks: **Function** and **Handler**.
    * Function, contains multiple activities linked together. Function can be called from another function (using _FunctionCallActivity_) or from outside world (library's client). Its execution begins from a **StartActivity** and terminates when it reaches an **EndActivity**
    * Handler is similar to _Function_, but instead of being called from another Function, it will be called internally (from the engine) to handle events dispatching from **EventSource**.
  * Activity
    * Define each step in a **Function**.
    * Activity can be a loop, a try/catch block or simply call another function.
    * There are multiple types of activities: _ForEach_, _Group_, _Trial_, _FunctionCall_, _PluginCall_, _Rethrow_, etc.
  * Plugin
    * Plugin, when executed, will invoke a Java code that will perform the job.
    * Plugin can return a value, but it must be serializable to JSON (Using Jackson library).
  * EngineRepository
    * [TBD]
  * TaskContainer
    * [TBD]
### Persistence
* The main purpose of this module is convert tasks to POJO objects (DTO) so that they can easily be serialized to JSON or be stored into database. The actual persistence implementation is free of choice.
### Event [DRAFT]
* List of activities:
  * FromSource
  * SendToSink
  * Start/Stop Source/Sink (manually/programmatically)
* Source/Sink auto start (when start container)
* Lazy Sink (only start when there are a sendToSink event)
* SourceSinkContainer
* Source/Sink identified by name
* Source/Sink plugin class
* Reply to source using output of end activity
* Send to Sink may reply back something
### Sample project
* A project for testing and demonstrate various usecases of the library.
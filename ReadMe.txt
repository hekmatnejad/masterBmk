There are two separate benchmark runner modules for Drools(5.6.0) and Drools(6.3.0) named drools5-BMk and drools6-Bmk respectively. 
- BenchmarkRunner is responsible to execute the benchmarks.
- It is recommended to uncomment only one block for each benchmarking purpose.
- benchmark config files are located in openCDS module. You can change them for different purposes.

each config file has the following parameters:

<param name="japex.engine" value="phreak"/>  <!-- rete or phreak -->
<param name="japex.disableProfiler" value="true"/>  <!-- true for generating profile files --> 
...

If you are openning the project from IntelliJIDEA, then there are 4 Run configuration, which makes it easy to run the code. drl5 postfic stands for Drools5 and drl6 stands for Drools6.


In order to use Jprofiler you might want to use the below line as vm options ((after installing JProfiler you should change "F:\FixedPath\jprofiler8" to your local JProfiler directory)
-Xms256m -Xmx1024m -XX:MaxPermSize=256m "-agentpath:F:\FixedPath\jprofiler8\bin\windows-x64\jprofilerti.dll=offline,id=1009,config=F:\FixedPath\jprofiler8\bin\config.xml" "-Xbootclasspath/a:F:\FixedPath\bin\agent.jar"

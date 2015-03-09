package benchmarking;

import com.jprofiler.api.agent.Controller;
import com.sun.japex.Japex;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/4/13
 * Time: 12:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class BenchmarkUtil {

    //to use Jprofiler you might want to use the below line as vm options (change "I:\FixedPath\jprofiler_windows-x64_8_0_1\jprofiler8" to your local jprofile directory)
    //-Xms256m -Xmx1024m -XX:MaxPermSize=256m "-agentpath:F:\FixedPath\jprofiler8\bin\windows-x64\jprofilerti.dll=offline,id=1009,config=F:\FixedPath\jprofiler8\bin\config.xml" "-Xbootclasspath/a:F:\FixedPath\bin\agent.jar"
/*
    public static void main(String[] args) {

//        runBenchmarkFromConfigFile("opencds/benchmarking/phreak/opencdsBnk-config-phreak.xml","japex");
//        runBenchmarkFromConfigFile("opencdsBnk-configComplex.xml","japex");
        //OpenCdsBenchmarkingTraitComplex.setKbFacilitator(new KBFacilitator());
        //OpenCdsBenchmarkingNativeComplex.setKbFacilitator(new KBFacilitator());
        //runBenchmarkFromConfigFile("opencds/benchmarking/phreak/opencdsBnk-configComplex-phreak.xml", "japex");
//        runBenchmarkFromConfigFile("traitDon-config.xml","japex");
//        runBenchmarkFromConfigFile("highlyJoin-config.xml","japex");
//        runBenchmarkFromConfigFile("BasicMultiObject-config.xml","japex");
//        runBenchmarkFromConfigFile("isAProfiler-config.xml","japex");
    }
*/
    public static void runBenchmarkFromConfigFile(String fName, String dirName)
    {
        Japex japex = new Japex();
        japex.setHtml(true);
        //if(java.nio.file.Files.notExists(Paths.get(dirName)))
        File theDir = new File(dirName);
        if (!theDir.exists()) {
            try{
                theDir.mkdir();
            } catch(SecurityException se){
                System.out.println(dirName+" directory cannot be created!");
                return;
            }
        }
        japex.setOutputDirectory(new File(dirName));
        //japex.setOutputWriter( new PrintWriter(System.out));
        japex.run(  ClassLoader.getSystemResource(fName).getPath().replaceAll("%20", " ") );
    }

    public static void startProfiler(String bookmark)
    {
        Controller.startCPURecording(true);
        Controller.addBookmark(bookmark);
    }

    public static void stopProfiler(String postfix, int maxStep, String dirName, String bmkName, String engine)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = dateFormat.format(new Date());
        Controller.saveSnapshot(new File(dirName+"/"+engine+"_"+bmkName+"_"+postfix+"_"+date+"("+maxStep+").jps"));
        Controller.stopCPURecording();
    }
}

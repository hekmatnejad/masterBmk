package opencds.benchmarking.phreak;

import benchmarking.BenchmarkUtil;
import com.sun.japex.JapexDriver;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import drools.traits.util.KBSessionInterface;
import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/7/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class HighlyUsedJoinTrait extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 1000;
    private static KBSessionInterface kbFacilitator = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);
    double[][] warmups;
    int wCounter = 0;
    int WC = 0;
    int tCounter = -1;
    int TN = 12;
    private boolean activeProfile = true;
    private KBSessionInterface.Engine engine = KBSessionInterface.Engine.PHREAK;
    private String profilingTestCase = "";


    public static void setKbFacilitator(KBSessionInterface kb)
    {
        kbFacilitator = kb;
    }

    @Override
    public void initializeDriver() {
        if(kbFacilitator==null)
        {
            System.err.println(">>use method setKbFacilitator in order to assign a KBFacilitator object from drl5 or drl6 modules.");
            System.exit(0);
        }
        WC = Integer.parseInt(getParam("japex.warmupIterations"));
        maxStep = Integer.parseInt(getParam("japex.maxStep"));
        if( getParam("japex.engine").equalsIgnoreCase("phreak"))
            engine =  KBSessionInterface.Engine.PHREAK;
        else
            engine =  KBSessionInterface.Engine.RETEOO;
        if( getParam("japex.disableProfiler").equalsIgnoreCase("true"))
            activeProfile = false;
        profilingTestCase = getParam("japex.profilingTestCase");
        warmups = new double[TN][WC];
        String strTraitable = "import org.drools.core.factmodel.traits.Traitable;\n";
        //if(getParam("japex.droolsVersion").startsWith("5"))
        if(kbFacilitator.getDroolsVersion().startsWith("5"))
            strTraitable = "import org.drools.factmodel.traits.Traitable;\n";
        System.out.println("\ninitializeDriver");

        drl = "package opencds.benchmarking.phreak;\n" +
                "\n" +
                strTraitable +
                "import java.util.*;\n" +
                "\n" +
                "declare InputObject\n" +
                "@Traitable\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "end\n" +
                "\n" ;

        for(int i=0; i<maxStep; i++){
            drl +=
                    "declare trait JoinT00"+i+"\n" +
                    "@propertyReactive\n" +
                    "end\n" +
                    "\n" ;
        }

        String rule = "";

            rule +=
                    "rule \"Initiate Joins\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00A001\" )\n" +
                            "then\n"+
                            "    don( $obj, java.util.Arrays.asList(";
        for(int i=0; i<maxStep; i++){
            rule += "JoinT00"+i+".class";
            if(i!=(maxStep-1))
                rule += ", ";
        }

        rule += "));\n" +
                "end\n";


        rule +=
                "rule \"Highly Join Check\"\n" +
                        "no-loop\n" +
                        "when\n" +
                        "    $obj : InputObject( $id : id == \"00A001\" \n" +
                        "";
        for(int i=0; i<maxStep; i++){

            rule +=
                            ",this isA JoinT00"+i+"\n";
        }

        rule += ")\n" +
                "then\n" +
//                "   System.out.println(\">>>fired\");\n" +
                "end\n";
        drl += rule;
//        System.out.println(drl);
        kbFacilitator.createKBFacilitator(false,"",drl,engine,true);
        kbFacilitator.fireAllRules();
        kbFacilitator.clearAgenda();



    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);

        try {
            kbFacilitator.createInstanceFromFactType( "opencds.benchmarking.phreak", "InputObject" );
            Object obj = kbFacilitator.getNewInstanceFromFactType();
            kbFacilitator.setObjectProperty(obj, "id", "00A001");
            facts.add(obj);
//            kbFacilitator.insertToKB(obj);
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        wCounter = 0;
        tCounter++;
    }

    @Override
    public void warmup(TestCase testCase) {
        long start = System.nanoTime();
        kbFacilitator.fireAllRules();
        warmups[tCounter][wCounter++] += (System.nanoTime()-start)/(double)1e6;
        assertEquals(0, kbFacilitator.clearVM());
        for ( Object obj:facts )
            kbFacilitator.insertToKB(obj);
        System.out.println("WT: " + (System.nanoTime() - start) / 1000000);
    }

    @Override
    public void run(TestCase testCase) {

        if(testCase.getName().equalsIgnoreCase(profilingTestCase) && activeProfile)
            BenchmarkUtil.startProfiler(this.getClass().getSimpleName());
//        for ( Object obj:facts )
//            kbFacilitator.insertToKB(obj);
        int fired = kbFacilitator.fireAllRules();
        System.out.println(fired);

//        assertEquals(0, kbFacilitator.clearVM());
        if(testCase.getName().equalsIgnoreCase(profilingTestCase) && activeProfile)  {
            BenchmarkUtil.stopProfiler(testCase.getName(), maxStep, "jprofiler", this.getClass().getSimpleName(), engine.name());
            System.out.println(">>>Profiling is completed!!!");
        }
    }

    @Override
    public void finish(TestCase testCase) {

        assertEquals(0,kbFacilitator.clearVM());
        if(testCase.getName().equals("test12"))
        {
            System.out.println("warmups: ");
            for(int j=0; j<TN; j++)
                for(int i=0; i< WC ; i+=4) {
                    System.out.println(warmups[j][i]);
                }
        }
    }

}

package opencds.benchmarking.phreak;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/7/13
 * Time: 5:16 PM
 * To change this template use File | Settings | File Templates.
 */

import benchmarking.BenchmarkUtil;
import com.sun.japex.JapexDriver;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import drools.traits.util.KBSessionInterface;

import java.util.ArrayList;
import java.util.Collection;

import static junit.framework.Assert.assertEquals;


public class OpenCdsBenchmarkingNativeComplexOrg extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 500;
    private static KBSessionInterface kbFacilitator;
    private static Collection<Object> facts = new ArrayList<Object>(maxStep);
    private double[][] warmups;
    private int wCounter = 0;
    private int WC = 0;
    private int tCounter = -1;
    private int TN = 12;
    private boolean activeProfile = true;
    private KBSessionInterface.Engine engine = KBSessionInterface.Engine.PHREAK;
    private String profilingTestCase = "";


    public static void setKbFacilitator(KBSessionInterface kb)
    {
        kbFacilitator = kb;
    }

    @Override
    public void initializeDriver() {
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
        System.out.println("\ninitializeDriver");


        drl = "package opencds.benchmarking.phreak;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import java.util.*;\n" +
                "\n" +
                "declare InputObject\n" +
                "   id : String \n" +
                "end\n" +
                "\n" +
                "declare CA\n" +
                "   tid : String \n" +
                "end\n" +
                "\n" +
                "declare CB\n" +
                "   tid : String \n" +
                "end\n" +
                "\n" +
                "declare CC\n" +
                "   tid : String \n" +
                "   xid : String \n" +
                "end\n" +
                "\n" +
                "declare CD\n" +
                "   tid : String \n" +
                "   sid : String \n" +
                "end\n" +
                "\n" +
//                "declare CCA\n" +
//                "   id : String \n" +
//                "end\n" +
//                "\n" +
//                "declare CCB\n" +
//                "   id : String \n" +
//                "end\n" +
//                "\n" +
//                "declare CCC\n" +
//                "   id : String \n" +
//                "end\n" +
                "";

        String rule = "";
        for(int i=0; i<=maxStep; i++){

            rule +=
                    "rule \"CA 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00A"+i+"\" )\n" +
                    "then\n" +
                    "    CA ca = new CA();\n" +
                    "    ca.setTid( $id );\n" +
                    "    insert( ca );\n" +
                    "end\n" +
                    "\n" +
                    "rule \"CB 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00B"+i+"\" )\n" +
                    "then\n" +
                    "    CB cb = new CB();\n" +
                    "    cb.setTid( $id );\n" +
                    "    insert( cb );\n" +
                    "end\n" +
                    "\n" +
                    "rule \"CC 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
                    "then\n" +
                    "    CC cc = new CC();\n" +
                    "    cc.setTid( $id );\n" +
                    "    cc.setXid( \"00B"+i+"\" );\n" +
                    "    insert( cc );\n" +
                    "end\n" +
                    "\n" +
                    "\n" +
                    "rule \"CD 1001"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +
                    "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
                    "then\n" +
                    "    CD cd = new CD();\n" +
                    "    cd.setTid( \"00B"+i+"\" );\n" +
                    "    cd.setSid( \"00A"+i+"\" );\n" +
                    "    insert( cd );\n" +
                    "end\n" +
//                    "\n" +
//                    "rule \"CCA 1001"+i+"\"\n" +
//                    "no-loop\n" +
//                    "when\n" +
//                    "    $obj : InputObject( $id : id == \"00A"+i+"\" )\n" +
//                    "then\n" +
//                    "    CCA ca = new CCA();\n" +
//                    "    ca.setId( $id );\n" +
//                    "    insert( ca );\n" +
//                    "end\n" +
//                    "\n" +
//                    "rule \"CCB 1001"+i+"\"\n" +
//                    "no-loop\n" +
//                    "when\n" +
//                    "    $obj : InputObject( $id : id == \"00B"+i+"\" )\n" +
//                    "then\n" +
//                    "    CCB ca = new CCB();\n" +
//                    "    ca.setId( $id );\n" +
//                    "    insert( ca );\n" +
//                    "end\n" +
//                    "\n" +
//                    "rule \"CCC 1001"+i+"\"\n" +
//                    "no-loop\n" +
//                    "when\n" +
//                    "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
//                    "then\n" +
//                    "    CCC ca = new CCC();\n" +
//                    "    ca.setId( $id );\n" +
//                    "    insert( ca );\n" +
//                    "end\n" +
                    "\n" +
                    "\n" +
                    "\n" +
                    "rule \"FinalCheck"+i+"\"\n" +
                    "no-loop\n" +
                    "when\n" +

                            "    $x : CA( $ca : tid == \"00A"+i+"\" )\n" +
                            "    $y : CB( $cb : tid == \"00B"+i+"\" )\n" +
                            "    $z : CC( $cc : tid, xid == $cb )\n" +
                            "    $w : CD( sid == $ca, tid == $cb)\n" +

//                    "    $x : CA( $ca : tid == \"00A"+i+"\" )\n" +
//                    "    $y : CB( $cb : tid == \"00B"+i+"\" )\n" +     //
//                    "    $z : CC( $cc : tid, xid == $cb )\n" +         //
//                    "    $w : CD( sid == $ca, tid == $cb)\n" +

//                    "    $x2 : CCA( $cca : id == \"00CA"+i+"\" )\n" +
//                    "    $y2 : CCB( $ccb : id == \"00CB"+i+"\" )\n" +
//                    "    $z2 : CCC( $ccc : id == \"00CC"+i+"\" )\n" +
                    "then\n" +
                    "      //System.out.println($w);\n"+
                    "end\n" +
                    "";
        }

        drl += rule;
        kbFacilitator.createKBFacilitator(false, "", drl, engine, false);
        kbFacilitator.fireAllRules();
        kbFacilitator.clearAgenda();

    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);

        try {
            kbFacilitator.createInstanceFromFactType("opencds.benchmarking.phreak","InputObject");
            for ( int j = 0; j < maxStep; j++ ) {
                Object obj = null;
                obj = kbFacilitator.getNewInstanceFromFactType();
                kbFacilitator.setObjectProperty(obj,"id","00A"+j);
                facts.add(obj);
                kbFacilitator.insertToKB(obj);
                obj = kbFacilitator.getNewInstanceFromFactType();
                kbFacilitator.setObjectProperty(obj, "id","00B"+j);
                facts.add(obj);
                kbFacilitator.insertToKB(obj);
                obj = kbFacilitator.getNewInstanceFromFactType();
                kbFacilitator.setObjectProperty(obj, "id","00C"+j);
                facts.add(obj);
                kbFacilitator.insertToKB(obj);
            }
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
        assertEquals(0,kbFacilitator.clearVM());
        for(Object obj:facts)
            kbFacilitator.insertToKB(obj);
        System.out.println("WT: "+ (System.nanoTime()-start)/1000000 );
    }

    @Override
    public void run(TestCase testCase) {
        if(testCase.getName().equalsIgnoreCase(profilingTestCase) && activeProfile)
            BenchmarkUtil.startProfiler(this.getClass().getSimpleName());
//        for(Object obj:facts)
//            kbFacilitator.insertToKB(obj);
        int fired = kbFacilitator.fireAllRules();
        System.out.println(fired);

//        assertEquals(0,kbFacilitator.clearVM());
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

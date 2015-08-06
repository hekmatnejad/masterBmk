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
public class OpenCdsBenchmarkingTraitComplexOrg extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 500;
    private static KBSessionInterface kbFacilitator = null;
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
        System.out.println("\ninitializeDriver");

        String strTraitable = "import org.drools.core.factmodel.traits.Traitable;\n";
        if(kbFacilitator.getDroolsVersion().startsWith("5"))
            strTraitable = "import org.drools.factmodel.traits.Traitable;\n";

        drl = "package opencds.benchmarking.phreak;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import org.opencds.vmr.v1_0.internal.datatypes.*;\n" +
                strTraitable +
                "import java.util.*;\n" +
                "" +
                "declare InputObject\n" +
                "@Traitable\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "   sID : String\n" +
                "   tID : String\n" +
                "end\n" +
                "" +
                "declare trait RelationTrait\n" +
                "@propertyReactive\n" +
                "    sID : String @position(0)\n" +
                "    tID : String @position(1)\n" +
                "end\n" +
                "" +
                "declare trait TA\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "end\n" +
                "\n" +
                "declare trait TB extends RelationTrait\n" +
                "@propertyReactive\n" +
                "   //id : String \n" +
                "end\n" +
                "\n" +
                "declare trait TC\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "end\n" +
                "\n" +
                "declare trait TD\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "end\n"+
                "";

        String rule = "";

        for(int i=0; i<maxStep; i++){

            rule +=
                            "rule \"TA 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00A"+i+"\" )\n" +
                            "then\n" +
                            "    TA ta = don( $obj , TA.class );\n" +
                            "end\n" +
                            "\n" +
                            "rule \"TB 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00B"+i+"\" )\n" +
                            "then\n" +
                            "    TB tb = don( $obj , TB.class );\n" +
                            "end\n" +
                            "\n" +
                            "rule \"TC 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
                            "then\n" +
                            "    TC tc = don( $obj , TC.class );\n" +
                            "end\n" +
                            "\n" +
                            "\n" +
                            "rule \"TD 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obj : InputObject( $id : id == \"00C"+i+"\" )\n" +
                            "then\n" +
                            "    TD td = don( $obj , TD.class );\n" +
                            "end\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "rule \"FinalCheck"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $x : TA( $ta := id == \"00A"+i+"\" )\n" +
                            "    $y : TB( $ta, $tc; )\n" +
                            "    $z : TC( $tc := id == \"00C"+i+"\", this isA TD )\n" +
                            "then\n" +
                            "      //System.out.println($x);\n"+
                            "      //System.out.println($y);\n"+
                            "      //System.out.println($z);\n"+
                            "      //System.out.println();\n"+
                            "end\n" +
                            "";
        }

        drl += rule;
        kbFacilitator.createKBFacilitator(false, "", drl, engine, true);
        kbFacilitator.fireAllRules();
        kbFacilitator.clearAgenda();
    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);
        try {
            kbFacilitator.createInstanceFromFactType( "opencds.benchmarking.phreak", "InputObject" );
            for ( int j = 0; j < maxStep; j++ ) {

                Object obj = null;
                obj = kbFacilitator.getNewInstanceFromFactType();
                kbFacilitator.setObjectProperty(obj, "id", "00A" + j);
                facts.add(obj);
                kbFacilitator.insertToKB(obj);
                obj = kbFacilitator.getNewInstanceFromFactType();
                kbFacilitator.setObjectProperty(obj, "id", "00B" + j);
                kbFacilitator.setObjectProperty(obj, "sID", "00A" + j);
                kbFacilitator.setObjectProperty(obj, "tID", "00C" + j);
                facts.add(obj);
                kbFacilitator.insertToKB(obj);
                obj = kbFacilitator.getNewInstanceFromFactType();
                kbFacilitator.setObjectProperty(obj,"id","00C"+j);
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

        assertEquals(0, kbFacilitator.clearVM());
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

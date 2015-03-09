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
 * Date: 10/15/13
 * Time: 1:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class TraitDonBmk extends JapexDriverBase implements JapexDriver {

    private static String drlHeader = "";
    private static String drl = "";
    private static String rule = "";
    private static int maxStep = 100;
    private static KBSessionInterface kbFacilitator = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);
    private double[][] warmups;
    private int wCounter = 0;
    private int WC = 0;
    private int tCounter = -1;
    private int TN = 13;
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

        drl = "package drools.traits.benchmarks;\n" +
                "\n" +
//                "import org.drools.core.factmodel.traits.Traitable;\n" +
                strTraitable +
                "";


        rule =
                "\n" +
                "rule \"match and don\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $obj : TestClass( $id : hField0 == \"val-000\")    // , hiddenField0 == \"0\" )\n" +
                "then\n" +
//                "    TestTrait tt = don( $obj , TestTrait.class );\n" +
                        "don( $obj , TestTrait.class );\n" +
                "end\n" +
                "\n" +
                "rule \"match trait\"\n" +
                "no-loop\n" +
                "when\n" +
                "    $obj : TestTrait( hField0 == \"val-000\" ) //, sField0 == 0)\n" +
                "then\n" +
                "    //System.out.println($obj);\n" +
                "end\n" +
                "\n" +
                "";
        drlHeader = drl;
        tCounter = -1;
    }

    @Override
    public void prepare(TestCase testCase) {
        int hardFieldNum = testCase.getIntParam("HardFieldNum");
        int softFieldNum = testCase.getIntParam("SoftFieldNum");
        int hiddenFieldNum = testCase.getIntParam("HiddenFieldNum");
        String strClass = "";
        String strTrait = "";
        strClass =
                "\n" +
                "declare TestClass\n" +
                "@Traitable\n" +
                "@propertyReactive\n";
        strTrait =
                "\n" +
                "declare trait TestTrait\n" +
                "@propertyReactive\n" ;

        for(int s=0; s< hardFieldNum; s++)
        {
            strClass += "   hField"+s+" : String = \"" + s + "\"\n";
            strTrait += "   hField"+s+" : String = \"" + s + "\"\n";

        }
        for(int hd=0; hd< hiddenFieldNum; hd++)
        {
            strClass += "   hiddenField"+hd+" : String = \"" + hd + "\"\n";
        }
        strClass += "end\n";
        for(int h=0; h< softFieldNum; h++)
        {
            strTrait += "   sField"+h+" : Integer = " + h + "\n";
        }
        strTrait += "end\n" ;

        drl = drlHeader + strClass + strTrait + rule;

//        System.out.println(drl);

        kbFacilitator.createKBFacilitator(false,"",drl,engine,true);
        kbFacilitator.fireAllRules();
        kbFacilitator.clearAgenda();

        facts = new ArrayList<Object>(maxStep);

        try {
            for ( int j = 0; j < maxStep; j++ ) {
                kbFacilitator.createInstanceFromFactType( "drools.traits.benchmarks", "TestClass" );

                Object obj = null;
                obj = kbFacilitator.getNewInstanceFromFactType();
                for(int s = 0; s< hardFieldNum; s++)
                    kbFacilitator.setObjectProperty(obj, "hField" + s, "val-00" + (0));
                facts.add(obj);
                //kbFacilitator.insertToKB(obj);

//                for ( Field fld : inputObject.getFactClass().getFields() ) {
//                    System.out.println( fld );
//                }
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
        for ( Object obj:facts ) {

            kbFacilitator.insertToKB(obj);
        }
        long start = System.nanoTime();
        kbFacilitator.fireAllRules();
        warmups[tCounter][wCounter++] += (System.nanoTime()-start)/(double)1e6;
        assertEquals(0, kbFacilitator.clearVM());
        System.out.println("WT: " + (System.nanoTime() - start) / 1000000);
    }

    @Override
    public void run(TestCase testCase) {
        if(testCase.getName().equalsIgnoreCase(profilingTestCase) && activeProfile)
            BenchmarkUtil.startProfiler(this.getClass().getSimpleName());
        for ( Object obj:facts ) {

            kbFacilitator.insertToKB(obj);
        }
        int fired = kbFacilitator.fireAllRules();
        System.out.println(fired);

        assertEquals(0, kbFacilitator.clearVM());
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

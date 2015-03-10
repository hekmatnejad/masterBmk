package opencds.benchmarking.phreak;

import benchmarking.BenchmarkUtil;
import com.sun.japex.JapexDriver;
import com.sun.japex.JapexDriverBase;
import com.sun.japex.TestCase;
import drools.traits.util.KBSessionInterface;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.datatypes.CD;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;


/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 10/7/13
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class OpenCdsBenchmarkingTrait extends JapexDriverBase implements JapexDriver {

    private static String drl = "";
    private static int maxStep = 100;
    private static KBSessionInterface kbFacilitator = null;
    static Collection<Object> facts = new ArrayList<Object>(maxStep);
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
        //if(getParam("japex.droolsVersion").startsWith("5"))
        if(kbFacilitator.getDroolsVersion().startsWith("5"))
            strTraitable = "import org.drools.factmodel.traits.Traitable;\n";

        drl = "package opencds.benchmarking.phreak;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import org.opencds.vmr.v1_0.internal.datatypes.*;\n" +
                //"import org.drools.core.factmodel.traits.Traitable;\n" +
                strTraitable+
//                "import org.drools.factmodel.traits.Thing;\n" +
                "import java.util.*;\n" +
                "\n" +
                "declare ObservationResult2\n" +
                "@Traitable\n" +
                "@propertyReactive\n" +
                "   id : String \n" +
                "   observationFocus : CD \n " +
                "   subjectIsFocalPerson : boolean \n" +
                "   observationValue : ObservationValue \n" +
                "" +
                "end\n" +
                "" +
                "" +
                "declare trait opencdsConcept\n" +
                "@propertyReactive\n" +
                "    openCdsConceptCode : String\n" +
                "    id : String\n" +
                "end\n" +
                "" +
                "";


        String trait = "";
        for(int i=0; i<=maxStep; i++){

            trait +=

                    "" +
                    "declare trait " + "autoTrait001" + i + "  extends opencdsConcept\n" +
                    "@propertyReactive\n" +
                    "    openCdsConceptCode : String = \"C001" + i + "\"\n" +
                    "end\n"  +
                    "declare trait " + "autoTrait002" + i + "  extends opencdsConcept\n" +
                    "@propertyReactive\n" +
                    "    openCdsConceptCode : String = \"C002" + i + "\"\n" +
                    "end\n";
        }

        String rule = "";

        for(int i=0; i<maxStep; i++){

            rule +=
                    "rule \"ObservationFocusConcept by concept 001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obs : ObservationResult2(  $id : id==\"001"+i+"\" , " +
                            "    $code : observationFocus.code == \"10220\",\n" +
                            "        $codeSystem : observationFocus.codeSystem )\n" +
                            "then\n" +
                            "    //System.out.println( \"Claxified \" + $id + \" as auto Trait1 \" + " + i + " ); \n" +
                            "    Object x1 = don( $obs, autoTrait001"+i+".class );\n" +
                            "    //insert(new CD()); \n" +
                            "end\n" +

                            "rule \"ObservationFocusConcept by concept 002"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obs : ObservationResult2(  $id : id==\"001"+(i)+"\" , " +
                            "$code : observationValue.concept.code == \"34254\",\n" +
                            "        $codeSystem : observationFocus.codeSystem )\n" +
                            "then\n" +
                            "    //System.out.println( \"Claxified \" + $id + \" as auto Trait2 \" + " + i + " ); \n" +
                            "    Object x1 = don( $obs, autoTrait002"+(i)+".class );\n" +
                            "    //insert(new CD()); \n" +
                            "end\n";

            rule +=
                    "rule \"IsReportableInfluenza001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
//                            "    $obj : autoTrait001"+i+"($obj2 : this isA autoTrait002"+(i)+".class, $obj3 : getCore())\n" +
                            "    $obj : autoTrait001"+i+"( this isA autoTrait002"+(i)+".class )\n" +
                            "then\n" +
                            "    //System.out.println(\":: InfluenzaTestForOrganism ResultPositive \"+$obj.getId());\n" +
                            "//System.out.println($obj.getId());\n" +
                            " //retract($obj3);\n" +
                            "end\n";

            ///Thing ( this isA autoTrait002 && isA autoTrait001 )


        }

//        rule += "rule \"Clean\"\n" +
//                "salience -999"+
//                "when\n" +
//                "  $s : String()\n" +
//                "  $o : Object( this != $s )\n" +
//                "then\n" +
//                "  if ( ! $o.getClass().getName().contains( \"Initial\" ) ) { \n" +
//                "   retract( $o );\n" +
//                "  } \n" +
//                "  //System.out.println(\"removed\");\n"+
//                "end  ";

        drl += trait + rule;
//        kbFacilitator = new KBFacilitator(false,"",drl,engine,true);
        kbFacilitator.createKBFacilitator(false,"",drl,engine,true);
        kbFacilitator.fireAllRules();
        kbFacilitator.clearAgenda();

    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);

        try {
            kbFacilitator.createInstanceFromFactType( "opencds.benchmarking.phreak", "ObservationResult2" );
            for ( int j = 0; j < maxStep; j++ ) {

                Object obs = null;
                obs = kbFacilitator.getNewInstanceFromFactType();
                CD cdFocus = new CD();
                cdFocus.setCodeSystem("AHRQ v4.3");
                cdFocus.setCode("10220");
                kbFacilitator.setObjectProperty( obs, "id", "001" + Integer.toString(j) );//UUID.randomUUID().toString());
                kbFacilitator.setObjectProperty( obs, "observationFocus", cdFocus );
                kbFacilitator.setObjectProperty( obs, "subjectIsFocalPerson", true );

                CD cdCoded = new CD();
                cdCoded.setCodeSystem("AHRQ v4.3");
                cdCoded.setCode("34254");
                ObservationValue obsValue = new ObservationValue();
                obsValue.setConcept(cdCoded);
                obsValue.setIdentifier(UUID.randomUUID().toString());
                kbFacilitator.setObjectProperty( obs, "observationValue", obsValue );

                facts.add(obs);
                //kbFacilitator.insertToKB(obs);

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

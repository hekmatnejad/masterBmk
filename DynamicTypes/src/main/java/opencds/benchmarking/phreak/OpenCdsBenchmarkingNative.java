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
import org.opencds.vmr.v1_0.internal.ObservationResult;
import org.opencds.vmr.v1_0.internal.ObservationValue;
import org.opencds.vmr.v1_0.internal.datatypes.CD;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;


public class OpenCdsBenchmarkingNative extends JapexDriverBase implements JapexDriver {

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

        drl = "package opencds.benchmarking;\n" +
                "\n" +
                "import org.opencds.vmr.v1_0.internal.*;\n" +
                "import org.opencds.vmr.v1_0.internal.concepts.*;\n" +
                "import java.util.*;\n" +
                "\n";
        String rule = "";
        for(int i=0; i<=maxStep; i++){

            rule +=
                    "rule \"ObservationFocusConcept by concept 1001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obs : ObservationResult( $id : id == \"001"+i+"\", $code : observationFocus.code == \"10220\",\n" +
                            "        $codeSystem : observationFocus.codeSystem )\n" +
                            "then\n" +
                            "    ObservationFocusConcept x1 = new ObservationFocusConcept();\n" +
                            "    x1.setOpenCdsConceptCode( \"C261001"+i+"\" );\n" +
                            "    x1.setConceptTargetId( $id );\n" +
                            "    insert( x1 );\n" +
                            "end\n" +
                            "\n" +
                            "rule \"ObservationFocusConcept by concept 2001"+i+"\"\n" +
                            "no-loop\n" +
                            "when\n" +
                            "    $obs : ObservationResult( $id : id == \"001"+i+"\", $code : observationValue.concept.code == \"34254\",\n" +
                            "        $codeSystem : observationValue.concept.codeSystem )\n" +
                            "then\n" +
                            "    ObservationCodedValueConcept x1 = new ObservationCodedValueConcept();\n" +
                            "    x1.setOpenCdsConceptCode( \"C87001"+i+"\" );\n" +
                            "    x1.setConceptTargetId( $id );\n" +
                            "    insert( x1 );\n" +
                            "end\n" +
                            "\n" +
                            "rule \"IsReportableInfluenza001"+i+"\"\n" +
                            "dialect \"java\"\n" +
                            "when\n" +
                            "      $y : ObservationFocusConcept( openCdsConceptCode == \"C261001"+i+"\" )\n" +
                            "      $z : ObservationCodedValueConcept( openCdsConceptCode == \"C87001"+i+"\" )\n" +
                            "      $x : ObservationResult( id == $y.conceptTargetId,\n" +
                            "                              id == $z.conceptTargetId,\n" +
                            "                              id == \"001"+i+"\" \n"+
                            "                               )\n" +
                            "then\n" +
                            "      //System.out.println($x.getId());\n"+
                            "end\n";
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
//                "end  ";

        drl += rule;
//        kbFacilitator = new KBFacilitator(false,"",drl,engine,false);
        kbFacilitator.createKBFacilitator(false,"",drl,engine,false);
        kbFacilitator.fireAllRules();
        kbFacilitator.clearAgenda();


    }

    @Override
    public void prepare(TestCase testCase) {

        facts = new ArrayList<Object>(maxStep);

        for ( int j = 0; j < maxStep; j++ ) {

            ObservationResult obs = new ObservationResult();
            CD cdFocus = new CD();
            cdFocus.setCodeSystem("AHRQ v4.3");
            cdFocus.setCode("10220");
            obs.setId("001" + Integer.toString(j));//UUID.randomUUID().toString());
            obs.setObservationFocus(cdFocus);
            obs.setSubjectIsFocalPerson(true);

            CD cdCoded = new CD();
            cdCoded.setCodeSystem("AHRQ v4.3");
            cdCoded.setCode("34254");
            ObservationValue obsValue = new ObservationValue();
            obsValue.setConcept(cdCoded);
            obsValue.setIdentifier(UUID.randomUUID().toString());
            obs.setObservationValue(obsValue);

            facts.add(obs);
            kbFacilitator.insertToKB(obs);

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

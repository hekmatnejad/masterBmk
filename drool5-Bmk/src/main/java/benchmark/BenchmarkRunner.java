package benchmark;

import benchmarking.BenchmarkUtil;
import drools.trait.knowledgebase.facilitator.KBFacilitator;
import opencds.benchmarking.phreak.*;


public class BenchmarkRunner extends BenchmarkUtil {

    //to use Jprofiler you might want to use the below line as vm options (change "I:\FixedPath\jprofiler_windows-x64_8_0_1\jprofiler8" to your local jprofile directory)
    //-Xms256m -Xmx1024m -XX:MaxPermSize=256m "-agentpath:F:\FixedPath\jprofiler8\bin\windows-x64\jprofilerti.dll=offline,id=1009,config=F:\FixedPath\jprofiler8\bin\config.xml" "-Xbootclasspath/a:F:\FixedPath\bin\agent.jar"

    public static void main(String[] args) {
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
//        OpenCdsBenchmarkingTrait.setKbFacilitator(new KBFacilitator());
//        OpenCdsBenchmarkingNative.setKbFacilitator(new KBFacilitator());
//        runBenchmarkFromConfigFile("opencds/benchmarking/phreak/opencdsBnk-config-phreak.xml","japex");
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
//        OpenCdsBenchmarkingTraitComplex.setKbFacilitator(new KBFacilitator());
//        OpenCdsBenchmarkingNativeComplex.setKbFacilitator(new KBFacilitator());
//        runBenchmarkFromConfigFile("opencds/benchmarking/phreak/opencdsBnk-config-Complex-phreak.xml", "japex");
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
        OpenCdsBenchmarkingTraitComplexOrg.setKbFacilitator(new KBFacilitator());
        OpenCdsBenchmarkingNativeComplexOrg.setKbFacilitator(new KBFacilitator());
        runBenchmarkFromConfigFile("opencds/benchmarking/phreak/opencdsBnk-config-ComplexOrg-phreak.xml", "japex");
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
//        HighlyUsedJoinNative.setKbFacilitator(new KBFacilitator());
//        HighlyUsedJoinTrait.setKbFacilitator(new KBFacilitator());
//        runBenchmarkFromConfigFile("opencds/benchmarking/phreak/highlyJoin-config-phreak.xml", "japex");
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
//        TraitDonBmk.setKbFacilitator(new KBFacilitator());
//        runBenchmarkFromConfigFile("opencds/benchmarking/phreak/traitDon-config-phreak.xml", "japex");
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//
//        BasicInsertMultiObject.setKbFacilitator(new KBFacilitator());
//        BasicDonMultiObject.setKbFacilitator(new KBFacilitator());
//        runBenchmarkFromConfigFile("opencds/benchmarking/phreak/BasicMultiObject-config-phreak.xml", "japex");
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>//

    }


}

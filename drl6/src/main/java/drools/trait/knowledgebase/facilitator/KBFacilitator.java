package drools.trait.knowledgebase.facilitator;

import drools.traits.util.KBSessionInterface;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.factmodel.traits.TraitFactory;
import org.drools.core.factmodel.traits.VirtualPropertyMode;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.utils.KieHelper;

import java.util.ArrayList;
import java.util.Collection;

/**

 * Created by mamad on 3/7/15.
 */
public class KBFacilitator implements KBSessionInterface {

    private final String drlVersion = "6.3.0";
    private KieSession kieSession;
    private Engine engine;
    private boolean isTrait;
    private FactType inputObject = null;
    private KBFacilitator selfObject= null;

    public KBFacilitator()
    {

    }

    public KBFacilitator(boolean readFromFile, String drlFileName, String drlSource, Engine engine, boolean isTrait) {
        this.engine = engine;
        this.isTrait = isTrait;
        if(readFromFile)
            kieSession = createKBfromDrlFile(drlFileName,engine);
        else
            kieSession = createKBfromDrlSource(drlSource,engine);
        selfObject = this;
    }

    @Override
    public void createKBFacilitator(boolean readFromFile, String drlFileName, String drlSource, Engine engine, boolean isTrait) {
        selfObject = new KBFacilitator(readFromFile, drlFileName, drlSource, engine, isTrait);
    }

    @Override
    public String getDroolsVersion() {
        return drlVersion;
    }

    @Override
    public int fireAllRules() {
        return selfObject.kieSession.fireAllRules();
    }

    @Override
    public void clearAgenda() {
        selfObject.kieSession.getAgenda().clear();
    }

    @Override
    public void insertToKB(Object obj) {
        selfObject.kieSession.insert(obj);
    }

    @Override
    public void createInstanceFromFactType(String strPackage, String strClassName) throws InstantiationException, IllegalAccessException{
        if(selfObject.inputObject==null)
            selfObject.inputObject = selfObject.kieSession.getKieBase().getFactType( strPackage, strClassName );
    }

    @Override
    public Object getNewInstanceFromFactType() throws InstantiationException, IllegalAccessException{
        if(selfObject.inputObject==null){
            System.err.println("<error> FactType object is not instantiated.");
            return null;
        }
        Object obj = selfObject.inputObject.newInstance();
        return obj;
    }

    @Override
    public void setObjectProperty(Object obj, String property, Object value) throws InstantiationException, IllegalAccessException{
        if(selfObject.inputObject==null){
            System.err.println("<error> FactType object is not instantiated.");
        }
        else
            selfObject.inputObject.set(obj,property, value);
    }

    @Override
    public long clearVM() {
        return clearVM(selfObject.kieSession,selfObject.isTrait);
    }

    private KieSession createKBfromDrlFile(String drlFileName, Engine engine)
    {
        KieHelper helper = new KieHelper();
        helper.addResource( KieServices.Factory.get().getResources()
                .newClassPathResource(drlFileName), ResourceType.DRL );
        Results res = helper.verify();
        if ( res.hasMessages( Message.Level.ERROR ) ) {

        }
        KieServices ks = KieServices.Factory.get();
        KieBaseConfiguration conf = ks.newKieBaseConfiguration();
        if(engine==Engine.PHREAK)
            conf.setOption(RuleEngineOption.PHREAK);
        else
            conf.setOption(RuleEngineOption.RETEOO);
        KieSession ksession = helper.build(conf).newKieSession();
        TraitFactory.setMode(VirtualPropertyMode.MAP, ksession.getKieBase());
        return ksession;
    }

    private KieSession createKBfromDrlSource(String drlSource, Engine engine)
    {
        KieHelper helper = new KieHelper();
        helper.addContent(drlSource,ResourceType.DRL);
        Results res = helper.verify();
        if ( res.hasMessages( Message.Level.ERROR ) ) {

        }
        KieServices ks = KieServices.Factory.get();
        KieBaseConfiguration conf = ks.newKieBaseConfiguration();
        if(engine==Engine.PHREAK)
            conf.setOption(RuleEngineOption.PHREAK);
        else
            conf.setOption(RuleEngineOption.RETEOO);

        KieSession ksession = helper.build(conf).newKieSession();
        TraitFactory.setMode(VirtualPropertyMode.MAP, ksession.getKieBase());
        return ksession;

    }

    private long clearVM(KieSession ksession, boolean isTrait)
    {
        Collection<DefaultFactHandle> factHandles = ksession.getFactHandles();
        Collection<DefaultFactHandle> cloneFactHandles = new ArrayList<DefaultFactHandle>(factHandles);
        if(isTrait)
            for(DefaultFactHandle factHandle : cloneFactHandles){
                if(factHandle.isTraitable())
                    ksession.retract(factHandle);
            }
        else
            for(DefaultFactHandle factHandle : cloneFactHandles)
                ksession.retract(factHandle);

        return ksession.getFactCount();
    }

    private static long clearVM2(KieSession ksession)
    {
        FactHandle c = ksession.insert( "clean-all" );
        ksession.fireAllRules();
        ksession.retract( c );
        ksession.fireAllRules();
        return ksession.getObjects().size();
    }
}

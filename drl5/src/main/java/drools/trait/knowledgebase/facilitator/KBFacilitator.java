package drools.trait.knowledgebase.facilitator;

import drools.traits.util.KBSessionInterface;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.DefaultFactHandle;
import org.drools.definition.type.FactType;
import org.drools.factmodel.traits.TraitFactory;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import java.util.ArrayList;
import java.util.Collection;

/**

 * Created by mamad on 3/7/15.
 */
public class KBFacilitator implements KBSessionInterface {

    private final String drlVersion = "5.6.0";
    private StatefulKnowledgeSession kieSession;
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
            kieSession = createKBfromDrlFile(drlFileName,engine).newStatefulKnowledgeSession();
        else
            kieSession = createKBfromDrlSource(drlSource,engine).newStatefulKnowledgeSession();
        if(isTrait)
            TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, kieSession.getKnowledgeBase());

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
            selfObject.inputObject = selfObject.kieSession.getKnowledgeBase().getFactType( strPackage, strClassName );
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

    private KnowledgeBase createKBfromDrlFile(String drlFileName, Engine engine)
    {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newClassPathResource(drlFileName), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            System.err.print(knowledgeBuilder.getErrors().toString());
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;

    }

    private KnowledgeBase createKBfromDrlSource(String drlSource, Engine engine)
    {
        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( ResourceFactory.newByteArrayResource(drlSource.getBytes()), ResourceType.DRL );
        if ( knowledgeBuilder.hasErrors() ) {
            System.err.print( knowledgeBuilder.getErrors().toString() );
        }
        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( knowledgeBuilder.getKnowledgePackages() );
        return knowledgeBase;


    }


    private long clearVM(StatefulKnowledgeSession ksession, boolean isTrait)
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

    private static long clearVM2(StatefulKnowledgeSession ksession)
    {
        FactHandle c = ksession.insert( "clean-all" );
        ksession.fireAllRules();
        ksession.retract( c );
        ksession.fireAllRules();
        return ksession.getObjects().size();
    }
}

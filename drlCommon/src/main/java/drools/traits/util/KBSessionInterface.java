package drools.traits.util;

/**
 * Created by mamad on 3/7/15.
 */
public interface KBSessionInterface {

    public enum Engine{
        RETEOO, PHREAK;
    }

    public void createKBFacilitator(boolean readFromFile, String drlFileName, String drlSource, Engine engine, boolean isTrait);

    public String getDroolsVersion();

    public int fireAllRules();

    public void clearAgenda();

    public void insertToKB(Object obj);

    public void createInstanceFromFactType(String strPackage, String strClassName) throws InstantiationException, IllegalAccessException;

    public Object getNewInstanceFromFactType() throws InstantiationException, IllegalAccessException;

    public void setObjectProperty(Object obj, String property, Object value) throws InstantiationException, IllegalAccessException;

    public long clearVM();
}

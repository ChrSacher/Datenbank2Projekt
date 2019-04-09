package application.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class BaseEntity {

    private Map<String, String> dbEntryValueMap = new HashMap();
    
    @Override
	public boolean equals(Object arg0)
	{
    	BaseEntity other = (BaseEntity)arg0;
    	if(other == null) return false;
    	return dbEntryValueMap.equals(other.dbEntryValueMap);
	}
	public BaseEntity(BaseEntity other)
    {
    	dbEntryValueMap = new HashMap(other.dbEntryValueMap);
    }
    /**
     * @return the dbEntryValueMap
     */
    public Map<String, String> getDbEntryValueMap() {
	return dbEntryValueMap;
    }

    /**
     * @param dbEntryValueMap
     *            the dbEntryValueMap to set
     */
    public void setDbEntryValueMap(Map<String, String> dbEntryValueMap) {
	this.dbEntryValueMap = dbEntryValueMap;
    }

    /**
     * @param dbEntryValueMap
     */
    public BaseEntity(Map<String, String> dbEntryValueMap) {
	super();
	this.dbEntryValueMap = dbEntryValueMap;
    }

    /**
     * 
     */
    public BaseEntity() {
	super();
    }

    public void setValue(String key,String value)
    {
	dbEntryValueMap.put(key, value);
    }
    
    public String getValue(String key)
    {
	return dbEntryValueMap.get(key);
    }
}

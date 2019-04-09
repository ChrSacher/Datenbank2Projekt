package application.TableInfo;

public class TableColumnData {
    private String name = "";
    private String displayName = "";
    private String type = "";
    private String foreignColumn = "";
    private String foreignTable = "";
    
    public String getForeignColumn()
	{
		return foreignColumn;
	}
	public void setForeignColumn(String foreignColumn)
	{
		this.foreignColumn = foreignColumn;
	}
	public String getForeignTable()
	{
		return foreignTable;
	}
	public void setForeignTable(String foreignTable)
	{
		this.foreignTable = foreignTable;
	}
	/**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return the displayName
     */
    public String getDisplayName() {
        return displayName;
    }
    /**
     * @param displayName the displayName to set
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    /**
     * @return the type
     */
    public String getType() {
        return type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    
}

package eg.com.vodafone.mi.export;

import com.vaadin.v7.data.util.sqlcontainer.connection.JDBCConnectionPool;

public class TableExporter
{
    private String tableName;
    private String colPrimaryKey;
    private RelativeTable[] relativeTables;
    private JDBCConnectionPool sourceEnvCon;

    public TableExporter(String tableName, String colPrimaryKey, RelativeTable[] relativeTables,
	    JDBCConnectionPool sourceEnvCon)
    {
	super();
	this.tableName = tableName;
	this.colPrimaryKey = colPrimaryKey;
	this.relativeTables = relativeTables;
	this.sourceEnvCon = sourceEnvCon;
    }

    public String getTableName()
    {
        return tableName;
    }

    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }

    public String getColPrimaryKey()
    {
        return colPrimaryKey;
    }

    public void setColPrimaryKey(String colPrimaryKey)
    {
        this.colPrimaryKey = colPrimaryKey;
    }

    public RelativeTable[] getRelativeTables()
    {
        return relativeTables;
    }

    public void setRelativeTables(RelativeTable[] relativeTables)
    {
        this.relativeTables = relativeTables;
    }

    public JDBCConnectionPool getSourceEnvCon()
    {
        return sourceEnvCon;
    }

    public void setSourceEnvCon(JDBCConnectionPool sourceEnvCon)
    {
        this.sourceEnvCon = sourceEnvCon;
    }

}
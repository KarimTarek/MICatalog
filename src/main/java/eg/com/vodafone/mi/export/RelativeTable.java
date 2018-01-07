package eg.com.vodafone.mi.export;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.com.vodafone.mi.loader.IResultSetHandler;
import eg.com.vodafone.mi.loader.JDBCTemplate;
import eg.com.vodafone.mi.sql.SQLStatement;

public class RelativeTable
{
    private static Logger logger = LoggerFactory.getLogger(RelativeTable.class);

    private String relation;
    private TableExporter tableExporter;

    public RelativeTable(String relation, TableExporter tableExporter)
    {
	super();
	this.relation = relation;
	this.tableExporter = tableExporter;
    }

    public String getRelation()
    {
	return relation;
    }

    public void setRelation(String relation)
    {
	this.relation = relation;
    }

    public TableExporter getTableExporter()
    {
	return tableExporter;
    }

    public void setTableExporter(TableExporter tableExporter)
    {
	this.tableExporter = tableExporter;
    }

    public List<SQLStatement> exportAllRelevantSQLs(String id)
    {
	final List<SQLStatement> result = new ArrayList<SQLStatement>();
	
	String relavantKey = relation.split("=")[1].split("\\.")[1];

	if (relavantKey.contains("|"))
	{
	    String[] keys = relavantKey.split("\\|");
	    for (int i = 0; i < keys.length; i++)
	    {
		result.addAll(getRows(id, keys[i]));
	    }
	}
	else
	{
	    result.addAll(getRows(id, relavantKey));
	}

	return result;
    }

    private List<SQLStatement> getRows(String id, String foreignKey)
    {
	final List<SQLStatement> result = new ArrayList<SQLStatement>();

	JDBCTemplate jdbcTemplate = new JDBCTemplate(this.tableExporter.getSourceEnvCon());

	jdbcTemplate.query("SELECT * FROM " + this.tableExporter.getTableName() + " WHERE " + foreignKey + " = '" + id
		+ "'", new IResultSetHandler()
	{
	    @Override
	    public void handle(ResultSet resultSet)
	    {
		try
		{
		    while (resultSet.next())
		    {
			SQLStatement sql = new SQLStatement(tableExporter.getTableName());

			ResultSetMetaData metaData = resultSet.getMetaData();
			int columnsCount = metaData.getColumnCount();
			for (int i = 1; i < columnsCount + 1; i++)
			{
			    String columnName = metaData.getColumnName(i);
			    String value = resultSet.getString(columnName);
			    sql.addColumnValue(columnName, value);
			}
			result.add(sql);

			for (int i = 0; i < tableExporter.getRelativeTables().length; i++)
			{
			    // String primaryKey = colPrimaryKey;
			    // if (colPrimaryKey.contains("&"))
			    // {
			    // primaryKey = colPrimaryKey.split("&")[0];
			    // }

			    List<SQLStatement> sqLs = tableExporter.getRelativeTables()[i]
				    .exportAllRelevantSQLs(resultSet.getString(getRelevantKey(tableExporter
					    .getRelativeTables()[i])));

			    for (SQLStatement sqlStatement : sqLs)
			    {
				if (!result.contains(sqlStatement))
				    result.add(sqlStatement);
			    }
			}
		    }
		}
		catch (SQLException e)
		{
		    logger.error("Exception while reading from the DB..", e);
		}
	    }

	    private String getRelevantKey(RelativeTable relativeTable)
	    {
		return relativeTable.getRelation().split("=")[0].split("\\.")[1];
	    }
	});

	return result;
    }
}
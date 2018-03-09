package eg.com.vodafone.mi.export;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.util.sqlcontainer.connection.JDBCConnectionPool;

import eg.com.vodafone.mi.loader.IResultSetHandler;
import eg.com.vodafone.mi.loader.JDBCTemplate;
import eg.com.vodafone.mi.sql.SQLStatement;

public class RowChecker
{
    private static Logger logger = LoggerFactory.getLogger(RowChecker.class);

    private JDBCConnectionPool destEnvCon;
    private Map<String, String> tablesPrimaryKeys;

    public RowChecker(JDBCConnectionPool destEnvCon, String[] tablesPrimaryKeys)
    {
	this.destEnvCon = destEnvCon;
	this.tablesPrimaryKeys = new HashMap<String, String>();

	for (int i = 0; i < tablesPrimaryKeys.length; i++)
	{
	    String[] strings = tablesPrimaryKeys[i].split("\\.");
	    this.tablesPrimaryKeys.put(strings[0], strings[1]);
	}
    }

    public boolean isExists(SQLStatement sql)
    {
	final StringBuilder result = new StringBuilder();

	JDBCTemplate jdbcTemplate = new JDBCTemplate(destEnvCon);

	String primaryKey = tablesPrimaryKeys.get(sql.getTable());
	StringBuilder queryString = new StringBuilder("SELECT * FROM " + sql.getTable() + " WHERE ");

	if (!primaryKey.contains("&"))
	{
	    queryString.append(primaryKey + " = '" + getPrimaryKeyValue(sql, primaryKey) + "'");
	}
	else
	{
	    String[] keys = primaryKey.split("&");
	    for (int i = 0; i < keys.length; i++)
	    {
		queryString.append(keys[i] + " = '" + getPrimaryKeyValue(sql, keys[i]) + "' AND ");
	    }

	   queryString.delete(queryString.length() - 4, queryString.length());
	}

	jdbcTemplate.query(queryString.toString(), new IResultSetHandler()
	{

	    @Override
	    public void handle(ResultSet resultSet)
	    {
		try
		{
		    if (resultSet.next())
			result.append("true");
		    else
			result.append("false");
		}
		catch (SQLException e)
		{
		    logger.error("Exception while reading from the DB..", e);
		}
	    }
	});

	return Boolean.parseBoolean(result.toString());
    }

    private String getPrimaryKeyValue(SQLStatement sql, String primaryKey)
    {
	List<String> cols = sql.getCols();

	for (int i = 0; i < cols.size(); i++)
	{
	    if (cols.get(i).equals(primaryKey))
		return sql.getValues().get(i);
	}

	return null;
    }

}
package eg.com.vodafone.mi.loader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;

public class JDBCTemplate
{
    private static Logger logger = LoggerFactory.getLogger(JDBCTemplate.class);
    private JDBCConnectionPool connectionPool;

    public JDBCTemplate(JDBCConnectionPool connectionPool)
    {
	this.connectionPool = connectionPool;
    }
    
    public void query(String queryString, IResultSetHandler handler)
    {
	logger.info("Quering the DB: {}", queryString);
	Connection conn = null;
	Statement statement = null;
	ResultSet resultSet = null;
	try
	{
	    conn = connectionPool.reserveConnection();
	    statement = conn.createStatement();
	    resultSet = statement.executeQuery(queryString);
	    handler.handle(resultSet);
	}
	catch (SQLException e)
	{
	    logger.error("SQLException - JDBCTemplate: ", e);
	}
	finally
	{
	    try
	    {
		if (resultSet != null)
		    resultSet.close();

		if (statement != null)
		    statement.close();
	    }
	    catch (SQLException e)
	    {
		logger.error("Exception: ", e);
	    }
	    finally
	    {
		connectionPool.releaseConnection(conn);
	    }
	}
    }
}

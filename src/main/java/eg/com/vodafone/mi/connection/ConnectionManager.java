package eg.com.vodafone.mi.connection;

import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaadin.data.util.sqlcontainer.connection.J2EEConnectionPool;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.ui.UI;

import eg.com.vodafone.mi.constants.ISessionConstants;

public class ConnectionManager
{
    private static Logger logger = LoggerFactory.getLogger(ConnectionManager.class);

    private static LoadingCache<Environment, JDBCConnectionPool> connectionsCache = CacheBuilder.newBuilder()
	    .expireAfterAccess(10, TimeUnit.MINUTES).build(new CacheLoader<Environment, JDBCConnectionPool>()
	    {
		@Override
		public JDBCConnectionPool load(Environment key) throws Exception
		{
		    return getNewDataSource(key);
		}
	    });

    public static JDBCConnectionPool getDataSource()
    {
	return connectionsCache.getUnchecked((Environment) UI.getCurrent().getSession().getAttribute(ISessionConstants.ENVIRONMENT));
    }

    public synchronized static JDBCConnectionPool getDataSource(Environment env)
    {
	if (env == null)
	    return null;
	else 
	    return connectionsCache.getUnchecked(env);
    }
    
    private synchronized static JDBCConnectionPool getNewDataSource(Environment env)
    {
	if (Environment.TEST_35.equals(env))
	{
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	    dataSource.setUrl("jdbc:oracle:thin:@10.230.86.188:1521/CRMT0001");
	    dataSource.setUsername("tibco");
	    dataSource.setPassword("apptibco");
	    dataSource.setMaxActive(5);
	    dataSource.setMaxIdle(5);
	    dataSource.setMinEvictableIdleTimeMillis(300);
	    
	    return new J2EEConnectionPool(dataSource);
	}
	else if (Environment.TEST_65.equals(env))
	{
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	    dataSource.setUrl("jdbc:oracle:thin:@10.230.86.188:1521/CRMT0002");
	    dataSource.setUsername("tibco");
	    dataSource.setPassword("apptibco");
	    dataSource.setMaxActive(5);
	    dataSource.setMaxIdle(5);
	    dataSource.setMinEvictableIdleTimeMillis(300);
	    
	    return new J2EEConnectionPool(dataSource);
	}
	else if (Environment.TEST_71.equals(env))
	{
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	    dataSource.setUrl("jdbc:oracle:thin:@10.230.86.188:1521/CRMT0006");
	    dataSource.setUsername("tibco");
	    dataSource.setPassword("apptibco");
	    dataSource.setMaxActive(5);
	    dataSource.setMaxIdle(5);
	    dataSource.setMinEvictableIdleTimeMillis(300);
	    
	    return new J2EEConnectionPool(dataSource);
	}
	else if (Environment.PRD.equals(env))
	{
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	    dataSource.setUrl("jdbc:oracle:thin:@10.230.92.42:1530/EAIPRD1");
	    dataSource.setUsername("tibco");
	    dataSource.setPassword("apptibco");
	    dataSource.setMaxActive(5);
	    dataSource.setMaxIdle(5);
	    dataSource.setMinEvictableIdleTimeMillis(300);

	    return new J2EEConnectionPool(dataSource);
	}
	else if (Environment.DEV.equals(env))
	{
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
	    dataSource.setUrl("jdbc:oracle:thin:@10.230.85.245:1525/EAICRM1");
	    dataSource.setUsername("sysadm");
	    dataSource.setPassword("sysadm123");
	    dataSource.setMaxActive(5);
	    dataSource.setMaxIdle(5);
	    dataSource.setMinEvictableIdleTimeMillis(300);

	    return new J2EEConnectionPool(dataSource);
	}
	else
	    return null;
    }

    public static void handleException(SQLException e)
    {
	logger.error("Exception while creating the SQLContainer", e);
    }
}
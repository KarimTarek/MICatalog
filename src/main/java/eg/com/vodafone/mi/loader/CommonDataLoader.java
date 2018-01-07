package eg.com.vodafone.mi.loader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import eg.com.vodafone.mi.connection.ConnectionManager;
import eg.com.vodafone.mi.connection.Environment;

public class CommonDataLoader
{
    private static Logger logger = LoggerFactory.getLogger(CommonDataLoader.class);
    
    private static LoadingCache<CommonProductsData, Object> commonDataCache = CacheBuilder.newBuilder()
	    .expireAfterAccess(15, TimeUnit.DAYS).build(new CacheLoader<CommonProductsData, Object>()
	    {
		@Override
		public Object load(CommonProductsData data) throws Exception
		{
		    CommonDataLoader loader = new CommonDataLoader(Environment.getCurrentEnvironment());
		    switch (data)
		    {
		    case Types:
			return loader.loadTypes();
			
		    case Categories:
			return loader.loadCategories();

		    default:
			break;
		    }
		    return null;
		}
	    });

    public enum CommonProductsData
    {
	Types,
	Categories
    }
    
    private JDBCTemplate jdbcTemplate;
    
    public CommonDataLoader(Environment env)
    {
	jdbcTemplate = new JDBCTemplate(ConnectionManager.getDataSource(env));
    }
    
    public List<String> loadTypes()
    {
	final List<String> types = new ArrayList<String>();
	
	this.jdbcTemplate.query(
		"select distinct(lower(type)) as Type from tibprd_defination where PRD_Stream = 'MI'", new IResultSetHandler()
		{

		    @Override
		    public void handle(ResultSet resultSet)
		    {
			try
			{
			    while (resultSet.next())
			    {
				types.add(resultSet.getString("Type"));
			    }
			}
			catch (SQLException e)
			{
			    logger.error("Exception: ", e);
			}
		    }
		});
	
	return types;
    }
    
    public List<String> loadCategories()
    {
	final List<String> categories = new ArrayList<String>();
	
	this.jdbcTemplate.query(
		"select distinct(category) as category from tibprd_defination where PRD_Stream = 'MI'", new IResultSetHandler()
		{

		    @Override
		    public void handle(ResultSet resultSet)
		    {
			try
			{
			    while (resultSet.next())
			    {
				categories.add(resultSet.getString("category"));
			    }
			}
			catch (SQLException e)
			{
			    logger.error("Exception: ", e);
			}
		    }
		});
	
	return categories;
    }
    
    public static Object getCommonData(CommonProductsData data)
    {
	return commonDataCache.getUnchecked(data);
    }
}

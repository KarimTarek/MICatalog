package eg.com.vodafone.mi.loader.conf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.com.vodafone.mi.loader.IResultSetHandler;
import eg.com.vodafone.mi.loader.JDBCTemplate;

public class VASSMSConfLoader implements IBackendConfLoader
{
    private static Logger logger = LoggerFactory.getLogger(VASSMSConfLoader.class);

    private JDBCTemplate jdbcTemplate;

    public VASSMSConfLoader(JDBCTemplate jdbcTemplate)
    {
	this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getType()
    {
	return "VASSMS";
    }

    @Override
    public Map<String, Map<String, String>> loadConf(final String externalID)
    {
	final Map<String, Map<String, String>> services = new HashMap<String, Map<String, String>>();

	this.jdbcTemplate.query(
		"SELECT SUBJECT, Parser, SCENARIO, SOURCE_ID, APPID, LANG, ID, SPLIT_FLAG FROM TIBSRV_SMS WHERE SMS_SRV_ID = '"
			+ externalID + "' AND PRD_STREAM = 'MI' ", new IResultSetHandler()
		{
		    @Override
		    public void handle(ResultSet resultSet)
		    {
			try
			{
			    if (resultSet.next())
			    {
				Map<String, String> service = new HashMap<String, String>();
				service.put("SUBJECT", resultSet.getString("SUBJECT"));
				service.put("Parser", resultSet.getString("Parser"));
				service.put("SCENARIO", getScenario(resultSet));
				service.put("SOURCE_ID", resultSet.getString("SOURCE_ID"));
				service.put("APPID", resultSet.getString("APPID"));
				String langID = resultSet.getString("LANG");
				service.put("LANG", langID);
				String id = resultSet.getString("ID");
				service.put("ID", id);
				service.put("script", getSMSScript(id, langID));

				services.put(externalID, service);
			    }
			}
			catch (SQLException e)
			{
			    logger.error("Exception: ", e);
			}
		    }

		    private String getScenario(ResultSet resultSet) throws SQLException
		    {
			String scenario = resultSet.getString("SCENARIO");
			
			if ("1".equals(scenario))
			    return "Successful Opting-in";
			else if ("2".equals(scenario))
			    return "Failed Opting-in";
			else if ("3".equals(scenario))
			    return "Successful Opting-out";
			else if ("4".equals(scenario))
			    return "Failed Opting-out";
			else if ("5".equals(scenario))
			    return "Balance not enough";
			
			return scenario;
		    }
		});

	return services;
    }
    
    private String getSMSScript(String id, String langID)
    {
	final StringBuilder script = new StringBuilder();
	
	this.jdbcTemplate.query(
		"SELECT SMS from SMSLOOKUP WHERE TEMPLATEID = '" + id + "' AND LANGID = '" + langID + "'", new IResultSetHandler()
		{
		    @Override
		    public void handle(ResultSet resultSet)
		    {
			try
			{
			    if (resultSet.next())
			    {
				script.append(resultSet.getString("SMS"));
			    }
			}
			catch (SQLException e)
			{
			    logger.error("Exception: ", e);
			}
		    }
		});
	
	return script.toString();
    }
}
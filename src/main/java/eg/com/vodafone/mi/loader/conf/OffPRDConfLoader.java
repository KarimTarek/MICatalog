package eg.com.vodafone.mi.loader.conf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.com.vodafone.mi.loader.IResultSetHandler;
import eg.com.vodafone.mi.loader.JDBCTemplate;

public class OffPRDConfLoader implements IBackendConfLoader
{
    private static Logger logger = LoggerFactory.getLogger(OffPRDConfLoader.class);

    private JDBCTemplate jdbcTemplate;

    public OffPRDConfLoader(JDBCTemplate jdbcTemplate)
    {
	this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getType()
    {
	return "OFFLINE_PRD";
    }

    @Override
    public Map<String, Map<String, String>> loadConf(final String externalID)
    {
	final Map<String, Map<String, String>> services = new HashMap<String, Map<String, String>>();

	this.jdbcTemplate.query(
		"SELECT OFF_PRD_NAME, SUBJECT, PARSER FROM TIBPRD_OFF_PRDS WHERE OFFLINE_PRD_ID = '"
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
				service.put("PARSER", resultSet.getString("PARSER"));
				service.put("OFF_PRD_NAME", resultSet.getString("OFF_PRD_NAME"));
				
				services.put(externalID, service);
			    }
			}
			catch (SQLException e)
			{
			    logger.error("Exception: ", e);
			}
		    }
		});

	return services;
    }
}
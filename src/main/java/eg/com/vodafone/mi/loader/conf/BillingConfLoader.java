package eg.com.vodafone.mi.loader.conf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eg.com.vodafone.mi.loader.IResultSetHandler;
import eg.com.vodafone.mi.loader.JDBCTemplate;

public class BillingConfLoader implements IBackendConfLoader
{
    private static Logger logger = LoggerFactory.getLogger(BillingConfLoader.class);

    private JDBCTemplate jdbcTemplate;

    public BillingConfLoader(JDBCTemplate jdbcTemplate)
    {
	this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getType()
    {
	return "BILLING";
    }

    @Override
    public Map<String, Map<String, String>> loadConf(final String externalID)
    {
	final Map<String, Map<String, String>> services = new HashMap<String, Map<String, String>>();

	this.jdbcTemplate.query(
		"SELECT OPERATION, SUBJECT, SUBJECT_PARSER, EXTERNAL_SRV_ID FROM TIBPRD_SRV_OPERATION WHERE OPERATION_SRV_ID = '"
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
				service.put("OPERATION", resultSet.getString("OPERATION"));
				service.put("SUBJECT", resultSet.getString("SUBJECT"));
				service.put("SUBJECT_PARSER", resultSet.getString("SUBJECT_PARSER"));
				service.put("EXTERNAL_SRV_ID", resultSet.getString("EXTERNAL_SRV_ID"));

				service.putAll(queryBillingTable(resultSet.getString("EXTERNAL_SRV_ID")));
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

    private Map<String, String> queryBillingTable(String externalID)
    {
	final Map<String, String> service = new HashMap<String, String>();

	this.jdbcTemplate.query("SELECT SN, SP, ID, VALUE, SN_DESC, SP_DESC, SN_SHORTDESC, SP_SHORTDESC, SERVICE_TYPE, END_DATE FROM TIBSRV_BASIC_BILLING WHERE BILLING_SRV_ID = '" + externalID
		+ "' AND PRD_STREAM = 'MI' ", new IResultSetHandler()
	{
	    @Override
	    public void handle(ResultSet resultSet)
	    {
		try
		{
		    if (resultSet.next())
		    {
			service.put("SN", resultSet.getString("SN"));
			service.put("SP", resultSet.getString("SP"));
			service.put("ID", resultSet.getString("ID"));
			service.put("VALUE", resultSet.getString("VALUE"));
			service.put("SN_DESC", resultSet.getString("SN_DESC"));
			service.put("SP_DESC", resultSet.getString("SP_DESC"));
			service.put("SN_SHORTDESC", resultSet.getString("SN_SHORTDESC"));
			service.put("SERVICE_TYPE", resultSet.getString("SERVICE_TYPE"));
			service.put("END_DATE", resultSet.getString("END_DATE"));
		    }
		}
		catch (SQLException e)
		{
		    logger.error("Exception: ", e);
		}
	    }
	});

	return service;
    }

}
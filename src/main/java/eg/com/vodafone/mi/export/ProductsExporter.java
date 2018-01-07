package eg.com.vodafone.mi.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.server.VaadinService;

import eg.com.vodafone.mi.sql.SQLStatement;

public class ProductsExporter
{
    private static Logger logger = LoggerFactory.getLogger(ProductsExporter.class);

    private String propertiesFile;
    private JDBCConnectionPool sourceEnvCon;
    private JDBCConnectionPool destEnvCon;
    private boolean validateDestEnv;

    public ProductsExporter(String propertiesFile, JDBCConnectionPool sourceEnvCon, JDBCConnectionPool destEnvCon,
	    boolean validateDestEnv)
    {
	this.propertiesFile = propertiesFile;
	this.sourceEnvCon = sourceEnvCon;
	this.destEnvCon = destEnvCon;
	this.validateDestEnv = validateDestEnv;
    }

    public List<SQLStatement> export(String[] prdIDs)
    {
	List<SQLStatement> result = new ArrayList<SQLStatement>();

	Properties prop = loadProperties();
	String[] tables = prop.get("Tables").toString().split(",");
	String[] tablesPrimaryKeys = prop.get("TablesPrimaryKeys").toString().split(",");
	String[] tablesForeignKeys = prop.get("TablesForeignKeys").toString().split(",");
	String[] relations = prop.get("Relations").toString().split(",");

	TableExporter defTableExporter = new TableExporter(tables[0], tablesPrimaryKeys[0].split("\\.")[1],
		getRelativesTables(tables[0], tablesPrimaryKeys, tablesForeignKeys, relations), sourceEnvCon);

	RelativeTable relativeTable = new RelativeTable(relations[0], defTableExporter);

	for (int i = 0; i < prdIDs.length; i++)
	{
	    result.addAll(relativeTable.exportAllRelevantSQLs(prdIDs[i]));
	}

	if (validateDestEnv)
	{
	    logger.info("Checking against the destination envrionment..");
	    return filterResult(result, tablesPrimaryKeys);
	}
	
	return result;
    }

    private List<SQLStatement> filterResult(List<SQLStatement> result, String[] tablesPrimaryKeys)
    {
	List<SQLStatement> filteredResult = new ArrayList<SQLStatement>(result.size());
	RowChecker rowChecker = new RowChecker(this.destEnvCon, tablesPrimaryKeys);

	for (SQLStatement sql : result)
	{
	    if (!rowChecker.isExists(sql))
	    {
		filteredResult.add(sql);
	    }
	}

	return filteredResult;
    }

    private RelativeTable[] getRelativesTables(String tableName, String[] tablesPrimaryKeys,
	    String[] tablesForeignKeys, String[] relations)
    {
	List<RelativeTable> exporters = new ArrayList<RelativeTable>(relations.length);

	for (int i = 0; i < relations.length; i++)
	{
	    if (relations[i].startsWith(tableName))
	    {
		String[] relation = relations[i].split("=");
		String tableName2 = relation[1].split("\\.")[0];

		logger.info(tableName2 + " is relative to " + tableName);

		exporters.add(new RelativeTable(relations[i], new TableExporter(tableName2, getPrimaryKey(tableName2,
			tablesPrimaryKeys), getRelativesTables(tableName2, tablesPrimaryKeys, tablesForeignKeys,
			relations), sourceEnvCon)));
	    }
	}

	RelativeTable[] relativeTables = new RelativeTable[exporters.size()];
	exporters.toArray(relativeTables);

	return relativeTables;
    }

    private String getPrimaryKey(String tableName, String[] tablesPrimaryKeys)
    {
	for (int i = 0; i < tablesPrimaryKeys.length; i++)
	{
	    if (tablesPrimaryKeys[i].startsWith(tableName))
		return tablesPrimaryKeys[i].split("\\.")[1];
	}
	return null;
    }

    private Properties loadProperties()
    {
	Properties prop = new Properties();
	try
	{
	    String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	    prop.load(new FileInputStream(new File(basepath + "/WEB-INF/tables/" + propertiesFile)));
	}
	catch (IOException e)
	{
	    logger.error("Exception while reading the properties file..", e);
	}

	Set<Object> keySet = prop.keySet();
	for (Iterator<Object> iterator = keySet.iterator(); iterator.hasNext();)
	{
	    Object object = iterator.next();
	    logger.debug(object + "=" + prop.getProperty((String) object));
	}

	return prop;
    }
}

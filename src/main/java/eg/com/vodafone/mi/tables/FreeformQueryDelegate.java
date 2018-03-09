package eg.com.vodafone.mi.tables;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.util.sqlcontainer.RowItem;
import com.vaadin.v7.data.util.sqlcontainer.query.FreeformStatementDelegate;
import com.vaadin.v7.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.OracleGenerator;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.StatementHelper;
import com.vaadin.v7.data.util.sqlcontainer.query.generator.filter.QueryBuilder;

@SuppressWarnings("serial")
public class FreeformQueryDelegate implements FreeformStatementDelegate
{
    private static Logger logger = LoggerFactory.getLogger(FreeformQueryDelegate.class);

    private List<Filter> filters;
    private List<OrderBy> orderBys;
    private OracleGenerator oracleGenerator = new OracleGenerator();    
    
    private String tableName;
    
    public FreeformQueryDelegate(String tableName)
    {
	this.tableName = tableName;
    }

    public StatementHelper getQueryStatement(int offset, int limit) throws UnsupportedOperationException
    {
	StatementHelper sh;
	
	sh = oracleGenerator.generateSelectQuery(tableName, filters, orderBys, offset, limit, "*");
	
	logger.debug("Oracle Generator: " + sh.getQueryString());
	return sh;
    }

    public StatementHelper getCountStatement() throws UnsupportedOperationException
    {
	StatementHelper sh = new StatementHelper();
	StringBuffer query = new StringBuffer("SELECT COUNT(*) FROM " + this.tableName + " ");
	if (filters != null)
	{
	    query.append(QueryBuilder.getWhereStringForFilters(filters, sh));
	}
	sh.setQueryString(query.toString());
	return sh;
    }

    public void setFilters(List<Filter> filters)
    {
	this.filters = filters;
    }

    public void setOrderBy(List<OrderBy> orderBys)
    {
	this.orderBys = orderBys;
    }

    public int storeRow(Connection conn, RowItem row) throws SQLException
    {
	throw new UnsupportedOperationException("Unsupported.");
    }

    public boolean removeRow(Connection conn, RowItem row) throws UnsupportedOperationException, SQLException
    {
	throw new UnsupportedOperationException("Unsupported.");
    }

    public StatementHelper getContainsRowQueryStatement(Object... keys) throws UnsupportedOperationException
    {
	StatementHelper sh = new StatementHelper();
	StringBuffer query = new StringBuffer("SELECT * FROM " + this.tableName + " WHERE ID = ?");
	sh.addParameterValue(keys[0]);
	sh.setQueryString(query.toString());
	return sh;
    }

    @Deprecated
    public String getContainsRowQueryString(Object... keys) throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException("Please use getContainsRowQueryStatement method.");
    }

    @Deprecated
    public String getQueryString(int offset, int limit) throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException("Use getQueryStatement method.");
    }
    
    @Deprecated
    public String getCountQuery() throws UnsupportedOperationException
    {
	throw new UnsupportedOperationException("Use getCountStatement method.");
    }
}
package eg.com.vodafone.mi.view;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.tepi.filtertable.FilterTable;

import com.vaadin.data.util.sqlcontainer.SQLContainer;
import com.vaadin.data.util.sqlcontainer.connection.JDBCConnectionPool;
import com.vaadin.data.util.sqlcontainer.query.FreeformQuery;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import eg.com.vodafone.mi.connection.ConnectionManager;
import eg.com.vodafone.mi.tables.FreeformQueryDelegate;

@SuppressWarnings("serial")
public class TableView extends VerticalLayout implements View
{
    protected FilterTable table;

    public TableView()
    {
	this.setSizeFull();
	this.setMargin(true);
    }

    @Override
    public void enter(ViewChangeEvent event)
    {
	createTable(getTableName(event));
    }

    protected String getTableName(ViewChangeEvent event)
    {
	return event.getViewName().split("/")[1];
    }

    private void createTable(String tableName)
    {
	// Remove first any components were added before
	this.removeAllComponents();
	
	Panel panel = new Panel(tableName);
	panel.setSizeFull();

	table = new FilterTable();
	table.setSizeFull();
	table.setSelectable(true);
	table.setFilterBarVisible(true);
	table.setImmediate(true);
	table.addStyleName(Runo.TABLE_BORDERLESS);

	panel.setContent(table);

	this.addComponent(panel);
	this.setMargin(false);
	this.setExpandRatio(panel, 1f);

	JDBCConnectionPool connectionPool = ConnectionManager.getDataSource();

	FreeformQueryDelegate formQuery = new FreeformQueryDelegate(tableName);

	FreeformQuery freeQuery = new FreeformQuery(getQueryString(tableName), connectionPool);
	freeQuery.setDelegate(formQuery);

	SQLContainer container = null;
	try
	{
	    container = new SQLContainer(freeQuery);
	    addContainerFilters(container);
	    table.setContainerDataSource(container);
	    table.setVisibleColumns(getVisibleColumns(table.getColumnHeaders()));
	}
	catch (SQLException e)
	{
	    ConnectionManager.handleException(e);
	}
    }

    protected void addContainerFilters(SQLContainer container)
    {
    }

    protected String getQueryString(String tableName)
    {
	return "SELECT * FROM " + tableName;
    }

    protected Object[] getVisibleColumns(String[] columnHeaders)
    {
	List<String> cols = new ArrayList<String>();
	for (int i = 0; i < columnHeaders.length; i++)
	{
	    if (!"rownum".equals(columnHeaders[i]))
		cols.add(columnHeaders[i]);
	}
	return cols.toArray();
    }

}
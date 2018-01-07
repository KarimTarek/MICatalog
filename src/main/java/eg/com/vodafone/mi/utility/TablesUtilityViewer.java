package eg.com.vodafone.mi.utility;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import eg.com.vodafone.mi.sql.SQLStatement;

public class TablesUtilityViewer
{
    public static Component createTablesLayout(List<SQLStatement> list)
    {
	Map<String, Table> tables = new HashMap<String, Table>();

	VerticalLayout layout = new VerticalLayout();
	layout.setMargin(false);

	int i = 0;
	for (SQLStatement sql : list)
	{
	    Table table = tables.get(sql.getTable());

	    if (table == null)
	    {
		table = createNewTable(sql);
		tables.put(sql.getTable(), table);

		Label lblTableName = new Label(sql.getTable());
		lblTableName.addStyleName(Runo.LABEL_H2);
		layout.addComponent(lblTableName);

		layout.addComponent(table);
	    }

	    Object[] array = sql.getValues().toArray();

	    table.addItem(array, i++);
	    table.setPageLength(table.getPageLength() + 1);
	}

	return layout;
    }
    
    private static Table createNewTable(SQLStatement sql)
    {
	Table table = new Table();
	table.setSizeFull();
	table.setPageLength(0);

	for (String col : sql.getCols())
	{
	    table.addContainerProperty(col, String.class, null);
	}

	return table;
    }
    
    @SuppressWarnings("serial")
    public static StreamResource createResource(String fileName, final List<SQLStatement> list)
    {
	return new StreamResource(new StreamSource()
	{
	    @Override
	    public InputStream getStream()
	    {
		StringBuilder builder = new StringBuilder();
		
		for (SQLStatement sqlStatement : list)
		{
		    builder.append(sqlStatement.generateSQLStatement());
		    builder.append("\n");
		}

		ByteArrayInputStream is = new ByteArrayInputStream(builder.toString().getBytes());
		return is;
	    }
	}, fileName + ".sql");
    }
}

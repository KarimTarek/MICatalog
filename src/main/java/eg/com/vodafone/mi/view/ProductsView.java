package eg.com.vodafone.mi.view;

import com.vaadin.v7.data.util.sqlcontainer.SQLContainer;
import com.vaadin.v7.data.util.sqlcontainer.query.OrderBy;
import com.vaadin.v7.event.ItemClickEvent;
import com.vaadin.v7.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class ProductsView extends TableView
{
//    private static Logger logger = LoggerFactory.getLogger(ProductsView.class);

    @Override
    protected Object[] getVisibleColumns(String[] columnHeaders)
    {
	return new Object[]
	{ "PRD_ID", "DESCRIPTION", "CATEGORY", "TYPE", "STATUS" };
    }

    @Override
    protected String getTableName(ViewChangeEvent event)
    {
	return "TIBPRD_DEFINATION";
    }

    @Override
    protected void addContainerFilters(SQLContainer container)
    {
	container.addContainerFilter("PRD_STREAM", "MI", true, true);
	container.addOrderBy(new OrderBy("PRD_ID", true));

	this.table.addItemClickListener(new ItemClickListener()
	{
	    @Override
	    public void itemClick(ItemClickEvent event)
	    {
		UI.getCurrent().getNavigator()
			.navigateTo(IViews.MI_PRODUCT + event.getItem().getItemProperty("PRD_ID").getValue());
	    }
	});
    }
}
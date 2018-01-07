package eg.com.vodafone.mi.ui.provider;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

import eg.com.vodafone.mi.view.TableView;

@SuppressWarnings("serial")
public class TablesViewProvider implements ViewProvider
{
    private TableView view;
    
    public TablesViewProvider()
    {
	view = new TableView();
    }
    
    @Override
    public String getViewName(String viewAndParameters)
    {
	if (viewAndParameters != null && viewAndParameters.startsWith("tables/"))
	{
	    return viewAndParameters;
	}
	return null;
    }

    @Override
    public View getView(String viewName)
    {
	if (viewName != null && viewName.startsWith("tables/"))
	{
	    return view;
	}
	
	return null;
    }

}

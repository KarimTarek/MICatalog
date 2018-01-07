package eg.com.vodafone.mi.ui.provider;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewProvider;

import eg.com.vodafone.mi.view.IViews;
import eg.com.vodafone.mi.view.product.MIProductView;

@SuppressWarnings("serial")
public class MIProductsViewProvider implements ViewProvider
{
    private MIProductView view;
    
    public MIProductsViewProvider()
    {
	view = new MIProductView();
    }
    
    @Override
    public String getViewName(String viewAndParameters)
    {
	if (viewAndParameters != null && viewAndParameters.startsWith(IViews.MI_PRODUCT))
	{
	    return viewAndParameters;
	}
	return null;
    }

    @Override
    public View getView(String viewName)
    {
	if (viewName != null && viewName.startsWith(IViews.MI_PRODUCT))
	{
	    return view;
	}
	
	return null;
    }

}

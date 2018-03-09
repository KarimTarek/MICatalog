package eg.com.vodafone.mi.ui;

import org.slf4j.bridge.SLF4JBridgeHandler;

import com.google.common.eventbus.EventBus;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;

import eg.com.vodafone.mi.connection.Environment;
import eg.com.vodafone.mi.constants.ISessionConstants;
import eg.com.vodafone.mi.ui.provider.MIProductsViewProvider;
import eg.com.vodafone.mi.ui.provider.TablesViewProvider;
import eg.com.vodafone.mi.view.ExportProductsView;
import eg.com.vodafone.mi.view.IViews;
import eg.com.vodafone.mi.view.MainView;
import eg.com.vodafone.mi.view.ProductsView;
import eg.com.vodafone.mi.view.wizard.ProductWizardView;

@SpringUI
@Theme("runo")
@Title("Products Catalog")
@SuppressWarnings("serial")
@Push(value = PushMode.MANUAL, transport = Transport.STREAMING)
public class NavigatorUI extends UI
{
    private Navigator navigator;
    private HomeLayout content;

    static
    {
	SLF4JBridgeHandler.install();
    }

    @Override
    protected void init(VaadinRequest request)
    {
	createLayout();

	initSessionData();

	createNavigator();
    }

    private void createLayout()
    {
        content = new HomeLayout();
        setContent(content);
    }

    private void createNavigator()
    {
        // Create a navigator to control the views
        navigator = new Navigator(this, content.getDetailsLayout());
        // Create and register the views
        navigator.addView(IViews.MAIN_VIEW, new MainView());
        // Add new providers
        navigator.addProvider(new MIProductsViewProvider());
        navigator.addProvider(new TablesViewProvider());
        // MI Views
        navigator.addView(IViews.MI_PRODUCTS, new ProductsView());
        navigator.addView(IViews.MI_NEW_PRODUCT, new ProductWizardView());
        navigator.addView(IViews.MI_EXPORT_PRODUCT, new ExportProductsView("MITables.properties","Export MI Product(s)"));
        navigator.addView(IViews.MI_EXPORT_COMM_PRODUCT, new ExportProductsView("MICommTables.properties", "Export MI Commercial Product(s)"));
    }

    private void initSessionData()
    {
	VaadinSession session = this.getSession();
	session.setAttribute(ISessionConstants.ENVIRONMENT, Environment.TEST_65);
	session.setAttribute(ISessionConstants.EVENT_BUS, new EventBus("generalEventBus"));
    }
}
package eg.com.vodafone.mi.ui;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Property.ValueChangeEvent;
import com.vaadin.v7.data.Property.ValueChangeListener;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.v7.shared.ui.combobox.FilteringMode;
import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.v7.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.v7.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;

import eg.com.vodafone.mi.connection.Environment;
import eg.com.vodafone.mi.constants.ISessionConstants;
import eg.com.vodafone.mi.view.IViews;

@SuppressWarnings("serial")
public class HomeLayout extends CustomComponent
{
    private static final String MENU_EXPORT_PRODUCTS = "Export Product(s)";

    private static final String MENU_EXPORT_COMM_PRODUCTS = "Export Commercial Product(s)";

    private static final String MENU_DEFINE_NEW_PRODUCT = "Define New Product";

    private static Logger logger = LoggerFactory.getLogger(HomeLayout.class);

    private Tree tablesMenu;
    private Panel detailsLayout;

    private String selectedMIMenuItem;
    private String selectedTableMenuItem;

    private VerticalLayout root;

    public HomeLayout()
    {
	this.setCompositionRoot(buildMainLayout());
	this.setSizeFull();
    }

    private VerticalLayout buildMainLayout()
    {
	root = new VerticalLayout();
	root.setMargin(true);
	root.setSizeFull();

	// Add the components
	root.addComponent(createHeader());

	// Horizontal layout with selection tree on the left and
	// a details panel on the right.
	HorizontalLayout horlayout = new HorizontalLayout();
	horlayout.setSizeFull();
	horlayout.setSpacing(true);
	root.addComponent(horlayout);
	root.setExpandRatio(horlayout, 1);

	Panel menuContainer = createMenu();

	horlayout.addComponent(menuContainer);

	// A panel for the main view area on the right side
	// Panel detailspanel = new Panel();
	// detailspanel.addStyleName("detailspanel");
	// detailspanel.addStyleName("light"); // No borders
	// detailspanel.setSizeFull();
	// horlayout.addComponent(detailspanel);

	detailsLayout = new Panel();
	detailsLayout.addStyleName("light");
	detailsLayout.setSizeFull();

	horlayout.addComponent(detailsLayout);
	// detailspanel.setContent(detailsLayout);

	// Let the details panel take as much space as possible and
	// have the selection tree to be as small as possible
	horlayout.setExpandRatio(detailsLayout, 5);
	horlayout.setExpandRatio(menuContainer, 1);

	tablesMenu.addValueChangeListener(new Property.ValueChangeListener()
	{
	    public void valueChange(ValueChangeEvent event)
	    {
		if (event.getProperty() != null && event.getProperty().getValue() != null)
		{
		    selectedTableMenuItem = event.getProperty().getValue().toString();
		}
		if (selectedTableMenuItem != null)
		{
		    UI.getCurrent().getNavigator().navigateTo("tables/" + selectedTableMenuItem);
		    tablesMenu.select(selectedTableMenuItem);
		}
	    }
	});
	tablesMenu.setImmediate(true);

	// A footer
	VerticalLayout footerLayout = new VerticalLayout();
	Label footer = new Label("All rights reserved - INTEGRATION TEAM");
	footer.setSizeFull();
	footerLayout.addComponent(footer);
	footerLayout.setComponentAlignment(footer, Alignment.BOTTOM_CENTER);
	footerLayout.setSizeFull();
	footerLayout.setWidth("100%");
	footerLayout.setHeight("");
	footerLayout.setExpandRatio(footer, 1f);

	// root.addComponent(footerLayout);
	// root.setComponentAlignment(footerLayout, Alignment.BOTTOM_CENTER);
	// footerLayout.addStyleName("vfStyle");

	return root;
    }

    private Panel createMenu()
    {
	// Layout for the menu area. Wrap the menu in a Panel to allow
	// scrollbar.
	Panel menuContainer = new Panel("Streams");
	// menuContainer.addStyleName("menucontainer");
	// menuContainer.addStyleName("light"); // No border
	// menuContainer.setWidth("200px"); // Undefined width
	// menuContainer.setHeight("100%");
	// menuContainer.setSizeFull();

	// Create the Accordion.
	Accordion accMenu = new Accordion();

	// Have it take all space available in the layout.
	accMenu.setSizeFull();

	// Some components to put in the Accordion.
	Label l2 = new Label("There are no links.");

	// A container for the Accordion.
	Panel panel = new Panel("Tasks");
	panel.setWidth("300px");
	panel.setHeight("300px");
	panel.setContent(accMenu);

	tablesMenu = new Tree();
	tablesMenu.setSizeUndefined();
	tablesMenu.setWidth("100%");
	menuContainer.setContent(accMenu);

	// Add the components as tabs in the Accordion.
	accMenu.addTab(tablesMenu, "Tables", null);

	// ////////////////////////////////////////////////////
	// Put in the application data and handle the UI logic

	final Object[][] tables = new Object[][]
	{ new Object[]
	{ "TIBPRD_DEFINATION" }, new Object[]
	{ "TIBPRD_PLANFRAGMENT" }, new Object[]
	{ "TIBPRD_CONF" }, new Object[]
	{ "TIBSRV_BASIC_BILLING" }, new Object[]
	{ "TIBPRD_SRV_OPERATION" }, new Object[]
	{ "TIBSERV_PREPAID" }, new Object[]
	{ "TIBPRD_OFF_PRDS" }, new Object[]
	{ "TIBPRD_ELGIBILITY_RULES" }, new Object[]
	{ "TIBSRV_PCRF" }, new Object[]
	{ "TIBSRV_SMS" }, new Object[]
	{ "TIBPRD_PROFILING" }, new Object[]
	{ "TIBPRD_COMPATIBILITY" } };

	// Add tables as root items in the tree.
	adjustTreeElements(tablesMenu, tables);

	// MI Menu
	final Tree treeMIMenu = new Tree();
	treeMIMenu.setSizeUndefined();
	treeMIMenu.setWidth("100%");
	treeMIMenu.setImmediate(true);

	final Object[][] miMenu = new Object[][]
	{ new Object[]
	{ "Products" }, new Object[]
	{ MENU_DEFINE_NEW_PRODUCT }, new Object[]
	{ MENU_EXPORT_PRODUCTS }, new Object[]
	{ MENU_EXPORT_COMM_PRODUCTS } };

	adjustTreeElements(treeMIMenu, miMenu);

	treeMIMenu.addValueChangeListener(new Property.ValueChangeListener()
	{
	    public void valueChange(ValueChangeEvent event)
	    {
		if (event.getProperty() != null && event.getProperty().getValue() != null)
		{
		    selectedMIMenuItem = (String) event.getProperty().getValue();
		}

		if (selectedMIMenuItem != null)
		{
		    if ("Products".equals(selectedMIMenuItem))
		    {
			UI.getCurrent().getNavigator().navigateTo("MI/" + selectedMIMenuItem);
		    }
		    else if (MENU_DEFINE_NEW_PRODUCT.equals(selectedMIMenuItem))
		    {
			UI.getCurrent().getNavigator().navigateTo(IViews.MI_NEW_PRODUCT);
		    }
		    else if (MENU_EXPORT_PRODUCTS.equals(selectedMIMenuItem))
		    {
			UI.getCurrent().getNavigator().navigateTo(IViews.MI_EXPORT_PRODUCT);
		    }
		    else if (MENU_EXPORT_COMM_PRODUCTS.equals(selectedMIMenuItem))
		    {
			UI.getCurrent().getNavigator().navigateTo(IViews.MI_EXPORT_COMM_PRODUCT);
		    }

		    treeMIMenu.select(selectedMIMenuItem);
		}
	    }
	});

	accMenu.addTab(treeMIMenu, "MI", null);
	accMenu.addTab(l2, "USB", null);

	return menuContainer;
    }

    private void adjustTreeElements(Tree tablesMenu, final Object[][] menu)
    {
	for (int i = 0; i < menu.length; i++)
	{
	    final String planet = (String) (menu[i][0]);
	    tablesMenu.addItem(planet);

	    if (menu[i].length == 1)
	    {
		// The planet has no moons so make it a leaf.
		tablesMenu.setChildrenAllowed(planet, false);
	    }
	    else
	    {
		// Add children (moons) under the planets.
		for (int j = 1; j < menu[i].length; j++)
		{
		    final String moon = (String) menu[i][j];

		    // Add the item as a regular item.
		    tablesMenu.addItem(moon);

		    // Set it to be a child.
		    tablesMenu.setParent(moon, planet);

		    // Make the moons look like leaves.
		    tablesMenu.setChildrenAllowed(moon, false);
		}

		// Expand the subtree.
		tablesMenu.expandItemsRecursively(planet);
	    }
	}
    }

    private VerticalLayout createHeader()
    {
	VerticalLayout header = new VerticalLayout();

	// Environment ComboBox
	HorizontalLayout horLayout = new HorizontalLayout();
	ComboBox comboEnv = new ComboBox();
	comboEnv.addItem(Environment.TEST_35.getText());
	comboEnv.addItem(Environment.TEST_65.getText());
	comboEnv.addItem(Environment.TEST_71.getText());
	comboEnv.addItem(Environment.PRD.getText());
	comboEnv.addItem(Environment.DEV.getText());
	comboEnv.setNewItemsAllowed(false);
	comboEnv.setNullSelectionAllowed(false);
	comboEnv.setFilteringMode(FilteringMode.STARTSWITH);
	comboEnv.select(Environment.TEST_65.getText());
	comboEnv.setImmediate(true);
	comboEnv.addValueChangeListener(new ValueChangeListener()
	{
	    @Override
	    public void valueChange(ValueChangeEvent event)
	    {
		String environment = event.getProperty().getValue().toString();
		Environment env = Environment.fromString(environment);
		UI.getCurrent().getSession().setAttribute(ISessionConstants.ENVIRONMENT, env);
		logger.info("Changing the environment to {}", env);
		UI.getCurrent().getNavigator().navigateTo(UI.getCurrent().getNavigator().getState());
	    }
	});

	Label lblCombo = new Label("Environment:" + "&nbsp;&nbsp;", ContentMode.HTML);
	horLayout.addComponent(lblCombo);
	horLayout.addComponent(comboEnv);
	horLayout.setComponentAlignment(lblCombo, Alignment.MIDDLE_RIGHT);
	horLayout.setComponentAlignment(comboEnv, Alignment.MIDDLE_RIGHT);

	header.addComponent(horLayout);
	header.setComponentAlignment(horLayout, Alignment.MIDDLE_RIGHT);

	// Title bar
	HorizontalLayout titleBar = new HorizontalLayout();
	titleBar.setWidth("100%");
	// VF Logo
	String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	// Image as a file resource
	FileResource resource = new FileResource(new File(basepath + "/WEB-INF/images/logo.png"));

	// Show the image in the application
	Image logo = new Image(null, resource);
	logo.addStyleName("logo");

	titleBar.addComponent(logo);
	// titleBar.setExpandRatio(logo, 1);

	HorizontalLayout titlePanelContainer = new HorizontalLayout();
	titlePanelContainer.setWidth("100%");
	titlePanelContainer.addStyleName("vfStyle");

	Label title = new Label("Products Catalog");
	title.setHeight("100%");
	title.setWidth("100%");
	title.addStyleName("title1");
	titlePanelContainer.addComponent(title);

	titleBar.addComponent(titlePanelContainer);
	titleBar.setExpandRatio(titlePanelContainer, 14);

	header.addComponent(titleBar);
	return header;
    }

    public Panel getDetailsLayout()
    {
	return detailsLayout;
    }

}

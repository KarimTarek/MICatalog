package eg.com.vodafone.mi.view.product;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import eg.com.vodafone.mi.domain.ExecutionPlan;
import eg.com.vodafone.mi.domain.Product;
import eg.com.vodafone.mi.domain.StepConf;
import eg.com.vodafone.mi.loader.ProductsLoader;
import eg.com.vodafone.mi.view.IViews;

@SuppressWarnings("serial")
public class MIProductView extends CustomComponent implements View
{
    private static Logger logger = LoggerFactory.getLogger(MIProductView.class);

    private List<IBackendConfViewer> backendsViewers;

    private String viewName;

    public MIProductView()
    {
	this.setCompositionRoot(new VerticalLayout());

	backendsViewers = new ArrayList<IBackendConfViewer>();
	backendsViewers.add(new MBCConfViewer());
	backendsViewers.add(new BillingConfViewer());
	backendsViewers.add(new PCRFConfViewer());
	backendsViewers.add(new VASSMSConfViewer());
	backendsViewers.add(new OffPRDConfViewer());
    }

    @Override
    public void enter(ViewChangeEvent event)
    {
	String executionPlanID = null;
	ExecutionPlan executionPlan = null;

	viewName = event.getViewName();
	String[] url = viewName.split("/");

	String productID = url[2];
	logger.info("Viewing the Product details for Product: {}", productID);

	Product product = ProductsLoader.getProduct(productID);

	if (url.length > 3)
	{
	    executionPlanID = url[3];
	    logger.info("Viewing the execution plan details for: {}", executionPlanID);

	    List<ExecutionPlan> executionPlans = product.getExecutionPlans();
	    for (ExecutionPlan execution : executionPlans)
	    {
		if (executionPlanID.equals(execution.getId()))
		{
		    executionPlan = execution;
		}
	    }
	}

	Panel panel = new Panel(productID + " - " + product.getName());
	panel.setSizeFull();

	this.setCompositionRoot(panel);
	this.setSizeFull();

	VerticalLayout content = new VerticalLayout();
	panel.setContent(content);
	content.setWidth("100%");
	content.setHeight("");

	content.addComponent(createBasicInformationPanel(product));
	content.addComponent(createExecutionPlansPanel(product, executionPlan));

	if (executionPlanID != null)
	{
	    content.addComponent(createExecutionPlanSteps(executionPlan));
	}
	else
	{
	    content.addComponent(new VerticalLayout());
	}
    }

    private VerticalLayout createExecutionPlanSteps(ExecutionPlan executionPlan)
    {
	VerticalLayout stepsLayout = new VerticalLayout();
	stepsLayout.setMargin(true);
	stepsLayout.setSizeFull();
	Label label = new Label("Execution Plan Steps:");
	label.addStyleName(Runo.LABEL_H1);
	stepsLayout.addComponent(label);

	Label temp;
	for (StepConf step : executionPlan.getSteps())
	{
	    temp = new Label("Step: " + step.getStepNo() + " - " + step.getType());
	    temp.addStyleName(Runo.LABEL_H2);
	    stepsLayout.addComponent(temp);

	    for (IBackendConfViewer viewer : this.backendsViewers)
	    {
		if (step.getType().equals(viewer.getType()))
		{
		    stepsLayout.addComponent(viewer.createView(step));
		}
	    }
	}

	return stepsLayout;
    }

    private Component createExecutionPlansPanel(final Product product, ExecutionPlan executionPlan)
    {
	VerticalLayout layout = new VerticalLayout();
	layout.setMargin(true);
	layout.setSizeFull();
	Label label = new Label("Execution Plans:");
	label.addStyleName(Runo.LABEL_H1);
	layout.addComponent(label);

	Table executionPlans = new Table(null, buildContainer(product.getExecutionPlans()));
	executionPlans.setWidth("100%");
	executionPlans.setHeight("");
	executionPlans.setSelectable(true);
	layout.addComponent(executionPlans);

	executionPlans.setPageLength(product.getExecutionPlans().size());
	executionPlans.addItemClickListener(new ItemClickListener()
	{

	    @Override
	    public void itemClick(ItemClickEvent event)
	    {
		UI.getCurrent()
			.getNavigator()
			.navigateTo(
				IViews.MI_PRODUCT + product.getPrdID() + "/"
					+ event.getItem().getItemProperty("ID").getValue());
	    }
	});

	executionPlans.select(getSelectedItem(executionPlans, executionPlan));

	return layout;
    }

    private Object getSelectedItem(Table table, ExecutionPlan executionPlan)
    {
	for (Iterator<?> iterator = table.getItemIds().iterator(); iterator.hasNext();)
	{
	    Object itemID = iterator.next();

	    if (executionPlan != null
		    && executionPlan.getId().equals(table.getContainerProperty(itemID, "ID").getValue()))
	    {
		return itemID;
	    }
	}

	return null;
    }

    @SuppressWarnings("unchecked")
    private Container buildContainer(List<ExecutionPlan> executionPlans)
    {
	IndexedContainer cont = new IndexedContainer();

	cont.addContainerProperty("ID", String.class, null);
	cont.addContainerProperty("Rate Plan Types", String.class, null);
	cont.addContainerProperty("Operation", String.class, null);

	int i = 0;
	for (ExecutionPlan executionPlan : executionPlans)
	{
	    cont.addItem(++i);
	    cont.getContainerProperty(i, "ID").setValue(executionPlan.getId());
	    cont.getContainerProperty(i, "Rate Plan Types").setValue(executionPlan.getRatePlanTypes());
	    cont.getContainerProperty(i, "Operation").setValue(executionPlan.getOperation());
	}

	return cont;
    }

    private Component createBasicInformationPanel(final Product product)
    {
	VerticalLayout verticalLayout = new VerticalLayout();

	HorizontalLayout headerLayout = new HorizontalLayout();
	headerLayout.setMargin(true);
	headerLayout.setSizeFull();

	Label label = new Label("Basic Information:");
	label.addStyleName(Runo.LABEL_H1);
	headerLayout.addComponent(label);

	Button refreshLink = new Button();
	refreshLink.setDescription("Reload the product configuration from the DB");
	refreshLink.addStyleName(Runo.BUTTON_LINK);
	refreshLink.addStyleName("refreshlbl");
	String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
	refreshLink.setIcon(new FileResource(new File(basepath + "/WEB-INF/images/Refresh-icon.png")));
	refreshLink.addClickListener(new ClickListener()
	{
	    
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		ProductsLoader.removeProduct(product.getPrdID());
		UI.getCurrent().getNavigator().navigateTo(viewName);
	    }
	});
	headerLayout.addComponent(refreshLink);
	headerLayout.setComponentAlignment(refreshLink, Alignment.MIDDLE_RIGHT);
	headerLayout.setExpandRatio(label, 1f);
	headerLayout.setExpandRatio(refreshLink, 0.2f);

	verticalLayout.addComponent(headerLayout);

	VerticalLayout leftSide = new VerticalLayout();
	VerticalLayout rightSide = new VerticalLayout();

	HorizontalLayout horizontalLayout = new HorizontalLayout();
	horizontalLayout.addComponent(leftSide);
	horizontalLayout.addComponent(rightSide);
	horizontalLayout.setMargin(true);

	createLabelWithCaption(leftSide, rightSide, "Name:&nbsp;", product.getName());
	createLabelWithCaption(leftSide, rightSide, "Category:&nbsp;", product.getCategory());
	createLabelWithCaption(leftSide, rightSide, "Type:&nbsp;", product.getType());
	createLabelWithCaption(leftSide, rightSide, "Commercial English Name:&nbsp;", product.getCommercialEnName());
	createLabelWithCaption(leftSide, rightSide, "Commercial Arabic Name:&nbsp;", product.getCommercialArName());
	createLabelWithCaption(leftSide, rightSide, "Status:&nbsp;", product.getStatus());

	verticalLayout.addComponent(horizontalLayout);
	return verticalLayout;
    }

    private void createLabelWithCaption(VerticalLayout leftSide, VerticalLayout rightSide, String caption, String value)
    {
	leftSide.addComponent(new Label("<b>" + caption + "</b>", ContentMode.HTML));
	if (value == null)
	    rightSide.addComponent(new Label("&nbsp;", ContentMode.HTML));
	else
	    rightSide.addComponent(new Label(value));
    }

}
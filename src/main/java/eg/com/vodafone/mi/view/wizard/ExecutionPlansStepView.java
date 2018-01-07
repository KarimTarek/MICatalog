package eg.com.vodafone.mi.view.wizard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.vaadin.data.Container;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

import eg.com.vodafone.mi.domain.ExecutionPlan;
import eg.com.vodafone.mi.domain.Product;
import eg.com.vodafone.mi.domain.StepConf;
import eg.com.vodafone.mi.utility.VaadinUtil;
import eg.com.vodafone.mi.view.wizard.bean.ExecutionPlanBean;
import eg.com.vodafone.mi.view.wizard.bean.StepBean;
import eg.com.vodafone.mi.view.wizard.ui.SegmentLabel;
import eg.com.vodafone.mi.view.wizard.ui.StepsLabel;
import eg.com.vodafone.mi.view.wizard.window.BillingWindow;
import eg.com.vodafone.mi.view.wizard.window.IBackendWindow;
import eg.com.vodafone.mi.view.wizard.window.PCRFWindow;
import eg.com.vodafone.mi.view.wizard.window.PrepaidWindow;

@SuppressWarnings("serial")
public class ExecutionPlansStepView extends AbstractStepView
{
    private static final String SUB_SEGMENT = "In case you want to edit an already added segment, just double click on the row and edit the data.";

    private static final String EXECUTION_PLAN_SUB_HEADER = "You can think of the Execution Plans as the technical products of the commercial product. The execution plan consists of two parts, first part which segment of subscribers will be linked with this execution plan and the second part the fullfillment steps which should be taken in order to fullfill this product.";

    private EventBus eventBus;

    private BeanContainer<String, ExecutionPlanBean> beans;
    private Map<String, ExecutionPlan> executionPlansMap;

    private List<IBackendWindow> backendWindows;
    private BeanContainer<String, StepBean> stepsBeans;

    private Map<String, List<StepBean>> executionStepsMap;

    private VerticalLayout stepsLayout;
    private Table tableSteps;
    private Table tableExec;
    private Label lblSubSegment;

    public ExecutionPlansStepView(Product product)
    {
	super(product);

	eventBus = new EventBus();

	executionPlansMap = new HashMap<String, ExecutionPlan>();
	executionStepsMap = new HashMap<String, List<StepBean>>();

	backendWindows = new ArrayList<IBackendWindow>();
	backendWindows.add(new PrepaidWindow());
	backendWindows.add(new BillingWindow());
	backendWindows.add(new PCRFWindow());

	VerticalLayout layout = new VerticalLayout();
	layout.setSizeFull();
	layout.setMargin(true);
	this.setCompositionRoot(layout);

	layout.addComponent(createExecutionPlansLayout());

	stepsLayout = createStepsLayout();
	layout.addComponent(stepsLayout);
    }

    private Component createExecutionPlansLayout()
    {
	VerticalLayout layout = new VerticalLayout();

	Label lblExecutionPlansHeader = new Label("Execution Plans");
	lblExecutionPlansHeader.addStyleName(Runo.LABEL_H1);
	layout.addComponent(lblExecutionPlansHeader);
	layout.addComponent(new Label(EXECUTION_PLAN_SUB_HEADER));

	layout.addComponent(new SegmentLabel(eventBus));

	Label lblSubSegment = new Label(SUB_SEGMENT);
	lblSubSegment.addStyleName(Runo.LABEL_SMALL);
	layout.addComponent(lblSubSegment);

	tableExec = new Table();
	tableExec.setPageLength(5);
	tableExec.setSizeFull();
	tableExec.setSelectable(true);
	tableExec.setImmediate(true);
	tableExec.setNullSelectionAllowed(false);
	tableExec.setContainerDataSource(createExcPlansDefaultContainer());
	tableExec.setColumnHeader("operation", "Operation");
	tableExec.setColumnHeader("ratePlanTypes", "Rate Plan Types");
	tableExec.setVisibleColumns("operation", "ratePlanTypes");
	layout.addComponent(tableExec);

	tableExec.addItemClickListener(new ItemClickListener()
	{
	    @Override
	    public void itemClick(ItemClickEvent event)
	    {
		if (event.isDoubleClick())
		{
		    tableExec.select(event.getItemId());
		    openExecutionPlanWindow(beans.getItem(event.getItemId()).getBean(), tableExec);
		}
		if (event.getItemId() != null)
		    updateStepsLayout(beans.getItem(event.getItemId()).getBean());
	    }
	});

	Button btnAdd = new Button("Add");
	btnAdd.addClickListener(new ClickListener()
	{
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		openExecutionPlanWindow(new ExecutionPlanBean(), tableExec);
	    }
	});

	Button btnRemove = new Button("Remove");
	btnRemove.addClickListener(new ClickListener()
	{
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		Object itemId = tableExec.getValue();
		beans.removeItem(itemId);
		executionStepsMap.remove(itemId);

		if (executionStepsMap.isEmpty())
		{
		    stepsLayout.setEnabled(false);
		}
	    }
	});

	HorizontalLayout btnsExec = new HorizontalLayout();
	btnsExec.setSpacing(true);
	btnsExec.addComponent(btnAdd);
	btnsExec.addComponent(btnRemove);

	layout.addComponent(btnsExec);
	layout.setComponentAlignment(btnsExec, Alignment.MIDDLE_RIGHT);

	return layout;
    }

    private VerticalLayout createStepsLayout()
    {
	VerticalLayout layout = new VerticalLayout();
	layout.setEnabled(false);
	StepsLabel lblStepsHeader = new StepsLabel(eventBus);
	lblStepsHeader.addStyleName(Runo.LABEL_H2);
	layout.addComponent(lblStepsHeader);

	lblSubSegment = new Label(
		"In case you want to edit an already added step, just double click on the row and edit the data.");
	lblSubSegment.addStyleName(Runo.LABEL_SMALL);
	layout.addComponent(lblSubSegment);

	tableSteps = new Table();
	tableSteps.setPageLength(4);
	tableSteps.setContainerDataSource(createStepsDefaultContainer());

	tableSteps.setSizeFull();
	tableSteps.setSelectable(true);
	tableSteps.setImmediate(true);
	tableSteps.setNullSelectionAllowed(false);
	tableSteps.setColumnHeader("stepNo", "Step No.");
	tableSteps.setColumnHeader("type", "Type");
	tableSteps.setColumnHeader("subject", "Subject");
	tableSteps.setColumnHeader("parser", "Parser");
	tableSteps.setVisibleColumns("stepNo", "type", "subject", "parser");

	layout.addComponent(tableSteps);

	tableSteps.addItemClickListener(new ItemClickListener()
	{
	    @Override
	    public void itemClick(ItemClickEvent event)
	    {
		if (event.isDoubleClick())
		{
		    tableSteps.select(event.getItemId());

		    StepBean bean = stepsBeans.getItem(event.getItemId()).getBean();

		    for (IBackendWindow backendWindow : backendWindows)
		    {
			if (backendWindow.getType().equalsIgnoreCase(bean.getType()))
			{
			    backendWindow.show(bean, ExecutionPlansStepView.this);
			}
		    }
		}
	    }
	});

	// Buttons
	final ComboBox cmboStepType = new ComboBox("Step Type: ");
	cmboStepType.setNullSelectionAllowed(false);
	cmboStepType.setContainerDataSource(getStepsTypes());
	cmboStepType.select("Prepaid");

	Button btnAdd = new Button("Add");
	btnAdd.addClickListener(new ClickListener()
	{
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		for (IBackendWindow backendWindow : backendWindows)
		{
		    if (backendWindow.getType().equalsIgnoreCase(cmboStepType.getValue().toString()))
		    {
			StepBean stepBean = new StepBean();
			List<StepBean> list = executionStepsMap.get(beans.getItem(tableExec.getValue()).getBean()
				.getID());

			if (list == null)
			    list = new ArrayList<StepBean>();

			list.add(stepBean);

			executionStepsMap.put(beans.getItem(tableExec.getValue()).getBean().getID(), list);
			backendWindow.show(stepBean, ExecutionPlansStepView.this);
		    }
		}
	    }
	});

	Button btnRemove = new Button("Remove");
	btnRemove.addClickListener(new ClickListener()
	{
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		String itemId = (String) tableSteps.getValue();

		if (itemId == null)
		    return;

		stepsBeans.removeItem(itemId);

		List<StepBean> list = executionStepsMap.get(tableExec.getValue());

		for (StepBean step : list)
		{
		    if (step.getID().equals(itemId))
		    {
			list.remove(step);
			break;
		    }
		}
	    }
	});

	HorizontalLayout btnsExec = new HorizontalLayout();
	btnsExec.setSpacing(true);

	FormLayout formLayout = new FormLayout(cmboStepType);
	formLayout.setSpacing(false);
	formLayout.setMargin(false);
	formLayout.addStyleName("TopMargin");

	btnsExec.addComponent(formLayout);
	btnsExec.addComponent(btnAdd);
	btnsExec.addComponent(btnRemove);

	layout.addComponent(btnsExec);
	layout.setComponentAlignment(btnsExec, Alignment.MIDDLE_RIGHT);

	return layout;
    }

    private Container getStepsTypes()
    {
	List<String> types = new ArrayList<String>(4);
	types.add("Billing");
	types.add("Prepaid");
	types.add("PCRF");
	types.add("SMS");

	IndexedContainer container = new IndexedContainer((List<String>) types);
	return container;
    }

    protected void updateStepsLayout(ExecutionPlanBean executionPlanBean)
    {
	stepsLayout.setEnabled(true);
	this.stepsBeans.removeAllItems();

	List<StepBean> steps = this.executionStepsMap.get(executionPlanBean.getID());
	if (steps != null)
	    stepsBeans.addAll(steps);
    }

    protected void removeExecutionPlan(String itemID)
    {
	beans.removeItem(itemID);
    }

    private Container createExcPlansDefaultContainer()
    {
	beans = new BeanContainer<String, ExecutionPlanBean>(ExecutionPlanBean.class);
	beans.setBeanIdProperty("ID");

	return beans;
    }

    private Container createStepsDefaultContainer()
    {
	stepsBeans = new BeanContainer<String, StepBean>(StepBean.class);
	stepsBeans.setBeanIdProperty("ID");

	return stepsBeans;
    }

    private void openExecutionPlanWindow(final ExecutionPlanBean bean, final Table tableExec)
    {
	// Create a sub-window and set the content
	final Window subWindow = new Window("New Execution Plan");
	VerticalLayout subContent = new VerticalLayout();
	subContent.setMargin(true);
	subWindow.setContent(subContent);
	subWindow.setDraggable(false);
	subWindow.setResizable(false);

	FormLayout formLayout = new FormLayout();
	formLayout.setSizeFull();

	final ComboBox cmboOperation = new ComboBox("Operation");
	VaadinUtil.adjustComboBox(cmboOperation, "Select the operation", getOperations());
	formLayout.addComponent(cmboOperation);

	if (!Strings.isNullOrEmpty(bean.getOperation()))
	{
	    cmboOperation.select(bean.getOperation());
	}

	subContent.addComponent(formLayout);
	final Label lblRatePlanTypes = new Label("Rate Plan Types:");
	lblRatePlanTypes.addStyleName(Runo.LAYOUT_DARKER);
	subContent.addComponent(lblRatePlanTypes);
	subContent.setComponentAlignment(lblRatePlanTypes, Alignment.MIDDLE_LEFT);

	FormLayout formRatePlansLayout = new FormLayout();
	formRatePlansLayout.setSizeFull();

	final CheckBox checPrepaid = new CheckBox("Prepaid", Strings.nullToEmpty(bean.getRatePlanTypes()).contains(
		"PRE"));
	final CheckBox checEasy = new CheckBox("Easy", Strings.nullToEmpty(bean.getRatePlanTypes()).contains("EASY"));
	final CheckBox checControl = new CheckBox("Control", Strings.nullToEmpty(bean.getRatePlanTypes()).contains(
		"CONT"));
	final CheckBox checPostpaid = new CheckBox("Postpaid", Strings.nullToEmpty(bean.getRatePlanTypes()).contains(
		"POST"));

	formRatePlansLayout.addComponent(checPrepaid);
	formRatePlansLayout.addComponent(checEasy);
	formRatePlansLayout.addComponent(checControl);
	formRatePlansLayout.addComponent(checPostpaid);
	subContent.addComponent(formRatePlansLayout);

	Button btnAdd = new Button("Add");
	subContent.addComponent(btnAdd);
	subContent.setComponentAlignment(btnAdd, Alignment.BOTTOM_RIGHT);
	btnAdd.addClickListener(new ClickListener()
	{
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		if (VaadinUtil.isValid(cmboOperation) && atLeastOneRatePlanTypeSelected())
		{
		    bean.setRatePlanTypes(getRatePlanTypes());
		    bean.setOperation(cmboOperation.getValue().toString());
		    beans.addBean(bean);
		    executionPlansMap.put(bean.getID(), getExecutionPlan(bean));

		    subWindow.close();
		    tableExec.sort(new Object[]
		    { "operation" }, new boolean[]
		    { true });

		    tableExec.select(bean.getID());
		    updateStepsLayout(bean);
		}
	    }

	    private ExecutionPlan getExecutionPlan(ExecutionPlanBean bean)
	    {
		ExecutionPlan executionPlan = executionPlansMap.get(bean.getID());

		if (executionPlan == null)
		    executionPlan = new ExecutionPlan();

		executionPlan.setOperation(bean.getOperation());
		executionPlan.setRatePlanTypes(bean.getRatePlanTypes());

		return executionPlan;
	    }

	    private String getRatePlanTypes()
	    {
		StringBuilder builder = new StringBuilder();
		if (checPrepaid.getValue())
		    builder.append("PRE");
		if (checEasy.getValue())
		    builder.append("_EASY");
		if (checControl.getValue())
		    builder.append("_CONT");
		if (checPostpaid.getValue())
		    builder.append("_POST");

		if (builder.toString().startsWith("_"))
		{
		    builder.deleteCharAt(0);
		}
		if (builder.toString().endsWith("_"))
		{
		    builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	    }

	    private boolean atLeastOneRatePlanTypeSelected()
	    {
		if (checControl.getValue() || checEasy.getValue() || checPostpaid.getValue() || checPrepaid.getValue())
		{
		    lblRatePlanTypes.setComponentError(null);
		    return true;
		}

		lblRatePlanTypes.setComponentError(new UserError("You have to select at least one rate plan type."));
		return false;
	    }
	});

	subContent.setComponentAlignment(formLayout, Alignment.MIDDLE_CENTER);
	// Center it in the browser window
	subWindow.center();
	subWindow.setModal(true);
	// Open it in the UI
	UI.getCurrent().addWindow(subWindow);
    }

    private Container getOperations()
    {
	List<String> operations = new ArrayList<String>();
	operations.add("Insert");
	operations.add("Delete");
	operations.add("Repurchase");
	IndexedContainer container = new IndexedContainer((List<String>) operations);
	return container;
    }

    @Override
    public Component getContent()
    {
	return this;
    }

    @Override
    public boolean onBack()
    {
	return true;
    }

    @Override
    public String getCaption()
    {
	return "Execution Plans";
    }

    @Override
    protected void processStep()
    {
	List<ExecutionPlan> executionPlans = new ArrayList<ExecutionPlan>();

	for (String id : this.executionPlansMap.keySet())
	{
	    ExecutionPlan executionPlan = executionPlansMap.get(id);
	    List<StepBean> list = this.executionStepsMap.get(id);
	    List<StepConf> steps = new ArrayList<StepConf>(list.size());
	    steps.addAll(list);
	    executionPlan.setSteps(steps);
	    executionPlans.add(executionPlan);
	}

	Product product = getProduct();
	product.setExecutionPlans(executionPlans);
    }

    public void updateView(StepBean stepBean)
    {
	stepsBeans.addBean(stepBean);
	tableSteps.sort(new Object[]
	{ "stepNo" }, new boolean[]
	{ true });
    }

    @Override
    public boolean validate()
    {
	if (this.beans.getItemIds().isEmpty())
	{
	    eventBus.post(new SegmentLabel.SegmentValidateEvent("At least one execution plan should be added!"));
	    return false;
	}

	eventBus.post(new SegmentLabel.SegmentValidateEvent(null));

	List<String> itemIds = this.beans.getItemIds();
	for (String itemId : itemIds)
	{
	    List<StepBean> list = this.executionStepsMap.get(itemId);
	    if (list == null || list.isEmpty())
	    {
		tableExec.select(itemId);
		eventBus.post(new StepsLabel.StepsValidateEvent("At least one step should be added!"));
		return false;
	    }
	}

	eventBus.post(new StepsLabel.StepsValidateEvent(null));
	return true;
    }

}
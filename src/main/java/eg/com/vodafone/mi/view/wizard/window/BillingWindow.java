package eg.com.vodafone.mi.view.wizard.window;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vaadin.data.Container;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class BillingWindow extends AbstractWindow
{

    private BeanContainer<String, BillingServiceBean> services;
    private FormLayout form;
    private BeanFieldGroup<BillingServiceBean> binder;
    private ComboBox cmboServicesType;
    private TextField txtSNCode;
    private TextField txtSPCode;
    private TextField txtShortDesc;
    private TextField txtID;
    private TextField txtValue;
    private TextField txtEndDate;
    private ComboBox cmboOperation;
    private ComboBox cmboSubject;
    private TextField txtParser;
    private TextField txtStepNo;
    private Label lblBillingServices;
    private Table table;

    @Override
    public String getType()
    {
	return "Billing";
    }

    @Override
    protected void updateBean()
    {
	if (isNewConf())
	{
	    this.stepBean.setParser(this.txtParser.getValue());
	    this.stepBean.setStepNo(Integer.parseInt(this.txtStepNo.getValue()));
	    this.stepBean.setSubject((String) this.cmboSubject.getValue());
	    this.stepBean.setType("BILLING");

	    Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();

	    List<String> itemIds = services.getItemIds();
	    for (String item : itemIds)
	    {
		BillingServiceBean bean = services.getItem(item).getBean();

		Map<String, String> serviceConf = new HashMap<String, String>();

		serviceConf.put("SN", bean.getSn());
		serviceConf.put("SP", bean.getSp());
		serviceConf.put("ID", bean.getID());
		serviceConf.put("VALUE", bean.getValue());
		serviceConf.put("OPERATION", bean.getOperation());
		serviceConf.put("ENDDATE", bean.getEndDate());
		serviceConf.put("SERVICE_TYPE", bean.getServiceType());
		serviceConf.put("SN_SHORTDESC", bean.getShortDesc());
		
		serviceConf.put("SUBJECT", bean.getSubject());
		serviceConf.put("SUBJECT_PARSER", bean.getParser());

		map.put(bean.getShortDesc(), serviceConf);
	    }

	    this.stepBean.setServices(map);
	}
    }

    @Override
    protected List<Field<?>> getNewFields()
    {
	List<Field<?>> fields = new ArrayList<Field<?>>(5);

	fields.add(txtStepNo);
	fields.add(cmboSubject);
	fields.add(txtParser);

	if (binder.isEnabled())
	{
	    fields.add(cmboServicesType);
	    fields.add(txtSNCode);
	    fields.add(txtSPCode);
	    fields.add(txtShortDesc);
	    fields.add(txtID);
	    fields.add(txtValue);
	    fields.add(txtEndDate);
	    fields.add(cmboOperation);
	}

	return fields;
    }

    @Override
    protected boolean isFieldsValid()
    {
	if (super.isFieldsValid())
	{
	    if (this.services == null || this.services.getItemIds() == null || this.services.getItemIds().size() == 0)
	    {
		this.lblBillingServices.setComponentError(new UserError("At least one service should be added"));
		return false;
	    }
	    else
	    {
		this.lblBillingServices.setComponentError(null);

		if (isNewConf())
		{
		    List<String> itemIds = services.getItemIds();
		    for (String item : itemIds)
		    {
			BillingServiceBean bean = services.getItem(item).getBean();

			if ("NEWSN".equals(bean.getSn()))
			{
			    binder.setEnabled(true);
			    binder.setItemDataSource(bean);

			    table.sort(new Object[]
			    { "serviceType" }, new boolean[]
			    { true });

			    table.select(item);
			    return super.isFieldsValid();
			}
		    }
		}
	    }
	    return true;
	}

	return false;
    }

    @Override
    protected Component getNewServiceConfLayout()
    {
	VerticalLayout layout = new VerticalLayout();
	layout.setSizeFull();

	layout.addComponent(createServicesTable());
	layout.addComponent(createEditorForm());

	return layout;
    }

    private Component createServicesTable()
    {
	VerticalLayout layout = new VerticalLayout();
	layout.setSizeFull();

	txtStepNo = new TextField("Step No.: ");
	txtStepNo.setSizeFull();
	txtStepNo.setRequired(true);
	txtStepNo.setNullRepresentation("");
	txtStepNo.setValue(Integer.toString(this.stepBean.getStepNo()));
	FormLayout formLayout = new FormLayout(txtStepNo);

	cmboSubject = new ComboBox("Subject: ", getPossibleSubjects());
	cmboSubject.setSizeFull();
	cmboSubject.setImmediate(true);
	cmboSubject.setNullSelectionAllowed(false);
	cmboSubject.setRequired(true);
	cmboSubject.setValue(this.stepBean.getSubject());
	formLayout.addComponent(cmboSubject);

	txtParser = new TextField("Parser: ", this.stepBean.getParser());
	txtParser.setSizeFull();
	txtParser.setRequired(true);
	txtParser.setNullRepresentation("");
	formLayout.addComponent(txtParser);
	layout.addComponent(formLayout);

	lblBillingServices = new Label(
		"Add new Billing services by clicking on the add button then editing the services by clicking on the services. </br> To refresh the table after editing the services configuration just click on any row.",
		ContentMode.HTML);
	lblBillingServices.setStyleName(Runo.LABEL_SMALL);
	layout.addComponent(lblBillingServices);

	table = new Table();
	table.setPageLength(3);
	table.setSizeFull();
	table.setSelectable(true);
	table.setImmediate(true);
	table.setNullSelectionAllowed(false);
	table.setContainerDataSource(createTableDataSource());
	table.setColumnHeader("serviceType", "Service Type");
	table.setColumnHeader("sn", "SN");
	table.setColumnHeader("sp", "SP");
	table.setColumnHeader("ID", "ID");
	table.setColumnHeader("value", "Value");
	table.setColumnHeader("operation", "Operation");
	table.setVisibleColumns("serviceType", "sn", "sp", "ID", "value", "operation");

	layout.addComponent(table);

	Button btnAdd = new Button("Add");
	btnAdd.addClickListener(new ClickListener()
	{
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		BeanItem<BillingServiceBean> bean = services.addBean(new BillingServiceBean());
		bean.getBean().setSn("NEWSN");
		binder.setItemDataSource(bean);

		table.sort(new Object[]
		{ "serviceType" }, new boolean[]
		{ true });

		table.select(bean.getBean().ID);
	    }
	});

	Button btnRemove = new Button("Remove");
	btnRemove.addClickListener(new ClickListener()
	{
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		Object value = table.getValue();

		if (value == null)
		    return;

		services.removeItem(value);
		binder.setEnabled(false);
	    }
	});

	table.addItemClickListener(new ItemClickListener()
	{
	    @Override
	    public void itemClick(ItemClickEvent event)
	    {
		binder.setEnabled(true);
		binder.setItemDataSource(services.getItem(event.getItemId()).getBean());

		table.sort(new Object[]
		{ "serviceType" }, new boolean[]
		{ true });

		table.select(event.getItemId());
	    }
	});

	HorizontalLayout btnsLayout = new HorizontalLayout();
	btnsLayout.setSpacing(true);
	btnsLayout.addComponent(btnAdd);
	btnsLayout.addComponent(btnRemove);

	layout.addComponent(btnsLayout);
	layout.setComponentAlignment(btnsLayout, Alignment.MIDDLE_RIGHT);

	return layout;
    }

    private Component createEditorForm()
    {
	VerticalLayout layout = new VerticalLayout();
	layout.setMargin(true);

	binder = new BeanFieldGroup<BillingServiceBean>(BillingServiceBean.class);
	binder.setBuffered(false);

	form = new FormLayout();
	form.setImmediate(true);

	cmboServicesType = new ComboBox("Service Type: ");
	cmboServicesType.setSizeFull();
	cmboServicesType.setRequired(true);
	cmboServicesType.setNullSelectionAllowed(false);
	cmboServicesType.setImmediate(true);
	cmboServicesType.setContainerDataSource(getPossibleTypes());
	form.addComponent(cmboServicesType);
	binder.bind(cmboServicesType, "serviceType");

	txtSNCode = new TextField("SN Code: ");
	txtSNCode.setSizeFull();
	txtSNCode.setRequired(true);
	txtSNCode.setNullRepresentation("");
	form.addComponent(txtSNCode);
	binder.bind(txtSNCode, "sn");

	txtSPCode = new TextField("SP Code: ");
	txtSPCode.setSizeFull();
	txtSPCode.setNullRepresentation("");
	form.addComponent(txtSPCode);
	binder.bind(txtSPCode, "sp");
	
	txtShortDesc = new TextField("SN Short Desc Code: ");
	txtShortDesc.setSizeFull();
	txtShortDesc.setRequired(true);
	txtShortDesc.setNullRepresentation("");
	form.addComponent(txtShortDesc);
	binder.bind(txtShortDesc, "shortDesc");

	txtID = new TextField("ID: ");
	txtID.setSizeFull();
	txtID.setNullRepresentation("");
	form.addComponent(txtID);
	binder.bind(txtID, "ID");

	txtValue = new TextField("Value: ");
	txtValue.setSizeFull();
	txtValue.setNullRepresentation("");
	form.addComponent(txtValue);
	binder.bind(txtValue, "value");
	
	txtEndDate = new TextField("End Date: ");
	txtEndDate.setSizeFull();
	txtEndDate.setNullRepresentation("");
	form.addComponent(txtEndDate);
	binder.bind(txtEndDate, "endDate");

	cmboOperation = new ComboBox("Operation: ");
	cmboOperation.setSizeFull();
	cmboOperation.setRequired(true);
	cmboOperation.setNullSelectionAllowed(false);
	cmboOperation.setImmediate(true);
	cmboOperation.setContainerDataSource(getPossibleOperations());
	form.addComponent(cmboOperation);
	binder.bind(cmboOperation, "operation");

	binder.setEnabled(false);

	layout.addComponent(form);
	return layout;
    }

    private Container getPossibleSubjects()
    {
	List<String> streams = new ArrayList<String>(2);
	streams.add("VFE.BILLING.writeContractServices.REQ");
	streams.add("VFE.BILLING.writeEventService.REQ");
	IndexedContainer container = new IndexedContainer((List<String>) streams);
	return container;
    }

    private Container getPossibleOperations()
    {
	List<String> streams = new ArrayList<String>(2);
	streams.add("INSERT");
	streams.add("DELETE");
	IndexedContainer container = new IndexedContainer((List<String>) streams);
	return container;
    }

    private Container getPossibleTypes()
    {
	List<String> streams = new ArrayList<String>(2);
	streams.add("EVENTSERVICE");
	streams.add("SUPPLEMENTARYSERVICE");
	IndexedContainer container = new IndexedContainer((List<String>) streams);
	return container;
    }

    private Container createTableDataSource()
    {
	services = new BeanContainer<String, BillingServiceBean>(BillingServiceBean.class);
	services.setBeanIdProperty("rowID");

	Map<String, Map<String, String>> servicesMap = this.stepBean.getServices();

	if (servicesMap != null)
	{
	    Collection<Map<String, String>> values = servicesMap.values();
	    for (Map<String, String> map : values)
	    {
		BillingServiceBean bean = new BillingServiceBean();
		bean.setID(map.get("ID"));
		bean.setSn(map.get("SN"));
		bean.setSp(map.get("SP"));
		bean.setValue(map.get("VALUE"));
		bean.setEndDate(map.get("ENDDATE"));
		bean.setServiceType(map.get("SERVICE_TYPE"));
		bean.setOperation(map.get("OPERATION"));
		bean.setShortDesc(map.get("SN_SHORTDESC"));
		bean.setRowID(map.get("SN_SHORTDESC"));
		
		this.services.addBean(bean);
	    }
	}

	return services;
    }

    public static class BillingServiceBean implements Serializable
    {
	private String rowID;
	private String serviceType;
	private String sn;
	private String sp;
	private String shortDesc;
	private String ID;
	private String value;
	private String endDate;
	private String operation;
	private String subject;
	private String parser;

	public BillingServiceBean()
	{
	    rowID = "ID:" + new Date().toString();

	    this.parser = "defaultParser";
	}

	public String getServiceType()
	{
	    return serviceType;
	}

	public void setServiceType(String serviceType)
	{
	    this.serviceType = serviceType;
	}

	public String getSn()
	{
	    return sn;
	}

	public void setSn(String sn)
	{
	    this.sn = sn;
	}

	public String getSp()
	{
	    return sp;
	}

	public void setSp(String sp)
	{
	    this.sp = sp;
	}

	public String getShortDesc()
	{
	    return shortDesc;
	}

	public void setShortDesc(String shortDesc)
	{
	    this.shortDesc = shortDesc;
	}

	public String getID()
	{
	    return ID;
	}

	public void setID(String iD)
	{
	    ID = iD;
	}

	public String getValue()
	{
	    return value;
	}

	public void setValue(String value)
	{
	    this.value = value;
	}

	public String getOperation()
	{
	    return operation;
	}

	public void setOperation(String operation)
	{
	    this.operation = operation;
	}

	public String getSubject()
	{
	    return subject;
	}

	public void setSubject(String subject)
	{
	    this.subject = subject;
	}

	public String getParser()
	{
	    return parser;
	}

	public void setParser(String parser)
	{
	    this.parser = parser;
	}

	public String getRowID()
	{
	    return rowID;
	}

	public void setRowID(String rowID)
	{
	    this.rowID = rowID;
	}

	public String getEndDate()
	{
	    return endDate;
	}

	public void setEndDate(String endDate)
	{
	    this.endDate = endDate;
	}
    }
}
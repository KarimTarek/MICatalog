package eg.com.vodafone.mi.view.wizard.window;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.vaadin.data.Container;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

import eg.com.vodafone.mi.view.wizard.bean.StepBean;

@SuppressWarnings("serial")
public class PrepaidWindow extends AbstractWindow
{
    private static final String INSERT = "INSERT";
    private ComboBox cmboOperation;
    private TextField txtStepNo;
    private TextField txtSubject;
    private TextField txtParser;
    private TextField txtServiceName;
    private TextField txtBundleID;

    @Override
    public String getType()
    {
	return "Prepaid";
    }

    @Override
    protected Component getNewServiceConfLayout()
    {
	FormLayout formLayout = new FormLayout();
	formLayout.setSizeFull();

	txtStepNo = new TextField("Step No.: ");
	txtStepNo.setSizeFull();
	txtStepNo.setRequired(true);
	txtStepNo.setValue(Integer.toString(this.stepBean.getStepNo()));
	formLayout.addComponent(txtStepNo);

	cmboOperation = new ComboBox("Operation: ");
	cmboOperation.setSizeFull();
	cmboOperation.setRequired(true);
	cmboOperation.setNullSelectionAllowed(false);
	cmboOperation.setImmediate(true);
	cmboOperation.setContainerDataSource(getPossibleOperations());

	String operation = getOperation(this.stepBean);
	if (!Strings.isNullOrEmpty(operation))
	    cmboOperation.setValue(operation);

	cmboOperation.addValueChangeListener(new ValueChangeListener()
	{
	    @Override
	    public void valueChange(ValueChangeEvent event)
	    {
		if (INSERT.equals(event.getProperty().getValue()))
		{
		    txtSubject.setValue("VFE.PREPAID.ServiceOPTIN.REQ");
		}
		else
		{
		    txtSubject.setValue("VFE.PREPAID.ServiceOPTOUT.REQ");
		}
	    }
	});
	formLayout.addComponent(cmboOperation);

	txtSubject = new TextField("Subject: ", "VFE.PREPAID.ServiceOPTIN.REQ");
	txtSubject.setSizeFull();
	txtSubject.setRequired(true);
	String subject = this.stepBean.getSubject();

	if (Strings.isNullOrEmpty(subject))
	    txtSubject.setValue("VFE.PREPAID.ServiceOPTIN.REQ");
	else
	    txtSubject.setValue(subject);

	formLayout.addComponent(txtSubject);

	txtParser = new TextField("Parser: ", "defaultParser");
	txtParser.setSizeFull();
	txtParser.setRequired(true);
	String parser = this.stepBean.getParser();

	if (Strings.isNullOrEmpty(parser))
	    txtParser.setValue("defaultParser");
	else
	    txtParser.setValue(parser);

	formLayout.addComponent(txtParser);

	txtServiceName = new TextField("Service Name: ");
	txtServiceName.setSizeFull();
	txtServiceName.setRequired(true);
	txtServiceName.setValue(getMapValue("SERVICE"));
	formLayout.addComponent(txtServiceName);

	txtBundleID = new TextField("Bundle ID: ");
	txtBundleID.setSizeFull();
	txtBundleID.setValue(getMapValue("BUNDLE_ID"));
	txtBundleID.setRequired(true);
	formLayout.addComponent(txtBundleID);

	return formLayout;
    }

    private String getOperation(StepBean stepBean)
    {
	Map<String, Map<String, String>> map = this.stepBean.getServices();

	if (map == null)
	    return "";
	
	Map<String, String> service = map.get("New");
	
	if (service == null)
	    return "";
	
	return service.get("OPERATION");
    }

    private Container getPossibleOperations()
    {
	List<String> streams = new ArrayList<String>(2);
	streams.add(INSERT);
	streams.add("DELETE");
	IndexedContainer container = new IndexedContainer((List<String>) streams);
	return container;
    }

    private String getMapValue(String key)
    {
	Map<String, Map<String, String>> services = this.stepBean.getServices();

	if (services == null)
	    return "";

	Map<String, String> map = services.get("New");

	if (map == null)
	    return "";

	return map.get(key);
    }

    @Override
    protected List<Field<?>> getNewFields()
    {
	List<Field<?>> fields = new ArrayList<Field<?>>(5);
	fields.add(txtStepNo);
	fields.add(txtSubject);
	fields.add(txtParser);
	fields.add(txtServiceName);
	fields.add(txtBundleID);

	return fields;
    }

    @Override
    protected void updateBean()
    {
	if (isNewConf())
	{
	    this.stepBean.setParser(this.txtParser.getValue());
	    this.stepBean.setStepNo(Integer.parseInt(this.txtStepNo.getValue()));
	    this.stepBean.setSubject(this.txtSubject.getValue());
	    this.stepBean.setType("PREPAID");

	    Map<String, String> serviceConf = new HashMap<String, String>();
	    serviceConf.put("SERVICE", this.txtServiceName.getValue());
	    serviceConf.put("BUNDLE_ID", this.txtBundleID.getValue());
	    serviceConf.put("OPERATION", (String) this.cmboOperation.getValue());

	    Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
	    map.put("New", serviceConf);

	    this.stepBean.setServices(map);
	}
    }

}
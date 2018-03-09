package eg.com.vodafone.mi.view.wizard.window;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.UI;
import com.vaadin.v7.ui.VerticalLayout;
import com.vaadin.ui.Window;

import eg.com.vodafone.mi.utility.VaadinUtil;
import eg.com.vodafone.mi.view.wizard.ExecutionPlansStepView;
import eg.com.vodafone.mi.view.wizard.bean.StepBean;

@SuppressWarnings("serial")
public abstract class AbstractWindow extends Window implements IBackendWindow
{
    protected StepBean stepBean;
    private boolean isNewConf = true;
    private ExecutionPlansStepView view;

    @Override
    public void show(StepBean stepBean, ExecutionPlansStepView view)
    {
        this.view = view;
        this.stepBean = stepBean;
        createWindow();
        UI.getCurrent().addWindow(this);
    }

    protected Component createLayout()
    {
	VerticalLayout layout = new VerticalLayout();
	layout.setMargin(true);

	ComboBox cmboStatus = new ComboBox("Select whether the " + this.getType() + " service is new or existing");
	cmboStatus.setContainerDataSource(getPossibleStatuses());
	cmboStatus.setWidth("80px");
	cmboStatus.select("New");
	cmboStatus.setNullSelectionAllowed(false);

	FormLayout formLayout = new FormLayout();
	formLayout.addComponent(cmboStatus);
	layout.addComponent(formLayout);

	Component confLayout = getNewServiceConfLayout();
	layout.addComponent(confLayout);
	layout.setComponentAlignment(confLayout, Alignment.MIDDLE_CENTER);

	Button btnAdd = new Button("Save & Close");
	layout.addComponent(btnAdd);
	layout.setComponentAlignment(btnAdd, Alignment.BOTTOM_RIGHT);
	btnAdd.addClickListener(new ClickListener()
	{
	    @Override
	    public void buttonClick(ClickEvent event)
	    {
		if (isFieldsValid())
		{
		    updateBean();
		    updateTable();
		    AbstractWindow.this.close();
		}
	    }
	});

	return layout;
    }

    protected void updateTable()
    {
	this.view.updateView(stepBean);
    }

    protected abstract void updateBean();

    protected boolean isFieldsValid()
    {
	if (isNewConf)
	{
	    List<Field<?>> fields = getNewFields();

	    for (Field<?> field : fields)
	    {
		if (!VaadinUtil.isValid(field))
		    return false;
	    }
	}

	return true;
    }

    protected abstract List<Field<?>> getNewFields();

    protected abstract Component getNewServiceConfLayout();

    private Container getPossibleStatuses()
    {
	List<String> types = new ArrayList<String>(1);
	types.add("New");

	IndexedContainer container = new IndexedContainer((List<String>) types);
	return container;
    }

    private void createWindow()
    {
        this.setCaption(this.getType() + " Configuration");
        this.setDraggable(false);
        this.setResizable(false);
        this.setContent(createLayout());
        this.center();
        this.setModal(true);
    }

    public boolean isNewConf()
    {
	return isNewConf;
    }
}
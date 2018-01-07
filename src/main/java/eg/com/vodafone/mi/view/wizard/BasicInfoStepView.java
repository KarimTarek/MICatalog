package eg.com.vodafone.mi.view.wizard;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.Runo;

import eg.com.vodafone.mi.domain.Product;
import eg.com.vodafone.mi.loader.CommonDataLoader;
import eg.com.vodafone.mi.loader.CommonDataLoader.CommonProductsData;
import eg.com.vodafone.mi.utility.VaadinUtil;

@SuppressWarnings("serial")
public class BasicInfoStepView extends AbstractStepView
{
    private TextField txtID;
    private TextField txtName;
    private ComboBox cmboType;
    private ComboBox cmboCategory;
    private ComboBox cmboStream;

    public BasicInfoStepView(Product product)
    {
	super(product);
	
	VerticalLayout layout = new VerticalLayout();
	
	Label lblHeader = new Label("Basic Product Info");
	lblHeader.addStyleName(Runo.LABEL_H1);
	
	layout.addComponent(lblHeader);
	
	Label lblTxt = new Label(
		"In this wizard you will go over a few steps to complete the definition of a new Product. As a first step, fill the below form with the basic information of the new Product.");
	layout.addComponent(lblTxt);
	layout.setSizeFull();
	layout.setMargin(true);

	FormLayout formLayout = new FormLayout();
	formLayout.setSizeFull();
	txtID = new TextField("ID: ");
	txtID.setSizeFull();
	txtID.setRequired(true);
	formLayout.addComponent(txtID);
	this.addField(txtID);
	txtName = new TextField("Name: ");
	txtName.setSizeFull();
	txtName.setRequired(true);
	formLayout.addComponent(txtName);
	this.addField(txtName);
	cmboType = new ComboBox("Type: ");
	VaadinUtil.adjustComboBox(cmboType, "Select the type...", getExistingTypes());
	formLayout.addComponent(cmboType);
	this.addField(cmboType);
	cmboCategory = new ComboBox("Category: ");
	VaadinUtil.adjustComboBox(cmboCategory, "Select the category...", getExistingCategories());
	formLayout.addComponent(cmboCategory);
	this.addField(cmboCategory);
	cmboStream = new ComboBox("Stream: ");
	VaadinUtil.adjustComboBox(cmboStream, "Select the stream...", getStreams());
	formLayout.addComponent(cmboStream);
	this.addField(cmboStream);

	layout.addComponent(formLayout);
	layout.setComponentAlignment(formLayout, Alignment.MIDDLE_LEFT);
	this.setCompositionRoot(layout);
    }

    @SuppressWarnings("unchecked")
    private Container getExistingTypes()
    {
	IndexedContainer container = new IndexedContainer((List<String>)CommonDataLoader.getCommonData(CommonProductsData.Types));
	return container;
    }
    
    @SuppressWarnings("unchecked")
    private Container getExistingCategories()
    {
	IndexedContainer container = new IndexedContainer((List<String>)CommonDataLoader.getCommonData(CommonProductsData.Categories));
	return container;
    }
    
    private Container getStreams()
    {
	List<String> streams = new ArrayList<String>();
	streams.add("MI");
	streams.add("USB");
	streams.add("ADSL");
	IndexedContainer container = new IndexedContainer((List<String>)streams);
	return container;
    }
    
    @Override
    public String getCaption()
    {
	return "Basic Product Info";
    }

    @Override
    public Component getContent()
    {
	return this;
    }

    @Override
    public boolean onBack()
    {
	return false;
    }

    @Override
    protected void processStep()
    {
	Product product = getProduct();
	product.setPrdID(this.txtID.getValue());
	product.setName(this.txtName.getValue());
	product.setType(this.cmboType.getValue().toString());
	product.setCategory(this.cmboCategory.getValue().toString());
	product.setStatus("LIVE");
    }
}
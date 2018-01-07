package eg.com.vodafone.mi.view.wizard;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.teemu.wizards.WizardStep;

import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Field;

import eg.com.vodafone.mi.domain.Product;

@SuppressWarnings("serial")
public abstract class AbstractStepView extends CustomComponent implements WizardStep
{
    private Product product;
    
    public AbstractStepView(Product product)
    {
	this.product = product;
    }
    
    private List<Field<?>> fields = new ArrayList<Field<?>>();

    protected void addField(Field<?> field)
    {
	fields.add(field);
	field.setErrorHandler(new DefaultErrorHandler());
    }

    public boolean validate()
    {
	for (Field<?> field : fields)
	{
	    try
	    {
		if (field.isValid())
		{
		    ((AbstractComponent)field).setComponentError(null);
		}
		else
		{
		    ((AbstractComponent)field).setComponentError(new UserError(""));
		    return false;
		}
	    }
	    catch (InvalidValueException e)
	    {
		return false;
	    }
	}

	processStep();
	
	return true;
    }

    protected abstract void processStep();
    
    @Override
    public boolean onAdvance()
    {
	if (this.validate())
	{
	    processStep();
	    
	    return true;
	}
	return false;
    }

    public Product getProduct()
    {
        return product;
    }
}

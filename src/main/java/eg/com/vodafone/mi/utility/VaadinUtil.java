package eg.com.vodafone.mi.utility;

import com.vaadin.v7.data.Container;
import com.vaadin.server.UserError;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.Field;

public class VaadinUtil
{
    public static void adjustComboBox(ComboBox cmboBox, String inputString, Container container)
    {
	cmboBox.setSizeFull();
	cmboBox.setRequired(true);
	cmboBox.setRequiredError("Mandatory Data");
	cmboBox.setContainerDataSource(container);
	cmboBox.setInputPrompt(inputString);
	cmboBox.setNullSelectionAllowed(false);
    }

    public static boolean isValid(Field<?> field)
    {
	if (field.isValid())
	{
	    ((AbstractComponent) field).setComponentError(null);
	    return true;
	}
	else
	{
	    ((AbstractComponent) field).setComponentError(new UserError(""));
	    return false;
	}
    }

    public static boolean isFieldsValid(Field<?>... fields)
    {
	for (int i = 0; i < fields.length; i++)
	{
	    Field<?> temp = fields[i];
	    if (temp.isValid())
	    {
		((AbstractComponent) temp).setComponentError(null);
	    }
	    else
	    {
		((AbstractComponent) temp).setComponentError(new UserError(""));
		return false;
	    }
	}
	return true;
    }
}

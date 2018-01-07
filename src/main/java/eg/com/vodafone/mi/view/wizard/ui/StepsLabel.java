package eg.com.vodafone.mi.view.wizard.ui;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.server.UserError;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.Runo;

@SuppressWarnings("serial")
public class StepsLabel extends AbstractComponent
{
    private Label lbl;
    
    public StepsLabel(EventBus bus)
    {
	super(bus);
	
	lbl = new Label("Steps");
	lbl.addStyleName(Runo.LABEL_H2);
	
	this.setCompositionRoot(lbl);
	this.setSizeUndefined();
	
	bus.register(this);
    }

    @Subscribe
    public void validateSteps(StepsValidateEvent event)
    {
	if (!Strings.isNullOrEmpty(event.getErrorMessage()))
	{
	    this.setComponentError(new UserError(event.getErrorMessage()));
	}
	else
	{
	    this.setComponentError(null);
	}
    }
    
    public static class StepsValidateEvent
    {
	private String errorMessage;
	
	public StepsValidateEvent(String errorMessage)
	{
	    this.errorMessage = errorMessage;
	}

	public String getErrorMessage()
	{
	    return errorMessage;
	}
    }
}

package eg.com.vodafone.mi.view.wizard.ui;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.vaadin.server.UserError;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.themes.Runo;

@SuppressWarnings("serial")
public class SegmentLabel extends AbstractComponent
{
    private Label lbl;
    
    public SegmentLabel(EventBus bus)
    {
	super(bus);
	
	lbl = new Label("Segment");
	lbl.addStyleName(Runo.LABEL_H2);
	
	this.setCompositionRoot(lbl);
	this.setSizeUndefined();
	
	bus.register(this);
    }

    @Subscribe
    public void validateSegment(SegmentValidateEvent event)
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
    
    public static class SegmentValidateEvent
    {
	private String errorMessage;
	
	public SegmentValidateEvent(String errorMessage)
	{
	    this.errorMessage = errorMessage;
	}

	public String getErrorMessage()
	{
	    return errorMessage;
	}
    }
}

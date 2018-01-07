package eg.com.vodafone.mi.view.wizard.ui;

import com.google.common.eventbus.EventBus;
import com.vaadin.ui.CustomComponent;

@SuppressWarnings("serial")
public class AbstractComponent extends CustomComponent
{
    private EventBus eventBus;
    
    public AbstractComponent(EventBus bus)
    {
	this.eventBus = bus;
    }

    protected EventBus getEventBus()
    {
        return eventBus;
    }

}
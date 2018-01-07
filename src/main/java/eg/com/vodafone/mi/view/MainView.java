package eg.com.vodafone.mi.view;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.VerticalLayout;

/** Main view with a menu */
@SuppressWarnings("serial")
public class MainView extends VerticalLayout implements View
{

    public MainView()
    {
        this.setSizeFull();
    }

    @Override
    public void enter(ViewChangeEvent event)
    {
    }
}
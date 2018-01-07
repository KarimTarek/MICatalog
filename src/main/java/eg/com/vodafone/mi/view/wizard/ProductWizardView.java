package eg.com.vodafone.mi.view.wizard;

import org.vaadin.teemu.wizards.Wizard;
import org.vaadin.teemu.wizards.event.WizardCancelledEvent;
import org.vaadin.teemu.wizards.event.WizardCompletedEvent;
import org.vaadin.teemu.wizards.event.WizardProgressListener;
import org.vaadin.teemu.wizards.event.WizardStepActivationEvent;
import org.vaadin.teemu.wizards.event.WizardStepSetChangedEvent;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Panel;

import eg.com.vodafone.mi.domain.Product;

@SuppressWarnings("serial")
public class ProductWizardView extends CustomComponent implements View
{
    private static final String NEW_PRODUCT = "Define New Product";
    private Panel container;

    public ProductWizardView()
    {
	container = new Panel();
	container.setSizeFull();
	container.setCaption(NEW_PRODUCT);

	this.setCompositionRoot(container);
	this.setSizeFull();
    }

    @Override
    public void enter(ViewChangeEvent event)
    {
	// init the product
	final Product product = new Product();

	Wizard wizard = new Wizard();
	wizard.setSizeFull();
	wizard.addStyleName("light");
	
	container.setContent(wizard);
	
	wizard.addListener(new WizardProgressListener()
	{

	    @Override
	    public void wizardCompleted(WizardCompletedEvent event)
	    {
	    }

	    @Override
	    public void wizardCancelled(WizardCancelledEvent event)
	    {
	    }

	    @Override
	    public void stepSetChanged(WizardStepSetChangedEvent event)
	    {
	    }

	    @Override
	    public void activeStepChanged(WizardStepActivationEvent event)
	    {
	    }
	});
	
	
	wizard.addStep(new BasicInfoStepView(product));
	wizard.addStep(new ExecutionPlansStepView(product));
	wizard.addStep(new ReviewChangesView(product));
    }

}
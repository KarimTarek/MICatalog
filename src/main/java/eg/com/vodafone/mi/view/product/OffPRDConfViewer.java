package eg.com.vodafone.mi.view.product;

import java.util.Map;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eg.com.vodafone.mi.domain.StepConf;

public class OffPRDConfViewer implements IBackendConfViewer
{

    @Override
    public String getType()
    {
	return "OFFLINE_PRD";
    }

    @Override
    public Component createView(StepConf step)
    {
	VerticalLayout leftSide = new VerticalLayout();
	VerticalLayout rightSide = new VerticalLayout();

	HorizontalLayout layout = new HorizontalLayout();
	layout.addComponent(leftSide);
	layout.addComponent(rightSide);
	layout.setMargin(true);

	for (String key : step.getServices().keySet())
	{
	    Map<String, String> service = step.getServices().get(key);
	    createLabelWithCaption(leftSide, rightSide, "Subject:&nbsp;", service.get("SUBJECT"));
	    createLabelWithCaption(leftSide, rightSide, "Subject Parser:&nbsp;", service.get("SUBJECT_PARSER"));
	    
	    createLabelWithCaption(leftSide, rightSide, "OFFline Product ID:&nbsp;", service.get("OFF_PRD_NAME"));
	    
	}
	
	return layout;
    }
    
    private void createLabelWithCaption(VerticalLayout leftSide, VerticalLayout rightSide, String caption, String value)
    {
	leftSide.addComponent(new Label("<b>" + caption + "</b>", ContentMode.HTML));
	if (value == null)
	    rightSide.addComponent(new Label("&nbsp;", ContentMode.HTML));
	else
	    rightSide.addComponent(new Label(value));
    }

}

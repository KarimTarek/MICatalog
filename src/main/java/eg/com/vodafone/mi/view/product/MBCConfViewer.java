package eg.com.vodafone.mi.view.product;

import java.util.Map;

import com.vaadin.v7.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.v7.ui.HorizontalLayout;
import com.vaadin.v7.ui.Label;
import com.vaadin.v7.ui.VerticalLayout;

import eg.com.vodafone.mi.domain.StepConf;

public class MBCConfViewer implements IBackendConfViewer
{

    @Override
    public String getType()
    {
	return "PREPAID";
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
	    createLabelWithCaption(leftSide, rightSide, "Operation:&nbsp;", service.get("OPERATION"));
	    
	    createLabelWithCaption(leftSide, rightSide, "Service ID:&nbsp;", service.get("SERVICE"));
	    createLabelWithCaption(leftSide, rightSide, "Bundle ID:&nbsp;", service.get("BUNDLE_ID"));
	    
	    createLabelWithCaption(leftSide, rightSide, "EXTERNAL_SRV_ID:&nbsp;", service.get("EXTERNAL_SRV_ID"));
	    
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

package eg.com.vodafone.mi.view.product;

import java.util.Map;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

import eg.com.vodafone.mi.domain.StepConf;

public class BillingConfViewer implements IBackendConfViewer
{

    @Override
    public String getType()
    {
	return "BILLING";
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
	    
	    createLabelWithCaption(leftSide, rightSide, "Service:&nbsp;", service.get("SN"));
	    
	    createLabelWithCaption(leftSide, rightSide, "Subject:&nbsp;", service.get("SUBJECT"));
	    createLabelWithCaption(leftSide, rightSide, "Subject Parser:&nbsp;", service.get("SUBJECT_PARSER"));
	    createLabelWithCaption(leftSide, rightSide, "Operation:&nbsp;", service.get("OPERATION"));
	    
	    createLabelWithCaption(leftSide, rightSide, "SN:&nbsp;", service.get("SN"));
	    createLabelWithCaption(leftSide, rightSide, "SP:&nbsp;", service.get("SP"));
	    createLabelWithCaption(leftSide, rightSide, "ID:&nbsp;", service.get("ID"));
	    createLabelWithCaption(leftSide, rightSide, "VALUE:&nbsp;", service.get("VALUE"));
	    createLabelWithCaption(leftSide, rightSide, "SN_DESC:&nbsp;", service.get("SN_DESC"));
	    createLabelWithCaption(leftSide, rightSide, "SP_DESC:&nbsp;", service.get("SP_DESC"));
	    createLabelWithCaption(leftSide, rightSide, "SN_SHORTDESC:&nbsp;", service.get("SN_SHORTDESC"));
	    createLabelWithCaption(leftSide, rightSide, "END_DATE:&nbsp;", service.get("END_DATE"));
	    createLabelWithCaption(leftSide, rightSide, "SERVICE_TYPE:&nbsp;", service.get("SERVICE_TYPE"));
	    
	    createLabelWithCaption(leftSide, rightSide, "EXTERNAL_SRV_ID:&nbsp;", service.get("EXTERNAL_SRV_ID"));
	    
	    createLabelWithCaption(leftSide, rightSide, "<br>", null);
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

package eg.com.vodafone.mi.view.product;

import com.vaadin.ui.Component;

import eg.com.vodafone.mi.domain.StepConf;

public interface IBackendConfViewer
{
    String getType();

   Component createView(StepConf step);
}
package eg.com.vodafone.mi.view.wizard.window;

import eg.com.vodafone.mi.view.wizard.ExecutionPlansStepView;
import eg.com.vodafone.mi.view.wizard.bean.StepBean;

public interface IBackendWindow
{
    String getType();

    void show(StepBean stepBean, ExecutionPlansStepView view);
    
}
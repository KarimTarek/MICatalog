package eg.com.vodafone.mi.view.wizard.bean;

import java.util.Date;

import eg.com.vodafone.mi.domain.StepConf;

@SuppressWarnings("serial")
public class StepBean extends StepConf
{
    private String ID;
    
    public StepBean()
    {
	ID = "ID:" + new Date().getTime();
    }

    public String getID()
    {
        return ID;
    }

    public void setID(String iD)
    {
        ID = iD;
    }

}
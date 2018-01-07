package eg.com.vodafone.mi.view.wizard.bean;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class ExecutionPlanBean implements Serializable
{
    private String ID;
    private String ratePlanTypes;
    private String operation;
    
    public ExecutionPlanBean()
    {
	ID = "ID:" + new Date().getTime();
    }
    
    public String getRatePlanTypes()
    {
        return ratePlanTypes;
    }
    public void setRatePlanTypes(String ratePlanTypes)
    {
        this.ratePlanTypes = ratePlanTypes;
    }
    public String getOperation()
    {
        return operation;
    }
    public void setOperation(String operation)
    {
        this.operation = operation;
    }
 
    public String getID()
    {
	return ID;
    }

    @Override
    public int hashCode()
    {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((ID == null) ? 0 : ID.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj)
    {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	ExecutionPlanBean other = (ExecutionPlanBean) obj;
	if (ID == null)
	{
	    if (other.ID != null)
		return false;
	}
	else if (!ID.equals(other.ID))
	    return false;
	return true;
    }
}
package eg.com.vodafone.mi.domain;

import java.util.List;

public class ExecutionPlan
{
    private String id;
    private String ratePlanTypes;
    private String operation;
    
    private List<StepConf> steps;
    
    public String getId()
    {
        return id;
    }
    public void setId(String id)
    {
        this.id = id;
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
    public List<StepConf> getSteps()
    {
        return steps;
    }
    public void setSteps(List<StepConf> steps)
    {
        this.steps = steps;
    }
}
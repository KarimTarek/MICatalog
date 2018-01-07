package eg.com.vodafone.mi.domain;

import java.util.List;

public class Product
{
    private String prdID;
    private String name;
    private String commercialEnName;
    private String commercialArName;
    private String type;
    private String category;
    private String status;
    
    private List<ExecutionPlan> executionPlans;

    public String getPrdID()
    {
	return prdID;
    }

    public void setPrdID(String prdID)
    {
	this.prdID = prdID;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name)
    {
	this.name = name;
    }

    public String getCommercialEnName()
    {
	return commercialEnName;
    }

    public void setCommercialEnName(String commercialEnName)
    {
	this.commercialEnName = commercialEnName;
    }

    public String getCommercialArName()
    {
	return commercialArName;
    }

    public void setCommercialArName(String commercialArName)
    {
	this.commercialArName = commercialArName;
    }

    public String getType()
    {
	return type;
    }

    public void setType(String type)
    {
	this.type = type;
    }

    public String getCategory()
    {
	return category;
    }

    public void setCategory(String category)
    {
	this.category = category;
    }

    public String getStatus()
    {
	return status;
    }

    public void setStatus(String status)
    {
	this.status = status;
    }

    public List<ExecutionPlan> getExecutionPlans()
    {
        return executionPlans;
    }

    public void setExecutionPlans(List<ExecutionPlan> executionPlans)
    {
        this.executionPlans = executionPlans;
    }
}
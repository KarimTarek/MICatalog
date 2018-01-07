package eg.com.vodafone.mi.domain;

import java.io.Serializable;
import java.util.Map;

@SuppressWarnings("serial")
public class StepConf implements Serializable
{
    private int stepNo;
    private String type;
    private String subject;
    private String parser;
    
    private Map<String, Map<String, String>> services;

    public StepConf()
    {
	this.parser = "defaultParser";
    }
    
    public int getStepNo()
    {
	return stepNo;
    }

    public void setStepNo(int stepNo)
    {
	this.stepNo = stepNo;
    }

    public String getSubject()
    {
	return subject;
    }

    public void setSubject(String subject)
    {
	this.subject = subject;
    }

    public String getParser()
    {
	return parser;
    }

    public void setParser(String parser)
    {
	this.parser = parser;
    }

    public Map<String, Map<String, String>> getServices()
    {
        return services;
    }

    public void setServices(Map<String, Map<String, String>> services)
    {
        this.services = services;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
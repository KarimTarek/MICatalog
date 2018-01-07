package eg.com.vodafone.mi.connection;

import com.vaadin.ui.UI;

import eg.com.vodafone.mi.constants.ISessionConstants;

public enum Environment
{
    PRD("Production"), TEST_35("TEST 35"), TEST_65("TEST 65"), TEST_71("TEST 71"), DEV("DEV");

    private String text;

    private Environment(String text)
    {
	this.text = text;
    }

    public String getText()
    {
	return text;
    }

    public static Environment fromString(String text)
    {
	if (text != null)
	{
	    for (Environment e : Environment.values())
	    {
		if (text.equalsIgnoreCase(e.text))
		{
		    return e;
		}
	    }
	}
	
	throw new IllegalArgumentException("No environment variable found for: " + text);
    }
    
    public static Environment getCurrentEnvironment()
    {
	return (Environment)UI.getCurrent().getSession().getAttribute(ISessionConstants.ENVIRONMENT);
    }
}
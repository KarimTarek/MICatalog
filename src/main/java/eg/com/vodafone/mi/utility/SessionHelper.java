package eg.com.vodafone.mi.utility;

import com.vaadin.ui.UI;

public class SessionHelper
{
    public static Object getSessionAttribute(String key)
    {
	return UI.getCurrent().getSession().getAttribute(key);
    }
    
    public static void setSessionAttribute(String key, Object value)
    {
	UI.getCurrent().getSession().setAttribute(key, value);
    }
}

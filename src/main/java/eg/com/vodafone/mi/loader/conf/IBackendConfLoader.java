package eg.com.vodafone.mi.loader.conf;

import java.util.Map;

public interface IBackendConfLoader
{
    String getType();
    
    Map<String, Map<String, String>> loadConf(String externalID);
}
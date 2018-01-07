package eg.com.vodafone.mi.loader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.vaadin.ui.UI;

import eg.com.vodafone.mi.connection.ConnectionManager;
import eg.com.vodafone.mi.connection.Environment;
import eg.com.vodafone.mi.constants.ISessionConstants;
import eg.com.vodafone.mi.domain.ExecutionPlan;
import eg.com.vodafone.mi.domain.Product;
import eg.com.vodafone.mi.domain.StepConf;
import eg.com.vodafone.mi.loader.conf.BillingConfLoader;
import eg.com.vodafone.mi.loader.conf.IBackendConfLoader;
import eg.com.vodafone.mi.loader.conf.MBCConfLoader;
import eg.com.vodafone.mi.loader.conf.OffPRDConfLoader;
import eg.com.vodafone.mi.loader.conf.PCRFConfLoader;
import eg.com.vodafone.mi.loader.conf.VASSMSConfLoader;

public class ProductsLoader
{
    private static Logger logger = LoggerFactory.getLogger(ProductsLoader.class);

    private static LoadingCache<String, Product> productsCacheEnv48 = CacheBuilder.newBuilder()
	    .expireAfterAccess(48, TimeUnit.HOURS).build(new CacheLoader<String, Product>()
	    {
		@Override
		public Product load(String prdID) throws Exception
		{
		    ProductsLoader loader = new ProductsLoader(Environment.TEST_35);
		    return loader.loadProduct(prdID);
		}
	    });

    private static LoadingCache<String, Product> productsCacheEnv65 = CacheBuilder.newBuilder()
	    .expireAfterAccess(48, TimeUnit.HOURS).build(new CacheLoader<String, Product>()
	    {
		@Override
		public Product load(String prdID) throws Exception
		{
		    ProductsLoader loader = new ProductsLoader(Environment.TEST_65);
		    return loader.loadProduct(prdID);
		}
	    });
    
    private static LoadingCache<String, Product> productsCacheEnv71 = CacheBuilder.newBuilder()
	    .expireAfterAccess(48, TimeUnit.HOURS).build(new CacheLoader<String, Product>()
	    {
		@Override
		public Product load(String prdID) throws Exception
		{
		    ProductsLoader loader = new ProductsLoader(Environment.TEST_71);
		    return loader.loadProduct(prdID);
		}
	    });
    
    private static LoadingCache<String, Product> productsCacheEnvDev = CacheBuilder.newBuilder()
	    .expireAfterAccess(48, TimeUnit.HOURS).build(new CacheLoader<String, Product>()
	    {
		@Override
		public Product load(String prdID) throws Exception
		{
		    ProductsLoader loader = new ProductsLoader(Environment.DEV);
		    return loader.loadProduct(prdID);
		}
	    });
    
    private static LoadingCache<String, Product> productsCacheEnvPRD = CacheBuilder.newBuilder()
	    .expireAfterAccess(48, TimeUnit.HOURS).build(new CacheLoader<String, Product>()
	    {
		@Override
		public Product load(String prdID) throws Exception
		{
		    ProductsLoader loader = new ProductsLoader(Environment.PRD);
		    return loader.loadProduct(prdID);
		}
	    });

    public static Product getProduct(String prdID)
    {
	Environment currentEnv = (Environment) UI.getCurrent().getSession().getAttribute(ISessionConstants.ENVIRONMENT);

	switch (currentEnv)
	{
	case PRD:
	    return productsCacheEnvPRD.getUnchecked(prdID);

	case TEST_35:
	    return productsCacheEnv48.getUnchecked(prdID);

	case TEST_65:
	    return productsCacheEnv65.getUnchecked(prdID);
	    
	case TEST_71:
	    return productsCacheEnv71.getUnchecked(prdID);
	    
	case DEV:
	    return productsCacheEnvDev.getUnchecked(prdID);

	default:
	    return null;
	}
    }

    public static void removeProduct(String prdID)
    {
	Environment currentEnv = (Environment) UI.getCurrent().getSession().getAttribute(ISessionConstants.ENVIRONMENT);

	switch (currentEnv)
	{
	case PRD:
	    productsCacheEnvPRD.invalidate(prdID);
	    break;

	case TEST_35:
	    productsCacheEnv48.invalidate(prdID);
	    break;

	case TEST_65:
	    productsCacheEnv65.invalidate(prdID);
	    
	case TEST_71:
	    productsCacheEnv71.invalidate(prdID);
	    
	case DEV:
	    productsCacheEnvDev.invalidate(prdID);
	    break;

	default:
	    return;
	}
    }

    private JDBCTemplate jdbcTemplate;
    private List<IBackendConfLoader> backendLoaders;

    public ProductsLoader(Environment env)
    {
	jdbcTemplate = new JDBCTemplate(ConnectionManager.getDataSource(env));
	backendLoaders = new ArrayList<IBackendConfLoader>();
	backendLoaders.add(new MBCConfLoader(jdbcTemplate));
	backendLoaders.add(new BillingConfLoader(jdbcTemplate));
	backendLoaders.add(new PCRFConfLoader(jdbcTemplate));
	backendLoaders.add(new VASSMSConfLoader(jdbcTemplate));
	backendLoaders.add(new OffPRDConfLoader(jdbcTemplate));
    }

    public Product loadProduct(String prdID)
    {
	logger.info("Loading the product data for product: {}", prdID);
	Product product = new Product();
	product.setPrdID(prdID);

	// Load the product Info
	loadBasicInfo(product);
	// Load the execution plans
	loadExecutionPlans(product);

	return product;
    }

    private void loadBasicInfo(final Product product)
    {
	this.jdbcTemplate.query(
		"SELECT CATEGORY, NAME, TYPE, ARNAME, ENNAME, STATUS FROM TIBPRD_DEFINATION WHERE PRD_ID = '"
			+ product.getPrdID() + "'", new IResultSetHandler()
		{

		    @Override
		    public void handle(ResultSet resultSet)
		    {
			try
			{
			    if (resultSet.next())
			    {
				product.setCategory(resultSet.getString("CATEGORY"));
				product.setName(resultSet.getString("NAME"));
				product.setCommercialEnName(resultSet.getString("ENNAME"));
				product.setCommercialArName(resultSet.getString("ARNAME"));
				product.setStatus(resultSet.getString("STATUS"));
				product.setType(resultSet.getString("TYPE"));
			    }
			}
			catch (SQLException e)
			{
			    logger.error("Exception: ", e);
			}
		    }
		});
    }

    private void loadExecutionPlans(final Product product)
    {
	this.jdbcTemplate.query(
		"SELECT ID, SEGMENT, PLAN_FRAGMENT FROM TIBPRD_PLANFRAGMENT WHERE PRD_ID = '" + product.getPrdID()
			+ "' AND PRD_STREAM = 'MI' ", new IResultSetHandler()
		{

		    @Override
		    public void handle(ResultSet resultSet)
		    {
			try
			{
			    List<ExecutionPlan> executionPlans = new ArrayList<ExecutionPlan>();

			    while (resultSet.next())
			    {
				ExecutionPlan plan = new ExecutionPlan();
				plan.setId(resultSet.getString("ID"));
				plan.setRatePlanTypes(resultSet.getString("SEGMENT"));
				plan.setOperation(resultSet.getString("PLAN_FRAGMENT"));

				executionPlans.add(plan);

				// Load the execution steps
				loadExecutionSteps(plan);
			    }

			    product.setExecutionPlans(executionPlans);
			}
			catch (SQLException e)
			{
			    logger.error("Exception: ", e);
			}
		    }
		});
    }

    private void loadExecutionSteps(ExecutionPlan plan)
    {
	loadConfSteps(plan);
    }

    private void loadConfSteps(final ExecutionPlan plan)
    {
	this.jdbcTemplate.query("SELECT EXTERNAL_ID, EXTERNAL_TYPE, STEP, WAIT FROM TIBPRD_CONF WHERE PRD_ID = '"
		+ plan.getId() + "' AND PRD_STREAM = 'MI' ", new IResultSetHandler()
	{

	    @Override
	    public void handle(ResultSet resultSet)
	    {
		try
		{
		    TreeMap<Integer, StepConf> stepsMap = new TreeMap<Integer, StepConf>();

		    while (resultSet.next())
		    {
			int stepNo = Integer.parseInt(resultSet.getString("STEP"));

			String exID = resultSet.getString("EXTERNAL_ID");
			if (stepsMap.containsKey(stepNo))
			{
			    StepConf stepConf = stepsMap.get(stepNo);
			    stepConf.getServices().put(exID, new HashMap<String, String>());
			}
			else
			{
			    Map<String, Map<String, String>> services = new HashMap<String, Map<String, String>>();
			    services.put(exID, new HashMap<String, String>());

			    StepConf step = new StepConf();
			    step.setStepNo(stepNo);
			    step.setType(resultSet.getString("EXTERNAL_TYPE"));
			    step.setServices(services);

			    stepsMap.put(stepNo, step);
			}

			queryBackendConf(stepsMap, exID, stepNo);
		    }

		    plan.setSteps(getStepsSorted(stepsMap));
		}
		catch (SQLException e)
		{
		    logger.error("Exception: ", e);
		}
	    }

	    private void queryBackendConf(TreeMap<Integer, StepConf> stepsMap, String exID, int stepNo)
	    {
		StepConf stepConf = stepsMap.get(stepNo);
		String type = stepConf.getType();

		for (IBackendConfLoader loader : backendLoaders)
		{
		    if (type.equals(loader.getType()))
		    {
			stepConf.getServices().putAll(loader.loadConf(exID));
			break;
		    }
		}
	    }

	    private List<StepConf> getStepsSorted(Map<Integer, StepConf> stepsMap)
	    {
		Set<Integer> keySet = stepsMap.keySet();

		List<StepConf> steps = new ArrayList<StepConf>(keySet.size());
		for (Integer key : keySet)
		{
		    steps.add(stepsMap.get(key));
		}
		return steps;
	    }
	});
    }
}
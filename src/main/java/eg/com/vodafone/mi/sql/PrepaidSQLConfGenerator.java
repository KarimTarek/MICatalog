package eg.com.vodafone.mi.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eg.com.vodafone.mi.domain.ExecutionPlan;
import eg.com.vodafone.mi.domain.Product;
import eg.com.vodafone.mi.domain.StepConf;

public class PrepaidSQLConfGenerator extends AbstractSQLGenerator
{

    @Override
    public List<SQLStatement> generate(Product product)
    {
	List<SQLStatement> sqls = new ArrayList<SQLStatement>();

	for (ExecutionPlan executionPlan : product.getExecutionPlans())
	{
	    for (StepConf step : executionPlan.getSteps())
	    {
		if ("prepaid".equalsIgnoreCase(step.getType()))
		{
		    Map<String, String> prepaidConf = step.getServices().get("New");

		    SQLStatement sql = createPrepaidSQL(product, executionPlan, prepaidConf);

		    if (isNewSQL(sql, sqls))
			sqls.add(sql);

		    SQLStatement operationSQL = createOperationSQL(generatePrepaidID(product.getPrdID(), executionPlan, prepaidConf),
			    executionPlan, step, prepaidConf);
		    
		    if (isNewSQL(operationSQL, sqls))
			sqls.add(operationSQL);
		    
		    SQLStatement confSQL = createConfSQL(product, executionPlan, step, prepaidConf);
		    sqls.add(confSQL);
		}
	    }
	}

	return sqls;
    }

    private SQLStatement createConfSQL(Product product, ExecutionPlan executionPlan, StepConf step,
	    Map<String, String> prepaidConf)
    {
	SQLStatement sql = new SQLStatement("TIBPRD_CONF");

	sql.addColumnValue("PRD_ID", generateExecutionPlan(product.getPrdID(), executionPlan));
	sql.addColumnValue("EXTERNAL_ID", prepaidConf.get("OPERATION_SRV_ID"));
	sql.addColumnValue("EXTERNAL_TYPE", "PREPAID");
	sql.addColumnValue("STEP", Integer.toString(step.getStepNo()));
	sql.addColumnValue("WAIT", "YES");
	sql.addColumnValue("PRD_STREAM", "MI");

	return sql;
    }

    private String generateExecutionPlan(String prdID, ExecutionPlan executionPlan)
    {
	StringBuilder builder = new StringBuilder(prdID);
	builder.append("_");
	builder.append(executionPlan.getRatePlanTypes().toUpperCase());
	builder.append("_");
	builder.append(executionPlan.getOperation().toUpperCase());

	return builder.toString();
    }

    private SQLStatement createOperationSQL(String prepaidID, ExecutionPlan executionPlan, StepConf step,
	    Map<String, String> prepaidConf)
    {
	SQLStatement sql = new SQLStatement("TIBPRD_SRV_OPERATION");

	String operationSrvID = getOperationSrvID(prepaidID, prepaidConf);
	prepaidConf.put("OPERATION_SRV_ID", operationSrvID);

	sql.addColumnValue("OPERATION_SRV_ID", operationSrvID);
	sql.addColumnValue("OPERATION", prepaidConf.get("OPERATION"));
	sql.addColumnValue("SUBJECT", step.getSubject());
	sql.addColumnValue("SUBJECT_PARSER", step.getParser());
	sql.addColumnValue("EXTERNAL_SRV_ID", prepaidID);
	sql.addColumnValue("PRD_STREAM", "MI");

	return sql;
    }

    private String getOperationSrvID(String prepaidID, Map<String, String> prepaidConf)
    {
	StringBuilder builder = new StringBuilder("PREPAID_");
	builder.append(prepaidConf.get("OPERATION"));
	builder.append("_");
	builder.append(prepaidID);

	return builder.toString();
    }

    private SQLStatement createPrepaidSQL(Product product, ExecutionPlan executionPlan, Map<String, String> prepaidConf)
    {
	SQLStatement sql = new SQLStatement("TIBSERV_PREPAID");
	String prepaidID = generatePrepaidID(product.getPrdID(), executionPlan, prepaidConf);
	prepaidConf.put("PREPAID_SERV_ID", prepaidID);

	sql.addColumnValue("PREPAID_SERV_ID", prepaidID);
	sql.addColumnValue("SERVICE", prepaidConf.get("SERVICE"));
	sql.addColumnValue("BUNDLE_ID", prepaidConf.get("BUNDLE_ID"));
	sql.addColumnValue("PRD_STREAM", "MI");
	return sql;
    }

    private String generatePrepaidID(String prdID, ExecutionPlan executionPlan, Map<String, String> prepaidConf)
    {
	StringBuilder builder = new StringBuilder(executionPlan.getRatePlanTypes().toUpperCase());
	builder.append("_MBC_");
	builder.append(prdID);

	return builder.toString();
    }
}
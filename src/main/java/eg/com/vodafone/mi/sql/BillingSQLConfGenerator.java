package eg.com.vodafone.mi.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eg.com.vodafone.mi.domain.ExecutionPlan;
import eg.com.vodafone.mi.domain.Product;
import eg.com.vodafone.mi.domain.StepConf;

public class BillingSQLConfGenerator extends AbstractSQLGenerator
{

    @Override
    public List<SQLStatement> generate(Product product)
    {
	List<SQLStatement> sqls = new ArrayList<SQLStatement>();

	for (ExecutionPlan executionPlan : product.getExecutionPlans())
	{
	    for (StepConf step : executionPlan.getSteps())
	    {
		if ("billing".equalsIgnoreCase(step.getType()))
		{
		    
		    Set<String> keys = step.getServices().keySet();
		    
		    for (String key : keys)
		    {
			Map<String, String> billingConf = step.getServices().get(key);
			
			SQLStatement sql = createBillingSQL(product, executionPlan, billingConf);
			
			if (isNewSQL(sql, sqls))
			    sqls.add(sql);
			
			SQLStatement operationSQL = createOperationSQL(generateBillingID(product.getPrdID(), executionPlan, billingConf),
				executionPlan, step, billingConf);
			
			if (isNewSQL(operationSQL, sqls))
			    sqls.add(operationSQL);
			
			SQLStatement confSQL = createConfSQL(product, executionPlan, step, billingConf);
			sqls.add(confSQL);
		    }
		}
	    }
	}

	return sqls;
    }

    private SQLStatement createConfSQL(Product product, ExecutionPlan executionPlan, StepConf step,
	    Map<String, String> billingConf)
    {
	SQLStatement sql = new SQLStatement("TIBPRD_CONF");

	sql.addColumnValue("PRD_ID", generateExecutionPlan(product.getPrdID(), executionPlan));
	sql.addColumnValue("EXTERNAL_ID", billingConf.get("OPERATION_SRV_ID"));
	sql.addColumnValue("EXTERNAL_TYPE", "BILLING");
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

    private SQLStatement createOperationSQL(String billingID, ExecutionPlan executionPlan, StepConf step,
	    Map<String, String> billingConf)
    {
	SQLStatement sql = new SQLStatement("TIBPRD_SRV_OPERATION");

	String operationSrvID = getOperationSrvID(billingID, billingConf);
	billingConf.put("OPERATION_SRV_ID", operationSrvID);

	sql.addColumnValue("OPERATION_SRV_ID", operationSrvID);
	sql.addColumnValue("OPERATION", billingConf.get("OPERATION"));
	sql.addColumnValue("SUBJECT", step.getSubject());
	sql.addColumnValue("SUBJECT_PARSER", step.getParser());
	sql.addColumnValue("EXTERNAL_SRV_ID", billingID);
	sql.addColumnValue("PRD_STREAM", "MI");

	return sql;
    }

    private String getOperationSrvID(String prepaidID, Map<String, String> prepaidConf)
    {
	StringBuilder builder = new StringBuilder("BILL_");
	builder.append(prepaidConf.get("OPERATION"));
	builder.append("_");
	builder.append(prepaidID);

	return builder.toString();
    }

    private SQLStatement createBillingSQL(Product product, ExecutionPlan executionPlan, Map<String, String> billingConf)
    {
	SQLStatement sql = new SQLStatement("TIBSRV_BASIC_BILLING");
	String billingID = generateBillingID(product.getPrdID(), executionPlan, billingConf);
	billingConf.put("BILLING_SRV_ID", billingID);

	sql.addColumnValue("BILLING_SRV_ID", billingID);
	sql.addColumnValue("SN", billingConf.get("SN"));
	sql.addColumnValue("SP", billingConf.get("SP"));
	sql.addColumnValue("ID", billingConf.get("ID"));
	sql.addColumnValue("VALUE", billingConf.get("VALUE"));
	sql.addColumnValue("END_DATE", billingConf.get("ENDDATE"));
	sql.addColumnValue("SERVICE_TYPE", billingConf.get("SERVICE_TYPE"));
	sql.addColumnValue("SN_SHORTDESC", billingConf.get("SN_SHORTDESC"));
	sql.addColumnValue("PRD_STREAM", "MI");
	
	return sql;
    }

    private String generateBillingID(String prdID, ExecutionPlan executionPlan, Map<String, String> billingConf)
    {
	StringBuilder builder = new StringBuilder("BSRV_");
	builder.append(billingConf.get("SN_SHORTDESC"));
	return builder.toString();
    }
}
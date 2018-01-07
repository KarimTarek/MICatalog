package eg.com.vodafone.mi.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import eg.com.vodafone.mi.domain.ExecutionPlan;
import eg.com.vodafone.mi.domain.Product;
import eg.com.vodafone.mi.domain.StepConf;

public class PCRFSQLConfGenerator extends AbstractSQLGenerator
{

    @Override
    public List<SQLStatement> generate(Product product)
    {
	List<SQLStatement> sqls = new ArrayList<SQLStatement>();

	for (ExecutionPlan executionPlan : product.getExecutionPlans())
	{
	    for (StepConf step : executionPlan.getSteps())
	    {
		if ("PCRF".equalsIgnoreCase(step.getType()))
		{
		    Map<String, String> prepaidConf = step.getServices().get("New");

		    SQLStatement sql = createPCRFSQL(product, executionPlan, prepaidConf);

		    if (isNewSQL(sql, sqls))
			sqls.add(sql);

		    SQLStatement operationSQL = createOperationSQL(generatePCRFID(product.getPrdID(), executionPlan, prepaidConf),
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
	sql.addColumnValue("EXTERNAL_TYPE", "PCRF");
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

    private String getOperationSrvID(String pcrfID, Map<String, String> prepaidConf)
    {
	StringBuilder builder = new StringBuilder("PCRF_");
	builder.append(prepaidConf.get("OPERATION"));
	builder.append("_");
	builder.append(pcrfID.replace("PCRF_", ""));

	return builder.toString();
    }

    private SQLStatement createPCRFSQL(Product product, ExecutionPlan executionPlan, Map<String, String> pcrfConf)
    {
	SQLStatement sql = new SQLStatement("TIBSRV_PCRF");
	String prepaidID = generatePCRFID(product.getPrdID(), executionPlan, pcrfConf);
	pcrfConf.put("PCRF_SRV_ID", prepaidID);

	sql.addColumnValue("PCRF_SRV_ID", prepaidID);
	sql.addColumnValue("SERVICE", pcrfConf.get("SERVICE"));
	sql.addColumnValue("SERVICEPACKAGE", pcrfConf.get("SERVICEPACKAGE"));
	sql.addColumnValue("QUOTANAME", pcrfConf.get("QUOTANAME"));
	sql.addColumnValue("PRD_STREAM", "MI");
	return sql;
    }

    private String generatePCRFID(String prdID, ExecutionPlan executionPlan, Map<String, String> pcrfConf)
    {
	StringBuilder builder = new StringBuilder("PCRF_");
	
	if (pcrfConf.get("SERVICE") != null)
	    builder.append(pcrfConf.get("SERVICE"));
	else if (pcrfConf.get("SERVICEPACKAGE") != null)
	    builder.append(pcrfConf.get("SERVICEPACKAGE"));
	else if (pcrfConf.get("QUOTANAME") != null)
	    builder.append(pcrfConf.get("QUOTANAME"));

	return builder.toString();
    }
}
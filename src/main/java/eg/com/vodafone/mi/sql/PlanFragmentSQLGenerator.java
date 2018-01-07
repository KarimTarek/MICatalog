package eg.com.vodafone.mi.sql;

import java.util.ArrayList;
import java.util.List;

import eg.com.vodafone.mi.domain.ExecutionPlan;
import eg.com.vodafone.mi.domain.Product;

public class PlanFragmentSQLGenerator implements ISQLConfGenerator
{

    @Override
    public List<SQLStatement> generate(Product product)
    {
	List<SQLStatement> sqls = new ArrayList<SQLStatement>();
	
	for (ExecutionPlan executionPlan : product.getExecutionPlans())
	{
	    SQLStatement sql = new SQLStatement("TIBPRD_PLANFRAGMENT");
	    sql.addColumnValue("ID", generateExecutionPlan(product.getPrdID(), executionPlan));
	    sql.addColumnValue("PRD_ID", product.getPrdID());
	    sql.addColumnValue("SEGMENT", executionPlan.getRatePlanTypes().toUpperCase().toUpperCase());
	    sql.addColumnValue("PLAN_FRAGMENT", executionPlan.getOperation().toUpperCase());
	    sql.addColumnValue("PRD_STREAM",  "MI");

	    sqls.add(sql);
	}
	
	return sqls;
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

}
package eg.com.vodafone.mi.sql;

import java.util.ArrayList;
import java.util.List;

import eg.com.vodafone.mi.domain.Product;

public class BasicInfoSQLConfGenerator implements ISQLConfGenerator
{

    @Override
    public List<SQLStatement> generate(Product product)
    {
	SQLStatement sql = new SQLStatement("TIBPRD_DEFINATION");
	sql.addColumnValue("PRD_ID", product.getPrdID());
	sql.addColumnValue("NAME", product.getName());
	sql.addColumnValue("DESCRIPTION", product.getName());
	sql.addColumnValue("TYPE", product.getType());
	sql.addColumnValue("PRD_STREAM", "MI");
	sql.addColumnValue("Category", product.getCategory());
	sql.addColumnValue("Status", product.getStatus());
	
	List<SQLStatement> sqls = new ArrayList<SQLStatement>();
	sqls.add(sql);
	
	return sqls;
    }

}
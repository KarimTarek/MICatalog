package eg.com.vodafone.mi.sql;

import java.util.ArrayList;
import java.util.List;

import eg.com.vodafone.mi.domain.Product;

public class SQLProductGenerator
{
    private Product product;
    
    private List<ISQLConfGenerator> generators;
    
    public SQLProductGenerator(Product product)
    {
	this.product = product;
	
	generators = new ArrayList<ISQLConfGenerator>();
	generators.add(new BasicInfoSQLConfGenerator());
	generators.add(new PlanFragmentSQLGenerator());
	generators.add(new PrepaidSQLConfGenerator());
	generators.add(new BillingSQLConfGenerator());
	generators.add(new PCRFSQLConfGenerator());
    }
    
    public List<SQLStatement> generate()
    {
	List<SQLStatement> list = new ArrayList<SQLStatement>();
	
	for (ISQLConfGenerator generator : generators)
	{
	    list.addAll(generator.generate(product));
	}
	
	return list;
    }
}
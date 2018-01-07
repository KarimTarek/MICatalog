package eg.com.vodafone.mi.sql;

import java.util.List;

public abstract class AbstractSQLGenerator implements ISQLConfGenerator
{

    public AbstractSQLGenerator()
    {
	super();
    }

    protected boolean isNewSQL(SQLStatement sql, List<SQLStatement> sqls)
    {
        for (SQLStatement sqlStatement : sqls)
        {
            if (sql.equals(sqlStatement))
        	return false;
            else
        	return true;
        }
    
        return true;
    }

}
package eg.com.vodafone.mi.sql;

import java.util.List;

import eg.com.vodafone.mi.domain.Product;

public interface ISQLConfGenerator
{
    List<SQLStatement> generate(Product product);
}
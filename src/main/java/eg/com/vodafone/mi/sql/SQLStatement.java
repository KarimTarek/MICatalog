package eg.com.vodafone.mi.sql;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Strings;

public class SQLStatement {
	private String table;
	private List<String> cols;
	private List<String> values;

	public SQLStatement(String table) {
		this.table = table;

		cols = new ArrayList<String>();
		values = new ArrayList<String>();
	}

	public void addColumnValue(String column, String value) {
		cols.add(column);
		values.add(value);
	}

	public String generateSQLStatement() {
		StringBuilder builder = new StringBuilder();
		String RegularExp = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
		builder.append("INSERT INTO ");
		builder.append(table);
		builder.append(" (");

		for (String col : cols) {
			builder.append(col);
			builder.append(", ");
		}

		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);

		builder.append(") VALUES (");
		for (String value : values) {
			if (Strings.isNullOrEmpty(value) || "null".equals(value.toLowerCase())) {
				builder.append("NULL, ");
			} else {
				
				boolean DateMatch = value.trim().matches(RegularExp);
				if (DateMatch) {
					String DateWithoutZeros = value.trim().replace(" 00:00:00","");
					String[] DateElements = DateWithoutZeros.split("-");
					String FinalDateValue = "to_date('"+ DateElements[0] +'-'+ DateElements[1] +'-'+ DateElements[2]+" 00:00:00','YYYY-MM-DD HH24:MI:SS')";
					builder.append(FinalDateValue);
					builder.append(", ");
				} else {
					builder.append("'");
					builder.append(value.trim());
					builder.append("', ");
				}
				
			}
		}

		builder.deleteCharAt(builder.length() - 1);
		builder.deleteCharAt(builder.length() - 1);

		builder.append(");");

		return builder.toString();
	}

	public List<String> getCols() {
		return cols;
	}

	public List<String> getValues() {
		return values;
	}

	public String getTable() {
		return table;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cols == null) ? 0 : cols.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SQLStatement other = (SQLStatement) obj;
		if (cols == null) {
			if (other.cols != null)
				return false;
		} else if (!((cols.size() == other.cols.size()) && cols.containsAll(other.cols)))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!((values.size() == other.values.size()) && values.containsAll(other.values)))
			return false;
		return true;
	}
}
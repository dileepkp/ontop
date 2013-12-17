package it.unibz.krdb.sql;

public class Db2ConstraintTest extends AbstractConstraintTest {

	public Db2ConstraintTest(String method) {
		super(method);
	}

	@Override
	protected String getConnectionPassword() {
		return "fish";
	}

	@Override
	protected String getConnectionString() {
		return "jdbc:db2://10.7.20.39:50001/dbconst";
	}

	@Override
	protected String getConnectionUsername() {
		return "db2inst2";
	}

	@Override
	protected String getDriverName() {
		return "com.ibm.db2.jcc.DB2Driver";
	}
}

package it.unibz.krdb.sql;

/*
 * #%L
 * ontop-obdalib-core
 * %%
 * Copyright (C) 2009 - 2014 Free University of Bozen-Bolzano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.Serializable;

import java.util.*;
import java.util.regex.Pattern;

public class DBMetadata implements Serializable {

	private static final long serialVersionUID = -806363154890865756L;

	private final Map<String, RelationDefinition> schema = new HashMap<>();

	private final String driverName;
	private final String driverVersion;
	private final String databaseProductName;

	private static final Pattern pQuotes = Pattern.compile("[\"`\\[][^\\.]*[\"`\\]]");

	/**
	 * Constructs an initial metadata with some general information about the
	 * database, e.g., the driver name, the database engine name.
	 * 
	 */

	public DBMetadata(String driverName, String driverVersion, String databaseProductName) {
		this.driverName = driverName;
		this.driverVersion = driverVersion;
		this.databaseProductName = databaseProductName;
	}

	/**
	 * Inserts a new data definition to this meta data object. The name is
	 * inserted without quotes so it can be used for the mapping, while the
	 * value that is used also for the generated SQL conserves the quotes
	 * 
	 * @param value
	 *            The data definition. It can be a {@link TableDefinition} or a
	 *            {@link ViewDefinition} object.
	 */
	public void add(RelationDefinition value) {
		String name = value.getName();
		// name without quotes
		if (pQuotes.matcher(name).matches())
			schema.put(name.substring(1, name.length() - 1), value);

		else {
			String[] names = name.split("\\."); // consider the case of
												// schema.table
			if (names.length == 2) {
				String schemaName = names[0];
				String tableName = names[1];
				if (pQuotes.matcher(schemaName).matches())
					schemaName = schemaName.substring(1,
							schemaName.length() - 1);
				if (pQuotes.matcher(tableName).matches())
					tableName = tableName.substring(1, tableName.length() - 1);
				schema.put(schemaName + "." + tableName, value);
			} else
				schema.put(name, value);
		}

	}

	/**
	 * Retrieves the data definition object based on its name. The
	 * <name>name</name> can be either a table name or a view name.
	 * 
	 * @param name
	 *            The string name.
	 */
	public RelationDefinition getDefinition(String name) {
		RelationDefinition def = schema.get(name);
		if (def == null)
			def = schema.get(name.toLowerCase());
		if (def == null)
			def = schema.get(name.toUpperCase());
		return def;
	}

	/**
	 * Retrieves the relation list (table and view definition) form the
	 * metadata.
	 */
	public Collection<RelationDefinition> getRelations() {
		return Collections.unmodifiableCollection(schema.values());
	}

	/**
	 * Retrieves the table list form the metadata.
	 */
	public Collection<TableDefinition> getTables() {
		List<TableDefinition> tableList = new ArrayList<>();
		for (RelationDefinition dd : schema.values()) {
			if (dd instanceof TableDefinition) 
				tableList.add((TableDefinition) dd);
		}
		return tableList;
	}



	public String getDriverName() {
		return driverName;
	}

	public String getDriverVersion() {
		return driverVersion;
	}

	public String getDatabaseProductName() {
		return databaseProductName;
	}

	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		for (String key : schema.keySet()) {
			bf.append(key);
			bf.append("=");
			bf.append(schema.get(key).toString());
			bf.append("\n");
		}
		return bf.toString();
	}
}

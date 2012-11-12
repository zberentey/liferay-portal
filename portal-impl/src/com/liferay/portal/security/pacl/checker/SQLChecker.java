/**
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.security.pacl.checker;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.parsers.sql.parser.SqlParser;
import com.liferay.portal.parsers.sql.statement.AlterTable;
import com.liferay.portal.parsers.sql.statement.CreateIndex;

import java.io.StringReader;

import java.security.Permission;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.test.tablesfinder.TablesNamesFinder;

/**
 * @author Brian Wing Shun Chan
 * @author Zsolt Berentey
 */
public class SQLChecker extends BaseChecker {

	public void afterPropertiesSet() {
		initTableNames();
	}

	public void checkPermission(Permission permission) {
		throw new UnsupportedOperationException();
	}

	public boolean hasSQL(String sql) {
		Statement statement = null;

		try {
			SqlParser parser = new SqlParser(new StringReader(sql));

			statement = parser.Parse();
		}
		catch (Exception e) {
			_log.error("Unable to parse SQL " + sql);

			return false;
		}

		if (statement instanceof AlterTable) {
			AlterTable alterTable = (AlterTable)statement;

			return hasSQL(alterTable);
		}
		else if (statement instanceof CreateIndex) {
			CreateIndex createIndex = (CreateIndex)statement;

			return hasSQL(createIndex);
		}
		else if (statement instanceof CreateTable) {
			CreateTable createTable = (CreateTable)statement;

			return hasSQL(createTable);
		}
		else if (statement instanceof Select) {
			Select select = (Select)statement;

			return hasSQL(select);
		}
		else if (statement instanceof Delete) {
			Delete delete = (Delete)statement;

			return hasSQL(delete);
		}
		else if (statement instanceof Drop) {
			Drop drop = (Drop)statement;

			return hasSQL(drop);
		}
		else if (statement instanceof Insert) {
			Insert insert = (Insert)statement;

			return hasSQL(insert);
		}
		else if (statement instanceof Replace) {
			Replace replace = (Replace)statement;

			return hasSQL(replace);
		}
		else if (statement instanceof Select) {
			Select select = (Select)statement;

			return hasSQL(select);
		}
		else if (statement instanceof Truncate) {
			Truncate truncate = (Truncate)statement;

			return hasSQL(truncate);
		}
		else if (statement instanceof Update) {
			Update update = (Update)statement;

			return hasSQL(update);
		}

		return false;
	}

	protected Set<TableNameWrapper> getWrappedPropertySet(String key) {
		Set<TableNameWrapper> propertySet = new HashSet<TableNameWrapper>();

		for (String property : getPropertyArray(key)) {
			propertySet.add(new TableNameWrapper(property));
		}

		return propertySet;
	}

	protected boolean hasSQL(AlterTable alterTable) {
		return isAllowedTable(alterTable.getTableName(), _alterTableNames);
	}

	protected boolean hasSQL(CreateIndex createIndex) {
		return isAllowedTable(createIndex.getTableName(), _createTableNames);
	}

	protected boolean hasSQL(CreateTable createTable) {
		return isAllowedTable(createTable.getTable(), _createTableNames);
	}

	protected boolean hasSQL(Delete delete) {
		TableNamesFinder tableNamesFinder = new TableNamesFinder();

		List<String> tableNames = tableNamesFinder.getTableNames(delete);

		return isAllowedTables(tableNames, _deleteTableNames);
	}

	protected boolean hasSQL(Drop drop) {
		return isAllowedTable(drop.getName(), _dropTableNames);
	}

	protected boolean hasSQL(Insert insert) {
		TableNamesFinder tableNamesFinder = new TableNamesFinder();

		List<String> tableNames = tableNamesFinder.getTableNames(insert);

		return isAllowedTables(tableNames, _insertTableNames);
	}

	protected boolean hasSQL(Replace replace) {
		TableNamesFinder tableNamesFinder = new TableNamesFinder();

		List<String> tableNames = tableNamesFinder.getTableNames(replace);

		return isAllowedTables(tableNames, _replaceTableNames);
	}

	protected boolean hasSQL(Select select) {
		TableNamesFinder tableNamesFinder = new TableNamesFinder();

		List<String> tableNames = tableNamesFinder.getTableNames(select);

		return isAllowedTables(tableNames, _selectTableNames);
	}

	protected boolean hasSQL(Truncate truncate) {
		return isAllowedTable(truncate.getTable(), _truncateTableNames);
	}

	protected boolean hasSQL(Update update) {
		TableNamesFinder tableNamesFinder = new TableNamesFinder();

		List<String> tableNames = tableNamesFinder.getTableNames(update);

		return isAllowedTables(tableNames, _updateTableNames);
	}

	protected void initTableNames() {
		_allTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-all");
		_alterTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-alter");
		_createTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-create");
		_deleteTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-delete");
		_dropTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-drop");
		_insertTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-insert");
		_replaceTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-replace");
		_selectTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-select");
		_truncateTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-truncate");
		_updateTableNames = getWrappedPropertySet(
			"security-manager-sql-tables-update");
	}

	protected boolean isAllowedTable(
		String tableName, Set<TableNameWrapper> allowedTableNames) {

		for (TableNameWrapper wrapper : _allTableNames) {
			if (wrapper.equals(tableName)) {
				return true;
			}
		}

		for (TableNameWrapper wrapper : allowedTableNames) {
			if (wrapper.equals(tableName)) {
				return true;
			}
		}

		return false;
	}

	protected boolean isAllowedTable(
		Table table, Set<TableNameWrapper> allowedTableNames) {

		String tableName = table.getName();

		return isAllowedTable(tableName, allowedTableNames);
	}

	protected boolean isAllowedTables(
		List<String> tableNames, Set<TableNameWrapper> allowedTableNames) {

		for (String tableName : tableNames) {
			if (!isAllowedTable(tableName, allowedTableNames)) {
				return false;
			}
		}

		return true;
	}

	private static Log _log = LogFactoryUtil.getLog(SQLChecker.class);

	private Set<TableNameWrapper> _allTableNames;
	private Set<TableNameWrapper> _alterTableNames;
	private Set<TableNameWrapper> _createTableNames;
	private Set<TableNameWrapper> _deleteTableNames;
	private Set<TableNameWrapper> _dropTableNames;
	private Set<TableNameWrapper> _insertTableNames;
	private Set<TableNameWrapper> _replaceTableNames;
	private Set<TableNameWrapper> _selectTableNames;
	private Set<TableNameWrapper> _truncateTableNames;
	private Set<TableNameWrapper> _updateTableNames;

	@SuppressWarnings("unchecked")
	private class TableNamesFinder extends TablesNamesFinder {

		public TableNamesFinder() {
			tables = new ArrayList<String>();
		}

		public List<String> getTableNames(Delete delete) {
			Table table = delete.getTable();

			tables.add(table.getName());

			Expression where = delete.getWhere();

			where.accept(this);

			return tables;
		}

		public List<String> getTableNames(Insert insert) {
			Table table = insert.getTable();

			tables.add(table.getName());

			ItemsList itemsList = insert.getItemsList();

			itemsList.accept(this);

			return tables;
		}

		public List<String> getTableNames(Replace replace) {
			Table table = replace.getTable();

			tables.add(table.getName());

			ItemsList itemsList = replace.getItemsList();

			itemsList.accept(this);

			return tables;
		}

		public List<String> getTableNames(Select select) {
			SelectBody selectBody = select.getSelectBody();

			selectBody.accept(this);

			return tables;
		}

		public List<String> getTableNames(Update update) {
			Table table = update.getTable();

			tables.add(table.getName());

			Expression where = update.getWhere();

			where.accept(this);

			return tables;
		}

	}

	private class TableNameWrapper {

		public TableNameWrapper(String tableName) {
			if (tableName.contains(StringPool.STAR)) {
				_wildCardCheck = true;

				try {
					_tableNamePattern = Pattern.compile(
						tableName.replaceAll("\\*", ".*?"));
				}
				catch (PatternSyntaxException pse) {
					_wildCardCheck = false;
				}
			}

			_tableName = tableName;
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof String)) {
				return false;
			}

			String tableName = (String)obj;

			if (_wildCardCheck) {
				Matcher m = _tableNamePattern.matcher(tableName);

				return m.matches();
			}

			return _tableName.equals(tableName);
		}

		private String _tableName = null;
		private Pattern _tableNamePattern = null;
		private boolean _wildCardCheck = false;

	}

}
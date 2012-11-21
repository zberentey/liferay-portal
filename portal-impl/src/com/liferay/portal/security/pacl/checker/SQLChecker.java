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

import com.liferay.apache.derby.iapi.error.StandardException;
import com.liferay.apache.derby.iapi.services.context.ContextManager;
import com.liferay.apache.derby.iapi.services.context.ContextService;
import com.liferay.apache.derby.iapi.sql.compile.CompilerContext;
import com.liferay.apache.derby.iapi.sql.compile.Parser;
import com.liferay.apache.derby.iapi.sql.compile.Visitable;
import com.liferay.apache.derby.iapi.sql.compile.Visitor;
import com.liferay.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import com.liferay.apache.derby.impl.jdbc.EmbedConnection;
import com.liferay.apache.derby.impl.sql.compile.AlterTableNode;
import com.liferay.apache.derby.impl.sql.compile.CompilerContextImpl;
import com.liferay.apache.derby.impl.sql.compile.CreateAliasNode;
import com.liferay.apache.derby.impl.sql.compile.CreateIndexNode;
import com.liferay.apache.derby.impl.sql.compile.CreateTableNode;
import com.liferay.apache.derby.impl.sql.compile.CreateTriggerNode;
import com.liferay.apache.derby.impl.sql.compile.CreateViewNode;
import com.liferay.apache.derby.impl.sql.compile.CursorNode;
import com.liferay.apache.derby.impl.sql.compile.DeleteNode;
import com.liferay.apache.derby.impl.sql.compile.DropAliasNode;
import com.liferay.apache.derby.impl.sql.compile.DropIndexNode;
import com.liferay.apache.derby.impl.sql.compile.DropTableNode;
import com.liferay.apache.derby.impl.sql.compile.DropTriggerNode;
import com.liferay.apache.derby.impl.sql.compile.DropViewNode;
import com.liferay.apache.derby.impl.sql.compile.FromBaseTable;
import com.liferay.apache.derby.impl.sql.compile.FromSubquery;
import com.liferay.apache.derby.impl.sql.compile.GrantNode;
import com.liferay.apache.derby.impl.sql.compile.GrantRoleNode;
import com.liferay.apache.derby.impl.sql.compile.InsertNode;
import com.liferay.apache.derby.impl.sql.compile.ParserImpl;
import com.liferay.apache.derby.impl.sql.compile.SubqueryNode;
import com.liferay.apache.derby.impl.sql.compile.TableName;
import com.liferay.apache.derby.impl.sql.compile.TypeCompilerFactoryImpl;
import com.liferay.apache.derby.impl.sql.compile.UpdateNode;
import com.liferay.apache.derby.jdbc.EmbeddedDriver;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;

import java.security.Permission;

import java.sql.DriverManager;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Brian Wing Shun Chan
 * @author Zsolt Berentey
 */
public class SQLChecker extends BaseChecker {

	public void afterPropertiesSet() {
		initParser();
		initTableNames();
	}

	public void checkPermission(Permission permission) {
		throw new UnsupportedOperationException();
	}

	public boolean hasSQL(String sql) {
		Visitable visitable = null;

		try {
			visitable = _parser.parseStatement(sql);
		}
		catch (Exception e) {
			_log.error("Unable to parse SQL " + sql);

			return false;
		}

		StatementVisitor statementVisitor = new StatementVisitor(this);

		try {
			statementVisitor.visit(visitable);
		}
		catch (StandardException se) {
			_log.error("Unable to determine access for SQL " + sql);

			return false;
		}

		return statementVisitor.isAllowed();
	}

	public boolean isAllowed(int checkPermission, String tableName) {
		if ((_allTableNameHandlers == null) || (_tableNameHandlers == null)) {
			_log.error("SQL checker is not correctly initialized");

			return false;
		}

		for (TableNameHandler tableNameHandler : _allTableNameHandlers) {
			if (tableNameHandler.matches(tableName)) {
				return true;
			}
		}

		Set<TableNameHandler> tableNameHandlers =
			_tableNameHandlers[checkPermission];

		if (tableNameHandlers != null) {
			for (TableNameHandler tableNameHandler : tableNameHandlers) {
				if (tableNameHandler.matches(tableName)) {
					return true;
				}
			}
		}

		return false;
	}

	protected Set<TableNameHandler> getTableNameHandlers(String key) {
		Set<TableNameHandler> tableNameHandlers =
			new HashSet<TableNameHandler>();

		for (String tableName : getPropertyArray(key)) {
			TableNameHandler tableNameHandler = new TableNameHandler(tableName);

			tableNameHandlers.add(tableNameHandler);
		}

		return tableNameHandlers;
	}

	protected void initParser() {
		if (_parser != null) {
			return;
		}

		try {
			new EmbeddedDriver();

			EmbedConnection embedConnection =
				(EmbedConnection)DriverManager.getConnection(
					"jdbc:derby:memory:dummy;create=true");

			ContextManager contextManager = embedConnection.getContextManager();

			LanguageConnectionContext languageConnectionContext =
				(LanguageConnectionContext)contextManager.getContext(
					"LanguageConnectionContext");

			ContextService contextService = ContextService.getFactory();

			contextService.setCurrentContextManager(contextManager);

			CompilerContext compilerContext = new CompilerContextImpl(
				contextManager, languageConnectionContext,
				new TypeCompilerFactoryImpl());

			_parser = new ParserImpl(compilerContext);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@SuppressWarnings("unchecked")
	protected void initTableNames() {
		_tableNameHandlers = new Set[9];

		_allTableNameHandlers = getTableNameHandlers(
			"security-manager-sql-tables-all");
		_tableNameHandlers[StatementVisitor.ALTER] = getTableNameHandlers(
			"security-manager-sql-tables-alter");
		_tableNameHandlers[StatementVisitor.CREATE] = getTableNameHandlers(
			"security-manager-sql-tables-create");
		_tableNameHandlers[StatementVisitor.DELETE] = getTableNameHandlers(
			"security-manager-sql-tables-delete");
		_tableNameHandlers[StatementVisitor.DROP] = getTableNameHandlers(
			"security-manager-sql-tables-drop");
		_tableNameHandlers[StatementVisitor.INSERT] = getTableNameHandlers(
			"security-manager-sql-tables-insert");
		_tableNameHandlers[StatementVisitor.SELECT] = getTableNameHandlers(
			"security-manager-sql-tables-select");
		_tableNameHandlers[StatementVisitor.TRUNCATE] = getTableNameHandlers(
			"security-manager-sql-tables-truncate");
		_tableNameHandlers[StatementVisitor.UPDATE] = getTableNameHandlers(
			"security-manager-sql-tables-update");
	}

	private static Log _log = LogFactoryUtil.getLog(SQLChecker.class);

	private static Parser _parser;

	private Set<TableNameHandler> _allTableNameHandlers;
	private Set<TableNameHandler>[] _tableNameHandlers;

	private class StatementVisitor implements Visitor {

		public static final int ALTER = 0;

		public static final int CREATE = 1;

		public static final int DELETE = 2;

		public static final int DROP = 3;

		public static final int GRANT = 4;

		public static final int INSERT = 5;

		public static final int NONE = -1;

		public static final int SELECT = 6;

		public static final int TRUNCATE = 7;

		public static final int UPDATE = 8;

		public StatementVisitor(SQLChecker sqlChecker) {
			_sqlChecker = sqlChecker;
		}

		public boolean isAllowed() {
			if (_allowed == null) {
				return false;
			}

			return _allowed;
		}

		public boolean skipChildren(Visitable visitable) {
			return false;
		}

		public boolean stopTraversal() {
			return false;
		}

		public Visitable visit(Visitable visitable) throws StandardException {
			if (_visitables.contains(visitable)) {
				return visitable;
			}

			_visitables.add(visitable);

			boolean changeState = changeState(visitable);

			String tableName = getTableName(visitable);

			if (tableName != null) {
				if (_states.isEmpty()) {
					_allowed = false;
				}
				else {
					boolean allowed = _sqlChecker.isAllowed(
						_states.peek(), tableName);

					if (!allowed || (allowed && (_allowed == null))) {
						_allowed = allowed;
					}
				}

				if ((_allowed != null) && !_allowed) {
					return visitable;
				}
			}

			visitable.accept(this);

			if (changeState) {
				_states.pop();
			}

			return visitable;
		}

		public boolean visitChildrenFirst(Visitable visitable) {
			return false;
		}

		protected boolean changeState(Visitable visitable) {
			int state = NONE;

			if (visitable instanceof AlterTableNode) {
				AlterTableNode alterTableNode = (AlterTableNode)visitable;

				String alterTableNodeString =
					alterTableNode.statementToString();

				if (alterTableNodeString.startsWith("TRUNCATE")) {
					state = TRUNCATE;
				}
				else {
					state = ALTER;
				}
			}
			else if ((visitable instanceof CreateAliasNode) ||
					 (visitable instanceof CreateIndexNode) ||
					 (visitable instanceof CreateTableNode) ||
					 (visitable instanceof CreateTriggerNode) ||
					 (visitable instanceof CreateViewNode)) {

				state = CREATE;
			}
			else if (visitable instanceof DeleteNode) {
				state = DELETE;
			}
			else if ((visitable instanceof DropAliasNode) ||
					 (visitable instanceof DropIndexNode) ||
					 (visitable instanceof DropTableNode) ||
					 (visitable instanceof DropTriggerNode) ||
					 (visitable instanceof DropViewNode)) {

				state = DROP;
			}
			else if ((visitable instanceof GrantNode) ||
					 (visitable instanceof GrantRoleNode)) {

				state = GRANT;
			}
			else if (visitable instanceof InsertNode) {
				state = INSERT;
			}
			else if ((visitable instanceof CursorNode) ||
					 (visitable instanceof SubqueryNode) ||
					 (visitable instanceof FromSubquery)) {

				state = SELECT;
			}
			else if (visitable instanceof UpdateNode) {
				state = UPDATE;
			}

			if (state != NONE) {
				_states.push(state);

				return true;
			}

			return false;
		}

		protected String getTableName(Visitable visitable) {
			if (visitable instanceof AlterTableNode) {
				AlterTableNode alterTableNode = (AlterTableNode)visitable;

				return alterTableNode.getRelativeName();
			}
			else if (visitable instanceof CreateTableNode) {
				CreateTableNode createTableNode = (CreateTableNode)visitable;

				return createTableNode.getRelativeName();
			}
			else if (visitable instanceof CreateIndexNode) {
				CreateIndexNode createIndexNode = (CreateIndexNode)visitable;

				TableName tableName = createIndexNode.getIndexTableName();

				return tableName.getTableName();
			}
			else if (visitable instanceof DropTableNode) {
				DropTableNode dropTableNode = (DropTableNode)visitable;

				return dropTableNode.getRelativeName();
			}
			else if (visitable instanceof FromBaseTable) {
				FromBaseTable fromBaseTable = (FromBaseTable)visitable;

				return fromBaseTable.getBaseTableName();
			}
			else if (visitable instanceof TableName) {
				TableName tableName = (TableName)visitable;

				return tableName.getTableName();
			}

			return null;
		}

		private Boolean _allowed;
		private SQLChecker _sqlChecker;
		private Stack<Integer> _states = new Stack<Integer>();
		private Set<Visitable> _visitables = new HashSet<Visitable>();

	}

	private class TableNameHandler {

		public TableNameHandler(String tableName) {
			if (tableName.contains(StringPool.STAR)) {
				try {
					_pattern = Pattern.compile(
						tableName.replaceAll("\\*", ".*?"),
						Pattern.CASE_INSENSITIVE);
				}
				catch (Exception e) {
					if (_log.isWarnEnabled()) {
						_log.warn("Unable to compile pattern " + tableName);
					}
				}
			}

			_tableName = tableName;
		}

		public boolean matches(String tableName) {
			if (_pattern != null) {
				Matcher matcher = _pattern.matcher(tableName);

				return matcher.matches();
			}

			return _tableName.equalsIgnoreCase(tableName);
		}

		private Pattern _pattern;
		private String _tableName;

	}

}
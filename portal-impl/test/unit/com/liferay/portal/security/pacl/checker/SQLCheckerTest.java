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

import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.security.pacl.ActivePACLPolicy;
import com.liferay.portal.security.pacl.PACLPolicy;
import com.liferay.portal.util.PropsImpl;

import java.util.Properties;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Zsolt Berentey
 */
public class SQLCheckerTest {

	@Before
	public void setUp() throws Exception {
		PropsUtil.setProps(new PropsImpl());

		Class<?> clazz = getClass();

		Properties properties = new Properties();

		properties.put("security-manager-sql-tables-all", "Prefix_*");
		properties.put("security-manager-sql-tables-alter", "ValidTable");
		properties.put("security-manager-sql-tables-create", "ValidTable");
		properties.put("security-manager-sql-tables-delete", "ValidTable");
		properties.put("security-manager-sql-tables-drop", "ValidTable");
		properties.put("security-manager-sql-tables-insert", "ValidTable");
		properties.put("security-manager-sql-tables-select", "ValidTable");
		properties.put("security-manager-sql-tables-truncate", "ValidTable");
		properties.put("security-manager-sql-tables-update", "ValidTable");

		_paclPolicy = new ActivePACLPolicy(
			SQLCheckerTest.class.getName(), clazz.getClassLoader(), properties);
	}

	@Test
	public void testAlterTable() {
		testSql(_SQL_ALTER_TABLE, "Prefix_ValidTable", null, true);
		testSql(_SQL_ALTER_TABLE, "InvalidTable", null, false);
		testSql(_SQL_ALTER_TABLE, "ValidTable", null, true);
	}

	@Test
	public void testCreateIndex() {
		testSql(_SQL_CREATE_INDEX, "Prefix_ValidTable", null, true);
		testSql(_SQL_CREATE_INDEX, "InvalidTable", null, false);
		testSql(_SQL_CREATE_INDEX, "ValidTable", null, true);
	}

	@Test
	public void testCreateTable() {
		testSql(_SQL_CREATE_TABLE, "Prefix_ValidTable", null, true);
		testSql(_SQL_CREATE_TABLE, "InvalidTable", null, false);
		testSql(_SQL_CREATE_TABLE, "ValidTable", null, true);
	}

	@Test
	public void testDelete() {
		testSql(_SQL_DELETE, "Prefix_ValidTable", null, true);
		testSql(_SQL_DELETE, "InvalidTable", null, false);
		testSql(_SQL_DELETE, "ValidTable", null, true);
	}

	@Test
	public void testDrop() {
		testSql(_SQL_DROP_TABLE, "Prefix_ValidTable", null, true);
		testSql(_SQL_DROP_TABLE, "InvalidTable", null, false);
		testSql(_SQL_DROP_TABLE, "ValidTable", null, true);
	}

	@Test
	public void testInsert() {
		testSql(_SQL_INSERT, "Prefix_ValidTable", null, true);
		testSql(_SQL_INSERT, "InvalidTable", null, false);
		testSql(_SQL_INSERT, "ValidTable", null, true);
	}

	@Test
	public void testSelect() {
		testSql(_SQL_SELECT, "Prefix_ValidTable", null, true);
		testSql(_SQL_SELECT, "InvalidTable", null, false);
		testSql(_SQL_SELECT, "ValidTable", null, true);
		testSql(
			_SQL_SELECT_INNER_JOIN, "Prefix_ValidTable_1",
			"Prefix_ValidTable_2", true);
		testSql(_SQL_SELECT_INNER_JOIN, "InvalidTable", "ValidTable", false);
		testSql(_SQL_SELECT_INNER_JOIN, "ValidTable", "ValidTable", true);
	}

	@Test
	public void testTruncate() {
		testSql(_SQL_TRUNCATE, "Prefix_ValidTable", null, true);
		testSql(_SQL_TRUNCATE, "InvalidTable", null, false);
		testSql(_SQL_TRUNCATE, "ValidTable", null, true);
	}

	@Test
	public void testUpdate() {
		testSql(_SQL_UPDATE, "Prefix_ValidTable", null, true);
		testSql(_SQL_UPDATE, "InvalidTable", null, false);
		testSql(_SQL_UPDATE, "ValidTable", null, true);
	}

	protected void testSql(
		String sql, String tableName1, String tableName2, boolean expected) {

		sql = StringUtil.replace(
			sql, new String[] {"[$TABLE_1$]", "[$TABLE_2$]"},
			new String[] {tableName1, tableName2});

		Assert.assertEquals(expected, _paclPolicy.hasSQL(sql));
	}

	private static final String _SQL_ALTER_TABLE =
		"alter table [$TABLE_1$] add column test varchar(255)";

	private static final String _SQL_CREATE_INDEX =
		"create index index_name on [$TABLE_1$] (id)";

	private static final String _SQL_CREATE_TABLE =
		"create table [$TABLE_1$] (test varchar(255))";

	private static final String _SQL_DELETE =
		"delete from [$TABLE_1$] where id = 12345";

	private static final String _SQL_DROP_TABLE = "drop table [$TABLE_1$]";

	private static final String _SQL_INSERT =
		"insert into [$TABLE_1$] (id, value) values (1, 2)";

	private static final String _SQL_SELECT =
		"select * from [$TABLE_1$] where id = 1";

	private static final String _SQL_SELECT_INNER_JOIN =
		"select [$TABLE_1$].* from [$TABLE_1$] inner join [$TABLE_2$] on " +
			"[$TABLE_1$].id = [$TABLE_2$].id where name = 'xyz'";

	private static final String _SQL_TRUNCATE = "truncate table [$TABLE_1$]";

	private static final String _SQL_UPDATE =
		"update [$TABLE_1$] set name = 'xy' where id = 1";

	private PACLPolicy _paclPolicy;

}
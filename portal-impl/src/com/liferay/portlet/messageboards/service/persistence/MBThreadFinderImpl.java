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

package com.liferay.portlet.messageboards.service.persistence;

import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.dao.orm.QueryPos;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.SQLQuery;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.dao.orm.Type;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.messageboards.model.MBMessage;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.model.impl.MBThreadImpl;
import com.liferay.util.dao.orm.CustomSQLUtil;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
public class MBThreadFinderImpl
	extends BasePersistenceImpl<MBThread> implements MBThreadFinder {

	public static final String COUNT_BY_G_C =
		MBThreadFinder.class.getName() + ".countByG_C";

	public static final String COUNT_BY_G_U_S =
		MBThreadFinder.class.getName() + ".countByG_U_S";

	public static final String COUNT_BY_G_U_A_S =
		MBThreadFinder.class.getName() + ".countByG_U_A_S";

	public static final String COUNT_BY_G_U_C_S =
		MBThreadFinder.class.getName() + ".countByG_U_C_S";

	public static final String COUNT_BY_G_U_MD_S =
		MBThreadFinder.class.getName() + ".countByG_U_MD_S";

	public static final String COUNT_BY_S_G_U_S =
		MBThreadFinder.class.getName() + ".countByS_G_U_S";

	public static final String COUNT_BY_G_U_C_A_S =
		MBThreadFinder.class.getName() + ".countByG_U_C_A_S";

	public static final String COUNT_BY_S_G_U_C_S =
		MBThreadFinder.class.getName() + ".countByS_G_U_C_S";

	public static final String FIND_BY_NO_ASSETS =
		MBThreadFinder.class.getName() + ".findByNoAssets";

	public static final String FIND_BY_G_C =
		MBThreadFinder.class.getName() + ".findByG_C";

	public static final String FIND_BY_G_U_S =
		MBThreadFinder.class.getName() + ".findByG_U_S";

	public static final String FIND_BY_G_U_A_S =
	MBThreadFinder.class.getName() + ".findByG_U_A_S";

	public static final String FIND_BY_G_U_C_S =
		MBThreadFinder.class.getName() + ".findByG_U_C_S";

	public static final String FIND_BY_G_U_MD_S =
		MBThreadFinder.class.getName() + ".findByG_U_MD_S";

	public static final String FIND_BY_S_G_U_S =
		MBThreadFinder.class.getName() + ".findByS_G_U_S";

	public static final String FIND_BY_G_U_C_A_S =
		MBThreadFinder.class.getName() + ".findByG_U_C_A_S";

	public static final String FIND_BY_S_G_U_C_S =
		MBThreadFinder.class.getName() + ".findByS_G_U_C_S";

	/**
	 * @deprecated {@link #countByG_C_S(
	 * 				long, long, int, QueryDefinition)}
	 */
	public int countByG_C_S(long groupId, long categoryId, int status)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(status);

		return doCountByG_C_S(groupId, categoryId, queryDefinition, false);
	}

	public int countByG_C_S(
			long groupId, long categoryId, QueryDefinition queryDefinition)
		throws SystemException {

		return doCountByG_C_S(groupId, categoryId, queryDefinition, false);
	}

	/**
	 * @deprecated {@link #countByG_U_S(
	 * 				long, long, QueryDefinition)}
	 */
	public int countByG_U_S(long groupId, long userId, int status)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(status);

		return countByG_U_S(groupId, userId, queryDefinition);
	}

	public int countByG_U_S(
			long groupId, long userId, QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_G_U_S);

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #countByG_U_A_S(
	 * 				long, long, boolean, QueryDefinition)}
	 */
	public int countByG_U_A_S(
			long groupId, long userId, boolean anonymous, int status)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(status);

		return countByG_U_A_S(groupId, userId, anonymous, queryDefinition);
	}

	public int countByG_U_A_S(
			long groupId, long userId, boolean anonymous,
			QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_G_U_A_S);

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(userId);
			qPos.add(anonymous);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public int countByG_U_C_S(
			long groupId, long userId, long[] categoryIds,
			QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_G_U_C_S);

			if ((categoryIds == null) || (categoryIds.length == 0)) {
				sql = StringUtil.replace(
					sql, "(MBThread.categoryId = ?) AND", StringPool.BLANK);
			}
			else {
				sql = StringUtil.replace(
					sql, "MBThread.categoryId = ?",
					"MBThread.categoryId = " +
						StringUtil.merge(
							categoryIds, " OR MBThread.categoryId = "));
			}

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #countByG_U_MD_S(
	 * 				long, long, Date, QueryDefinition)}
	 */
	public int countByG_U_MD_S(
			long groupId, long userId, Date modifiedDate, int status)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(status);

		return countByG_U_MD_S(groupId, userId, modifiedDate, queryDefinition);
	}

	public int countByG_U_MD_S(
			long groupId, long userId, Date modifiedDate,
			QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_G_U_MD_S);

			if (userId <= 0) {
				sql = StringUtil.replace(
					sql, _INNER_JOIN_SQL, StringPool.BLANK);
				sql = StringUtil.replace(sql, _USER_ID_SQL, StringPool.BLANK);
			}

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(modifiedDate);

			if (userId > 0) {
				qPos.add(userId);
			}

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #countByS_G_U_S(
	 * 				long, long, int, QueryDefinition)}
	 */
	public int countByS_G_U_S(long groupId, long userId, int status)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(status);

		return doCountByS_G_U_S(groupId, userId, queryDefinition);
	}

	public int countByS_G_U_S(
			long groupId, long userId, QueryDefinition queryDefinition)
		throws SystemException {

		return doCountByS_G_U_S(groupId, userId, queryDefinition);
	}

	public int countByG_U_C_A_S(
			long groupId, long userId, long[] categoryIds, boolean anonymous,
			QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_G_U_C_S);

			if ((categoryIds == null) || (categoryIds.length == 0)) {
				sql = StringUtil.replace(
					sql, "(MBThread.categoryId = ?) AND", StringPool.BLANK);
			}
			else {
				sql = StringUtil.replace(
					sql, "MBThread.categoryId = ?",
					"MBThread.categoryId = " +
						StringUtil.merge(
							categoryIds, " OR MBThread.categoryId = "));
			}

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(userId);
			qPos.add(anonymous);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #countByS_G_U_C_S(
	 * 				long, long, long[], QueryDefinition)}
	 */
	public int countByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds, int status)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(status);

		return doCountByS_G_U_C_S(
			groupId, userId, categoryIds, queryDefinition, false);
	}

	public int countByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds,
			QueryDefinition queryDefinition)
		throws SystemException {

		return doCountByS_G_U_C_S(
			groupId, userId, categoryIds, queryDefinition, false);
	}

	public int filterCountByG_C(long groupId, long categoryId)
		throws SystemException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return MBThreadUtil.countByG_C(groupId, categoryId);
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_G_C);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "MBThread.rootMessageId",
				groupId);

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(categoryId);

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #filterCountByG_C_S(
	 * 				long, long, QueryDefinition)}
	 */
	public int filterCountByG_C_S(long groupId, long categoryId, int status)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(status);

		return doCountByG_C_S(groupId, categoryId, queryDefinition, true);
	}

	public int filterCountByG_C_S(
			long groupId, long categoryId, QueryDefinition queryDefinition)
		throws SystemException {

		return doCountByG_C_S(groupId, categoryId, queryDefinition, true);
	}

	/**
	 * @deprecated {@link #filterCountByS_G_U_C_S(
	 * 				long, long, long[], QueryDefinition)}
	 */
	public int filterCountByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds, int status)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(status);

		return doCountByS_G_U_C_S(
			groupId, userId, categoryIds, queryDefinition, true);
	}

	public int filterCountByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds,
			QueryDefinition queryDefinition)
		throws SystemException {

		return doCountByS_G_U_C_S(
			groupId, userId, categoryIds, queryDefinition, true);
	}

	public List<MBThread> filterFindByG_C(
			long groupId, long categoryId, int start, int end)
		throws SystemException {

		if (!InlineSQLHelperUtil.isEnabled(groupId)) {
			return MBThreadUtil.findByG_C(groupId, categoryId, start, end);
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_G_C);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "MBThread.rootMessageId",
				groupId);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(categoryId);

			return (List<MBThread>)QueryUtil.list(q, getDialect(), start, end);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #filterFindByG_C_S(
	 * 				long, long, int, QueryDefinition)}
	 */
	public List<MBThread> filterFindByG_C_S(
			long groupId, long categoryId, int status, int start, int end)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(
			status, start, end, null);

		return doFindByG_C_S(groupId, categoryId, queryDefinition, true);
	}

	public List<MBThread> filterFindByG_C_S(
			long groupId, long categoryId, QueryDefinition queryDefinition)
		throws SystemException {

		return doFindByG_C_S(groupId, categoryId, queryDefinition, true);
	}

	/**
	 * @deprecated {@link #filterFindByS_G_U_C_S(
	 * 				long, long, long[], QueryDefinition)}
	 */
	public List<MBThread> filterFindByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds, int status,
			int start, int end)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(
			status, start, end, null);

		return doFindByS_G_U_C_S(
			groupId, userId, categoryIds, queryDefinition, true);
	}

	public List<MBThread> filterFindByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds,
			QueryDefinition queryDefinition)
		throws SystemException {

		return doFindByS_G_U_C_S(
			groupId, userId, categoryIds, queryDefinition, true);
	}

	public List<MBThread> findByNoAssets() throws SystemException {
		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_NO_ASSETS);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			return q.list(true);
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #findByG_C_S(
	 * 				long, long, QueryDefinition)}
	 */
	public List<MBThread> findByG_C_S(
			long groupId, long categoryId, int status, int start, int end)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(
			status, start, end, null);

		return findByG_C_S(groupId, categoryId, queryDefinition);
	}

	public List<MBThread> findByG_C_S(
			long groupId, long categoryId, QueryDefinition queryDefinition)
		throws SystemException {

		return doFindByG_C_S(groupId, categoryId, queryDefinition, false);
	}

	/**
	 * @deprecated {@link #findByG_U_S(
	 * 				long, long, QueryDefinition)}
	 */
	public List<MBThread> findByG_U_S(
			long groupId, long userId, int status, int start, int end)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(
			status, start, end, null);

		return findByG_U_S(groupId, userId, queryDefinition);
	}

	public List<MBThread> findByG_U_S(
			long groupId, long userId, QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_G_U_S);

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				q, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #findByG_U_A_S(
	 * 				long, long, boolean, QueryDefinition)}
	 */
	public List<MBThread> findByG_U_A_S(
			long groupId, long userId, boolean anonymous, int status, int start,
			int end)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(
			status, start, end, null);

		return findByG_U_A_S(groupId, userId, anonymous, queryDefinition);
	}

	public List<MBThread> findByG_U_A_S(
			long groupId, long userId, boolean anonymous,
			QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_G_U_A_S);

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(userId);
			qPos.add(anonymous);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				q, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<MBThread> findByG_U_C_S(
			long groupId, long userId, long[] categoryIds,
			QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_G_U_C_S);

			if ((categoryIds == null) || (categoryIds.length == 0)) {
				sql = StringUtil.replace(
					sql, "(MBThread.categoryId = ?) AND", StringPool.BLANK);
			}
			else {
				sql = StringUtil.replace(
					sql, "MBThread.categoryId = ?",
					"MBThread.categoryId = " +
						StringUtil.merge(
							categoryIds, " OR MBThread.categoryId = "));
			}

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				q, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #findByG_U_MD_S(
	 * 				long, long, Date, QueryDefinition)}
	 */
	public List<MBThread> findByG_U_MD_S(
			long groupId, long userId, Date modifiedDate, int status, int start,
			int end)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(
			status, start, end, null);

		return findByG_U_MD_S(groupId, userId, modifiedDate, queryDefinition);
	}

	public List<MBThread> findByG_U_MD_S(
			long groupId, long userId, Date modifiedDate,
			QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_G_U_MD_S);

			if (userId <= 0) {
				sql = StringUtil.replace(
					sql, _INNER_JOIN_SQL, StringPool.BLANK);
				sql = StringUtil.replace(sql, _USER_ID_SQL, StringPool.BLANK);
			}

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(modifiedDate);

			if (userId > 0) {
				qPos.add(userId);
			}

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				q, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #findByS_G_U_S(
	 * 				long, long, QueryDefinition)}
	 */
	public List<MBThread> findByS_G_U_S(
			long groupId, long userId, int status, int start, int end)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(
			status, start, end, null);

		return findByS_G_U_S(groupId, userId, queryDefinition);
	}

	public List<MBThread> findByS_G_U_S(
			long groupId, long userId, QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_S_G_U_S);

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(PortalUtil.getClassNameId(MBThread.class.getName()));
			qPos.add(groupId);
			qPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				q, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	public List<MBThread> findByG_U_C_A_S(
			long groupId, long userId, long[] categoryIds, boolean anonymous,
			QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_G_U_C_A_S);

			if ((categoryIds == null) || (categoryIds.length == 0)) {
				sql = StringUtil.replace(
					sql, "(MBThread.categoryId = ?) AND", StringPool.BLANK);
			}
			else {
				sql = StringUtil.replace(
					sql, "MBThread.categoryId = ?",
					"MBThread.categoryId = " +
						StringUtil.merge(
							categoryIds, " OR MBThread.categoryId = "));
			}

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(userId);
			qPos.add(anonymous);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				q, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	/**
	 * @deprecated {@link #findByS_G_U_C_S(
	 * 				long, long, long[], QueryDefinition)}
	 */
	public List<MBThread> findByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds, int status,
			int start, int end)
		throws SystemException {

		QueryDefinition queryDefinition = new QueryDefinition(
			status, start, end, null);

		return findByS_G_U_C_S(groupId, userId, categoryIds, queryDefinition);
	}

	public List<MBThread> findByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds,
			QueryDefinition queryDefinition)
		throws SystemException {

		return doFindByS_G_U_C_S(
			groupId, userId, categoryIds, queryDefinition, false);
	}

	protected String addThreadStatusClause(
		String sql, QueryDefinition queryDefinition) {

		if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
			if (queryDefinition.isExcludeStatus()) {
				sql = CustomSQLUtil.appendCriteria(
					sql, "AND (MBThread.status != ?)");
			}
			else {
				sql = CustomSQLUtil.appendCriteria(
					sql, "AND (MBThread.status = ?)");
			}
		}

		return sql;
	}

	protected int doCountByG_C_S(
			long groupId, long categoryId, QueryDefinition queryDefinition,
			boolean inlineSQLHelper)
		throws SystemException {

		if (!inlineSQLHelper || !InlineSQLHelperUtil.isEnabled(groupId)) {
			if (queryDefinition.isExcludeStatus()) {
				return MBThreadUtil.countByG_C_NeS(
					groupId, categoryId, queryDefinition.getStatus());
			}
			else {
				if (queryDefinition.getStatus() !=
						WorkflowConstants.STATUS_ANY) {

					return MBThreadUtil.countByG_C_S(
						groupId, categoryId, queryDefinition.getStatus());
				}
				else {
					return MBThreadUtil.countByG_C(groupId, categoryId);
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_G_C);

			sql = addThreadStatusClause(sql, queryDefinition);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "MBThread.rootMessageId",
				groupId);

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(categoryId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw processException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected int doCountByS_G_U_S(
			long groupId, long userId, QueryDefinition queryDefinition)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_S_G_U_S);

			sql = addThreadStatusClause(sql, queryDefinition);

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(PortalUtil.getClassNameId(MBThread.class.getName()));
			qPos.add(groupId);
			qPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected int doCountByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds,
			QueryDefinition queryDefinition, boolean inlineSQLHelper)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(COUNT_BY_S_G_U_C_S);

			if ((categoryIds == null) || (categoryIds.length == 0)) {
				sql = StringUtil.replace(
					sql, "(MBThread.categoryId = ?) AND", StringPool.BLANK);
			}
			else {
				sql = StringUtil.replace(
					sql, "MBThread.categoryId = ?",
					"MBThread.categoryId = " +
						StringUtil.merge(
							categoryIds, " OR MBThread.categoryId = "));
			}

			sql = addThreadStatusClause(sql, queryDefinition);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, MBMessage.class.getName(), "MBThread.rootMessageId",
					groupId);
			}

			SQLQuery q = session.createSQLQuery(sql);

			q.addScalar(COUNT_COLUMN_NAME, Type.LONG);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(PortalUtil.getClassNameId(MBThread.class.getName()));
			qPos.add(groupId);
			qPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			Iterator<Long> itr = q.iterate();

			if (itr.hasNext()) {
				Long count = itr.next();

				if (count != null) {
					return count.intValue();
				}
			}

			return 0;
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<MBThread> doFindByG_C_S(
			long groupId, long categoryId, QueryDefinition queryDefinition,
			boolean inlineSQLHelper)
		throws SystemException {

		if (!inlineSQLHelper || !InlineSQLHelperUtil.isEnabled(groupId)) {
			if (queryDefinition.isExcludeStatus()) {
				return MBThreadUtil.findByG_C_NeS(
					groupId, categoryId, queryDefinition.getStatus(),
					queryDefinition.getStart(), queryDefinition.getEnd());
			}
			else {
				if (queryDefinition.getStatus() !=
						WorkflowConstants.STATUS_ANY) {

					return MBThreadUtil.findByG_C_S(
						groupId, categoryId, queryDefinition.getStatus(),
						queryDefinition.getStart(), queryDefinition.getEnd());
				}
				else {
					return MBThreadUtil.findByG_C(
						groupId, categoryId, queryDefinition.getStart(),
						queryDefinition.getEnd());
				}
			}
		}

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_G_C);

			sql = addThreadStatusClause(sql, queryDefinition);

			sql = InlineSQLHelperUtil.replacePermissionCheck(
				sql, MBMessage.class.getName(), "MBThread.rootMessageId",
				groupId);

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(groupId);
			qPos.add(categoryId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				q, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	protected List<MBThread> doFindByS_G_U_C_S(
			long groupId, long userId, long[] categoryIds,
			QueryDefinition queryDefinition, boolean inlineSQLHelper)
		throws SystemException {

		Session session = null;

		try {
			session = openSession();

			String sql = CustomSQLUtil.get(FIND_BY_S_G_U_C_S);

			if ((categoryIds == null) || (categoryIds.length == 0)) {
				sql = StringUtil.replace(
					sql, "(MBThread.categoryId = ?) AND", StringPool.BLANK);
			}
			else {
				sql = StringUtil.replace(
					sql, "MBThread.categoryId = ?",
					"MBThread.categoryId = " +
						StringUtil.merge(
							categoryIds, " OR MBThread.categoryId = "));
			}

			sql = addThreadStatusClause(sql, queryDefinition);

			if (inlineSQLHelper) {
				sql = InlineSQLHelperUtil.replacePermissionCheck(
					sql, MBMessage.class.getName(), "MBThread.rootMessageId",
					groupId);
			}

			SQLQuery q = session.createSQLQuery(sql);

			q.addEntity("MBThread", MBThreadImpl.class);

			QueryPos qPos = QueryPos.getInstance(q);

			qPos.add(PortalUtil.getClassNameId(MBThread.class.getName()));
			qPos.add(groupId);
			qPos.add(userId);

			if (queryDefinition.getStatus() != WorkflowConstants.STATUS_ANY) {
				qPos.add(queryDefinition.getStatus());
			}

			return (List<MBThread>)QueryUtil.list(
				q, getDialect(), queryDefinition.getStart(),
				queryDefinition.getEnd());
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
		finally {
			closeSession(session);
		}
	}

	private static final String _INNER_JOIN_SQL =
		"INNER JOIN MBMessage ON (MBThread.threadId = MBMessage.threadId)";

	private static final String _USER_ID_SQL = "AND (MBMessage.userId = ?)";

}
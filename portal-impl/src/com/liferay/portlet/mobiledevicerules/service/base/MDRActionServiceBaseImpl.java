/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.mobiledevicerules.service.base;

import com.liferay.counter.service.CounterLocalService;

import com.liferay.portal.kernel.bean.BeanReference;
import com.liferay.portal.kernel.bean.IdentifiableBean;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdate;
import com.liferay.portal.kernel.dao.jdbc.SqlUpdateFactoryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.BaseServiceImpl;
import com.liferay.portal.service.ResourceLocalService;
import com.liferay.portal.service.SystemEventEntryLocalService;
import com.liferay.portal.service.SystemEventEntryService;
import com.liferay.portal.service.UserLocalService;
import com.liferay.portal.service.UserService;
import com.liferay.portal.service.persistence.SystemEventEntryPersistence;
import com.liferay.portal.service.persistence.UserFinder;
import com.liferay.portal.service.persistence.UserPersistence;

import com.liferay.portlet.mobiledevicerules.model.MDRAction;
import com.liferay.portlet.mobiledevicerules.service.MDRActionLocalService;
import com.liferay.portlet.mobiledevicerules.service.MDRActionService;
import com.liferay.portlet.mobiledevicerules.service.MDRRuleGroupInstanceLocalService;
import com.liferay.portlet.mobiledevicerules.service.MDRRuleGroupInstanceService;
import com.liferay.portlet.mobiledevicerules.service.MDRRuleGroupLocalService;
import com.liferay.portlet.mobiledevicerules.service.MDRRuleGroupService;
import com.liferay.portlet.mobiledevicerules.service.MDRRuleLocalService;
import com.liferay.portlet.mobiledevicerules.service.MDRRuleService;
import com.liferay.portlet.mobiledevicerules.service.persistence.MDRActionPersistence;
import com.liferay.portlet.mobiledevicerules.service.persistence.MDRRuleGroupFinder;
import com.liferay.portlet.mobiledevicerules.service.persistence.MDRRuleGroupInstancePersistence;
import com.liferay.portlet.mobiledevicerules.service.persistence.MDRRuleGroupPersistence;
import com.liferay.portlet.mobiledevicerules.service.persistence.MDRRulePersistence;

import javax.sql.DataSource;

/**
 * Provides the base implementation for the m d r action remote service.
 *
 * <p>
 * This implementation exists only as a container for the default service methods generated by ServiceBuilder. All custom service methods should be put in {@link com.liferay.portlet.mobiledevicerules.service.impl.MDRActionServiceImpl}.
 * </p>
 *
 * @author Edward C. Han
 * @see com.liferay.portlet.mobiledevicerules.service.impl.MDRActionServiceImpl
 * @see com.liferay.portlet.mobiledevicerules.service.MDRActionServiceUtil
 * @generated
 */
public abstract class MDRActionServiceBaseImpl extends BaseServiceImpl
	implements MDRActionService, IdentifiableBean {
	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this class directly. Always use {@link com.liferay.portlet.mobiledevicerules.service.MDRActionServiceUtil} to access the m d r action remote service.
	 */

	/**
	 * Returns the m d r action local service.
	 *
	 * @return the m d r action local service
	 */
	public MDRActionLocalService getMDRActionLocalService() {
		return mdrActionLocalService;
	}

	/**
	 * Sets the m d r action local service.
	 *
	 * @param mdrActionLocalService the m d r action local service
	 */
	public void setMDRActionLocalService(
		MDRActionLocalService mdrActionLocalService) {
		this.mdrActionLocalService = mdrActionLocalService;
	}

	/**
	 * Returns the m d r action remote service.
	 *
	 * @return the m d r action remote service
	 */
	public MDRActionService getMDRActionService() {
		return mdrActionService;
	}

	/**
	 * Sets the m d r action remote service.
	 *
	 * @param mdrActionService the m d r action remote service
	 */
	public void setMDRActionService(MDRActionService mdrActionService) {
		this.mdrActionService = mdrActionService;
	}

	/**
	 * Returns the m d r action persistence.
	 *
	 * @return the m d r action persistence
	 */
	public MDRActionPersistence getMDRActionPersistence() {
		return mdrActionPersistence;
	}

	/**
	 * Sets the m d r action persistence.
	 *
	 * @param mdrActionPersistence the m d r action persistence
	 */
	public void setMDRActionPersistence(
		MDRActionPersistence mdrActionPersistence) {
		this.mdrActionPersistence = mdrActionPersistence;
	}

	/**
	 * Returns the m d r rule local service.
	 *
	 * @return the m d r rule local service
	 */
	public MDRRuleLocalService getMDRRuleLocalService() {
		return mdrRuleLocalService;
	}

	/**
	 * Sets the m d r rule local service.
	 *
	 * @param mdrRuleLocalService the m d r rule local service
	 */
	public void setMDRRuleLocalService(MDRRuleLocalService mdrRuleLocalService) {
		this.mdrRuleLocalService = mdrRuleLocalService;
	}

	/**
	 * Returns the m d r rule remote service.
	 *
	 * @return the m d r rule remote service
	 */
	public MDRRuleService getMDRRuleService() {
		return mdrRuleService;
	}

	/**
	 * Sets the m d r rule remote service.
	 *
	 * @param mdrRuleService the m d r rule remote service
	 */
	public void setMDRRuleService(MDRRuleService mdrRuleService) {
		this.mdrRuleService = mdrRuleService;
	}

	/**
	 * Returns the m d r rule persistence.
	 *
	 * @return the m d r rule persistence
	 */
	public MDRRulePersistence getMDRRulePersistence() {
		return mdrRulePersistence;
	}

	/**
	 * Sets the m d r rule persistence.
	 *
	 * @param mdrRulePersistence the m d r rule persistence
	 */
	public void setMDRRulePersistence(MDRRulePersistence mdrRulePersistence) {
		this.mdrRulePersistence = mdrRulePersistence;
	}

	/**
	 * Returns the m d r rule group local service.
	 *
	 * @return the m d r rule group local service
	 */
	public MDRRuleGroupLocalService getMDRRuleGroupLocalService() {
		return mdrRuleGroupLocalService;
	}

	/**
	 * Sets the m d r rule group local service.
	 *
	 * @param mdrRuleGroupLocalService the m d r rule group local service
	 */
	public void setMDRRuleGroupLocalService(
		MDRRuleGroupLocalService mdrRuleGroupLocalService) {
		this.mdrRuleGroupLocalService = mdrRuleGroupLocalService;
	}

	/**
	 * Returns the m d r rule group remote service.
	 *
	 * @return the m d r rule group remote service
	 */
	public MDRRuleGroupService getMDRRuleGroupService() {
		return mdrRuleGroupService;
	}

	/**
	 * Sets the m d r rule group remote service.
	 *
	 * @param mdrRuleGroupService the m d r rule group remote service
	 */
	public void setMDRRuleGroupService(MDRRuleGroupService mdrRuleGroupService) {
		this.mdrRuleGroupService = mdrRuleGroupService;
	}

	/**
	 * Returns the m d r rule group persistence.
	 *
	 * @return the m d r rule group persistence
	 */
	public MDRRuleGroupPersistence getMDRRuleGroupPersistence() {
		return mdrRuleGroupPersistence;
	}

	/**
	 * Sets the m d r rule group persistence.
	 *
	 * @param mdrRuleGroupPersistence the m d r rule group persistence
	 */
	public void setMDRRuleGroupPersistence(
		MDRRuleGroupPersistence mdrRuleGroupPersistence) {
		this.mdrRuleGroupPersistence = mdrRuleGroupPersistence;
	}

	/**
	 * Returns the m d r rule group finder.
	 *
	 * @return the m d r rule group finder
	 */
	public MDRRuleGroupFinder getMDRRuleGroupFinder() {
		return mdrRuleGroupFinder;
	}

	/**
	 * Sets the m d r rule group finder.
	 *
	 * @param mdrRuleGroupFinder the m d r rule group finder
	 */
	public void setMDRRuleGroupFinder(MDRRuleGroupFinder mdrRuleGroupFinder) {
		this.mdrRuleGroupFinder = mdrRuleGroupFinder;
	}

	/**
	 * Returns the m d r rule group instance local service.
	 *
	 * @return the m d r rule group instance local service
	 */
	public MDRRuleGroupInstanceLocalService getMDRRuleGroupInstanceLocalService() {
		return mdrRuleGroupInstanceLocalService;
	}

	/**
	 * Sets the m d r rule group instance local service.
	 *
	 * @param mdrRuleGroupInstanceLocalService the m d r rule group instance local service
	 */
	public void setMDRRuleGroupInstanceLocalService(
		MDRRuleGroupInstanceLocalService mdrRuleGroupInstanceLocalService) {
		this.mdrRuleGroupInstanceLocalService = mdrRuleGroupInstanceLocalService;
	}

	/**
	 * Returns the m d r rule group instance remote service.
	 *
	 * @return the m d r rule group instance remote service
	 */
	public MDRRuleGroupInstanceService getMDRRuleGroupInstanceService() {
		return mdrRuleGroupInstanceService;
	}

	/**
	 * Sets the m d r rule group instance remote service.
	 *
	 * @param mdrRuleGroupInstanceService the m d r rule group instance remote service
	 */
	public void setMDRRuleGroupInstanceService(
		MDRRuleGroupInstanceService mdrRuleGroupInstanceService) {
		this.mdrRuleGroupInstanceService = mdrRuleGroupInstanceService;
	}

	/**
	 * Returns the m d r rule group instance persistence.
	 *
	 * @return the m d r rule group instance persistence
	 */
	public MDRRuleGroupInstancePersistence getMDRRuleGroupInstancePersistence() {
		return mdrRuleGroupInstancePersistence;
	}

	/**
	 * Sets the m d r rule group instance persistence.
	 *
	 * @param mdrRuleGroupInstancePersistence the m d r rule group instance persistence
	 */
	public void setMDRRuleGroupInstancePersistence(
		MDRRuleGroupInstancePersistence mdrRuleGroupInstancePersistence) {
		this.mdrRuleGroupInstancePersistence = mdrRuleGroupInstancePersistence;
	}

	/**
	 * Returns the counter local service.
	 *
	 * @return the counter local service
	 */
	public CounterLocalService getCounterLocalService() {
		return counterLocalService;
	}

	/**
	 * Sets the counter local service.
	 *
	 * @param counterLocalService the counter local service
	 */
	public void setCounterLocalService(CounterLocalService counterLocalService) {
		this.counterLocalService = counterLocalService;
	}

	/**
	 * Returns the resource local service.
	 *
	 * @return the resource local service
	 */
	public ResourceLocalService getResourceLocalService() {
		return resourceLocalService;
	}

	/**
	 * Sets the resource local service.
	 *
	 * @param resourceLocalService the resource local service
	 */
	public void setResourceLocalService(
		ResourceLocalService resourceLocalService) {
		this.resourceLocalService = resourceLocalService;
	}

	/**
	 * Returns the system event entry local service.
	 *
	 * @return the system event entry local service
	 */
	public SystemEventEntryLocalService getSystemEventEntryLocalService() {
		return systemEventEntryLocalService;
	}

	/**
	 * Sets the system event entry local service.
	 *
	 * @param systemEventEntryLocalService the system event entry local service
	 */
	public void setSystemEventEntryLocalService(
		SystemEventEntryLocalService systemEventEntryLocalService) {
		this.systemEventEntryLocalService = systemEventEntryLocalService;
	}

	/**
	 * Returns the system event entry remote service.
	 *
	 * @return the system event entry remote service
	 */
	public SystemEventEntryService getSystemEventEntryService() {
		return systemEventEntryService;
	}

	/**
	 * Sets the system event entry remote service.
	 *
	 * @param systemEventEntryService the system event entry remote service
	 */
	public void setSystemEventEntryService(
		SystemEventEntryService systemEventEntryService) {
		this.systemEventEntryService = systemEventEntryService;
	}

	/**
	 * Returns the system event entry persistence.
	 *
	 * @return the system event entry persistence
	 */
	public SystemEventEntryPersistence getSystemEventEntryPersistence() {
		return systemEventEntryPersistence;
	}

	/**
	 * Sets the system event entry persistence.
	 *
	 * @param systemEventEntryPersistence the system event entry persistence
	 */
	public void setSystemEventEntryPersistence(
		SystemEventEntryPersistence systemEventEntryPersistence) {
		this.systemEventEntryPersistence = systemEventEntryPersistence;
	}

	/**
	 * Returns the user local service.
	 *
	 * @return the user local service
	 */
	public UserLocalService getUserLocalService() {
		return userLocalService;
	}

	/**
	 * Sets the user local service.
	 *
	 * @param userLocalService the user local service
	 */
	public void setUserLocalService(UserLocalService userLocalService) {
		this.userLocalService = userLocalService;
	}

	/**
	 * Returns the user remote service.
	 *
	 * @return the user remote service
	 */
	public UserService getUserService() {
		return userService;
	}

	/**
	 * Sets the user remote service.
	 *
	 * @param userService the user remote service
	 */
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	/**
	 * Returns the user persistence.
	 *
	 * @return the user persistence
	 */
	public UserPersistence getUserPersistence() {
		return userPersistence;
	}

	/**
	 * Sets the user persistence.
	 *
	 * @param userPersistence the user persistence
	 */
	public void setUserPersistence(UserPersistence userPersistence) {
		this.userPersistence = userPersistence;
	}

	/**
	 * Returns the user finder.
	 *
	 * @return the user finder
	 */
	public UserFinder getUserFinder() {
		return userFinder;
	}

	/**
	 * Sets the user finder.
	 *
	 * @param userFinder the user finder
	 */
	public void setUserFinder(UserFinder userFinder) {
		this.userFinder = userFinder;
	}

	public void afterPropertiesSet() {
	}

	public void destroy() {
	}

	/**
	 * Returns the Spring bean ID for this bean.
	 *
	 * @return the Spring bean ID for this bean
	 */
	@Override
	public String getBeanIdentifier() {
		return _beanIdentifier;
	}

	/**
	 * Sets the Spring bean ID for this bean.
	 *
	 * @param beanIdentifier the Spring bean ID for this bean
	 */
	@Override
	public void setBeanIdentifier(String beanIdentifier) {
		_beanIdentifier = beanIdentifier;
	}

	protected Class<?> getModelClass() {
		return MDRAction.class;
	}

	protected String getModelClassName() {
		return MDRAction.class.getName();
	}

	/**
	 * Performs an SQL query.
	 *
	 * @param sql the sql query
	 */
	protected void runSQL(String sql) throws SystemException {
		try {
			DataSource dataSource = mdrActionPersistence.getDataSource();

			SqlUpdate sqlUpdate = SqlUpdateFactoryUtil.getSqlUpdate(dataSource,
					sql, new int[0]);

			sqlUpdate.update();
		}
		catch (Exception e) {
			throw new SystemException(e);
		}
	}

	@BeanReference(type = MDRActionLocalService.class)
	protected MDRActionLocalService mdrActionLocalService;
	@BeanReference(type = MDRActionService.class)
	protected MDRActionService mdrActionService;
	@BeanReference(type = MDRActionPersistence.class)
	protected MDRActionPersistence mdrActionPersistence;
	@BeanReference(type = MDRRuleLocalService.class)
	protected MDRRuleLocalService mdrRuleLocalService;
	@BeanReference(type = MDRRuleService.class)
	protected MDRRuleService mdrRuleService;
	@BeanReference(type = MDRRulePersistence.class)
	protected MDRRulePersistence mdrRulePersistence;
	@BeanReference(type = MDRRuleGroupLocalService.class)
	protected MDRRuleGroupLocalService mdrRuleGroupLocalService;
	@BeanReference(type = MDRRuleGroupService.class)
	protected MDRRuleGroupService mdrRuleGroupService;
	@BeanReference(type = MDRRuleGroupPersistence.class)
	protected MDRRuleGroupPersistence mdrRuleGroupPersistence;
	@BeanReference(type = MDRRuleGroupFinder.class)
	protected MDRRuleGroupFinder mdrRuleGroupFinder;
	@BeanReference(type = MDRRuleGroupInstanceLocalService.class)
	protected MDRRuleGroupInstanceLocalService mdrRuleGroupInstanceLocalService;
	@BeanReference(type = MDRRuleGroupInstanceService.class)
	protected MDRRuleGroupInstanceService mdrRuleGroupInstanceService;
	@BeanReference(type = MDRRuleGroupInstancePersistence.class)
	protected MDRRuleGroupInstancePersistence mdrRuleGroupInstancePersistence;
	@BeanReference(type = CounterLocalService.class)
	protected CounterLocalService counterLocalService;
	@BeanReference(type = ResourceLocalService.class)
	protected ResourceLocalService resourceLocalService;
	@BeanReference(type = SystemEventEntryLocalService.class)
	protected SystemEventEntryLocalService systemEventEntryLocalService;
	@BeanReference(type = SystemEventEntryService.class)
	protected SystemEventEntryService systemEventEntryService;
	@BeanReference(type = SystemEventEntryPersistence.class)
	protected SystemEventEntryPersistence systemEventEntryPersistence;
	@BeanReference(type = UserLocalService.class)
	protected UserLocalService userLocalService;
	@BeanReference(type = UserService.class)
	protected UserService userService;
	@BeanReference(type = UserPersistence.class)
	protected UserPersistence userPersistence;
	@BeanReference(type = UserFinder.class)
	protected UserFinder userFinder;
	private String _beanIdentifier;
}
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

package com.liferay.portal.service.persistence;

import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.Property;
import com.liferay.portal.kernel.dao.orm.PropertyFactoryUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lar.ManifestSummary;
import com.liferay.portal.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.lar.StagedModelDataHandler;
import com.liferay.portal.kernel.lar.StagedModelDataHandlerRegistryUtil;
import com.liferay.portal.kernel.lar.StagedModelDataHandlerUtil;
import com.liferay.portal.model.Website;
import com.liferay.portal.util.PortalUtil;

import com.liferay.portlet.social.model.SocialActivityConstants;
import com.liferay.portlet.social.service.persistence.SocialActivityActionableDynamicQuery;

import java.util.Date;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
public class WebsiteExportActionableDynamicQuery
	extends WebsiteActionableDynamicQuery {
	public WebsiteExportActionableDynamicQuery(
		PortletDataContext portletDataContext) throws SystemException {
		_portletDataContext = portletDataContext;
	}

	@Override
	public long performCount() throws PortalException, SystemException {
		long count = super.performCount();

		ManifestSummary manifestSummary = _portletDataContext.getManifestSummary();

		manifestSummary.addModelCount(getManifestSummaryKey(), count);

		long deletionCount = getDeletionCount();

		manifestSummary.addDeletionCount(getManifestSummaryKey(), deletionCount);

		return count;
	}

	@Override
	protected void addCriteria(DynamicQuery dynamicQuery) {
		_portletDataContext.addDateRangeCriteria(dynamicQuery, "modifiedDate");
	}

	protected long getDeletionCount() throws PortalException, SystemException {
		ActionableDynamicQuery actionableDynamicQuery = new SocialActivityActionableDynamicQuery() {
				@Override
				protected void addCriteria(DynamicQuery dynamicQuery) {
					Property groupProperty = PropertyFactoryUtil.forName(
							"groupId");

					dynamicQuery.add(groupProperty.eq(
							_portletDataContext.getScopeGroupId()));

					Property typeProperty = PropertyFactoryUtil.forName("type");

					dynamicQuery.add(typeProperty.eq(
							SocialActivityConstants.TYPE_DELETE));

					Property classNameIdProperty = PropertyFactoryUtil.forName(
							"classNameId");

					dynamicQuery.add(classNameIdProperty.eq(
							PortalUtil.getClassNameId(Website.class.getName())));

					if (!_portletDataContext.hasDateRange()) {
						return;
					}

					Property modifiedDateProperty = PropertyFactoryUtil.forName(
							"createDate");

					Date startDate = _portletDataContext.getStartDate();
					Date endDate = _portletDataContext.getEndDate();

					dynamicQuery.add(modifiedDateProperty.ge(
							startDate.getTime()));
					dynamicQuery.add(modifiedDateProperty.lt(endDate.getTime()));
				}

				@Override
				protected void performAction(Object object)
					throws PortalException, SystemException {
				}
			};

		return actionableDynamicQuery.performCount();
	}

	protected String getManifestSummaryKey() {
		StagedModelDataHandler<?> stagedModelDataHandler = StagedModelDataHandlerRegistryUtil.getStagedModelDataHandler(Website.class.getName());

		return stagedModelDataHandler.getManifestSummaryKey(null);
	}

	@Override
	@SuppressWarnings("unused")
	protected void performAction(Object object)
		throws PortalException, SystemException {
		Website stagedModel = (Website)object;

		StagedModelDataHandlerUtil.exportStagedModel(_portletDataContext,
			stagedModel);
	}

	private PortletDataContext _portletDataContext;
}
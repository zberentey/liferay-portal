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

package com.liferay.portlet.sitesadmin.util;

import com.liferay.portal.kernel.staging.StagingConstants;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.Group;
import com.liferay.portlet.asset.model.AssetTag;
import com.liferay.portlet.asset.service.AssetTagServiceUtil;

import java.util.List;

import javax.portlet.PortletRequest;

/**
 * @author Zsolt Szabo
 */
public class SitesAdminImpl implements SitesAdmin {

	public boolean getActive(
		PortletRequest portletRequest, boolean defaultActive) {

		String active = portletRequest.getParameter("active");

		if (Validator.isNull(active)) {
			return defaultActive;
		}

		return Boolean.parseBoolean(active);
	}

	public String[] getAssetTagNames(
		PortletRequest portletRequest, Group liveGroup) throws Exception{

		List<AssetTag> tags = AssetTagServiceUtil.getTags(
			liveGroup.getClassName(), liveGroup.getClassPK());

		String defaultAssetTagNames = ListUtil.toString(
			tags, AssetTag.NAME_ACCESSOR);

		String assetTagNames = portletRequest.getParameter("assetTagNames");

		if (assetTagNames == null) {
			assetTagNames = defaultAssetTagNames;
		}

		return StringUtil.split(assetTagNames);
	}

	public long getRemoteGroupId(
		PortletRequest portletRequest, String defaultRemoteGroupId) {

		String remoteGroupId = portletRequest.getParameter("remoteGroupId");

		if (Validator.isNull(remoteGroupId) && (defaultRemoteGroupId == null)) {
			return SitesAdmin.DEFAULT_REMOTE_GROUP_ID;
		}

		if (Validator.isNull(remoteGroupId)) {
			return Long.parseLong(defaultRemoteGroupId);
		}

		return Long.parseLong(remoteGroupId);
	}

	public int getRemotePort(
		PortletRequest portletRequest, String defaultRemotePort) {

		String remotePort = portletRequest.getParameter("remotePort");

		if (Validator.isNull(remotePort) && (defaultRemotePort == null)) {
			return SitesAdmin.DEFAULT_REMOTE_PORT;
		}

		if (Validator.isNull(remotePort)) {
			return Integer.parseInt(defaultRemotePort);
		}

		return Integer.parseInt(remotePort);
	}

	public boolean getSecureConnection(
		PortletRequest portletRequest, String defaultSecureConnection) {

		String secureConnection = portletRequest.getParameter(
			"secureConnection");

		if (Validator.isNull(secureConnection) &&
			(defaultSecureConnection == null)) {

			return SitesAdmin.DEFAULT_SECURE_CONNECTION;
		}

		if (Validator.isNull(secureConnection)) {
			return Boolean.parseBoolean(defaultSecureConnection);
		}

		return Boolean.parseBoolean(secureConnection);
	}

	public int getStagingType(Group liveGroup, PortletRequest portletRequest) {
		String stagingType = portletRequest.getParameter("stagingType");

		if (stagingType == null) {
			if (liveGroup.isStagedRemotely()) {
				return StagingConstants.TYPE_REMOTE_STAGING;
			}

			if (liveGroup.hasStagingGroup()) {
				return StagingConstants.TYPE_LOCAL_STAGING;
			}

			return StagingConstants.TYPE_NOT_STAGED;
		}

		return Integer.parseInt(stagingType);
	}

	public int getType(PortletRequest portletRequest, int defaultType) {
		String type = portletRequest.getParameter("type");

		if (Validator.isNull(type)) {
			return defaultType;
		}

		return Integer.parseInt(type);
	}

}
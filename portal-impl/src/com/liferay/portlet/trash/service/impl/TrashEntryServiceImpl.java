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

package com.liferay.portlet.trash.service.impl;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil;
import com.liferay.portlet.asset.model.AssetRendererFactory;
import com.liferay.portlet.trash.model.TrashEntry;
import com.liferay.portlet.trash.service.base.TrashEntryServiceBaseImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Julio Camarero
 */
public class TrashEntryServiceImpl extends TrashEntryServiceBaseImpl {
	public Object[] getEntries(long groupId)
		throws SystemException, PrincipalException {

		return getEntries(groupId, QueryUtil.ALL_POS, QueryUtil.ALL_POS);
	}

	public Object[] getEntries(long groupId, int start, int end)
		throws SystemException, PrincipalException {

		List<TrashEntry> entries = trashEntryLocalService.getEntries(
			groupId, 0, end + PropsValues.TRASH_SEARCH_LIMIT);

		List<TrashEntry> filteredEntries = new ArrayList<TrashEntry>();

		PermissionChecker permissionChecker = getPermissionChecker();

		for (TrashEntry entry : entries) {
			String className = entry.getClassName();
			long classPK = entry.getClassPK();

			AssetRendererFactory assetRendererFactory =
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(className);

			try {
				if (assetRendererFactory.hasPermission(
						permissionChecker, classPK, ActionKeys.VIEW)) {

					filteredEntries.add(entry);
				}
			}
			catch (Exception e) {
			}
		}

		int filteredEntriesCount = filteredEntries.size();

		if ((end != QueryUtil.ALL_POS) && (start != QueryUtil.ALL_POS)) {
			if (end > filteredEntriesCount) {
				end = filteredEntriesCount;
			}

			if (start > filteredEntriesCount) {
				start = filteredEntriesCount;
			}

			filteredEntries = filteredEntries.subList(start, end);
		}

		return new Object[] {filteredEntries, filteredEntriesCount};
	}

}
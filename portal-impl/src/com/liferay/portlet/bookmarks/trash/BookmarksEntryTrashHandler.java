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

package com.liferay.portlet.bookmarks.trash;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.ContainerModel;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portlet.PortletURLFactoryUtil;
import com.liferay.portlet.bookmarks.model.BookmarksEntry;
import com.liferay.portlet.bookmarks.model.BookmarksFolder;
import com.liferay.portlet.bookmarks.model.BookmarksFolderConstants;
import com.liferay.portlet.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.portlet.bookmarks.service.BookmarksEntryServiceUtil;
import com.liferay.portlet.bookmarks.service.permission.BookmarksPermission;
import com.liferay.portlet.trash.model.TrashEntry;
import com.liferay.portlet.trash.service.TrashEntryLocalServiceUtil;

import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

/**
 * Represents the trash handler for bookmarks entries entity.
 *
 * @author Levente Hudák
 */
public class BookmarksEntryTrashHandler extends BookmarksTrashHandler {

	public static final String CLASS_NAME = BookmarksEntry.class.getName();

	public void checkPermission(
		PermissionChecker permissionChecker, long groupId, String actionId)
		throws PortalException, SystemException {

		BookmarksPermission.check(permissionChecker, groupId, actionId);
	}

	/**
	 * Deletes all bookmarks entries with the matching primary keys.
	 *
	 * @param  classPKs the primary keys of the bookmarks entries to be deleted
	 * @throws PortalException if any one of the bookmarks entries could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteTrashEntries(long[] classPKs, boolean checkPermission)
		throws PortalException, SystemException {

		for (long classPK : classPKs) {
			if (checkPermission) {
				BookmarksEntryServiceUtil.deleteEntry(classPK);
			}
			else {
				BookmarksEntryLocalServiceUtil.deleteEntry(classPK);
			}
		}
	}

	/**
	 * Returns the bookmarks entry entity's class name
	 *
	 * @return the bookmarks entry entity's class name
	 */
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public List<ContainerModel> getContainers(
		long entryId, long containerId, int start, int end)
		throws PortalException, SystemException {

		TrashEntry trashEntry = TrashEntryLocalServiceUtil.getTrashEntry(
			entryId);

		return super.doGetContainers(
			trashEntry.getGroupId(), containerId, start, end);
	}

	@Override
	public String getRestoreLink(PortletRequest portletRequest, long classPK)
		throws PortalException, SystemException {

		BookmarksEntry entry = getBookmarksEntry(classPK);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			portletRequest, PortletKeys.BOOKMARKS,
			PortalUtil.getControlPanelPlid(themeDisplay.getCompanyId()),
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter("struts_action", "/bookmarks/view");
		portletURL.setParameter("entryId", String.valueOf(entry.getEntryId()));

		return portletURL.toString();
	}

	@Override
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException, SystemException {

		BookmarksEntry entry = getBookmarksEntry(classPK);

		ThemeDisplay themeDisplay = (ThemeDisplay)portletRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (entry.getFolderId() ==
			BookmarksFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			return StringPool.SLASH.concat(themeDisplay.translate("home"));
		}

		BookmarksFolder folder = entry.getFolder();

		List<BookmarksFolder> bookmarksFolders = folder.getAncestors();

		StringBundler sb = new StringBundler((bookmarksFolders.size() + 2) * 2);

		sb.append(StringPool.SLASH);
		sb.append(themeDisplay.translate("home"));

		for (int i = bookmarksFolders.size() - 1; i >= 0; i--) {
			BookmarksFolder curDLFolder = bookmarksFolders.get(i);

			sb.append(StringPool.SLASH);
			sb.append(curDLFolder.getName());
		}

		sb.append(StringPool.SLASH);
		sb.append(entry.getName());

		return sb.toString();
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK)
		throws PortalException, SystemException {

		BookmarksEntry entry = getBookmarksEntry(classPK);

		return new BookmarksEntryTrashRenderer(entry);
	}

	@Override
	public boolean isRestorable(long classPK)
		throws PortalException, SystemException {

		BookmarksEntry entry = getBookmarksEntry(classPK);

		BookmarksFolder folder = entry.getFolder();

		return !(folder.isInTrash() || folder.isInTrashFolder());
	}

	@Override
	public void moveTrashEntry(
		long classPK, long containerId, ServiceContext serviceContext)
		throws PortalException, SystemException {

		BookmarksEntryServiceUtil.moveEntryFromTrash(classPK, containerId);
	}

	/**
	 * Restores all bookmarks entries with the matching primary keys.
	 *
	 * @param  classPKs the primary key of the bookmarks entry to be restored
	 * @throws PortalException if any one of the bookmarks entries could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void restoreTrashEntries(long[] classPKs)
		throws PortalException, SystemException {

		for (long classPK : classPKs) {
			BookmarksEntryServiceUtil.restoreEntryFromTrash(classPK);
		}
	}

}
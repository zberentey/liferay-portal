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
import com.liferay.portal.kernel.trash.BaseTrashRenderer;
import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.bookmarks.model.BookmarksFolder;
import com.liferay.portlet.bookmarks.service.BookmarksEntryServiceUtil;
import com.liferay.portlet.bookmarks.service.BookmarksFolderServiceUtil;
import com.liferay.portlet.bookmarks.service.permission.BookmarksFolderPermission;

import java.util.Locale;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * @author Levente Hudák
 */
public class BookmarksFolderTrashRenderer extends BaseTrashRenderer {

	public static final String TYPE = "bookmarksFolder";

	public BookmarksFolderTrashRenderer(BookmarksFolder folder)
		throws PortalException, SystemException {

		_folder = folder;
	}

	@Override
	public String getIconPath(ThemeDisplay themeDisplay) {
		int foldersCount = 0;
		int entryCount = 0;

		try {
			foldersCount = BookmarksFolderServiceUtil.getFoldersCount(
				_folder.getGroupId(), _folder.getFolderId());

			entryCount = BookmarksEntryServiceUtil.getEntriesCount(
				_folder.getGroupId(), _folder.getFolderId());
		}
		catch (SystemException e) {
		}

		if ((foldersCount + entryCount) > 0) {
			return themeDisplay.getPathThemeImages() +
				"/common/folder_full_document.png";
		}

		return themeDisplay.getPathThemeImages() + "/common/folder_empty.png";
	}

	public String getPortletId() {
		return PortletKeys.BOOKMARKS;
	}

	public String getSummary(Locale locale) {
		return HtmlUtil.stripHtml(_folder.getDescription());
	}

	public String getTitle(Locale locale) {
		return _folder.getName();
	}

	public String getType() {
		return TYPE;
	}

	public boolean hasDeletePermission(PermissionChecker permissionChecker)
		throws PortalException, SystemException {

		return BookmarksFolderPermission.contains(
			permissionChecker, _folder, ActionKeys.DELETE);
	}

	public boolean hasViewPermission(PermissionChecker permissionChecker)
		throws PortalException, SystemException {

		return BookmarksFolderPermission.contains(
			permissionChecker, _folder, ActionKeys.VIEW);
	}

	public String render(
		RenderRequest renderRequest, RenderResponse renderResponse,
		String template)
		throws Exception {

		if (template.equals(AssetRenderer.TEMPLATE_FULL_CONTENT)) {

			renderRequest.setAttribute(WebKeys.BOOKMARKS_FOLDER, _folder);

			return "/html/portlet/bookmarks/asset/" + template + ".jsp";
		}

		return null;
	}

	private BookmarksFolder _folder;

}
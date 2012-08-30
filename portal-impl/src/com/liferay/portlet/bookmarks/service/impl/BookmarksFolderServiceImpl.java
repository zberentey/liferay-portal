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

package com.liferay.portlet.bookmarks.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.security.auth.PrincipalException;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.bookmarks.model.BookmarksFolder;
import com.liferay.portlet.bookmarks.service.base.BookmarksFolderServiceBaseImpl;
import com.liferay.portlet.bookmarks.service.permission.BookmarksFolderPermission;

import java.util.List;

/**
 * @author Brian Wing Shun Chan
 * @author Levente Hudák
 */
public class BookmarksFolderServiceImpl extends BookmarksFolderServiceBaseImpl {

	public BookmarksFolder addFolder(
			long parentFolderId, String name, String description,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		BookmarksFolderPermission.check(
			getPermissionChecker(), serviceContext.getScopeGroupId(),
			parentFolderId, ActionKeys.ADD_FOLDER);

		return bookmarksFolderLocalService.addFolder(
			getUserId(), parentFolderId, name, description, serviceContext);
	}

	public void deleteFolder(long folderId)
		throws PortalException, SystemException {

		BookmarksFolder folder = bookmarksFolderLocalService.getFolder(
			folderId);

		BookmarksFolderPermission.check(
			getPermissionChecker(), folder, ActionKeys.DELETE);

		bookmarksFolderLocalService.deleteFolder(folderId);
	}

	public BookmarksFolder getFolder(long folderId)
		throws PortalException, SystemException {

		BookmarksFolder folder = bookmarksFolderLocalService.getFolder(
			folderId);

		BookmarksFolderPermission.check(
			getPermissionChecker(), folder, ActionKeys.VIEW);

		return folder;
	}

	public List<BookmarksFolder> getFolders(long groupId)
		throws SystemException {

		return bookmarksFolderPersistence.filterFindByGroupId(groupId);
	}

	public List<BookmarksFolder> getFolders(long groupId, long parentFolderId)
		throws SystemException {

		return bookmarksFolderPersistence.filterFindByG_P(
			groupId, parentFolderId);
	}

	public List<BookmarksFolder> getFolders(
			long groupId, long parentFolderId, int start, int end)
		throws SystemException {

		return bookmarksFolderPersistence.filterFindByG_P(
			groupId, parentFolderId, start, end);
	}

	public List<BookmarksFolder> getFolders(
		long groupId, long parentFolderId, int status, int start, int end)
	throws SystemException {

	return bookmarksFolderPersistence.filterFindByG_P_S(
		groupId, parentFolderId, status, start, end);
}

	public int getFoldersCount(long groupId, long parentFolderId)
		throws SystemException {

		return bookmarksFolderPersistence.filterCountByG_P(
			groupId, parentFolderId);
	}

	public int getFoldersCount(long groupId, long parentFolderId, int status)
		throws SystemException {

		return bookmarksFolderPersistence.filterCountByG_P_S(
			groupId, parentFolderId, status);
	}

	public void getSubfolderIds(
			List<Long> folderIds, long groupId, long folderId)
		throws SystemException {

		List<BookmarksFolder> folders =
			bookmarksFolderPersistence.filterFindByG_P(groupId, folderId);

		for (BookmarksFolder folder : folders) {
			folderIds.add(folder.getFolderId());

			getSubfolderIds(
				folderIds, folder.getGroupId(), folder.getFolderId());
		}
	}

	/**
	 * Moves the BookmarksFolder with the primary key from the trash portlet to the new
	 * parent folder with the primary key.
	 *
	 * @param  folderId the primary key of the folder
	 * @param  parentFolderId the primary key of the new parent folder
	 * @return the BookmarksFolder
	 * @throws PortalException if the folder could not be found
	 * @throws SystemException if a system exception occurred
	 */
	public BookmarksFolder moveFolderFromTrash(
			long folderId, long parentFolderId)
		throws PortalException, SystemException {

		BookmarksFolder folder = getFolder(folderId);

		BookmarksFolderPermission.check(
			getPermissionChecker(), folder, ActionKeys.UPDATE);

		return bookmarksFolderLocalService.moveFolderFromTrash(
			getUserId(), folder, parentFolderId);
	}

	public void moveFolderToTrash(long folderId)
		throws PortalException, PrincipalException, SystemException {

		BookmarksFolder folder = getFolder(folderId);

		BookmarksFolderPermission.check(
			getPermissionChecker(), folder, ActionKeys.DELETE);

		bookmarksFolderLocalService.moveFolderToTrash(getUserId(), folderId);
	}

	public void restoreFolderFromTrash(long folderId)
		throws PortalException, PrincipalException, SystemException {

		BookmarksFolder folder = getFolder(folderId);

		BookmarksFolderPermission.check(
			getPermissionChecker(), folder, ActionKeys.UPDATE);

		bookmarksFolderLocalService.restoreFolderFromTrash(
			getUserId(), folderId);
	}

	public void subscribeFolder(long groupId, long folderId)
		throws PortalException, SystemException {

		BookmarksFolderPermission.check(
			getPermissionChecker(), groupId, folderId, ActionKeys.SUBSCRIBE);

		bookmarksFolderLocalService.subscribeFolder(
			getUserId(), groupId, folderId);
	}

	public void unsubscribeFolder(long groupId, long folderId)
		throws PortalException, SystemException {

		BookmarksFolderPermission.check(
			getPermissionChecker(), groupId, folderId, ActionKeys.SUBSCRIBE);

		bookmarksFolderLocalService.unsubscribeFolder(
			getUserId(), groupId, folderId);
	}

	public BookmarksFolder updateFolder(
			long folderId, long parentFolderId, String name, String description,
			boolean mergeWithParentFolder, ServiceContext serviceContext)
		throws PortalException, SystemException {

		BookmarksFolder folder = bookmarksFolderLocalService.getFolder(
			folderId);

		BookmarksFolderPermission.check(
			getPermissionChecker(), folder, ActionKeys.UPDATE);

		return bookmarksFolderLocalService.updateFolder(
			folderId, parentFolderId, name, description, mergeWithParentFolder,
			serviceContext);
	}

}
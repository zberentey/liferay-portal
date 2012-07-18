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
import com.liferay.portal.kernel.trash.BaseTrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portlet.bookmarks.model.BookmarksFolder;
import com.liferay.portlet.bookmarks.service.BookmarksFolderLocalServiceUtil;
import com.liferay.portlet.bookmarks.service.BookmarksFolderServiceUtil;

/**
 * Represents the trash handler for bookmarks folders entity.
 *
 * @author Levente Hudák
 */
public class BookmarksFolderTrashHandler extends BaseTrashHandler {

	public static final String CLASS_NAME = BookmarksFolder.class.getName();

	/**
	 * Deletes all bookmarks folders with the matching primary keys.
	 *
	 * @param  classPKs the primary keys of the bookmarks folders to be deleted
	 * @throws PortalException if any one of the bookmarks folders could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteTrashEntries(long[] classPKs, boolean checkPermission)
		throws PortalException, SystemException {

		for (long classPK : classPKs) {
			if (checkPermission) {
				BookmarksFolderServiceUtil.deleteFolder(classPK);
			}
			else {
				BookmarksFolderLocalServiceUtil.deleteFolder(classPK);
			}
		}
	}

	/**
	 * Returns the bookmarks folders entity's class name
	 *
	 * @return the bookmarks folders entity's class name
	 */
	public String getClassName() {
		return CLASS_NAME;
	}

	@Override
	public TrashRenderer getTrashRenderer(long classPK)
		throws PortalException, SystemException {

		BookmarksFolder folder = BookmarksFolderServiceUtil.getFolder(classPK);

		return new BookmarksFolderTrashRenderer(folder);
	}

	/**
	 * Restores all bookmarks folders with the matching primary keys.
	 *
	 * @param  classPKs the primary key of the bookmarks folder to be restored
	 * @throws PortalException if any one of the bookmarks folders could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void restoreTrashEntries(long[] classPKs)
		throws PortalException, SystemException {

		for (long classPK : classPKs) {
			BookmarksFolderServiceUtil.restoreFolderFromTrash(classPK);
		}
	}

}
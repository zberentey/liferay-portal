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
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.ContainerModel;
import com.liferay.portlet.bookmarks.model.BookmarksEntry;
import com.liferay.portlet.bookmarks.model.BookmarksFolder;
import com.liferay.portlet.bookmarks.service.BookmarksEntryLocalServiceUtil;
import com.liferay.portlet.bookmarks.service.BookmarksFolderLocalServiceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Levente Hudák
 */
public abstract class BookmarksTrashHandler extends BaseTrashHandler {

	@Override
	public ContainerModel getContainer(long containerId)
		throws PortalException, SystemException {

		return (ContainerModel)getBookmarksFolder(containerId);
	}

	@Override
	public int getContainersCount(long groupId, long containerId)
		throws PortalException, SystemException {

		return BookmarksFolderLocalServiceUtil.getFoldersCount(
			groupId, containerId);
	}

	@Override
	public String getRootContainerName() {
		return "home";
	}

	protected List<ContainerModel> doGetContainers(
			long groupId, long containerId, int start, int end)
		throws PortalException, SystemException {

		List<BookmarksFolder> folders =
			BookmarksFolderLocalServiceUtil.getFolders(
				groupId, containerId, WorkflowConstants.STATUS_APPROVED, start,
				end);

		List<ContainerModel> containers = new ArrayList<ContainerModel>();

		for (BookmarksFolder folder : folders) {
			containers.add((ContainerModel)folder);
		}

		return containers;
	}

	protected BookmarksEntry getBookmarksEntry(long classPK)
		throws PortalException, SystemException {

		return BookmarksEntryLocalServiceUtil.getBookmarksEntry(classPK);
	}

	protected BookmarksFolder getBookmarksFolder(long classPK)
		throws PortalException, SystemException {

		return BookmarksFolderLocalServiceUtil.getBookmarksFolder(classPK);
	}

}
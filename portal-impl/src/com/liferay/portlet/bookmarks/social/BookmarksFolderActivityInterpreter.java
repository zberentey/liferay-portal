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

package com.liferay.portlet.bookmarks.social;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.bookmarks.model.BookmarksFolder;
import com.liferay.portlet.bookmarks.service.BookmarksFolderLocalServiceUtil;
import com.liferay.portlet.social.model.BaseSocialActivityInterpreter;
import com.liferay.portlet.social.model.SocialActivity;
import com.liferay.portlet.social.model.SocialActivityConstants;

/**
 * @author Zsolt Berentey
 */
public class BookmarksFolderActivityInterpreter
	extends BaseSocialActivityInterpreter {

	@Override
	public String[] getClassNames() {
		return _CLASS_NAMES;
	}

	@Override
	protected Object doGetEntity(
			SocialActivity activity, ServiceContext serviceContext)
		throws SystemException {

		return BookmarksFolderLocalServiceUtil.fetchBookmarksFolder(
			activity.getClassPK());
	}

	@Override
	protected String getPath(
		SocialActivity activity, ServiceContext serviceContext) {

		return "/bookmarks/find_folder?folderId=" + activity.getClassPK();
	}

	@Override
	protected String getTitlePattern(
		String groupName, SocialActivity activity) {

		int activityType = activity.getType();

		if (activityType == SocialActivityConstants.TYPE_MOVE_TO_TRASH) {
			if (Validator.isNull(groupName)) {
				return "activity-bookmarks-folder-move-to-trash";
			}
			else {
				return "activity-bookmarks-folder-move-to-trash-in";
			}
		}
		else if (activityType ==
					SocialActivityConstants.TYPE_RESTORE_FROM_TRASH) {

			if (Validator.isNull(groupName)) {
				return "activity-bookmarks-folder-restore-from-trash";
			}
			else {
				return "activity-bookmarks-folder-restore-from-trash-in";
			}
		}
		else if (activityType == SocialActivityConstants.TYPE_DELETE) {
			if (Validator.isNull(groupName)) {
				return "activity-bookmarks-folder-delete";
			}
			else {
				return "activity-bookmarks-folder-delete-in";
			}
		}

		return null;
	}

	private static final String[] _CLASS_NAMES =
		{BookmarksFolder.class.getName()};

}
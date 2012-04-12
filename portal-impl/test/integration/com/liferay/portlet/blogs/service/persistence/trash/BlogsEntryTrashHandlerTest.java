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

package com.liferay.portlet.blogs.service.persistence.trash;

import com.liferay.portal.NoSuchGroupException;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.GroupConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.security.permission.ActionKeys;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserServiceUtil;
import com.liferay.portal.test.EnvironmentExecutionTestListener;
import com.liferay.portal.test.ExecutionTestListeners;
import com.liferay.portal.test.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.test.TransactionalExecutionTestListener;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.blogs.model.BlogsEntry;
import com.liferay.portlet.blogs.service.BlogsEntryServiceUtil;
import com.liferay.portlet.trash.model.TrashEntry;
import com.liferay.portlet.trash.service.TrashEntryLocalServiceUtil;

import java.io.InputStream;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@ExecutionTestListeners(
	listeners = {
		EnvironmentExecutionTestListener.class,
		TransactionalExecutionTestListener.class
	})
@RunWith(LiferayIntegrationJUnitTestRunner.class)
public class BlogsEntryTrashHandlerTest {

	@Test
	@Transactional
	public void testWholeCycle() {
		String title = "Title";
		String description = "Description";
		String content = "Content";
		int displayDateMonth = 1;
		int displayDateDay = 1;
		int displayDateYear = 2012;
		int displayDateHour = 12;
		int displayDateMinute = 0;
		boolean allowPingbacks = true;
		boolean allowTrackbacks = true;
		String[] trackbacks = new String[0];
		boolean smallImage = false;
		String smallImageURL = StringPool.BLANK;
		String smallImageFileName = StringPool.BLANK;
		InputStream smallImageInputStream = null;

		BlogsEntry blogsEntry = null;
		TrashEntry trashEntry = null;

		try {
			User user = UserServiceUtil.getUserByScreenName(
				PortalUtil.getDefaultCompanyId(), "test");

			ServiceContext serviceContext = new ServiceContext();

			serviceContext.setUserId(user.getUserId());
			serviceContext.setAddGroupPermissions(true);
			serviceContext.setAddGuestPermissions(true);

			Group group = createGroup(serviceContext);

			serviceContext.setGroupPermissions(
				new String[] {ActionKeys.ADD_MESSAGE, ActionKeys.VIEW});
			serviceContext.setGuestPermissions(
				new String[] {ActionKeys.ADD_MESSAGE, ActionKeys.VIEW});

			serviceContext.setScopeGroupId(group.getGroupId());

			int initialBlogsEntriesCount = _getBlogsEntriesCount(
				group.getGroupId());

			int initialTrashItemsCount = _getTrashEntriesCount(
				group.getGroupId());

			// create blogsEntry

			blogsEntry = BlogsEntryServiceUtil.addEntry(
				title, description, content, displayDateMonth, displayDateDay,
				displayDateYear, displayDateHour, displayDateMinute,
				allowPingbacks, allowTrackbacks, trackbacks, smallImage,
				smallImageURL, smallImageFileName, smallImageInputStream,
				serviceContext);

			int afterCreatingBlogsEntryCount = _getBlogsEntriesCount(
				blogsEntry.getGroupId());

			// move blogsEntry to trash

			BlogsEntryServiceUtil.moveEntryToTrash(blogsEntry.getEntryId());

			trashEntry = TrashEntryLocalServiceUtil.getEntry(
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

			// check item at recycle bin

			int afterInsertingTrashItemsCount = _getTrashEntriesCount(
				blogsEntry.getGroupId());

			Assert.assertEquals(
				initialTrashItemsCount + 1, afterInsertingTrashItemsCount);

			// check blogsEntry is not displayed

			int afterMovingToTrashBlogsEntryCount = _getBlogsEntriesCount(
				blogsEntry.getGroupId());

			Assert.assertEquals(
				initialBlogsEntriesCount, afterMovingToTrashBlogsEntryCount);

			// restore blogsEntry

			TrashHandler trashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(
					trashEntry.getClassName());

			trashHandler.restoreTrashEntry(trashEntry.getClassPK());

			// check blogsEntry is displayed again

			int afterRestoringBlogsEntryCount = _getBlogsEntriesCount(
				blogsEntry.getGroupId());

			Assert.assertEquals(
				afterCreatingBlogsEntryCount, afterRestoringBlogsEntryCount);

			// move blogsEntry to trash again

			BlogsEntryServiceUtil.moveEntryToTrash(blogsEntry.getEntryId());

			trashEntry = TrashEntryLocalServiceUtil.getEntry(
				BlogsEntry.class.getName(), blogsEntry.getEntryId());

			// delete blogsEntry

			trashHandler.deleteTrashEntry(trashEntry.getClassPK());

			// check deletion

			int finalTrashItemsCount = _getTrashEntriesCount(
				blogsEntry.getGroupId());

			Assert.assertEquals(initialTrashItemsCount, finalTrashItemsCount);
		}
		catch (Exception e) {
			Assert.fail(
				"Unexpected error testing the whole cycle: " + e.getMessage());
		}
	}

	protected Group createGroup(ServiceContext serviceContext)
		throws Exception {

		String name = "Sample Group";
		String description ="This is a sample group";
		int type = GroupConstants.TYPE_SITE_OPEN;
		String friendlyURL =  "/sample-group";
		boolean active = true;
		boolean site = true;
		Group group = null;

		try {
			group = GroupLocalServiceUtil.getGroup(
				serviceContext.getCompanyId(), name);

			return group;
		}
		catch (NoSuchGroupException nsge) {
		}

		return GroupLocalServiceUtil.addGroup(
			serviceContext.getUserId(), null, 0, name, description, type,
			friendlyURL, site, active, serviceContext);
	}

	private int _getBlogsEntriesCount(long groupId) throws Exception {
		List results = BlogsEntryServiceUtil.getGroupEntries(
			groupId, new Date(), WorkflowConstants.STATUS_APPROVED, 20);

		return results.size();
	}

	private int _getTrashEntriesCount(long groupId) throws Exception {
		return TrashEntryLocalServiceUtil.getEntriesCount(groupId);
	}

}
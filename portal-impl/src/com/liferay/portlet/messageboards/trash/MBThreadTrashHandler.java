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

package com.liferay.portlet.messageboards.trash;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.trash.BaseTrashHandler;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.CompanyConstants;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portlet.documentlibrary.NoSuchDirectoryException;
import com.liferay.portlet.documentlibrary.store.DLStoreUtil;
import com.liferay.portlet.messageboards.model.MBThread;
import com.liferay.portlet.messageboards.service.MBThreadLocalServiceUtil;
import com.liferay.portlet.messageboards.service.MBThreadServiceUtil;
import com.liferay.portlet.trash.util.TrashUtil;

import java.util.Date;

/**
 * Represents the trash handler for message boards threads.
 *
 * @author Zsolt Berentey
 */
public class MBThreadTrashHandler extends BaseTrashHandler {

	public static final String CLASS_NAME = MBThread.class.getName();

	/**
	 * Deletes trash attachments from all the message boards messages from a
	 * group that were deleted after a given date
	 *
	 * @param  groupId the primary key of the group
	 * @param  date the date from which attachments will be deleted
	 * @throws PortalException if an entry with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteTrashAttachments(long groupId, Date date)
		throws PortalException, SystemException {

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		long repositoryId = CompanyConstants.SYSTEM;

		String[] threadFileNames = null;

		try {
			threadFileNames = DLStoreUtil.getFileNames(
				group.getCompanyId(), repositoryId, "messageboards");
		}
		catch (NoSuchDirectoryException nsde) {
			return;
		}

		for (String threadFileName : threadFileNames) {
			String[] messageFileNames = null;

			try {
				messageFileNames = DLStoreUtil.getFileNames(
					group.getCompanyId(), repositoryId, threadFileName);
			}
			catch (NoSuchDirectoryException nsde) {
				continue;
			}

			for (String messageFileName : messageFileNames) {
				String fileTitle = StringUtil.extractLast(
					messageFileName, StringPool.FORWARD_SLASH);

				if (fileTitle.startsWith(TrashUtil.TRASH_ATTACHMENTS_DIR)) {
					String[] attachmentFileNames = DLStoreUtil.getFileNames(
						group.getCompanyId(), repositoryId,
						threadFileName + StringPool.FORWARD_SLASH + fileTitle);

					TrashUtil.deleteEntriesAttachments(
						group.getCompanyId(), repositoryId, date,
						attachmentFileNames);
				}
			}
		}
	}

	/**
	 * Deletes all message boards threads with the matching primary keys.
	 *
	 * @param  classPKs the primary keys of the message boards threads to be
	 *         deleted
	 * @throws PortalException if any one of the message boards threads could
	 *         not be found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteTrashEntries(long[] classPKs, boolean checkPermission)
		throws PortalException, SystemException {

		for (long classPK : classPKs) {
			if (checkPermission) {
				MBThreadServiceUtil.deleteThread(classPK);
			}
			else {
				MBThreadLocalServiceUtil.deleteThread(classPK);
			}
		}
	}

	/**
	 * Returns the message boards thread's class name
	 *
	 * @return the message boards thread's class name
	 */
	public String getClassName() {
		return CLASS_NAME;
	}

	/**
	 * Returns the trash renderer for the message boards thread with the primary
	 * key.
	 *
	 * @param  classPK the primary key of the message boards thread
	 * @return Returns the trash renderer
	 * @throws PortalException if the message boards thread could not be found
	 * @throws SystemException if a system exception occurred
	 */
	@Override
	public TrashRenderer getTrashRenderer(long classPK)
		throws PortalException, SystemException {

		MBThread thread = MBThreadLocalServiceUtil.getThread(classPK);

		return new MBThreadTrashRenderer(thread);
	}

	/**
	 * Restores all message boards threads with the matching primary keys.
	 *
	 * @param  classPKs the primary key of the message boards threads to be
	 *         restored
	 * @throws PortalException if any one of the message boards threads could
	 *         not be found
	 * @throws SystemException if a system exception occurred
	 */
	public void restoreTrashEntries(long[] classPKs)
		throws PortalException, SystemException {

		for (long classPK : classPKs) {
			MBThreadServiceUtil.restoreEntryFromTrash(classPK);
		}
	}

}
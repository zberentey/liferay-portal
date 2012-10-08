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

package com.liferay.portal.kernel.trash;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.ContainerModel;
import com.liferay.portal.model.Group;
import com.liferay.portal.security.permission.PermissionChecker;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.trash.model.TrashEntry;

import java.util.Date;
import java.util.List;

import javax.portlet.PortletRequest;

/**
 * Represents the interface to manage the basic operations of the Recycle Bin.
 *
 * <p>
 * The basic operations are:
 * </p>
 *
 * <ul>
 * <li>
 * Delete entries
 * </li>
 * <li>
 * Move entries out of the Recycle Bin to a new destination
 * </li>
 * <li>
 * Restore entries to their original destination
 * </li>
 * </ul>
 *
 * <p>
 * The entities that support these operations are:
 * </p>
 *
 * <ul>
 * <li>
 * BlogsEntry {@link com.liferay.portlet.blogs.trash.BlogsEntryTrashHandler}
 * </li>
 * <li>
 * BookmarksEntry {@link
 * com.liferay.portlet.bookmarks.trash.BookmarksEntryTrashHandler}
 * </li>
 * <li>
 * DLFileEntry {@link
 * com.liferay.portlet.documentlibrary.trash.DLFileEntryTrashHandler}
 * </li>
 * <li>
 * DLFileShortcut {@link
 * com.liferay.portlet.documentlibrary.trash.DLFileShortcutTrashHandler}
 * </li>
 * <li>
 * DLFolder {@link
 * com.liferay.portlet.documentlibrary.trash.DLFolderTrashHandler}
 * </li>
 * <li>
 * MBThread {@link
 * com.liferay.portlet.messageboards.trash.MBThreadTrashHandler}
 * </li>
 * <li>
 * WikiNode {@link
 * com.liferay.portlet.wiki.trash.WikiNodeTrashHandler}
 * </li>
 * <li>
 * WikiPage {@link
 * com.liferay.portlet.wiki.trash.WikiPageTrashHandler}
 * </li>
 * </ul>
 *
 * @author Alexander Chow
 * @author Zsolt Berentey
 */
public interface TrashHandler {

	/**
	 * Checks if an entry already exists in the destination container with the
	 * given name.
	 *
	 * This method is used to check for duplicates when an entry is
	 * being restored or moved out of the Recycle Bin.
	 *
	 * @param  trashEntry the entry to check
	 * @param  containerModelId the primary key of the destination (e.g. folder)
	 * @param  newName the new name to be assigned if the check is successful
	 *         (optionally <code>null</code> if there is no rename)
	 * @throws PortalException if the entry could not be found
	 * @throws SystemException if a system exception occurred
	 */
	public void checkDuplicateTrashEntry(
			TrashEntry trashEntry, long containerModelId, String newName)
		throws PortalException, SystemException;

	/**
	 * Deletes all trash attachments from a group that were deleted after a
	 * given date.
	 *
	 * @param  group ID the primary key of the group
	 * @param  date the date from which attachments will be deleted
	 * @throws PortalException if the attachment file names were invalid
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteTrashAttachments(Group group, Date date)
		throws PortalException, SystemException;

	/**
	 * Deletes all entities with the primary keys.
	 *
	 * @param  classPKs the primary keys of the entities to delete
	 * @throws PortalException if an entity with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteTrashEntries(long[] classPKs)
		throws PortalException, SystemException;

	/**
	 * Deletes all entities with the primary keys.
	 *
	 * @param  classPKs the primary keys of the entities to deletes
	 * @param  checkPermission whether to check permission before deleting each
	 *         entry
	 * @throws PortalException if an entity with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteTrashEntries(long[] classPKs, boolean checkPermission)
		throws PortalException, SystemException;

	/**
	 * Deletes the entity with the primary key.
	 *
	 * @param  classPK the primary key of the entity to delete
	 * @throws PortalException if an entity with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteTrashEntry(long classPK)
		throws PortalException, SystemException;

	/**
	 * Deletes the entity with the primary key.
	 *
	 * @param  classPK the primary key of the entity to delete
	 * @param  checkPermission whether to check permission before deleting the
	 *         entity
	 * @throws PortalException if an entity with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void deleteTrashEntry(long classPK, boolean checkPermission)
		throws PortalException, SystemException;

	/**
	 * Returns the class name handled by this trash handler.
	 *
	 * @return Returns the class name
	 */
	public String getClassName();

	/**
	 * Returns the container model with the primary key that can contain this
	 * item.
	 *
	 * @param  containerModelId the primary key of the container model
	 * @return Returns the container model
	 * @throws PortalException if the container model with the primary key could
	 *         not be found
	 * @throws SystemException if a system exception occurred
	 */
	public ContainerModel getContainerModel(long containerModelId)
		throws PortalException, SystemException;

	/**
	 * Returns the name of the container model (e.g. folder).
	 *
	 * @return Returns the name of the container model
	 */
	public String getContainerModelName();

	/**
	 * Returns a range of all container models with the parent identified by the
	 * container model ID. These container models must be able to contain the
	 * entry identified by entry ID.
	 *
	 * This method should check for view permission when retrieving the
	 * container models.
	 *
	 * <p>
	 * Useful when paginating results. Returns a maximum of <code>end -
	 * start</code> instances. <code>start</code> and <code>end</code> are not
	 * primary keys, they are indexes in the result set. Thus, <code>0</code>
	 * refers to the first result in the set. Setting both <code>start</code>
	 * and <code>end</code> to {@link
	 * com.liferay.portal.kernel.dao.orm.QueryUtil#ALL_POS} will return the full
	 * result set.
	 * </p>
	 *
	 * @param  trashEntryId the primary key of the entry
	 * @param  containerModelId the primary key of the parent container model
	 * @param  start the lower bound of the range of results
	 * @param  end the upper bound of the range of results (not inclusive)
	 * @return the range of container models
	 * @throws PortalException if the trash entry with the primary key could not
	 *         be found
	 * @throws SystemException if a system exception occurred
	 */
	public List<ContainerModel> getContainerModels(
			long trashEntryId, long containerModelId, int start, int end)
		throws PortalException, SystemException;

	/**
	 * Returns the number of container models with the parent identified by the
	 * container model ID. These container models must be able to contain the
	 * entry identified by trash entry ID.
	 *
	 * This method should check for view permission when determining the
	 * container model count.
	 *
	 * @param  trashEntryId the primary key of the entry
	 * @param  containerModelId the primary key of the parent container model
	 * @return the number of container models
	 * @throws PortalException if the trash entry with the primary key could not
	 *         be found
	 * @throws SystemException if a system exception occurred
	 */
	public int getContainerModelsCount(long trashEntryId, long containerModelId)
		throws PortalException, SystemException;

	/**
	 * Returns the message to indicate that a search hit was found in a deleted
	 * container (e.g. folder)
	 *
	 * @return Returns the message
	 */
	public String getDeleteMessage();

	/**
	 * Returns the link to the destination the entity was restored to.
	 *
	 * @param  portletRequest the portlet request
	 * @param  classPK the primary key of the restored entity
	 * @return Returns the restore link
	 * @throws PortalException if the entity with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public String getRestoreLink(PortletRequest portletRequest, long classPK)
		throws PortalException, SystemException;

	/**
	 * Returns the message describing the destination the entity was restored
	 * to.
	 *
	 * @param  portletRequest the portlet request
	 * @param  classPK the primary key of the restored entity
	 * @return Returns the restore message
	 * @throws PortalException if the entry with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public String getRestoreMessage(PortletRequest portletRequest, long classPK)
		throws PortalException, SystemException;

	/**
	 * Returns the name of the root container (e.g. home).
	 *
	 * @return Returns the root container name
	 */
	public String getRootContainerModelName();

	/**
	 * Returns the name of the sub container model (e.g. subfolder).
	 *
	 * @return Returns the name of the sub container model
	 */
	public String getSubcontainerModelName();

	/**
	 * Returns the trash renderer associated to the entity with the primary key.
	 *
	 * @param  classPK the primary key of the entity
	 * @return the trash renderer associated to the entity
	 * @throws PortalException if an entry with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public TrashRenderer getTrashRenderer(long classPK)
		throws PortalException, SystemException;

	/**
	 * Returns <code>true</code> if the user has the required permission on the
	 * entity with the primary key.
	 *
	 * This method is intended to be a mapper for special Recycle Bin operations
	 * that are not real permissions. The implementations of this method should
	 * translate these virtual permissions to real permission checks.
	 *
	 * @param  permissionChecker the permission checker
	 * @param  groupId the primary key of the group
	 * @param  classPK the primary key of the entity
	 * @param  trashActionId the permission to check for
	 * @return <code>true</code> if the user has the required permission;
	 *         <code>false</code> otherwise
	 * @throws PortalException if the entity with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public boolean hasTrashPermission(
			PermissionChecker permissionChecker, long groupId, long classPK,
			String trashActionId)
		throws PortalException, SystemException;

	/**
	 * Returns <code>true</code> if the entity with the primary key is in the
	 * Recycle Bin.
	 *
	 * @param  classPK the primary key of the entity
	 * @return <code>true</code> if the entity is in the Recycle Bin;
	 *         <code>false</code> otherwise
	 * @throws PortalException if the entry with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public boolean isInTrash(long classPK)
		throws PortalException, SystemException;

	/**
	 * Returns <code>true</code> if the entity is restorable to its original
	 * place.
	 *
	 * This method usually returns <code>false</code> if the container (e.g.
	 * folder) of the entity is no longer available (e.g. moved to the Recycle
	 * Bin or deleted).
	 *
	 * @param  classPK the primary key of the entity
	 * @return <code>true</code> if the entity is restorable; <code>false</code>
	 *         otherwise
	 * @throws PortalException if the entity with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public boolean isRestorable(long classPK)
		throws PortalException, SystemException;

	/**
	 * Moves the entity with the primary key out of the Recycle Bin to a new
	 * destination identified by container model ID.
	 *
	 * @param  classPK the primary key of the entity
	 * @param  containerModelId the primary key of the destination container
	 *         model
	 * @param  serviceContext the service context
	 * @throws PortalException if the entity with the primary key or the
	 *         destination container model could not be found
	 * @throws SystemException if a system exception occurred
	 */
	public void moveTrashEntry(
			long classPK, long containerModelId, ServiceContext serviceContext)
		throws PortalException, SystemException;

	/**
	 * Restores all entities with the primary keys.
	 *
	 * @param  classPKs the primary keys of the entities to restore
	 * @throws PortalException if an entity with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void restoreTrashEntries(long[] classPKs)
		throws PortalException, SystemException;

	/**
	 * Restores the entity with the primary key.
	 *
	 * @param  classPK the primary key of the entry to restore
	 * @throws PortalException if the entity with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void restoreTrashEntry(long classPK)
		throws PortalException, SystemException;

	/**
	 * Updates the title of the entity with the primary key before restore.
	 *
	 * @param  classPK the primary key of the entity
	 * @param  title the title to be assigned
	 * @throws PortalException if the entry with the primary key could not be
	 *         found
	 * @throws SystemException if a system exception occurred
	 */
	public void updateTitle(long classPK, String title)
		throws PortalException, SystemException;

}
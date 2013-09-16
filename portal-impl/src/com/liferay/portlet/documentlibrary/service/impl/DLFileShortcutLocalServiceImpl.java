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

package com.liferay.portlet.documentlibrary.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.trash.TrashConstants;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.ResourceConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.NoSuchFileEntryException;
import com.liferay.portlet.documentlibrary.model.DLFileEntryConstants;
import com.liferay.portlet.documentlibrary.model.DLFileShortcut;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.base.DLFileShortcutLocalServiceBaseImpl;
import com.liferay.portlet.social.model.SocialActivityConstants;
import com.liferay.portlet.trash.model.TrashEntry;
import com.liferay.portlet.trash.model.TrashVersion;

import java.util.Date;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class DLFileShortcutLocalServiceImpl
	extends DLFileShortcutLocalServiceBaseImpl {

	@Override
	public DLFileShortcut addFileShortcut(
			long userId, long groupId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		// File shortcut

		User user = userPersistence.findByPrimaryKey(userId);
		folderId = getFolderId(user.getCompanyId(), folderId);
		Date now = new Date();

		validate(user, toFileEntryId);

		long fileShortcutId = counterLocalService.increment();

		DLFileShortcut fileShortcut = dlFileShortcutPersistence.create(
			fileShortcutId);

		fileShortcut.setUuid(serviceContext.getUuid());
		fileShortcut.setGroupId(groupId);
		fileShortcut.setCompanyId(user.getCompanyId());
		fileShortcut.setUserId(user.getUserId());
		fileShortcut.setUserName(user.getFullName());
		fileShortcut.setCreateDate(serviceContext.getCreateDate(now));
		fileShortcut.setModifiedDate(serviceContext.getModifiedDate(now));
		fileShortcut.setFolderId(folderId);
		fileShortcut.setToFileEntryId(toFileEntryId);
		fileShortcut.setTreePath(fileShortcut.buildTreePath());
		fileShortcut.setActive(true);
		fileShortcut.setStatus(WorkflowConstants.STATUS_APPROVED);
		fileShortcut.setStatusByUserId(userId);
		fileShortcut.setStatusByUserName(user.getFullName());
		fileShortcut.setStatusDate(now);

		dlFileShortcutPersistence.update(fileShortcut);

		// Resources

		if (serviceContext.isAddGroupPermissions() ||
			serviceContext.isAddGuestPermissions()) {

			addFileShortcutResources(
				fileShortcut, serviceContext.isAddGroupPermissions(),
				serviceContext.isAddGuestPermissions());
		}
		else {
			addFileShortcutResources(
				fileShortcut, serviceContext.getGroupPermissions(),
				serviceContext.getGuestPermissions());
		}

		// Folder

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

			dlFolder.setLastPostDate(fileShortcut.getModifiedDate());

			dlFolderPersistence.update(dlFolder);
		}

		// Asset

		FileEntry fileEntry = dlAppLocalService.getFileEntry(toFileEntryId);

		copyAssetTags(fileEntry, serviceContext);

		updateAsset(
			userId, fileShortcut, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		return fileShortcut;
	}

	@Override
	public void addFileShortcutResources(
			DLFileShortcut fileShortcut, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException, SystemException {

		resourceLocalService.addResources(
			fileShortcut.getCompanyId(), fileShortcut.getGroupId(),
			fileShortcut.getUserId(), DLFileShortcut.class.getName(),
			fileShortcut.getFileShortcutId(), false, addGroupPermissions,
			addGuestPermissions);
	}

	@Override
	public void addFileShortcutResources(
			DLFileShortcut fileShortcut, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException, SystemException {

		resourceLocalService.addModelResources(
			fileShortcut.getCompanyId(), fileShortcut.getGroupId(),
			fileShortcut.getUserId(), DLFileShortcut.class.getName(),
			fileShortcut.getFileShortcutId(), groupPermissions,
			guestPermissions);
	}

	@Override
	public void addFileShortcutResources(
			long fileShortcutId, boolean addGroupPermissions,
			boolean addGuestPermissions)
		throws PortalException, SystemException {

		DLFileShortcut fileShortcut =
			dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);

		addFileShortcutResources(
			fileShortcut, addGroupPermissions, addGuestPermissions);
	}

	@Override
	public void addFileShortcutResources(
			long fileShortcutId, String[] groupPermissions,
			String[] guestPermissions)
		throws PortalException, SystemException {

		DLFileShortcut fileShortcut =
			dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);

		addFileShortcutResources(
			fileShortcut, groupPermissions, guestPermissions);
	}

	@Override
	public void deleteFileShortcut(DLFileShortcut fileShortcut)
		throws PortalException, SystemException {

		// File shortcut

		dlFileShortcutPersistence.remove(fileShortcut);

		// Resources

		resourceLocalService.deleteResource(
			fileShortcut.getCompanyId(), DLFileShortcut.class.getName(),
			ResourceConstants.SCOPE_INDIVIDUAL,
			fileShortcut.getFileShortcutId());

		// Asset

		assetEntryLocalService.deleteEntry(
			DLFileShortcut.class.getName(), fileShortcut.getFileShortcutId());

		// Trash

		trashEntryLocalService.deleteEntry(
			DLFileShortcut.class.getName(), fileShortcut.getFileShortcutId());
	}

	@Override
	public void deleteFileShortcut(long fileShortcutId)
		throws PortalException, SystemException {

		DLFileShortcut fileShortcut =
			dlFileShortcutLocalService.getDLFileShortcut(fileShortcutId);

		deleteFileShortcut(fileShortcut);
	}

	@Override
	public void deleteFileShortcuts(long toFileEntryId)
		throws PortalException, SystemException {

		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByToFileEntryId(toFileEntryId);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			deleteFileShortcut(fileShortcut);
		}
	}

	@Override
	public void deleteFileShortcuts(long groupId, long folderId)
		throws PortalException, SystemException {

		deleteFileShortcuts(groupId, folderId, true);
	}

	@Override
	public void deleteFileShortcuts(
			long groupId, long folderId, boolean includeTrashedEntries)
		throws PortalException, SystemException {

		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByG_F(groupId, folderId);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			if (includeTrashedEntries || !fileShortcut.isInTrash()) {
				deleteFileShortcut(fileShortcut);
			}
		}
	}

	@Override
	public void disableFileShortcuts(long toFileEntryId)
		throws SystemException {

		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByToFileEntryId(toFileEntryId);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			fileShortcut.setActive(false);

			dlFileShortcutPersistence.update(fileShortcut);
		}
	}

	@Override
	public void enableFileShortcuts(long toFileEntryId) throws SystemException {
		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByToFileEntryId(toFileEntryId);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			fileShortcut.setActive(true);

			dlFileShortcutPersistence.update(fileShortcut);
		}
	}

	@Override
	public DLFileShortcut getFileShortcut(long fileShortcutId)
		throws PortalException, SystemException {

		return dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);
	}

	@Override
	public List<DLFileShortcut> getFileShortcuts(
			long groupId, long folderId, boolean active, int status, int start,
			int end)
		throws SystemException {

		return dlFileShortcutPersistence.findByG_F_A_S(
			groupId, folderId, active, status, start, end);
	}

	@Override
	public int getFileShortcutsCount(
			long groupId, long folderId, boolean active, int status)
		throws SystemException {

		return dlFileShortcutPersistence.countByG_F_A_S(
			groupId, folderId, active, status);
	}

	@Override
	public DLFileShortcut moveFileShortcutToTrash(
			long userId, DLFileShortcut dlFileShortcut,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		TrashEntry trashEntry = (TrashEntry)serviceContext.getAttribute(
			TrashConstants.TRASH_ENTRY);

		if (!trashEntry.isTrashEntry(dlFileShortcut) &&
			!dlFileShortcut.isApproved()) {

			trashVersionLocalService.addTrashVersion(
				trashEntry.getEntryId(), DLFileEntryConstants.getClassName(),
				dlFileShortcut.getFileShortcutId(), dlFileShortcut.getStatus());
		}

		dlFileShortcutLocalService.updateStatus(
			userId, dlFileShortcut.getFileShortcutId(),
			WorkflowConstants.STATUS_IN_TRASH, serviceContext);

		return dlFileShortcut;
	}

	@Override
	public DLFileShortcut moveFileShortcutToTrash(
			long userId, long dlFileShortcutId)
		throws PortalException, SystemException {

		DLFileShortcut dlFileShortcut =
			dlFileShortcutPersistence.findByPrimaryKey(dlFileShortcutId);

		// Trash

		int oldStatus = dlFileShortcut.getStatus();

		if (oldStatus == WorkflowConstants.STATUS_PENDING) {
			oldStatus = WorkflowConstants.STATUS_DRAFT;
		}

		String title = dlFileShortcut.getToTitle();

		UnicodeProperties typeSettingsProperties = new UnicodeProperties();

		typeSettingsProperties.put("title", title);

		TrashEntry trashEntry = trashEntryLocalService.addTrashEntry(
			userId, dlFileShortcut.getGroupId(), DLFileShortcut.class.getName(),
			dlFileShortcut.getFileShortcutId(), dlFileShortcut.getUuid(), null,
			oldStatus, null, null);

		ServiceContext trashContext = new ServiceContext();

		trashContext.setAttribute(TrashConstants.TRASH_ENTRY, trashEntry);

		dlFileShortcut = moveFileShortcutToTrash(
			userId, dlFileShortcut, trashContext);

		// Social

		JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject();

		extraDataJSONObject.put("title", title);

		socialActivityLocalService.addActivity(
			userId, dlFileShortcut.getGroupId(), DLFileShortcut.class.getName(),
			dlFileShortcut.getFileShortcutId(),
			SocialActivityConstants.TYPE_MOVE_TO_TRASH,
			extraDataJSONObject.toString(), 0);

		return dlFileShortcut;
	}

	@Override
	public void rebuildTree(long companyId)
		throws PortalException, SystemException {

		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByC_NotS(
				companyId, WorkflowConstants.STATUS_IN_TRASH);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			if (fileShortcut.isInTrashContainer()) {
				continue;
			}

			fileShortcut.setTreePath(fileShortcut.buildTreePath());

			dlFileShortcutPersistence.update(fileShortcut);
		}
	}

	@Override
	public void restoreFileShortcutFromTrash(
			long userId, DLFileShortcut dlFileShortcut, int status,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		dlFileShortcutLocalService.updateStatus(
			userId, dlFileShortcut.getFileShortcutId(), status, serviceContext);
	}

	@Override
	public void restoreFileShortcutFromTrash(long userId, long dlFileShortcutId)
		throws PortalException, SystemException {

		// File shortcut

		DLFileShortcut dlFileShortcut = dlAppService.getFileShortcut(
			dlFileShortcutId);

		TrashEntry trashEntry = dlFileShortcut.getTrashEntry();

		ServiceContext trashContext = new ServiceContext();

		trashContext.setAttribute(TrashConstants.TRASH_ENTRY, trashEntry);

		int status = WorkflowConstants.STATUS_APPROVED;

		if (trashEntry.isTrashEntry(dlFileShortcut)) {
			status = trashEntry.getStatus();
		}
		else {
			TrashVersion trashVersion =
				trashVersionLocalService.fetchVersion(
					trashEntry.getEntryId(), DLFileShortcut.class.getName(),
					dlFileShortcut.getFileShortcutId());

			if (trashVersion != null) {
				status = trashVersion.getStatus();
			}
		}

		restoreFileShortcutFromTrash(
			userId, dlFileShortcut, status, trashContext);

		if (trashEntry.isTrashEntry(dlFileShortcut)) {

			// Social

			JSONObject extraDataJSONObject = JSONFactoryUtil.createJSONObject();

			extraDataJSONObject.put("title", dlFileShortcut.getToTitle());

			socialActivityLocalService.addActivity(
				userId, dlFileShortcut.getGroupId(),
				DLFileShortcut.class.getName(),
				dlFileShortcut.getFileShortcutId(),
				SocialActivityConstants.TYPE_RESTORE_FROM_TRASH,
				extraDataJSONObject.toString(), 0);
		}

		// Trash

		trashEntryLocalService.deleteEntry(trashEntry.getEntryId());
	}

	@Override
	public void updateAsset(
			long userId, DLFileShortcut fileShortcut, long[] assetCategoryIds,
			String[] assetTagNames)
		throws PortalException, SystemException {

		FileEntry fileEntry = dlAppLocalService.getFileEntry(
			fileShortcut.getToFileEntryId());

		assetEntryLocalService.updateEntry(
			userId, fileShortcut.getGroupId(), fileShortcut.getCreateDate(),
			fileShortcut.getModifiedDate(), DLFileShortcut.class.getName(),
			fileShortcut.getFileShortcutId(), fileShortcut.getUuid(), 0,
			assetCategoryIds, assetTagNames, false, null, null, null,
			fileEntry.getMimeType(), fileEntry.getTitle(),
			fileEntry.getDescription(), null, null, null, 0, 0, null, false);
	}

	@Override
	public DLFileShortcut updateFileShortcut(
			long userId, long fileShortcutId, long folderId, long toFileEntryId,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		// File shortcut

		User user = userPersistence.findByPrimaryKey(userId);

		DLFileShortcut fileShortcut =
			dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);

		validate(user, toFileEntryId);

		fileShortcut.setModifiedDate(
			serviceContext.getModifiedDate(new Date()));
		fileShortcut.setFolderId(folderId);
		fileShortcut.setToFileEntryId(toFileEntryId);
		fileShortcut.setTreePath(fileShortcut.buildTreePath());

		dlFileShortcutPersistence.update(fileShortcut);

		// Folder

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
			DLFolder dlFolder = dlFolderPersistence.findByPrimaryKey(folderId);

			dlFolder.setLastPostDate(fileShortcut.getModifiedDate());

			dlFolderPersistence.update(dlFolder);
		}

		// Asset

		FileEntry fileEntry = dlAppLocalService.getFileEntry(toFileEntryId);

		copyAssetTags(fileEntry, serviceContext);

		updateAsset(
			userId, fileShortcut, serviceContext.getAssetCategoryIds(),
			serviceContext.getAssetTagNames());

		return fileShortcut;
	}

	@Override
	public void updateFileShortcuts(
			long oldToFileEntryId, long newToFileEntryId)
		throws SystemException {

		List<DLFileShortcut> fileShortcuts =
			dlFileShortcutPersistence.findByToFileEntryId(oldToFileEntryId);

		for (DLFileShortcut fileShortcut : fileShortcuts) {
			fileShortcut.setToFileEntryId(newToFileEntryId);

			dlFileShortcutPersistence.update(fileShortcut);
		}
	}

	@Override
	public void updateStatus(
			long userId, long fileShortcutId, int status,
			ServiceContext serviceContext)
		throws PortalException, SystemException {

		User user = userPersistence.findByPrimaryKey(userId);

		DLFileShortcut fileShortcut =
			dlFileShortcutPersistence.findByPrimaryKey(fileShortcutId);

		fileShortcut.setStatus(status);
		fileShortcut.setStatusByUserId(user.getUserId());
		fileShortcut.setStatusByUserName(user.getFullName());
		fileShortcut.setStatusDate(serviceContext.getModifiedDate(new Date()));

		dlFileShortcutPersistence.update(fileShortcut);
	}

	protected void copyAssetTags(
			FileEntry fileEntry, ServiceContext serviceContext)
		throws PortalException, SystemException {

		String[] assetTagNames = assetTagLocalService.getTagNames(
			FileEntry.class.getName(), fileEntry.getFileEntryId());

		assetTagLocalService.checkTags(
			serviceContext.getUserId(), serviceContext.getScopeGroupId(),
			assetTagNames);

		serviceContext.setAssetTagNames(assetTagNames);
	}

	protected long getFolderId(long companyId, long folderId)
		throws SystemException {

		if (folderId != DLFolderConstants.DEFAULT_PARENT_FOLDER_ID) {

			// Ensure folder exists and belongs to the proper company

			DLFolder dlFolder = dlFolderPersistence.fetchByPrimaryKey(folderId);

			if ((dlFolder == null) || (companyId != dlFolder.getCompanyId())) {
				folderId = DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
			}
		}

		return folderId;
	}

	protected void validate(User user, long toFileEntryId)
		throws PortalException, SystemException {

		FileEntry fileEntry = dlAppLocalService.getFileEntry(toFileEntryId);

		if (user.getCompanyId() != fileEntry.getCompanyId()) {
			throw new NoSuchFileEntryException();
		}
	}

}
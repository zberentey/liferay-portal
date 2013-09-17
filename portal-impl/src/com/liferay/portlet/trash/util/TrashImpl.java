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

package com.liferay.portlet.trash.util;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Hits;
import com.liferay.portal.kernel.security.pacl.DoPrivileged;
import com.liferay.portal.kernel.trash.TrashConstants;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.trash.TrashRenderer;
import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.PrefsPropsUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.ContainerModel;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.permission.PortletPermissionUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portal.util.PortletKeys;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.util.WebKeys;
import com.liferay.portlet.documentlibrary.store.DLStoreUtil;
import com.liferay.portlet.trash.model.TrashEntry;
import com.liferay.portlet.trash.model.TrashVersion;
import com.liferay.portlet.trash.model.impl.TrashEntryImpl;
import com.liferay.portlet.trash.service.TrashEntryLocalServiceUtil;
import com.liferay.portlet.trash.service.TrashVersionLocalServiceUtil;
import com.liferay.portlet.trash.util.comparator.EntryCreateDateComparator;
import com.liferay.portlet.trash.util.comparator.EntryTypeComparator;
import com.liferay.portlet.trash.util.comparator.EntryUserNameComparator;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletRequest;
import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Sergio González
 * @author Julio Camarero
 */
@DoPrivileged
public class TrashImpl implements Trash {

	@Override
	public void addBaseModelBreadcrumbEntries(
			HttpServletRequest request, String className, long classPK,
			PortletURL containerModelURL)
		throws PortalException, SystemException {

		addBreadcrumbEntries(
			request, className, classPK, "classPK", containerModelURL);
	}

	@Override
	public void addContainerModelBreadcrumbEntries(
			HttpServletRequest request, String className, long classPK,
			PortletURL containerModelURL)
		throws PortalException, SystemException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		String rootContainerModelName = LanguageUtil.get(
			themeDisplay.getLocale(), trashHandler.getRootContainerModelName());

		if (classPK == 0) {
			PortalUtil.addPortletBreadcrumbEntry(
				request, rootContainerModelName, null);

			return;
		}

		containerModelURL.setParameter("containerModelId", "0");

		PortalUtil.addPortletBreadcrumbEntry(
			request, rootContainerModelName, containerModelURL.toString());

		addBreadcrumbEntries(
			request, className, classPK, "containerModelId", containerModelURL);
	}

	@Override
	public void addDependentStatus(
			long trashEntryId, ServiceContext serviceContext)
		throws SystemException {

		Map<String, Map<Long, Integer>> dependentStatusMaps =
			(Map<String, Map<Long, Integer>>)serviceContext.getAttribute(
				TrashConstants.TRASH_DEPENDENT_STATUS_MAP);

		if (dependentStatusMaps == null) {
			dependentStatusMaps = new HashMap<String, Map<Long, Integer>>();

			serviceContext.setAttribute(
				TrashConstants.TRASH_DEPENDENT_STATUS_MAP,
				(HashMap<String, Map<Long, Integer>>)dependentStatusMaps);
		}

		List<TrashVersion> trashVersions =
			TrashVersionLocalServiceUtil.getVersions(trashEntryId);

		for (TrashVersion trashVersion : trashVersions) {
			Map<Long, Integer> dependentStatusMap = dependentStatusMaps.get(
				trashVersion.getClassName());

			if (dependentStatusMap == null) {
				dependentStatusMap = new HashMap<Long, Integer>();

				dependentStatusMaps.put(
					trashVersion.getClassName(), dependentStatusMap);
			}

			dependentStatusMap.put(
				trashVersion.getClassPK(), trashVersion.getStatus());
		}
	}

	@Override
	public void addDependentStatus(
			long trashEntryId, String className, ServiceContext serviceContext)
		throws SystemException {

		Map<String, Map<Long, Integer>> dependentStatusMaps =
			(Map<String, Map<Long, Integer>>)serviceContext.getAttribute(
				TrashConstants.TRASH_DEPENDENT_STATUS_MAP);

		if (dependentStatusMaps == null) {
			dependentStatusMaps = new HashMap<String, Map<Long, Integer>>();

			serviceContext.setAttribute(
				TrashConstants.TRASH_DEPENDENT_STATUS_MAP,
				(HashMap<String, Map<Long, Integer>>)dependentStatusMaps);
		}

		Map<Long, Integer> dependentStatusMap = getDependentStatusMap(
			trashEntryId, className);

		dependentStatusMaps.put(className, dependentStatusMap);
	}

	@Override
	public void addTrashEntries(
			long groupId, String className, ServiceContext serviceContext)
		throws SystemException {

		HashSet<Long> trashEntryClassPKs = new HashSet<Long>();

		List<TrashEntry> trashEntries = TrashEntryLocalServiceUtil.getEntries(
			groupId, className);

		if (trashEntries.isEmpty()) {
			return;
		}

		for (TrashEntry trashEntry : trashEntries) {
			trashEntryClassPKs.add(trashEntry.getClassPK());
		}

		Map<String, Set<Long>> trashEntryClassPKMap =
			(Map<String, Set<Long>>)serviceContext.getAttribute(
				TrashConstants.TRASH_ENTRY_CLASS_PK_MAP);

		if (trashEntryClassPKMap == null) {
			trashEntryClassPKMap = new HashMap<String, Set<Long>>();

			serviceContext.setAttribute(
				TrashConstants.TRASH_ENTRY_CLASS_PK_MAP,
				(HashMap<String, Set<Long>>)trashEntryClassPKMap);
		}

		trashEntryClassPKMap.put(className, trashEntryClassPKs);
	}

	@Override
	public void deleteEntriesAttachments(
			long companyId, long repositoryId, Date date,
			String[] attachmentFileNames)
		throws PortalException, SystemException {

		for (String attachmentFileName : attachmentFileNames) {
			String trashTime = TrashUtil.getTrashTime(
				attachmentFileName, TRASH_TIME_SEPARATOR);

			long timestamp = GetterUtil.getLong(trashTime);

			if (timestamp < date.getTime()) {
				DLStoreUtil.deleteDirectory(
					companyId, repositoryId, attachmentFileName);
			}
		}
	}

	@Override
	public int getDependentStatus(
		String className, long classPK, ServiceContext serviceContext) {

		Map<String, Map<Long, Integer>> dependentStatusMaps =
			(Map<String, Map<Long, Integer>>)serviceContext.getAttribute(
				TrashConstants.TRASH_DEPENDENT_STATUS_MAP);

		if (dependentStatusMaps == null) {
			return WorkflowConstants.STATUS_APPROVED;
		}

		Map<Long, Integer> dependentStatusMap = dependentStatusMaps.get(
			className);

		if (dependentStatusMap == null) {
			return WorkflowConstants.STATUS_APPROVED;
		}

		Integer status = dependentStatusMap.get(classPK);

		if (status == null) {
			return WorkflowConstants.STATUS_APPROVED;
		}

		return status;
	}

	@Override
	public List<TrashEntry> getEntries(Hits hits) {
		List<TrashEntry> entries = new ArrayList<TrashEntry>();

		for (Document document : hits.getDocs()) {
			String entryClassName = GetterUtil.getString(
				document.get(Field.ENTRY_CLASS_NAME));
			long classPK = GetterUtil.getLong(
				document.get(Field.ENTRY_CLASS_PK));

			try {
				TrashEntry entry = TrashEntryLocalServiceUtil.fetchEntry(
					entryClassName, classPK);

				if (entry == null) {
					String userName = GetterUtil.getString(
						document.get(Field.REMOVED_BY_USER_NAME));

					Date removedDate = document.getDate(Field.REMOVED_DATE);

					entry = new TrashEntryImpl();

					entry.setClassName(entryClassName);
					entry.setClassPK(classPK);

					entry.setUserName(userName);
					entry.setCreateDate(removedDate);

					String rootEntryClassName = GetterUtil.getString(
						document.get(Field.ROOT_ENTRY_CLASS_NAME));
					long rootEntryClassPK = GetterUtil.getLong(
						document.get(Field.ROOT_ENTRY_CLASS_PK));

					TrashEntry rootTrashEntry =
						TrashEntryLocalServiceUtil.fetchEntry(
							rootEntryClassName, rootEntryClassPK);

					if (rootTrashEntry != null) {
						entry.setRootEntry(rootTrashEntry);
					}
				}

				entries.add(entry);
			}
			catch (Exception e) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						"Unable to find trash entry for " + entryClassName +
							" with primary key " + classPK);
				}
			}
		}

		return entries;
	}

	@Override
	public OrderByComparator getEntryOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator orderByComparator = null;

		if (orderByCol.equals("removed-by")) {
			orderByComparator = new EntryUserNameComparator(orderByAsc);
		}
		else if (orderByCol.equals("removed-date")) {
			orderByComparator = new EntryCreateDateComparator(orderByAsc);
		}
		else if (orderByCol.equals("type")) {
			orderByComparator = new EntryTypeComparator(orderByAsc);
		}

		return orderByComparator;
	}

	@Override
	public int getMaxAge(Group group) throws PortalException, SystemException {
		if (group.isLayout()) {
			group = group.getParentGroup();
		}

		int trashEntriesMaxAge = PrefsPropsUtil.getInteger(
			group.getCompanyId(), PropsKeys.TRASH_ENTRIES_MAX_AGE,
			PropsValues.TRASH_ENTRIES_MAX_AGE);

		UnicodeProperties typeSettingsProperties =
			group.getTypeSettingsProperties();

		return GetterUtil.getInteger(
			typeSettingsProperties.getProperty("trashEntriesMaxAge"),
			trashEntriesMaxAge);
	}

	@Override
	public String getNewName(String oldName, String token) {
		StringBundler sb = new StringBundler(3);

		sb.append(oldName);
		sb.append(StringPool.SPACE);
		sb.append(token);

		return sb.toString();
	}

	@Override
	public String getNewName(
			ThemeDisplay themeDisplay, String className, long classPK,
			String oldName)
		throws PortalException, SystemException {

		TrashRenderer trashRenderer = null;

		if (Validator.isNotNull(className) && (classPK > 0)) {
			TrashHandler trashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(className);

			trashRenderer = trashHandler.getTrashRenderer(classPK);
		}

		Format dateFormatDateTime = FastDateFormatFactoryUtil.getDateTime(
			themeDisplay.getLocale(), themeDisplay.getTimeZone());

		StringBundler sb = new StringBundler(3);

		sb.append(StringPool.OPEN_PARENTHESIS);
		sb.append(
			StringUtil.replace(
				dateFormatDateTime.format(new Date()), CharPool.SLASH,
				CharPool.PERIOD));
		sb.append(StringPool.CLOSE_PARENTHESIS);

		if (trashRenderer != null) {
			return trashRenderer.getNewName(oldName, sb.toString());
		}
		else {
			return getNewName(oldName, sb.toString());
		}
	}

	@Override
	public String getOriginalTitle(String title) {
		return getOriginalTitle(title, StringPool.SLASH);
	}

	@Override
	public String getTrashTime(String title, String separator) {
		int index = title.lastIndexOf(separator);

		if (index < 0) {
			return StringPool.BLANK;
		}

		return title.substring(index + 1, title.length());
	}

	@Override
	public String getTrashTitle(long trashEntryId) {
		return getTrashTitle(trashEntryId, StringPool.SLASH);
	}

	@Override
	public PortletURL getViewContentURL(
			HttpServletRequest request, String className, long classPK)
		throws PortalException, SystemException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		if (!themeDisplay.isSignedIn() ||
			!isTrashEnabled(themeDisplay.getScopeGroupId()) ||
			!PortletPermissionUtil.hasControlPanelAccessPermission(
				themeDisplay.getPermissionChecker(),
				themeDisplay.getScopeGroupId(), PortletKeys.TRASH)) {

			return null;
		}

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		if (trashHandler.isInTrashContainer(classPK)) {
			TrashEntry trashEntry = trashHandler.getTrashEntry(classPK);

			className = trashEntry.getClassName();
			classPK = trashEntry.getClassPK();

			trashHandler = TrashHandlerRegistryUtil.getTrashHandler(className);
		}

		TrashRenderer trashRenderer = trashHandler.getTrashRenderer(classPK);

		if (trashRenderer == null) {
			return null;
		}

		Layout layout = themeDisplay.getLayout();

		PortletURL portletURL = PortalUtil.getControlPanelPortletURL(
			request, PortletKeys.TRASH, layout.getLayoutId(),
			PortletRequest.RENDER_PHASE);

		portletURL.setParameter("struts_action", "/trash/view_content");
		portletURL.setParameter("redirect", themeDisplay.getURLCurrent());

		TrashEntry trashEntry = TrashEntryLocalServiceUtil.getEntry(
			className, classPK);

		if (trashEntry.getRootEntry() != null) {
			portletURL.setParameter("className", className);
			portletURL.setParameter("classPK", String.valueOf(classPK));
		}
		else {
			portletURL.setParameter(
				"trashEntryId", String.valueOf(trashEntry.getEntryId()));
		}

		portletURL.setParameter("type", trashRenderer.getType());
		portletURL.setParameter("showActions", Boolean.FALSE.toString());
		portletURL.setParameter("showAssetMetadata", Boolean.TRUE.toString());
		portletURL.setParameter("showEditURL", Boolean.FALSE.toString());

		return portletURL;
	}

	@Override
	public boolean hasTrashEntry(
		String className, long classPK, ServiceContext serviceContext) {

		Map<String, Set<Long>> trashEntryClassPKMap =
			(Map<String, Set<Long>>)serviceContext.getAttribute(
				TrashConstants.TRASH_ENTRY_CLASS_PK_MAP);

		if (trashEntryClassPKMap == null) {
			return false;
		}

		Set<Long> trashEntryClassPKs = trashEntryClassPKMap.get(className);

		if (trashEntryClassPKs == null) {
			trashEntryClassPKs = new HashSet<Long>();

			trashEntryClassPKMap.put(className, trashEntryClassPKs);
		}

		return trashEntryClassPKs.contains(classPK);
	}

	@Override
	public boolean isInTrash(String className, long classPK)
		throws PortalException, SystemException {

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		if (trashHandler == null) {
			return false;
		}

		if (trashHandler.isInTrash(classPK) ||
			trashHandler.isInTrashContainer(classPK)) {

			return true;
		}

		return false;
	}

	@Override
	public boolean isTrashEnabled(long groupId)
		throws PortalException, SystemException {

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		UnicodeProperties typeSettingsProperties =
			group.getParentLiveGroupTypeSettingsProperties();

		boolean companyTrashEnabled = PrefsPropsUtil.getBoolean(
			group.getCompanyId(), PropsKeys.TRASH_ENABLED);

		if (!companyTrashEnabled) {
			return false;
		}

		return GetterUtil.getBoolean(
			typeSettingsProperties.getProperty("trashEnabled"), true);
	}

	protected void addBreadcrumbEntries(
			HttpServletRequest request, String className, long classPK,
			String paramName, PortletURL containerModelURL)
		throws PortalException, SystemException {

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			className);

		List<ContainerModel> containerModels =
			trashHandler.getParentContainerModels(classPK);

		Collections.reverse(containerModels);

		containerModelURL.setParameter("struts_action", "/trash/view");

		PortalUtil.addPortletBreadcrumbEntry(
			request, LanguageUtil.get(themeDisplay.getLocale(), "recycle-bin"),
			containerModelURL.toString());

		for (ContainerModel containerModel : containerModels) {
			TrashHandler containerModelTrashHandler =
				TrashHandlerRegistryUtil.getTrashHandler(
					containerModel.getModelClassName());

			if (!containerModelTrashHandler.isInTrash(
					containerModel.getContainerModelId()) &&
				!containerModelTrashHandler.isInTrashContainer(
					containerModel.getContainerModelId())) {

				continue;
			}

			containerModelURL.setParameter(
				"struts_action", "/trash/view_content");

			containerModelURL.setParameter(
				paramName,
				String.valueOf(containerModel.getContainerModelId()));

			String name = containerModel.getContainerModelName();

			if (containerModelTrashHandler.isInTrash(
					containerModel.getContainerModelId())) {

				name = TrashUtil.getOriginalTitle(name);
			}

			PortalUtil.addPortletBreadcrumbEntry(
				request, name, containerModelURL.toString());
		}

		TrashRenderer trashRenderer = trashHandler.getTrashRenderer(classPK);

		PortalUtil.addPortletBreadcrumbEntry(
			request, trashRenderer.getTitle(themeDisplay.getLocale()), null);
	}

	protected Map<Long, Integer> getDependentStatusMap(
			long entryId, String className)
		throws SystemException {

		List<TrashVersion> versions = TrashVersionLocalServiceUtil.getVersions(
			entryId, className);

		Map<Long, Integer> dependentStatusMap = new HashMap<Long, Integer>();

		for (TrashVersion version : versions) {
			dependentStatusMap.put(version.getClassPK(), version.getStatus());
		}

		return dependentStatusMap;
	}

	protected String getOriginalTitle(String title, String prefix) {
		if (!title.startsWith(prefix)) {
			return title;
		}

		title = title.substring(prefix.length());

		long trashEntryId = GetterUtil.getLong(title);

		if (trashEntryId <= 0) {
			return title;
		}

		try {
			TrashEntry trashEntry = TrashEntryLocalServiceUtil.getEntry(
				trashEntryId);

			title = trashEntry.getTypeSettingsProperty("title");
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug("No trash entry exists with ID " + trashEntryId);
			}
		}

		return title;
	}

	protected String getTrashTitle(long trashEntryId, String prefix) {
		return prefix.concat(String.valueOf(trashEntryId));
	}

	private static Log _log = LogFactoryUtil.getLog(TrashImpl.class);

}
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

package com.liferay.portal.kernel.systemevents;

import com.liferay.counter.service.CounterLocalServiceUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.AutoResetThreadLocal;
import com.liferay.portal.model.SystemEventConstants;
import com.liferay.portal.util.PortalUtil;

import java.util.Stack;

/**
 * @author Zsolt Berentey
 */
public class SystemEventHierarchyEntryThreadLocal {

	public static void clear() {
		Stack<SystemEventHierarchyEntry> systemEventHierarchyEntries =
			_systemEventHierarchyEntries.get();

		systemEventHierarchyEntries.clear();
	}

	public static SystemEventHierarchyEntry peek() {
		Stack<SystemEventHierarchyEntry> systemEventHierarchyEntries =
			_systemEventHierarchyEntries.get();

		if (systemEventHierarchyEntries.isEmpty()) {
			return null;
		}

		return systemEventHierarchyEntries.peek();
	}

	public static SystemEventHierarchyEntry pop() {
		Stack<SystemEventHierarchyEntry> systemEventHierarchyEntries =
			_systemEventHierarchyEntries.get();

		if (systemEventHierarchyEntries.isEmpty()) {
			return null;
		}

		return systemEventHierarchyEntries.pop();
	}

	public static void push() throws SystemException {
		push(SystemEventConstants.ACTION_SKIP);
	}

	public static void push(Class<?> clazz, long classPK)
		throws SystemException {

		push(
			PortalUtil.getClassNameId(clazz), classPK,
			SystemEventConstants.ACTION_SKIP);
	}

	public static void push(Class<?> clazz, long classPK, int action)
		throws SystemException {

		push(PortalUtil.getClassNameId(clazz), classPK, action);
	}

	public static void push(int action) throws SystemException {
		push(0, 0, action);
	}

	public static void push(long classNameId, long classPK, int action)
		throws SystemException {

		Stack<SystemEventHierarchyEntry> systemEventHierarchyEntries =
			_systemEventHierarchyEntries.get();

		SystemEventHierarchyEntry parentEntry = null;

		if (!systemEventHierarchyEntries.isEmpty()) {
			parentEntry = systemEventHierarchyEntries.peek();
		}

		long eventSetId;
		long parentEventId = 0;

		if (parentEntry == null) {
			eventSetId = CounterLocalServiceUtil.increment();
		}
		else if (parentEntry.getAction() == SystemEventConstants.ACTION_SKIP) {
			return;
		}
		else {
			eventSetId = parentEntry.getEventSetId();
			parentEventId = parentEntry.getEventId();
		}

		long eventId = CounterLocalServiceUtil.increment();

		systemEventHierarchyEntries.push(
			new SystemEventHierarchyEntry(
				classNameId, classPK, eventSetId, eventId, parentEventId,
				action));
	}

	private static ThreadLocal<Stack<SystemEventHierarchyEntry>>
		_systemEventHierarchyEntries =
			new AutoResetThreadLocal<Stack<SystemEventHierarchyEntry>>(
				SystemEventHierarchyEntryThreadLocal.class +
					"._systemEventHierarchyEntries",
				new Stack<SystemEventHierarchyEntry>());

}
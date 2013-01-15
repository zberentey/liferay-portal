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

package com.liferay.portal.kernel.util;

import com.liferay.portal.kernel.security.pacl.permission.PortalRuntimePermission;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutSet;
import com.liferay.portal.theme.ThemeDisplay;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Gergely Mathe
 */
public class BreadcrumbUtil {

	public static void buildGuestGroupBreadcrumb(
		ThemeDisplay themeDisplay, StringBundler sb)
	throws Exception {

		getBreadcrumb().buildGuestGroupBreadcrumb(themeDisplay, sb);
	}

	public static void buildLayoutBreadcrumb(
			Layout selLayout, String selLayoutParam, boolean selectedLayout,
			PortletURL portletURL, ThemeDisplay themeDisplay, StringBundler sb)
		throws Exception {

		getBreadcrumb().buildLayoutBreadcrumb(
			selLayout, selLayoutParam, selectedLayout, portletURL, themeDisplay,
			sb);
	}

	public static void buildParentGroupsBreadcrumb(
			LayoutSet layoutSet, PortletURL portletURL,
			ThemeDisplay themeDisplay, StringBundler sb)
		throws Exception {

		getBreadcrumb().buildParentGroupsBreadcrumb(
			layoutSet, portletURL, themeDisplay, sb);
	}

	public static void buildPortletBreadcrumb(
			HttpServletRequest request, boolean showCurrentGroup,
			boolean showCurrentPortlet, ThemeDisplay themeDisplay,
			StringBundler sb)
		throws Exception {

		getBreadcrumb().buildPortletBreadcrumb(
			request, showCurrentGroup, showCurrentPortlet, themeDisplay, sb);
	}

	public static Breadcrumb getBreadcrumb() {
		PortalRuntimePermission.checkGetBeanProperty(BreadcrumbUtil.class);

		return _breadcrumb;
	}

	public static String insertClassOption(String breadcrumbString) {
		return getBreadcrumb().insertClassOption(breadcrumbString);
	}

	public static String removeLastClass(String breadcrumbs) {
		return getBreadcrumb().removeLastClass(breadcrumbs);
	}

	public void setBreadcrumb(Breadcrumb breadcrumb) {
		PortalRuntimePermission.checkSetBeanProperty(getClass());

		_breadcrumb = breadcrumb;
	}

	private static Breadcrumb _breadcrumb;

}
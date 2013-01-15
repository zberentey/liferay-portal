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

import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutSet;
import com.liferay.portal.theme.ThemeDisplay;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Gergely Mathe
 */
public interface Breadcrumb {

	public void buildGuestGroupBreadcrumb(
			ThemeDisplay themeDisplay, StringBundler sb)
		throws Exception;

	public void buildLayoutBreadcrumb(
			Layout selLayout, String selLayoutParam, boolean selectedLayout,
			PortletURL portletURL, ThemeDisplay themeDisplay, StringBundler sb)
		throws Exception;

	public void buildParentGroupsBreadcrumb(
			LayoutSet layoutSet, PortletURL portletURL,
			ThemeDisplay themeDisplay, StringBundler sb)
		throws Exception;

	public void buildPortletBreadcrumb(
			HttpServletRequest request, boolean showCurrentGroup,
			boolean showCurrentPortlet, ThemeDisplay themeDisplay,
			StringBundler sb)
		throws Exception;

	public String insertClassOption(String breadcrumbString);

	public String removeLastClass(String breadcrumbs);

}
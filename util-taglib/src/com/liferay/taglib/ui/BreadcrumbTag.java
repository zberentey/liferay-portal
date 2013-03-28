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

package com.liferay.taglib.ui;

import com.liferay.portal.kernel.util.BreadcrumbUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringBundler;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.Layout;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.taglib.util.IncludeTag;

import javax.portlet.PortletURL;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class BreadcrumbTag extends IncludeTag {

	public void setDisplayStyle(String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setPortletURL(PortletURL portletURL) {
		_portletURL = portletURL;
	}

	public void setSelLayout(Layout selLayout) {
		_selLayout = selLayout;
	}

	public void setSelLayoutParam(String selLayoutParam) {
		_selLayoutParam = selLayoutParam;
	}

	public void setShowCurrentGroup(boolean showCurrentGroup) {
		_showCurrentGroup = showCurrentGroup;
	}

	public void setShowCurrentPortlet(boolean showCurrentPortlet) {
		_showCurrentPortlet = showCurrentPortlet;
	}

	public void setShowGuestGroup(boolean showGuestGroup) {
		_showGuestGroup = showGuestGroup;
	}

	public void setShowLayout(boolean showLayout) {
		_showLayout = showLayout;
	}

	public void setShowParentGroups(boolean showParentGroups) {
		_showParentGroups = showParentGroups;
	}

	public void setShowPortletBreadcrumb(boolean showPortletBreadcrumb) {
		_showPortletBreadcrumb = showPortletBreadcrumb;
	}

	@Override
	protected void cleanUp() {
		_displayStyle = _DISPLAY_STYLE;
		_portletURL = null;
		_selLayout = null;
		_selLayoutParam = null;
		_showCurrentGroup = true;
		_showCurrentPortlet = true;
		_showGuestGroup = _SHOW_GUEST_GROUP;
		_showLayout = true;
		_showParentGroups = _SHOW_PARENT_GROUPS;
		_showPortletBreadcrumb = true;
	}

	protected String getBreadcrumbString(HttpServletRequest request) {
		StringBundler sb = new StringBundler();

		ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			if (Validator.isNull(_selLayout)) {
				setSelLayout(themeDisplay.getLayout());
			}

			Group group = _selLayout.getGroup();

			boolean showLayout = _showLayout && !group.isLayoutPrototype();

			if (_showGuestGroup) {
				BreadcrumbUtil.buildGuestGroupBreadcrumb(themeDisplay, sb);
			}

			if (_showParentGroups) {
				BreadcrumbUtil.buildParentGroupsBreadcrumb(
					_selLayout.getLayoutSet(), _portletURL, themeDisplay, sb);
			}

			if (showLayout) {
				BreadcrumbUtil.buildLayoutBreadcrumb(
					_selLayout, _selLayoutParam, true, _portletURL,
					themeDisplay, sb);
			}

			if (_showPortletBreadcrumb) {
				BreadcrumbUtil.buildPortletBreadcrumb(
					request, _showCurrentGroup, _showCurrentPortlet,
					themeDisplay, sb);
			}
		}
		catch (Exception e) {
		}

		String breadcrumbString = BreadcrumbUtil.insertClassOption(
			sb.toString());

		return breadcrumbString;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		request.setAttribute(
			"liferay-ui:breadcrumb:breadcrumbString",
			getBreadcrumbString(request));
		request.setAttribute(
			"liferay-ui:breadcrumb:displayStyle", _displayStyle);
	}

	private static final String _DISPLAY_STYLE = "0";

	private static final String _PAGE = "/html/taglib/ui/breadcrumb/page.jsp";

	private static final boolean _SHOW_GUEST_GROUP = GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.BREADCRUMB_SHOW_GUEST_GROUP));

	private static final boolean _SHOW_PARENT_GROUPS = GetterUtil.getBoolean(
		PropsUtil.get(PropsKeys.BREADCRUMB_SHOW_PARENT_GROUPS));

	private String _displayStyle = _DISPLAY_STYLE;
	private PortletURL _portletURL;
	private Layout _selLayout;
	private String _selLayoutParam;
	private boolean _showCurrentGroup = true;
	private boolean _showCurrentPortlet = true;
	private boolean _showGuestGroup = _SHOW_GUEST_GROUP;
	private boolean _showLayout = true;
	private boolean _showParentGroups = _SHOW_PARENT_GROUPS;
	private boolean _showPortletBreadcrumb = true;

}
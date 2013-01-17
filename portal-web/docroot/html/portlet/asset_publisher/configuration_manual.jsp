<%--
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
--%>

<%@ include file="/html/portlet/asset_publisher/init.jsp" %>

<%
List<AssetRendererFactory> classTypesAssetRendererFactories = (List<AssetRendererFactory>)request.getAttribute("configuration.jsp-classTypesAssetRendererFactories");
PortletURL configurationRenderURL = (PortletURL)request.getAttribute("configuration.jsp-configurationRenderURL");
String redirect = (String)request.getAttribute("configuration.jsp-redirect");
String rootPortletId = (String)request.getAttribute("configuration.jsp-rootPortletId");
String selectScope = (String)request.getAttribute("configuration.jsp-selectScope");
%>

<liferay-ui:panel-container extended="<%= true %>" id="assetPublisherSelectionStylePanelContainer" persistState="<%= true %>">
	<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="assetPublisherSelectionStylePanel" persistState="<%= true %>" title="selection">
		<aui:fieldset label="scope">
			<%= selectScope %>
		</aui:fieldset>

		<aui:fieldset label="model.resource.com.liferay.portlet.asset">

			<%
			List<String> deletedAssets = new ArrayList<String>();

			List<String> headerNames = new ArrayList<String>();

			headerNames.add("title");
			headerNames.add("type");
			headerNames.add("modified-date");
			headerNames.add(StringPool.BLANK);

			SearchContainer searchContainer = new SearchContainer(renderRequest, new DisplayTerms(renderRequest), new DisplayTerms(renderRequest), SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, configurationRenderURL, headerNames, LanguageUtil.get(pageContext, "no-assets-selected"));

			int total = assetEntryXmls.length;

			searchContainer.setTotal(total);

			List results = ListUtil.fromArray(assetEntryXmls);

			int end = (assetEntryXmls.length < searchContainer.getEnd()) ? assetEntryXmls.length : searchContainer.getEnd();

			results = results.subList(searchContainer.getStart(), end);

			searchContainer.setResults(results);

			List resultRows = searchContainer.getResultRows();

			for (int i = 0; i < results.size(); i++) {
				String assetEntryXml = (String)results.get(i);

				Document doc = SAXReaderUtil.read(assetEntryXml);

				Element root = doc.getRootElement();

				int assetEntryOrder = searchContainer.getStart() + i;

				DocUtil.add(root, "asset-order", assetEntryOrder);

				if (assetEntryOrder == (total - 1)) {
					DocUtil.add(root, "last", true);
				}
				else {
					DocUtil.add(root, "last", false);
				}

				String assetEntryClassName = root.element("asset-entry-type").getText();
				String assetEntryUuid = root.element("asset-entry-uuid").getText();

				AssetEntry assetEntry = null;

				boolean deleteAssetEntry = true;

				for (long groupId : groupIds) {
					try {
						assetEntry = AssetEntryLocalServiceUtil.getEntry(groupId, assetEntryUuid);

						assetEntry = assetEntry.toEscapedModel();

						deleteAssetEntry = false;
					}
					catch (NoSuchEntryException nsee) {
					}
				}

				if (deleteAssetEntry) {
					deletedAssets.add(assetEntryUuid);

					continue;
				}

				ResultRow row = new ResultRow(doc, null, assetEntryOrder);

				// Title

				AssetRendererFactory assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(assetEntry.getClassName());

				AssetRenderer assetRenderer = assetRendererFactory.getAssetRenderer(assetEntry.getClassPK());

				StringBundler sb = new StringBundler(4);

				sb.append("<img alt=\"\" src=\"");
				sb.append(assetRenderer.getIconPath(renderRequest));
				sb.append("\" />");
				sb.append(HtmlUtil.escape(assetRenderer.getTitle(locale)));

				row.addText(sb.toString());

				// Type

				row.addText(assetRendererFactory.getTypeName(locale, false));

				// Modified Date

				Date modifiedDate = assetEntry.getModifiedDate();

				row.addText(LanguageUtil.format(pageContext, "x-ago", LanguageUtil.getTimeDescription(pageContext, System.currentTimeMillis() - modifiedDate.getTime(), true)));

				// Action

				row.addJSP("right", SearchEntry.DEFAULT_VALIGN, "/html/portlet/asset_publisher/asset_selection_action.jsp");

				// Add result row

				resultRows.add(row);
			}

			AssetPublisherUtil.removeAndStoreSelection(deletedAssets, preferences);
			%>

			<c:if test="<%= !deletedAssets.isEmpty() %>">
				<div class="portlet-msg-info">
					<liferay-ui:message key="the-selected-assets-have-been-removed-from-the-list-because-they-do-not-belong-in-the-scope-of-this-portlet" />
				</div>
			</c:if>

			<liferay-ui:search-iterator paginate="<%= total > SearchContainer.DEFAULT_DELTA %>" searchContainer="<%= searchContainer %>" />

			<%
			classNameIds = availableClassNameIds;

			String portletId = portletResource;

			for (long groupId : groupIds) {
			%>

				<div class="select-asset-selector">
					<div class="lfr-meta-actions edit-controls">
						<liferay-ui:icon-menu align="left" cssClass="select-existing-selector" icon='<%= themeDisplay.getPathThemeImages() + "/common/add.png" %>' message='<%= LanguageUtil.format(pageContext, (groupIds.length == 1) ? "select" : "select-in-x", new Object[] {HtmlUtil.escape((GroupLocalServiceUtil.getGroup(groupId)).getDescriptiveName(locale))}) %>' showWhenSingleIcon="<%= true %>">

							<%
							PortletURL assetBrowserURL = PortletURLFactoryUtil.create(request, PortletKeys.ASSET_BROWSER, PortalUtil.getControlPanelPlid(company.getCompanyId()), PortletRequest.RENDER_PHASE);

							assetBrowserURL.setParameter("struts_action", "/asset_browser/view");
							assetBrowserURL.setParameter("groupId", String.valueOf(groupId));
							assetBrowserURL.setParameter("callback", liferayPortletResponse.getNamespace() + "selectAsset");
							assetBrowserURL.setPortletMode(PortletMode.VIEW);
							assetBrowserURL.setWindowState(LiferayWindowState.POP_UP);

							for (AssetRendererFactory curRendererFactory : AssetRendererFactoryRegistryUtil.getAssetRendererFactories()) {
								if (!curRendererFactory.isSelectable()) {
									continue;
								}

								assetBrowserURL.setParameter("typeSelection", curRendererFactory.getClassName());

								String taglibURL = "javascript:Liferay.Util.openWindow({dialog: {width: 960}, id: '" + liferayPortletResponse.getNamespace() + "selectAsset', title: '" + LanguageUtil.format(pageContext, "select-x", curRendererFactory.getTypeName(locale, false)) + "', uri:'" + HtmlUtil.escapeURL(assetBrowserURL.toString()) + "'});";
							%>

								<liferay-ui:icon message="<%= curRendererFactory.getTypeName(locale, false) %>" src="<%= curRendererFactory.getIconPath(renderRequest) %>" url="<%= taglibURL %>" />

							<%
							}
							%>

						</liferay-ui:icon-menu>
					</div>
				</div>

			<%
			}
			%>

		</aui:fieldset>
	</liferay-ui:panel>
	<liferay-ui:panel collapsible="<%= true %>" extended="<%= true %>" id="assetPublisherSelectionDisplaySettingsPanel" persistState="<%= true %>" title="display-settings">
		<%@ include file="/html/portlet/asset_publisher/display_settings.jspf" %>
	</liferay-ui:panel>
</liferay-ui:panel-container>

<aui:button-row>
	<aui:button onClick='<%= renderResponse.getNamespace() + "saveSelectBoxes();" %>' type="submit" />
</aui:button-row>
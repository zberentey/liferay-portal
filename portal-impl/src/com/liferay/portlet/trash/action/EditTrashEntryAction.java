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

package com.liferay.portlet.trash.action;

import com.liferay.portal.kernel.servlet.SessionErrors;
import com.liferay.portal.kernel.servlet.SessionMessages;
import com.liferay.portal.kernel.trash.TrashHandler;
import com.liferay.portal.kernel.trash.TrashHandlerRegistryUtil;
import com.liferay.portal.kernel.util.Constants;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.struts.PortletAction;
import com.liferay.portlet.trash.model.TrashEntry;
import com.liferay.portlet.trash.service.TrashEntryLocalServiceUtil;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * @author Manuel de la Peña
 */
public class EditTrashEntryAction extends PortletAction {

	@Override
	public void processAction(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			ActionRequest actionRequest, ActionResponse actionResponse)
		throws Exception {

		String cmd = ParamUtil.getString(actionRequest, Constants.CMD);

		String key = StringPool.BLANK;

		try {
			if (cmd.equals(Constants.DELETE)) {
				deleteTrashEntries(actionRequest);

				key = "delete_entries";
			}
			else if (cmd.equals(Constants.RESTORE)) {
				restoreTrashEntries(actionRequest);

				key = "restore_entries";
			}

			SessionMessages.add(actionRequest, key);
		}
		catch (Exception e) {
			SessionErrors.add(actionRequest, e.getClass().getName());

			return;
		}
	}

	@Override
	public ActionForward render(
			ActionMapping mapping, ActionForm form, PortletConfig portletConfig,
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws Exception {

		return mapping.findForward(
			getForward(renderRequest, "portlet.trash.view"));
	}

	private TrashHandler _getTrashHandler(long trashEntryId) throws Exception {
		TrashEntry trashEntry = TrashEntryLocalServiceUtil.getTrashEntry(
			trashEntryId);

		TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(
			trashEntry.getClassName());

		return trashHandler;
	}

	private void deleteTrashEntry(long trashEntryId) throws Exception {
		TrashHandler trashHandler = _getTrashHandler(trashEntryId);

		trashHandler.deleteTrashEntry(trashEntryId);
	}

	private void deleteTrashEntries(ActionRequest actionRequest)
		throws Exception {

		long trashEntryId = ParamUtil.getLong(actionRequest, "p_t_i_d");

		if (trashEntryId > 0) {
			deleteTrashEntry(trashEntryId);
		}
		else {
			long[] deleteTrashEntryIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "trashEntriesPrimaryKeys"), 0L);

			for (int i = 0; i < deleteTrashEntryIds.length; i++) {
				deleteTrashEntry(deleteTrashEntryIds[i]);
			}
		}
	}

	private void restoreTrashEntry(long trashEntryId) throws Exception {
		TrashHandler trashHandler = _getTrashHandler(trashEntryId);

		trashHandler.restoreTrashEntry(trashEntryId);
	}

	private void restoreTrashEntries(ActionRequest actionRequest)
		throws Exception {

		long trashEntryId = ParamUtil.getLong(actionRequest, "p_t_i_d");

		if (trashEntryId > 0) {
			restoreTrashEntry(trashEntryId);
		}
		else {
			long[] restoreTrashEntryIds = StringUtil.split(
				ParamUtil.getString(
					actionRequest, "trashEntriesPrimaryKeys"), 0L);

			for (int i = 0; i < restoreTrashEntryIds.length; i++) {
				restoreTrashEntry(restoreTrashEntryIds[i]);
			}
		}
	}

}
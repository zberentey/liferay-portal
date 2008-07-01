/**
 * Copyright (c) 2000-2008 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portal.kernel.portlet;

import java.io.IOException;

import javax.portlet.GenericPortlet;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;

/**
 * <a href="LiferayPortlet.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class LiferayPortlet extends GenericPortlet {

	protected void doDispatch(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		WindowState state = renderRequest.getWindowState();

		if (!state.equals(WindowState.MINIMIZED)) {
			PortletMode mode = renderRequest.getPortletMode();

			if (mode.equals(PortletMode.VIEW)) {
				doView(renderRequest, renderResponse);
			}
			else if (mode.equals(LiferayPortletMode.ABOUT)) {
				doAbout(renderRequest, renderResponse);
			}
			else if (mode.equals(LiferayPortletMode.CONFIG)) {
				doConfig(renderRequest, renderResponse);
			}
			else if (mode.equals(PortletMode.EDIT)) {
				doEdit(renderRequest, renderResponse);
			}
			else if (mode.equals(LiferayPortletMode.EDIT_DEFAULTS)) {
				doEditDefaults(renderRequest, renderResponse);
			}
			else if (mode.equals(LiferayPortletMode.EDIT_GUEST)) {
				doEditGuest(renderRequest, renderResponse);
			}
			else if (mode.equals(PortletMode.HELP)) {
				doHelp(renderRequest, renderResponse);
			}
			else if (mode.equals(LiferayPortletMode.PREVIEW)) {
				doPreview(renderRequest, renderResponse);
			}
			else if (mode.equals(LiferayPortletMode.PRINT)) {
				doPrint(renderRequest, renderResponse);
			}
			else {
				throw new PortletException(mode.toString());
			}
		}
	}

	protected void doAbout(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doAbout method not implemented");
	}

	protected void doConfig(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doConfig method not implemented");
	}

	protected void doEditDefaults(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doEditDefaults method not implemented");
	}

	protected void doEditGuest(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doEditGuest method not implemented");
	}

	protected void doPreview(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doPreview method not implemented");
	}

	protected void doPrint(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		throw new PortletException("doPrint method not implemented");
	}

}
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

package com.liferay.portal.servlet.filters.sso.ntlm;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.security.ldap.PortalLDAPUtil;
import com.liferay.portal.util.PortalInstances;
import com.liferay.portal.util.PrefsPropsUtil;
import com.liferay.portal.util.PropsKeys;
import com.liferay.portal.util.WebKeys;
import com.liferay.util.servlet.filters.DynamicFilterConfig;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import jcifs.Config;
import jcifs.UniAddress;

import jcifs.http.NtlmHttpFilter;
import jcifs.http.NtlmSsp;

import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbSession;

import jcifs.util.Base64;

/**
 * <a href="NtlmFilter.java.html"><b><i>View Source</i></b></a>
 *
 * @author Bruno Farache
 * @author Marcus Schmidke
 *
 */
public class NtlmFilter extends NtlmHttpFilter {

	public void init(FilterConfig config) throws ServletException {
		super.init(config);

		_config = new DynamicFilterConfig(config);
	}

	public void doFilter(
			ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain)
		throws IOException, ServletException {

		try {
			HttpServletRequest request = (HttpServletRequest)servletRequest;
			HttpServletResponse response = (HttpServletResponse)servletResponse;

			long companyId = PortalInstances.getCompanyId(request);

			if (PortalLDAPUtil.isNtlmEnabled(companyId)) {
				if ((_config.getInitParameter("jcifs.http.domainController")
						== null) &&
					(_config.getInitParameter("jcifs.smb.client.domain")
						== null)) {

					String domainController = PrefsPropsUtil.getString(
						companyId, PropsKeys.NTLM_DOMAIN_CONTROLLER);

					String domain = PrefsPropsUtil.getString(
						companyId, PropsKeys.NTLM_DOMAIN);

					_config.addInitParameter(
						"jcifs.http.domainController", domainController);

					_config.addInitParameter(
						"jcifs.smb.client.domain", domain);

					super.init(_config);

					if (_log.isDebugEnabled()) {
						_log.debug("Host " + domainController);
						_log.debug("Domain " + domain);
					}
				}

				// Type 1 NTLM requests from browser can (and should) always
				// immediately be replied to with an Type 2 NTLM response, no
				// matter whether we're yet logging in or whether it is much
				// later in the session.

				String msg = request.getHeader("Authorization");

				if (msg != null && msg.startsWith("NTLM")) {
					byte[] src = Base64.decode(msg.substring(5));

					if (src[8] == 1) {
						UniAddress dc = UniAddress.getByName(
							Config.getProperty("jcifs.http.domainController"),
							true);

						byte[] challenge = SmbSession.getChallenge(dc);

						Type1Message type1 = new Type1Message(src);
						Type2Message type2 = new Type2Message(
							type1, challenge, null);

						msg = Base64.encode(type2.toByteArray());

						response.setHeader("WWW-Authenticate", "NTLM " + msg);
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
						response.setContentLength(0);

						response.flushBuffer();

						// Interrupt filter chain, send response. Browser will
						// immediately post a new request.

						return;
					}
				}

				String path = request.getPathInfo();

				if (path != null && path.endsWith("/login")) {
					NtlmPasswordAuthentication ntlm = negotiate(
						request, response, false);

					if (ntlm == null) {
						return;
					}

					String remoteUser = ntlm.getName();

					int pos = remoteUser.indexOf(StringPool.BACK_SLASH);

					if (pos != -1) {
						remoteUser = remoteUser.substring(pos + 1);
					}

					if (_log.isDebugEnabled()) {
						_log.debug("NTLM remote user " + remoteUser);
					}

					servletRequest.setAttribute(
						WebKeys.NTLM_REMOTE_USER, remoteUser);
				}
			}
		}
		catch (Exception e) {
			_log.error(e);
		}

		chain.doFilter(servletRequest, servletResponse);
	}

	public NtlmPasswordAuthentication negotiate(
			HttpServletRequest request, HttpServletResponse response,
			boolean skipAuthentication)
		throws IOException, ServletException {

		NtlmPasswordAuthentication ntlm = null;

		HttpSession session = request.getSession(false);

		String authorizationHeader = request.getHeader("Authorization");

		if (_log.isDebugEnabled()) {
			_log.debug("Authorization header " + authorizationHeader);
		}

		if ((authorizationHeader != null) && (
			(authorizationHeader.startsWith("NTLM ")))) {

			String domainController = Config.getProperty(
				"jcifs.http.domainController");

			UniAddress uniAddress = UniAddress.getByName(
				domainController, true);

			if (_log.isDebugEnabled()) {
				_log.debug("Address " + uniAddress);
			}

			byte[] challenge = SmbSession.getChallenge(uniAddress);

			ntlm = NtlmSsp.authenticate(request, response, challenge);

			session.setAttribute("NtlmHttpAuth", ntlm);
		}
		else {
			if (session != null) {
				ntlm = (NtlmPasswordAuthentication)session.getAttribute(
					"NtlmHttpAuth");
			}

			if (ntlm == null) {
				response.setHeader("WWW-Authenticate", "NTLM");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentLength(0);

				response.flushBuffer();

				return null;
			}
		}

		if (_log.isDebugEnabled()) {
			_log.debug("Password authentication " + ntlm);
		}

		return ntlm;
	}

	private static Log _log = LogFactoryUtil.getLog(NtlmFilter.class);

	private DynamicFilterConfig _config;

}
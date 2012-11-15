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

package com.liferay.portal.kernel.security.pacl.permission;

import java.security.BasicPermission;

/**
 * @author Raymond Augé
 */
public class CheckMemberAccessPermission extends BasicPermission {

	public CheckMemberAccessPermission(
		String name, ClassLoader callerClassLoader,
		Class<?> subject, ClassLoader subjectClassLoader,
		int publicOrDeclared) {

		super(name);

		_callerClassLoader = callerClassLoader;
		_subject = subject;
		_subjectClassLoader = subjectClassLoader;
		_publicOrDeclared = publicOrDeclared;
	}

	public boolean canAccess() {
		if ((_callerClassLoader == null) ||
			(_subjectClassLoader == null) ||
			(_subjectClassLoader == _callerClassLoader)) {

			return true;
		}

		return false;
	}

	public int getPublicOrDeclared() {
		return _publicOrDeclared;
	}

	public Class<?> getSubject() {
		return _subject;
	}

	private ClassLoader _callerClassLoader;
	private int _publicOrDeclared;
	private Class<?> _subject;
	private ClassLoader _subjectClassLoader;

}
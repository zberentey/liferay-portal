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

package com.liferay.portlet.social.service;

import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceTestUtil;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.social.model.SocialActivity;
import com.liferay.portlet.social.model.SocialActivityCounter;
import com.liferay.portlet.social.model.SocialActivityCounterConstants;
import com.liferay.portlet.social.model.SocialActivityLimit;
import com.liferay.portlet.social.model.impl.SocialActivityImpl;
import com.liferay.portlet.social.util.SocialConfigurationUtil;

import java.io.InputStream;

/**
 * @author Zsolt Berentey
 * @author Manuel de la Peña
 */
public class BaseSocialActivityTestCase {

	public void setUp() throws Exception {
		_userClassNameId = PortalUtil.getClassNameId(User.class.getName());

		Class<?> clazz = SocialActivitySettingLocalServiceTest.class;

		InputStream inputStream = clazz.getResourceAsStream(
			"dependencies/liferay-social.xml");

		String xml = new String(FileUtil.getBytes(inputStream));

		SocialConfigurationUtil.read(
			clazz.getClassLoader(), new String[] {xml});
	}

	protected void addAsset() throws Exception {
		_assetEntry = AssetEntryLocalServiceUtil.updateEntry(
			_creatorUser.getUserId(), _group.getGroupId(), TEST_MODEL, 1, null,
			null);
	}

	protected void addGroup() throws Exception {
		_group = ServiceTestUtil.addGroup();
	}

	protected void addUsers() throws Exception {
		_actorUser = ServiceTestUtil.addUser(
			"actor", false, new long[] {_group.getGroupId()});

		_creatorUser = ServiceTestUtil.addUser(
			"creator", false, new long[] {_group.getGroupId()});
	}

	protected SocialActivity addActivity(User user, int type) {
		SocialActivity activity = new SocialActivityImpl();

		activity.setAssetEntry(_assetEntry);
		activity.setClassNameId(_assetEntry.getClassNameId());
		activity.setClassPK(_assetEntry.getClassPK());
		activity.setCompanyId(_group.getCompanyId());
		activity.setGroupId(_group.getGroupId());
		activity.setType(type);
		activity.setUserId(user.getUserId());
		activity.setUserUuid(user.getUuid());

		return activity;
	}

	protected SocialActivityCounter getActivityCounter(
			String name, Object owner)
		throws Exception {

		long classNameId = 0;
		long classPk = 0;
		int ownerType = SocialActivityCounterConstants.TYPE_ACTOR;

		if (owner instanceof User) {
			classNameId = _userClassNameId;
			classPk = ((User)owner).getUserId();
		}
		else if (owner instanceof AssetEntry) {
			classNameId = ((AssetEntry)owner).getClassNameId();
			classPk = ((AssetEntry)owner).getClassPK();
			ownerType = SocialActivityCounterConstants.TYPE_ASSET;
		}

		if (name.equals(SocialActivityCounterConstants.NAME_CONTRIBUTION)) {
			ownerType = SocialActivityCounterConstants.TYPE_CREATOR;
		}

		return
			SocialActivityCounterLocalServiceUtil.fetchLatestActivityCounter(
				_group.getGroupId(), classNameId, classPk, name, ownerType);
	}

	protected SocialActivityLimit getActivityLimit(
			User user, AssetEntry assetEntry, int activityType,
			String activityCounterName)
		throws Exception {

		long classPK = assetEntry.getClassPK();

		if (activityCounterName.equals(
				SocialActivityCounterConstants.NAME_PARTICIPATION)) {

			classPK = 0;
		}

		return SocialActivityLimitLocalServiceUtil.fetchActivityLimit(
			_group.getGroupId(), user.getUserId(), _assetEntry.getClassNameId(),
			classPK, activityType, activityCounterName);
	}

	protected static final String TEST_MODEL = "test-model";

	protected User _actorUser;
	protected AssetEntry _assetEntry;
	protected User _creatorUser;
	protected Group _group;
	protected long _userClassNameId;

}
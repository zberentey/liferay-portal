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

package com.liferay.portlet.social.service.impl;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.lock.LockProtectedAction;
import com.liferay.portal.util.PropsValues;
import com.liferay.portlet.social.model.SocialActivity;
import com.liferay.portlet.social.model.SocialActivitySet;
import com.liferay.portlet.social.service.base.SocialActivitySetLocalServiceBaseImpl;
import com.liferay.portlet.social.util.comparator.SocialActivitySetModifiedDateComparator;

import java.util.List;

/**
 * @author Matthew Kong
 * @author Zsolt Berentey
 */
public class SocialActivitySetLocalServiceImpl
	extends SocialActivitySetLocalServiceBaseImpl {

	@Override
	public SocialActivitySet addActivitySet(long activityId)
		throws PortalException, SystemException {

		// Activity set

		SocialActivity activity = socialActivityPersistence.findByPrimaryKey(
			activityId);

		SocialActivitySet activitySet = createActivitySet(activity, false);

		// Activity

		activity.setActivitySetId(activitySet.getActivitySetId());

		socialActivityPersistence.update(activity);

		return activitySet;
	}

	@Override
	public SocialActivitySet addAssetActivitySet(final SocialActivity activity)
		throws PortalException, SystemException {

		String lockKey = StringUtil.merge(
			new Object[] {activity.getClassName(), activity.getClassPK(), 0},
			StringPool.POUND);

		LockProtectedAction<SocialActivitySet> protectedAction =
			new LockProtectedAction<SocialActivitySet>(
				SocialActivitySet.class.getName(), lockKey,
				PropsValues.SOCIAL_ACTIVITY_LOCK_TIMEOUT,
				PropsValues.SOCIAL_ACTIVITY_LOCK_RETRY_DELAY) {

			@Override
			protected SocialActivitySet performProtectedAction()
				throws SystemException {

				SocialActivitySet activitySet =
					socialActivitySetPersistence.fetchByC_C_T_First(
						activity.getClassNameId(), activity.getClassPK(), 0,
						null);

				if (activitySet == null) {
					activitySet =
						socialActivitySetLocalService.createActivitySet(
							activity, true);

					socialActivitySetPersistence.cacheResult(activitySet);
				}

				return activitySet;
			}
		};

		protectedAction.performAction();

		return protectedAction.getReturnValue();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public SocialActivitySet createActivitySet(
			SocialActivity activity, boolean assetActivitySet)
		throws SystemException {

		long activitySetId = counterLocalService.increment();

		SocialActivitySet activitySet = socialActivitySetPersistence.create(
			activitySetId);

		activitySet.setGroupId(activity.getGroupId());
		activitySet.setCompanyId(activity.getCompanyId());
		activitySet.setUserId(activity.getUserId());
		activitySet.setCreateDate(activity.getCreateDate());
		activitySet.setModifiedDate(activity.getCreateDate());
		activitySet.setClassName(activity.getClassName());
		activitySet.setClassPK(activity.getClassPK());

		if (!assetActivitySet) {
			activitySet.setType(activity.getType());
		}

		activitySet.setActivityCount(1);

		socialActivitySetPersistence.update(activitySet);

		return activitySet;
	}

	@Override
	public void decrementActivityCount(long activitySetId)
		throws PortalException, SystemException {

		if (activitySetId == 0) {
			return;
		}

		SocialActivitySet activitySet =
			socialActivitySetPersistence.findByPrimaryKey(activitySetId);

		if (activitySet.getActivityCount() == 1) {
			socialActivitySetPersistence.remove(activitySetId);

			return;
		}

		activitySet.setActivityCount(activitySet.getActivityCount() - 1);

		socialActivitySetPersistence.update(activitySet);
	}

	@Override
	public void decrementActivityCount(long classNameId, long classPK)
		throws PortalException, SystemException {

		List<SocialActivity> activities = socialActivityPersistence.findByC_C(
			classNameId, classPK);

		for (SocialActivity activity : activities) {
			decrementActivityCount(activity.getActivitySetId());
		}
	}

	@Override
	public SocialActivitySet fetchAssetActivitySet(
			long classNameId, long classPK)
		throws SystemException {

		return socialActivitySetPersistence.fetchByC_C_T_First(
			classNameId, classPK, 0, null);
	}

	@Override
	public SocialActivitySet getClassActivitySet(
			long classNameId, long classPK, int type)
		throws SystemException {

		return socialActivitySetPersistence.fetchByC_C_T_First(
			classNameId, classPK, type,
			new SocialActivitySetModifiedDateComparator());
	}

	@Override
	public SocialActivitySet getClassActivitySet(
			long userId, long classNameId, long classPK, int type)
		throws SystemException {

		return socialActivitySetPersistence.fetchByU_C_C_T_First(
			userId, classNameId, classPK, type,
			new SocialActivitySetModifiedDateComparator());
	}

	@Override
	public List<SocialActivitySet> getGroupActivitySets(
			long groupId, int start, int end)
		throws SystemException {

		return socialActivitySetPersistence.findByGroupId(
			groupId, start, end, new SocialActivitySetModifiedDateComparator());
	}

	@Override
	public int getGroupActivitySetsCount(long groupId) throws SystemException {
		return socialActivitySetPersistence.countByGroupId(groupId);
	}

	@Override
	public List<SocialActivitySet> getRelationActivitySets(
			long userId, int start, int end)
		throws SystemException {

		return socialActivitySetFinder.findByRelation(userId, start, end);
	}

	@Override
	public List<SocialActivitySet> getRelationActivitySets(
			long userId, int type, int start, int end)
		throws SystemException {

		return socialActivitySetFinder.findByRelationType(
			userId, type, start, end);
	}

	@Override
	public int getRelationActivitySetsCount(long userId)
		throws SystemException {

		return socialActivitySetFinder.countByRelation(userId);
	}

	@Override
	public int getRelationActivitySetsCount(long userId, int type)
		throws SystemException {

		return socialActivitySetFinder.countByRelationType(userId, type);
	}

	@Override
	public SocialActivitySet getUserActivitySet(
			long groupId, long userId, int type)
		throws SystemException {

		return socialActivitySetPersistence.fetchByG_U_T_First(
			groupId, userId, type,
			new SocialActivitySetModifiedDateComparator());
	}

	@Override
	public List<SocialActivitySet> getUserActivitySets(
			long userId, int start, int end)
		throws SystemException {

		return socialActivitySetFinder.findByUser(userId, start, end);
	}

	@Override
	public int getUserActivitySetsCount(long userId) throws SystemException {
		return socialActivitySetFinder.countByUser(userId);
	}

	@Override
	public List<SocialActivitySet> getUserGroupsActivitySets(
			long userId, int start, int end)
		throws SystemException {

		return socialActivitySetFinder.findByUserGroups(userId, start, end);
	}

	@Override
	public int getUserGroupsActivitySetsCount(long userId)
		throws SystemException {

		return socialActivitySetFinder.countByUserGroups(userId);
	}

	@Override
	public void incrementActivityCount(long activitySetId, long activityId)
		throws PortalException, SystemException {

		// Activity set

		SocialActivitySet activitySet =
			socialActivitySetPersistence.findByPrimaryKey(activitySetId);

		SocialActivity activity = socialActivityPersistence.findByPrimaryKey(
			activityId);

		activitySet.setModifiedDate(activity.getCreateDate());

		activitySet.setActivityCount(activitySet.getActivityCount() + 1);

		socialActivitySetPersistence.update(activitySet);

		// Activity

		activity.setActivitySetId(activitySetId);

		socialActivityPersistence.update(activity);
	}

}
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

package com.liferay.portlet.messageboards.lar;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lar.StagedModelType;
import com.liferay.portal.kernel.test.ExecutionTestListeners;
import com.liferay.portal.lar.BaseStagedModelDataHandlerTestCase;
import com.liferay.portal.model.Group;
import com.liferay.portal.model.StagedModel;
import com.liferay.portal.test.LiferayIntegrationJUnitTestRunner;
import com.liferay.portal.test.MainServletExecutionTestListener;
import com.liferay.portal.test.TransactionalExecutionTestListener;
import com.liferay.portlet.messageboards.model.MBCategory;
import com.liferay.portlet.messageboards.service.MBCategoryLocalServiceUtil;
import com.liferay.portlet.messageboards.util.MBTestUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.runner.RunWith;

/**
 * @author Daniel Kocsis
 */
@ExecutionTestListeners(
	listeners = {
		MainServletExecutionTestListener.class,
		TransactionalExecutionTestListener.class
	})
@RunWith(LiferayIntegrationJUnitTestRunner.class)
public class MBCategoryStagedModelDataHandlerTest
	extends BaseStagedModelDataHandlerTestCase {

	@Override
	protected Map<String, List<StagedModel>> addDependentStagedModelsMap(
			Group group)
		throws Exception {

		Map<String, List<StagedModel>> dependentStagedModelsMap =
			new HashMap<String, List<StagedModel>>();

		MBCategory category = MBTestUtil.addCategory(group.getGroupId());

		addDependentStagedModel(
			dependentStagedModelsMap, MBCategory.class, category);

		return dependentStagedModelsMap;
	}

	@Override
	protected StagedModel addStagedModel(
			Group group,
			Map<String, List<StagedModel>> dependentStagedModelsMap)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			MBCategory.class.getSimpleName());

		MBCategory category = (MBCategory)dependentStagedModels.get(0);

		return MBTestUtil.addCategory(
			group.getGroupId(), category.getCategoryId());
	}

	@Override
	protected void deleteStagedModel(
			StagedModel stagedModel,
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		MBCategoryLocalServiceUtil.deleteCategory((MBCategory)stagedModel);

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			MBCategory.class.getSimpleName());

		MBCategory category = (MBCategory)dependentStagedModels.get(0);

		MBCategoryLocalServiceUtil.deleteCategory(category);
	}

	@Override
	protected StagedModelType[] getDeletionSystemEventStagedModelTypes() {
		MBPortletDataHandler mbPortletDataHandler = new MBPortletDataHandler();

		return mbPortletDataHandler.getDeletionSystemEventStagedModelTypes();
	}

	@Override
	protected StagedModel getStagedModel(String uuid, Group group)
		throws SystemException {

		return MBCategoryLocalServiceUtil.fetchMBCategoryByUuidAndGroupId(
			uuid, group.getGroupId());
	}

	@Override
	protected Class<? extends StagedModel> getStagedModelClass() {
		return MBCategory.class;
	}

	@Override
	protected void validateDeletion(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			MBCategory.class.getSimpleName());

		Assert.assertEquals(1, dependentStagedModels.size());

		MBCategory category = (MBCategory)dependentStagedModels.get(0);

		category = (MBCategory)getStagedModel(category.getUuid(), group);

		Assert.assertNull("Not Deleted: " + MBCategory.class, category);
	}

	@Override
	protected void validateImport(
			Map<String, List<StagedModel>> dependentStagedModelsMap,
			Group group)
		throws Exception {

		List<StagedModel> dependentStagedModels = dependentStagedModelsMap.get(
			MBCategory.class.getSimpleName());

		Assert.assertEquals(1, dependentStagedModels.size());

		MBCategory category = (MBCategory)dependentStagedModels.get(0);

		MBCategoryLocalServiceUtil.getMBCategoryByUuidAndGroupId(
			category.getUuid(), group.getGroupId());
	}

}
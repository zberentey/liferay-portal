/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.dynamicdatamapping;

import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.MainServletTestRule;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.dynamicdatamapping.model.DDMForm;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormField;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormLayout;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormLayoutColumn;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormLayoutPage;
import com.liferay.portlet.dynamicdatamapping.model.DDMFormLayoutRow;
import com.liferay.portlet.dynamicdatamapping.model.LocalizedValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Leonardo Barros
 */
public class DDMStructureManagerUtilTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), MainServletTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_classNameId = PortalUtil.getClassNameId(
			"com.liferay.dynamic.data.lists.model.DDLRecordSet");

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			_group.getGroupId(), TestPropsValues.getUserId());
	}

	@Test
	public void testAddStructure() throws Exception {
		DDMStructure structure = addStructure();

		Assert.assertNotNull(structure);
	}

	@Test
	public void testDeleteStructure() throws Exception {
		DDMStructure structure = addStructure();

		Assert.assertNotNull(structure);

		DDMStructureManagerUtil.deleteStructure(structure.getStructureId());

		structure = DDMStructureManagerUtil.fetchStructure(
			structure.getGroupId(), structure.getClassNameId(),
			structure.getStructureKey());

		Assert.assertNull(structure);
	}

	@Test
	public void testFetchStructure() throws Exception {
		DDMStructure expectedStructure = addStructure();

		DDMStructure actualStructure = DDMStructureManagerUtil.fetchStructure(
			_group.getGroupId(), expectedStructure.getClassNameId(),
			expectedStructure.getStructureKey());

		Assert.assertNotNull(actualStructure);

		assertEquals(expectedStructure, actualStructure);
	}

	@Test
	public void testFetchStructureByUuidAndGroupId() throws Exception {
		DDMStructure expectedStructure = addStructure();

		DDMStructure actualStructure =
			DDMStructureManagerUtil.fetchStructureByUuidAndGroupId(
				expectedStructure.getUuid(), expectedStructure.getGroupId());

		Assert.assertNotNull(actualStructure);

		assertEquals(expectedStructure, actualStructure);
	}

	@Test
	public void testGetClassStructures() throws Exception {
		List<DDMStructure> structures =
			DDMStructureManagerUtil.getClassStructures(
				_group.getCompanyId(), _classNameId, QueryUtil.ALL_POS,
				QueryUtil.ALL_POS);

		int initialSize = structures.size();

		addStructure();

		structures = DDMStructureManagerUtil.getClassStructures(
			_group.getCompanyId(), _classNameId, QueryUtil.ALL_POS,
			QueryUtil.ALL_POS);

		Assert.assertEquals(initialSize + 1, structures.size());
	}

	@Test
	public void testGetStructure() throws Exception {
		DDMStructure expectedStructure = addStructure();

		DDMStructure actualStructure = DDMStructureManagerUtil.getStructure(
			_group.getGroupId(), expectedStructure.getClassNameId(),
			expectedStructure.getStructureKey());

		assertEquals(expectedStructure, actualStructure);
	}

	@Test
	public void testGetStructureById() throws Exception {
		DDMStructure expectedStructure = addStructure();

		DDMStructure actualStructure = DDMStructureManagerUtil.getStructure(
			expectedStructure.getStructureId());

		assertEquals(expectedStructure, actualStructure);
	}

	@Test
	public void testGetStructureByUuidAndGroupId() throws Exception {
		DDMStructure expectedStructure = addStructure();

		DDMStructure actualStructure =
			DDMStructureManagerUtil.getStructureByUuidAndGroupId(
				expectedStructure.getUuid(), _group.getGroupId());

		assertEquals(expectedStructure, actualStructure);
	}

	@Test
	public void testUpdateStructure() throws Exception {
		DDMStructure expectedStructure = addStructure();

		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.US, "Structure Name Modified");

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(LocaleUtil.US, "Structure Description Modified");

		DDMStructure actualStructure = DDMStructureManagerUtil.updateStructure(
			TestPropsValues.getUserId(), expectedStructure.getStructureId(), 0,
			nameMap, descriptionMap, expectedStructure.getDDMForm(),
			expectedStructure.getDDMFormLayout(), _serviceContext);

		Assert.assertEquals(nameMap, actualStructure.getNameMap());
		Assert.assertEquals(
			descriptionMap, actualStructure.getDescriptionMap());
	}

	protected DDMStructure addStructure() throws Exception {
		Map<Locale, String> nameMap = new HashMap<>();

		nameMap.put(LocaleUtil.US, "Test Structure Name");

		Map<Locale, String> descriptionMap = new HashMap<>();

		descriptionMap.put(LocaleUtil.US, "Test Structure Description");

		return DDMStructureManagerUtil.addStructure(
			TestPropsValues.getUserId(), _group.getGroupId(), null,
			_classNameId, StringUtil.randomString(), nameMap, descriptionMap,
			createDDMForm(), createDDMFormLayout(),
			DDMStructureManager.STRUCTURE_DEFAULT_STORAGE_TYPE,
			DDMStructureManager.STRUCTURE_TYPE_DEFAULT, _serviceContext);
	}

	protected void assertEquals(
		DDMStructure expectedStructure, DDMStructure actualStructure) {

		Assert.assertEquals(
			expectedStructure.getStructureId(),
			actualStructure.getStructureId());
		Assert.assertEquals(
			expectedStructure.getGroupId(), actualStructure.getGroupId());
		Assert.assertEquals(
			expectedStructure.getCompanyId(), actualStructure.getCompanyId());
		Assert.assertEquals(
			expectedStructure.getClassNameId(),
			actualStructure.getClassNameId());
		Assert.assertEquals(
			expectedStructure.getStructureKey(),
			actualStructure.getStructureKey());
		Assert.assertEquals(
			expectedStructure.getNameMap(), actualStructure.getNameMap());
		Assert.assertEquals(
			expectedStructure.getDescriptionMap(),
			actualStructure.getDescriptionMap());
	}

	protected DDMForm createDDMForm() {
		DDMForm ddmForm = new DDMForm();

		ddmForm.setDefaultLocale(LocaleUtil.US);
		ddmForm.addAvailableLocale(LocaleUtil.US);

		DDMFormField ddmFormField = new DDMFormField();

		ddmFormField.setType("text");
		ddmFormField.setName("fieldName");

		ddmForm.addDDMFormField(ddmFormField);

		return ddmForm;
	}

	protected DDMFormLayout createDDMFormLayout() {
		DDMFormLayout ddmFormLayout = new DDMFormLayout();

		ddmFormLayout.setDefaultLocale(LocaleUtil.US);

		ddmFormLayout.addDDMFormLayoutPage(createDDMFormLayoutPage());

		return ddmFormLayout;
	}

	protected DDMFormLayoutColumn createDDMFormLayoutColumn() {
		DDMFormLayoutColumn ddmFormLayoutColumn = new DDMFormLayoutColumn();

		List<String> ddmFormFieldNames = new ArrayList<>();

		ddmFormFieldNames.add("fieldName");

		ddmFormLayoutColumn.setDDMFormFieldNames(ddmFormFieldNames);
		ddmFormLayoutColumn.setSize(DDMFormLayoutColumn.FULL);

		return ddmFormLayoutColumn;
	}

	protected DDMFormLayoutPage createDDMFormLayoutPage() {
		LocalizedValue title = new LocalizedValue();

		title.setDefaultLocale(LocaleUtil.US);

		title.addString(LocaleUtil.US, "Title");

		LocalizedValue description = new LocalizedValue();

		description.setDefaultLocale(LocaleUtil.US);

		description.addString(LocaleUtil.US, "Description");

		DDMFormLayoutPage ddmFormLayoutPage = new DDMFormLayoutPage();

		ddmFormLayoutPage.setTitle(title);
		ddmFormLayoutPage.setDescription(description);
		ddmFormLayoutPage.addDDMFormLayoutRow(createDDMFormLayoutRow());

		return ddmFormLayoutPage;
	}

	protected DDMFormLayoutRow createDDMFormLayoutRow() {
		DDMFormLayoutRow ddmFormLayoutRow = new DDMFormLayoutRow();

		ddmFormLayoutRow.addDDMFormLayoutColumn(createDDMFormLayoutColumn());

		return ddmFormLayoutRow;
	}

	private long _classNameId;

	@DeleteAfterTestRun
	private Group _group;

	private ServiceContext _serviceContext;

}
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

package com.liferay.dynamic.data.mapping.type.select;

import com.liferay.portlet.dynamicdatamapping.model.DDMFormFieldOptions;
import com.liferay.portlet.dynamicdatamapping.registry.DefaultDDMFormFieldTypeSettings;
import com.liferay.portlet.dynamicdatamapping.registry.annotations.DDMForm;
import com.liferay.portlet.dynamicdatamapping.registry.annotations.DDMFormField;

/**
 * @author Marcellus Tavares
 */
@DDMForm
public interface SelectDDMFormFieldTypeSettings
	extends DefaultDDMFormFieldTypeSettings {

	@DDMFormField(label = "%multiple")
	public boolean multiple();

	@DDMFormField(
		dataType = "ddm-options", label = "%options", type = "options"
	)
	public DDMFormFieldOptions options();

}
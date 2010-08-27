/**
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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

package com.liferay.portlet.asset.model;

import com.liferay.portal.kernel.annotation.AutoEscape;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.model.BaseModel;
import com.liferay.portal.service.ServiceContext;

import com.liferay.portlet.expando.model.ExpandoBridge;

import java.io.Serializable;

import java.util.Date;

/**
 * The base model interface for the AssetTag service. Represents a row in the &quot;AssetTag&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation {@link com.liferay.portlet.asset.model.impl.AssetTagModelImpl} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link com.liferay.portlet.asset.model.impl.AssetTagImpl}.
 * </p>
 *
 * <p>
 * Never modify or reference this interface directly. All methods that expect a asset tag model instance should use the {@link AssetTag} interface instead.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see AssetTag
 * @see com.liferay.portlet.asset.model.impl.AssetTagImpl
 * @see com.liferay.portlet.asset.model.impl.AssetTagModelImpl
 * @generated
 */
public interface AssetTagModel extends BaseModel<AssetTag> {
	/**
	 * Gets the primary key of this asset tag.
	 *
	 * @return the primary key of this asset tag
	 */
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this asset tag
	 *
	 * @param pk the primary key of this asset tag
	 */
	public void setPrimaryKey(long pk);

	/**
	 * Gets the tag id of this asset tag.
	 *
	 * @return the tag id of this asset tag
	 */
	public long getTagId();

	/**
	 * Sets the tag id of this asset tag.
	 *
	 * @param tagId the tag id of this asset tag
	 */
	public void setTagId(long tagId);

	/**
	 * Gets the group id of this asset tag.
	 *
	 * @return the group id of this asset tag
	 */
	public long getGroupId();

	/**
	 * Sets the group id of this asset tag.
	 *
	 * @param groupId the group id of this asset tag
	 */
	public void setGroupId(long groupId);

	/**
	 * Gets the company id of this asset tag.
	 *
	 * @return the company id of this asset tag
	 */
	public long getCompanyId();

	/**
	 * Sets the company id of this asset tag.
	 *
	 * @param companyId the company id of this asset tag
	 */
	public void setCompanyId(long companyId);

	/**
	 * Gets the user id of this asset tag.
	 *
	 * @return the user id of this asset tag
	 */
	public long getUserId();

	/**
	 * Sets the user id of this asset tag.
	 *
	 * @param userId the user id of this asset tag
	 */
	public void setUserId(long userId);

	/**
	 * Gets the user uuid of this asset tag.
	 *
	 * @return the user uuid of this asset tag
	 * @throws SystemException if a system exception occurred
	 */
	public String getUserUuid() throws SystemException;

	/**
	 * Sets the user uuid of this asset tag.
	 *
	 * @param userUuid the user uuid of this asset tag
	 */
	public void setUserUuid(String userUuid);

	/**
	 * Gets the user name of this asset tag.
	 *
	 * @return the user name of this asset tag
	 */
	@AutoEscape
	public String getUserName();

	/**
	 * Sets the user name of this asset tag.
	 *
	 * @param userName the user name of this asset tag
	 */
	public void setUserName(String userName);

	/**
	 * Gets the create date of this asset tag.
	 *
	 * @return the create date of this asset tag
	 */
	public Date getCreateDate();

	/**
	 * Sets the create date of this asset tag.
	 *
	 * @param createDate the create date of this asset tag
	 */
	public void setCreateDate(Date createDate);

	/**
	 * Gets the modified date of this asset tag.
	 *
	 * @return the modified date of this asset tag
	 */
	public Date getModifiedDate();

	/**
	 * Sets the modified date of this asset tag.
	 *
	 * @param modifiedDate the modified date of this asset tag
	 */
	public void setModifiedDate(Date modifiedDate);

	/**
	 * Gets the name of this asset tag.
	 *
	 * @return the name of this asset tag
	 */
	@AutoEscape
	public String getName();

	/**
	 * Sets the name of this asset tag.
	 *
	 * @param name the name of this asset tag
	 */
	public void setName(String name);

	/**
	 * Gets the asset count of this asset tag.
	 *
	 * @return the asset count of this asset tag
	 */
	public int getAssetCount();

	/**
	 * Sets the asset count of this asset tag.
	 *
	 * @param assetCount the asset count of this asset tag
	 */
	public void setAssetCount(int assetCount);

	public boolean isNew();

	public void setNew(boolean n);

	public boolean isCachedModel();

	public void setCachedModel(boolean cachedModel);

	public boolean isEscapedModel();

	public void setEscapedModel(boolean escapedModel);

	public Serializable getPrimaryKeyObj();

	public ExpandoBridge getExpandoBridge();

	public void setExpandoBridgeAttributes(ServiceContext serviceContext);

	public Object clone();

	public int compareTo(AssetTag assetTag);

	public int hashCode();

	public AssetTag toEscapedModel();

	public String toString();

	public String toXmlString();
}
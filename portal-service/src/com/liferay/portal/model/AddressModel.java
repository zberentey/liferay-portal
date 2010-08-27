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

package com.liferay.portal.model;

import com.liferay.portal.kernel.annotation.AutoEscape;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;

import com.liferay.portlet.expando.model.ExpandoBridge;

import java.io.Serializable;

import java.util.Date;

/**
 * The base model interface for the Address service. Represents a row in the &quot;Address&quot; database table, with each column mapped to a property of this class.
 *
 * <p>
 * This interface and its corresponding implementation {@link com.liferay.portal.model.impl.AddressModelImpl} exist only as a container for the default property accessors generated by ServiceBuilder. Helper methods and all application logic should be put in {@link com.liferay.portal.model.impl.AddressImpl}.
 * </p>
 *
 * <p>
 * Never modify or reference this interface directly. All methods that expect a address model instance should use the {@link Address} interface instead.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see Address
 * @see com.liferay.portal.model.impl.AddressImpl
 * @see com.liferay.portal.model.impl.AddressModelImpl
 * @generated
 */
public interface AddressModel extends BaseModel<Address> {
	/**
	 * Gets the primary key of this address.
	 *
	 * @return the primary key of this address
	 */
	public long getPrimaryKey();

	/**
	 * Sets the primary key of this address
	 *
	 * @param pk the primary key of this address
	 */
	public void setPrimaryKey(long pk);

	/**
	 * Gets the address id of this address.
	 *
	 * @return the address id of this address
	 */
	public long getAddressId();

	/**
	 * Sets the address id of this address.
	 *
	 * @param addressId the address id of this address
	 */
	public void setAddressId(long addressId);

	/**
	 * Gets the company id of this address.
	 *
	 * @return the company id of this address
	 */
	public long getCompanyId();

	/**
	 * Sets the company id of this address.
	 *
	 * @param companyId the company id of this address
	 */
	public void setCompanyId(long companyId);

	/**
	 * Gets the user id of this address.
	 *
	 * @return the user id of this address
	 */
	public long getUserId();

	/**
	 * Sets the user id of this address.
	 *
	 * @param userId the user id of this address
	 */
	public void setUserId(long userId);

	/**
	 * Gets the user uuid of this address.
	 *
	 * @return the user uuid of this address
	 * @throws SystemException if a system exception occurred
	 */
	public String getUserUuid() throws SystemException;

	/**
	 * Sets the user uuid of this address.
	 *
	 * @param userUuid the user uuid of this address
	 */
	public void setUserUuid(String userUuid);

	/**
	 * Gets the user name of this address.
	 *
	 * @return the user name of this address
	 */
	@AutoEscape
	public String getUserName();

	/**
	 * Sets the user name of this address.
	 *
	 * @param userName the user name of this address
	 */
	public void setUserName(String userName);

	/**
	 * Gets the create date of this address.
	 *
	 * @return the create date of this address
	 */
	public Date getCreateDate();

	/**
	 * Sets the create date of this address.
	 *
	 * @param createDate the create date of this address
	 */
	public void setCreateDate(Date createDate);

	/**
	 * Gets the modified date of this address.
	 *
	 * @return the modified date of this address
	 */
	public Date getModifiedDate();

	/**
	 * Sets the modified date of this address.
	 *
	 * @param modifiedDate the modified date of this address
	 */
	public void setModifiedDate(Date modifiedDate);

	/**
	 * Gets the class name of the model instance this address is polymorphically associated with.
	 *
	 * @return the class name of the model instance this address is polymorphically associated with
	 */
	public String getClassName();

	/**
	 * Gets the class name id of this address.
	 *
	 * @return the class name id of this address
	 */
	public long getClassNameId();

	/**
	 * Sets the class name id of this address.
	 *
	 * @param classNameId the class name id of this address
	 */
	public void setClassNameId(long classNameId);

	/**
	 * Gets the class p k of this address.
	 *
	 * @return the class p k of this address
	 */
	public long getClassPK();

	/**
	 * Sets the class p k of this address.
	 *
	 * @param classPK the class p k of this address
	 */
	public void setClassPK(long classPK);

	/**
	 * Gets the street1 of this address.
	 *
	 * @return the street1 of this address
	 */
	@AutoEscape
	public String getStreet1();

	/**
	 * Sets the street1 of this address.
	 *
	 * @param street1 the street1 of this address
	 */
	public void setStreet1(String street1);

	/**
	 * Gets the street2 of this address.
	 *
	 * @return the street2 of this address
	 */
	@AutoEscape
	public String getStreet2();

	/**
	 * Sets the street2 of this address.
	 *
	 * @param street2 the street2 of this address
	 */
	public void setStreet2(String street2);

	/**
	 * Gets the street3 of this address.
	 *
	 * @return the street3 of this address
	 */
	@AutoEscape
	public String getStreet3();

	/**
	 * Sets the street3 of this address.
	 *
	 * @param street3 the street3 of this address
	 */
	public void setStreet3(String street3);

	/**
	 * Gets the city of this address.
	 *
	 * @return the city of this address
	 */
	@AutoEscape
	public String getCity();

	/**
	 * Sets the city of this address.
	 *
	 * @param city the city of this address
	 */
	public void setCity(String city);

	/**
	 * Gets the zip of this address.
	 *
	 * @return the zip of this address
	 */
	@AutoEscape
	public String getZip();

	/**
	 * Sets the zip of this address.
	 *
	 * @param zip the zip of this address
	 */
	public void setZip(String zip);

	/**
	 * Gets the region id of this address.
	 *
	 * @return the region id of this address
	 */
	public long getRegionId();

	/**
	 * Sets the region id of this address.
	 *
	 * @param regionId the region id of this address
	 */
	public void setRegionId(long regionId);

	/**
	 * Gets the country id of this address.
	 *
	 * @return the country id of this address
	 */
	public long getCountryId();

	/**
	 * Sets the country id of this address.
	 *
	 * @param countryId the country id of this address
	 */
	public void setCountryId(long countryId);

	/**
	 * Gets the type id of this address.
	 *
	 * @return the type id of this address
	 */
	public int getTypeId();

	/**
	 * Sets the type id of this address.
	 *
	 * @param typeId the type id of this address
	 */
	public void setTypeId(int typeId);

	/**
	 * Gets the mailing of this address.
	 *
	 * @return the mailing of this address
	 */
	public boolean getMailing();

	/**
	 * Determines whether this address is mailing.
	 *
	 * @return whether this address is mailing
	 */
	public boolean isMailing();

	/**
	 * Sets whether this {$entity.humanName} is mailing.
	 *
	 * @param mailing the mailing of this address
	 */
	public void setMailing(boolean mailing);

	/**
	 * Gets the primary of this address.
	 *
	 * @return the primary of this address
	 */
	public boolean getPrimary();

	/**
	 * Determines whether this address is primary.
	 *
	 * @return whether this address is primary
	 */
	public boolean isPrimary();

	/**
	 * Sets whether this {$entity.humanName} is primary.
	 *
	 * @param primary the primary of this address
	 */
	public void setPrimary(boolean primary);

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

	public int compareTo(Address address);

	public int hashCode();

	public Address toEscapedModel();

	public String toString();

	public String toXmlString();
}
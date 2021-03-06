package de.metas.vertical.pharma.msv3.server.peer.protocol;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import lombok.Builder;
import lombok.Data;

/*
 * #%L
 * metasfresh-pharma.msv3.server-peer
 * %%
 * Copyright (C) 2018 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
@Data
@Builder
public class MSV3ServerRequest
{
	public static MSV3ServerRequest requestAll(final String nodeId)
	{
		return MSV3ServerRequest.builder()
				.nodeId(nodeId)
				.requestAllUsers(true)
				.requestAllStockAvailabilities(true)
				.build();
	}

	String nodeId;
	boolean requestAllUsers;
	boolean requestAllStockAvailabilities;
}

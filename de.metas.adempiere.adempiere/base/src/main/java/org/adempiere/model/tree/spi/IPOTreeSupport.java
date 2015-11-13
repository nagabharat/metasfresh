/**
 * 
 */
package org.adempiere.model.tree.spi;

/*
 * #%L
 * de.metas.adempiere.adempiere.base
 * %%
 * Copyright (C) 2015 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.compiere.model.GridTab;
import org.compiere.model.MTree;
import org.compiere.model.MTreeNode;
import org.compiere.model.MTree_Base;
import org.compiere.model.PO;

/**
 * 
 * NOTE: implementations of this interface are stateful
 * 
 * @author tsa
 *
 */
public interface IPOTreeSupport
{
	public static final int UNKNOWN_ParentID = -100;
	public static final int UNKNOWN_TreeID = -100;

	/**
	 * Returns the AD_Tree_ID for the given <code>po</code>
	 * 
	 * @param po
	 * @return
	 */
	public int getAD_Tree_ID(PO po);

	/**
	 * This method returns the tree node ID of the given <code>po</code>'s parent, if it can be deducted from the po (which is for example the case with product categories).
	 * <p>
	 * Note that the default implementation returns {@link #UNKNOWN_ParentID}
	 * 
	 * @param po
	 * @return
	 */
	public int getParent_ID(PO po);

	public int getOldParent_ID(PO po);

	public boolean isParentChanged(PO po);

	public String getParentIdSQL();

	public String getTreeType();

	public void setParent_ID(MTree_Base tree, int nodeId, int parentId, String trxName);

	public String getNodeInfoSelectSQL(MTree tree, final List<Object> sqlParams);

	/**
	 * Where Clause for selecting records from PO table
	 * 
	 * @param tree
	 * @return SQL Where Clause or null
	 */
	public String getWhereClause(MTree_Base tree);

	public MTreeNode getNodeInfo(GridTab gridTab);

	/**
	 * Advice the implementation to not enforce role access while loading the {@link MTreeNode}.
	 * 
	 * @see #loadNodeInfo(MTree, ResultSet)
	 */
	void disableRoleAccessCheckWhileLoading();

	/**
	 * Load {@link MTreeNode}.
	 * 
	 * @param tree
	 * @param rs
	 * @return loaded tree node or null if load could not be loaded or the role does not have access to that node
	 * @throws SQLException
	 * @see {@link #disableRoleAccessCheckWhileLoading()}
	 */
	MTreeNode loadNodeInfo(MTree tree, ResultSet rs) throws SQLException;

	/**
	 * To be called by the API!
	 * 
	 * @param tableName
	 */
	void setTableName(String tableName);
}

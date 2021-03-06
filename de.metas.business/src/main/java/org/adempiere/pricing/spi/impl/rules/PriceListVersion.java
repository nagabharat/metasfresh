package org.adempiere.pricing.spi.impl.rules;

import org.adempiere.pricing.api.IPriceListDAO;
import org.adempiere.pricing.api.IPricingContext;
import org.adempiere.pricing.api.IPricingResult;
import org.adempiere.util.Services;
import org.compiere.model.I_M_PriceList;
import org.compiere.model.I_M_PriceList_Version;
import org.compiere.model.I_M_Product;
import org.compiere.model.I_M_ProductPrice;
import org.compiere.util.Env;

import de.metas.pricing.ProductPrices;

/**
 * Calculate Price using Price List Version
 *
 * @author tsa
 *
 */
public class PriceListVersion extends AbstractPriceListBasedRule
{
	@Override
	public void calculate(final IPricingContext pricingCtx, final IPricingResult result)
	{
		if (!applies(pricingCtx, result))
		{
			return;
		}

		final int productId = pricingCtx.getM_Product_ID();

		final I_M_PriceList_Version plv = getPriceListVersionEffective(pricingCtx);
		if (plv == null || !plv.isActive())
		{
			return;
		}

		// task 09688: in order to make the material tracking module (and others!) testable decoupled, incl. price list versions and stuff,
		// we get rid of the hardcoded SQL. For the time beeing it's still here (commented), so we can see how it used to be.
		// !!IMPORTANT!! with this change of implementation, we loose the bomPriceList calculation.
		// Should bomPricing be needed in future, please consider adding a dedicated pricing rule
		final I_M_ProductPrice productPrice = ProductPrices.retrieveMainProductPriceOrNull(plv, productId);

		//
		//
		if (productPrice == null)
		{
			log.trace("Not found (PLV)");
			return;
		}

		final I_M_PriceList priceList = plv.getM_PriceList();
		final I_M_Product product = productPrice.getM_Product();

		result.setPriceStd(productPrice.getPriceStd());
		result.setPriceList(productPrice.getPriceList());
		result.setPriceLimit(productPrice.getPriceLimit());
		result.setC_Currency_ID(priceList.getC_Currency_ID());
		result.setM_Product_Category_ID(product.getM_Product_Category_ID());
		result.setPriceEditable(productPrice.isPriceEditable());
		result.setDiscountEditable(productPrice.isDiscountEditable());
		result.setEnforcePriceLimit(priceList.isEnforcePriceLimit());
		result.setTaxIncluded(priceList.isTaxIncluded());
		result.setC_TaxCategory_ID(productPrice.getC_TaxCategory_ID());
		result.setM_PriceList_Version_ID(plv.getM_PriceList_Version_ID()); // make sure that the result doesn't lack this important info, even if it was already known from the context!
		result.setCalculated(true);

		// 06942 : use product price uom all the time
		if (productPrice.getC_UOM_ID() <= 0)
		{
			result.setPrice_UOM_ID(product.getC_UOM_ID());
		}
		else
		{
			result.setPrice_UOM_ID(productPrice.getC_UOM_ID());
		}
	}

	private I_M_PriceList_Version getPriceListVersionEffective(final IPricingContext pricingCtx)
	{
		final I_M_PriceList_Version plv = pricingCtx.getM_PriceList_Version();
		if(plv != null)
		{
			return plv;
		}
		
		return Services.get(IPriceListDAO.class).retrievePriceListVersionOrNull(Env.getCtx(),
				pricingCtx.getM_PriceList_ID(),
				pricingCtx.getPriceDate(),
				(Boolean)null // processed
		);

	}
}

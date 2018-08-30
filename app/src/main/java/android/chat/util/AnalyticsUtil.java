/*
 * Copyright © 2016, Craftsvilla.com
 *  Written under contract by Robosoft Technologies Pvt. Ltd.
 */

package android.chat.util;

import android.content.Context;

import java.util.Map;

/**
 * Created by Ram Prakash on 15/4/16.
 */
public class AnalyticsUtil {


    public interface AnalyticsConstants {

        // EventName
        String BANNER_TAPPED_EVENT            = "Tapped Banner";
        String RESULT_FILTERED_SORTED_EVENT   = "ResultsFilteredOrSorted";
        String PRODUCT_DETAIL_VIEWED_EVENT    = "ProductDetailsViewed";
        String BUTTON_TAPPED_EVENT            = "Tapped Button";
        String CHECKED_OUT_EVENT              = "CheckedOut";
        String ADD_TO_CART_EVENT              = "AddedToCart";
        String WENT_TO_CART_EVENT             = "WentToCart";
        String POLICIES_VIEWED_EVENT          = "PoliciesViewed";
        String SEARCH_PERFORMED_EVENT         = "SearchPerformed";
        String NAVIGATION_ITEM_ACCESSED_EVENT = "NavigationItemAccessed";
        String NAVIGATION_DRAWER_OPENED_EVENT = "NavigationDrawerOpened";
        String REMOVE_ITEM_FROM_CART_EVENT    = "RemoveItemFromCart";
        String SELLER_CONTACTED_EVENT         = "SellerContacted";
        String FOOTER_LINK_ACCESSED_EVENT     = "footerLinkAccessed";
        String PRODUCT_BOOKMARKED_EVENT       = "ProductBookmarked";
        String USER_LOGGED_IN_EVENT           = "UserLoggedIn";
        String USER_SIGNED_UP_EVENT           = "UserSignedUp";
        String CHARGE_EVENT                   = "Charged";
        String IMAGE_VIEWED_EVENT             = "ImagesViewed";
        String PRE_CHARGED_EVENT              = "Pre-charged event";
        String PRODUCT_DETAIL_EXPANED_EVENT   = "ProductDetailsExpanded";
        String RECOMMENDATION_EVENT           = "Recommendation Feeds";

        // Properties/ Attributes
        String BANNER_NAME               = "bannerName";
        String SECTIONE                  = "section";
        String HAS_OFFERS                = "hasOffers";
        String PRICE_RANGE_ALTERED       = "priceRangeAltered";
        String SORT_USED                 = "sortUsed";
        String COLOR_FILTERUSED          = "colorFilterUsed";
        String DISCOUNT_PERCENTAGE       = "discountPercentage";
        String CATEGORY                  = "category";
        String SUB_CATEGORY              = "subCategory";
        String POSITION_ON_PAGE          = "positionOnPage";
        String PRICE                     = "price";
        String PRODUCT_ID                = "productId";
        String TYPE                      = "type";
        String PRODUCT_INFO              = "productInfo";
        String FINAL_AMOUNT              = "final amount";
        String ITEMS_COUNT               = "itemsCount";
        String NUMBER_OF_NOTES_TO_SELLER = "numberOfNotesToSeller";
        String LOCAL_SHIPPING_COST       = "localShippingCost";
        String SOURCE                    = "source";
        String BUTTON_TEXT               = "buttonText";
        String COUNT                     = "count";
        String PLACE                     = "place";
        String SUGGESTION_USED           = "suggestionUsed";
        String SEARCH_QUERY              = "Search query";
        String NAVIGATION_SELECTION      = "navigationSection";
        String POSITION_IN_PARENT_GROUP  = "positionInParentGroup";
        String POSITION_OF_PARENT_GROUP  = "positionOfParentGroup";
        String TEXT_OF_NAVI_ITEM         = "textOfNaviItem";
        String BALENCE_QTY               = "balanceQty";
        String LINK                      = "link";
        String CART_VALUE                = "cartValue";
        String CHARGED_AMOUNT            = "chargedAmount";
        String CHARGED_ID                = "chargedId";
        String LINE_ITEMS                = "lineItems";
        String AMOUNT                    = "amount";
    }

    public static void trackEvent( Context context, String eventName, Map eventValues) {
       /* if (context != null) {
            CleverTapAPI cleverTapAPI = ((CraftApplication) context.getApplicationContext()).getCleverTapApi();
            cleverTapAPI.event.push(eventName, eventValues);
        }*/
    }
}

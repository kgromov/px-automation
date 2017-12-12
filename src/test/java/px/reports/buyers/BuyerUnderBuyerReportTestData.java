package px.reports.buyers;

import dto.ObjectIdentityData;
import org.json.JSONObject;
import px.reports.UserReports;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kgr on 11/17/2016.
 */
public class BuyerUnderBuyerReportTestData extends BuyerReportTestData implements UserReports {

    public BuyerUnderBuyerReportTestData() {
        super(0);
    }

    public BuyerUnderBuyerReportTestData(String buyerCategory) {
        super(true);
        this.buyerCategory = buyerCategory;
        // headers in report overview table
        setHeaders();
        // items in table by buyer category
        setItemsByReportType(buyerCategory);
        // check buyer campaigns belong to 1 buyer
        checkCampaignsTo1Buyer(campaigns());
    }

    private List<ObjectIdentityData> campaigns() {
        int size = itemsByBuyerCategory.length();
        List<ObjectIdentityData> list = new ArrayList<>(size > 0 ? size - 1 : 0);
        for (int i = 1; i < itemsByBuyerCategory.length(); i++) {
            JSONObject object = itemsByBuyerCategory.getJSONObject(i);
            list.add(new ObjectIdentityData(null,
                    String.valueOf(object.get("buyerInstance")),
                    String.valueOf(object.get("buyerInstanceGuid")).toLowerCase())
            );
        }
        return list;
    }

    @Override
    public List<String> filters() {
        return Collections.singletonList(REPORT_TYPE_FILTER);
    }
}
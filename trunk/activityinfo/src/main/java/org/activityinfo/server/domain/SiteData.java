package org.activityinfo.server.domain;

import org.activityinfo.server.dao.SiteColumn;
import org.activityinfo.shared.report.content.SiteGeoData;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A "non-tabular" representation of sites used by 
 * map and narrative generators.
 * 
 * @author Alex Bertram
 *
 */
public class SiteData implements SiteGeoData {

	public Object[] values;
	public Map<Integer, String> adminNames = new HashMap<Integer,String>(0);
    public Map<Integer, AdminEntity> adminEntities = new HashMap<Integer,AdminEntity>(0);
	public Map<Integer, Double> indicatorValues = new HashMap<Integer, Double>(0);
	public Map<Integer, Boolean> attributes = new HashMap<Integer, Boolean>(0);

    public SiteData() {
        this.values = new Object[SiteColumn.values().length+1];
    }

	public SiteData(Object[] values) {
        this.values = values;
	}

	public Object getValue(SiteColumn column) {
		return values[column.index()];
	}

    public void setValue(SiteColumn column, Object value) {
        values[column.index()] = value;
    }
    
	@Override
	public double getLongitude() {
		return (Double) getValue(SiteColumn.x);
	}

	@Override
	public double getLatitude() {
		return (Double) getValue(SiteColumn.y);
	}
    
    public int getId() {
        return (Integer) getValue(SiteColumn.id);
    }

	@Override
	public boolean hasLatLong() {
		return  getValue(SiteColumn.y) != null &&
                getValue(SiteColumn.x) != null;
	}

    public int getActivityId() {
        return (Integer)values[SiteColumn.activity_id.index()];
    }

    public int getDatabaseId() {
        return (Integer)values[SiteColumn.database_id.index()];
    }

    public String getLocationName() {
        return (String)values[SiteColumn.location_name.index()];
    }

    public String getLocationAxe() {
        return (String)values[SiteColumn.location_axe.index()];
    }

    public String getPartnerName() {
        return (String)values[SiteColumn.partner_name.index()];
    }

    public Double getIndicatorValue(int id) {
        return indicatorValues.get(id);
    }

    public Date getDate1() {
        return (Date)values[SiteColumn.date1.index()];
    }
    
    public Date getDate2() {
        return (Date)values[SiteColumn.date2.index()];
    }

    public int getPartnerId() {
        return (Integer)values[SiteColumn.partner_id.index()];
    }

    public String getComments() {
        return (String)values[SiteColumn.comments.index()];
    }

    public Boolean getAttributeValue(int attribId) {
        return attributes.get(attribId);
    }

    public void setIndicatorValue(int indicatorId, Double value) {
        indicatorValues.put(indicatorId, value);
    }
}

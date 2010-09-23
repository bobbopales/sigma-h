package org.activityinfo.shared.dto;

import com.extjs.gxt.ui.client.data.BaseModelData;
import org.activityinfo.shared.report.model.ReportFrequency;

public class ReportTemplateDTO extends BaseModelData implements DTO {

    private ReportFrequency freq_;

    public ReportTemplateDTO() {
		setFrequency(ReportFrequency.NotDateBound);
	}

    public int getId() {
		return (Integer)get("id");
	}
	
	public void setId(int id) {
		set("id", id);
	}
	
	public void setTitle(String title) {
		set("title", title);
	}
	
	public String getTitle() {
		return get("title");
	}
	
	public void setDescription(String description) {
		set("description", description);
	}
	
	public String getDescription() {
		return get("description");
	}
	
	public Integer getDatabaseId() {
		return get("databaseId");
	}
	
	public void setDatabaseId(Integer id) {
		set("databaseId", id);
	}

    public String getDatabaseName() {
        return get("databaseName");
    }

    public void setDatabaseName(String name) {
        set("databaseName", name);
    }

	public boolean isEditAllowed() {
		return (Boolean)get("editAllowed");
	}
	
	public void setEditAllowed(boolean allowed) {
		set("editAllowed", allowed);
	}
	
	public String getOwnerName() {
		return get("ownerName");
	}
	
	public void setOwnerName(String name) {
		set("ownerName", name);
	}

    public void setAmOwner(boolean amOwner) {
		set("amOwner", amOwner);
	}
	
	public boolean getAmOwner() {
		return (Boolean)get("amOwner");
	}

    public ReportFrequency getFrequency() {
        return get("frequency");
    }

    public void setFrequency(ReportFrequency frequency) {
        set("frequency", frequency);
    }

    public Integer getDay() {
        return get("day");
    }

    public void setDay(Integer day) {
        set("day", day);
    }

    public boolean isSubscribed() {
        return (Boolean)get("subscribed");
    }

    public void setSubscribed(boolean subscribed) {
        set("subscribed", subscribed);
    }

}
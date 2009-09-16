package org.activityinfo.shared.command;

import java.util.Map;


import org.activityinfo.shared.command.result.VoidResult;
import org.activityinfo.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.RpcMap;
import com.extjs.gxt.ui.client.data.ModelData;

public class UpdateEntity implements Command<VoidResult> {

	private int id;
	private String entityName;
	private RpcMap changes;

    protected UpdateEntity() {
		
	}

    public UpdateEntity(String entityName, int id, Map<String, Object> changes) {
        this.entityName = entityName;
        this.id = id;
        this.changes = new RpcMap();
        this.changes.putAll(changes);
    }

	public UpdateEntity(EntityDTO model, Map<String, Object> changes) {
		this.id = model.getId();
        this.entityName = model.getEntityName();
		this.changes = new RpcMap();
		this.changes.putAll(changes);
	}

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public RpcMap getChanges() {
		return changes;
	}

	public void setChanges(RpcMap changes) {
		this.changes = changes;
	}


}

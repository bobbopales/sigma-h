package org.activityinfo.client.command.loader;

import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.ListLoadConfig;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.SortInfo;
import org.activityinfo.client.command.CommandService;
import org.activityinfo.shared.command.GetListCommand;
import org.activityinfo.shared.command.result.ListResult;

public abstract class AbstractListCmdLoader<	
							ResultT extends ListResult,
							CommandT extends GetListCommand<ResultT> >
		extends AbstractCmdLoader<ResultT, CommandT>
		implements ListLoader<ResultT> {

	private SortInfo sortInfo = new SortInfo();
	private boolean remoteSort = true;
	
	public AbstractListCmdLoader(CommandService service) {
		super(service);
	}
	
	
	@Override
	protected void prepareCommand(CommandT cmd) {
		super.prepareCommand(cmd);
		
		cmd.setSortInfo(sortInfo);
	}


	@Override
	public SortDir getSortDir() {
		return sortInfo.getSortDir();
	}

	@Override
	public String getSortField() {
		return sortInfo.getSortField();
	}

    public void setSortInfo(SortInfo si) {
        setSortField(si.getSortField());
        setSortDir(si.getSortDir());
    }

	@Override
	public boolean isRemoteSort() {
		return remoteSort;
	}

	@Override
	public void setRemoteSort(boolean remote) {
		this.remoteSort = remote;
	}

	@Override
	public void setSortDir(SortDir dir) {
		this.sortInfo.setSortDir(dir);
		
	}

	@Override
	public void setSortField(String field) {
		this.sortInfo.setSortField(field);
		
	}

    public SortInfo getSortInfo() {

        return new SortInfo(getSortField(), getSortDir());
    }

    @Override
    public boolean load(Object loadConfig) {

        if(loadConfig instanceof ListLoadConfig) {
            ListLoadConfig listLoadConfig = ((ListLoadConfig)loadConfig);

            if(listLoadConfig.getSortInfo() != null) {
                this.setSortInfo(listLoadConfig.getSortInfo());
            }
        }

        return super.load(loadConfig);
    }
}

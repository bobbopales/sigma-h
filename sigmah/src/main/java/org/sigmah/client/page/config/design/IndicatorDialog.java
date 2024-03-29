package org.sigmah.client.page.config.design;

import java.util.ArrayList;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.event.IndicatorEvent;
import org.sigmah.client.event.IndicatorEvent.ChangeType;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.BatchResult;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.report.model.DimensionType;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class IndicatorDialog extends Dialog {

	private final EventBus eventBus;
	private final Dispatcher dispatcher;

	private IndicatorForm indicatorForm;
	private IndicatorPivotPanel pivotPanel;
	private ButtonBar buttonBar;

	private FormDialogCallback callback;

	private IndicatorDTO indicator;

	private TabItem defTab;
	private TabItem valuesTab;
	private TabPanel tabPanel;

	@Inject
	public IndicatorDialog(EventBus eventBus, Dispatcher dispatcher, IndicatorPivotPanel pivotPanel) {
		this.eventBus = eventBus;
		this.dispatcher = dispatcher;
		this.pivotPanel = pivotPanel;

		setWidth(475);
		setHeight(300);
		setClosable(true);
		setModal(true);

		indicatorForm = new IndicatorForm(dispatcher);
		indicatorForm.setHeaderVisible(false);
		indicatorForm.setScrollMode(Scroll.AUTOY);
		indicatorForm.setStyleAttribute("backgroundColor", "white");
		
		String title = "";

		title = "Definition";

		defTab = new TabItem(title);
		defTab.setLayout(new FitLayout());
		defTab.add(indicatorForm);

		tabPanel = new TabPanel();
		tabPanel.add(defTab);

		valuesTab = new TabItem(I18N.CONSTANTS.value());
		valuesTab.addListener(Events.Select, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				onValuesTabSelected();
			}
		});
		valuesTab.setLayout(new FitLayout());
		valuesTab.add(pivotPanel);
		tabPanel.add(valuesTab);

		setLayout(new FitLayout());
		add(tabPanel);

		setButtons(OKCANCEL);

	}

	public void show(int databaseId, IndicatorDTO indicator) {
		this.indicator = new IndicatorDTO();
		this.indicator.setProperties(indicator.getProperties());

		setHeading(indicator.getName() == null ? I18N.CONSTANTS.newIndicator() : indicator.getName());

		indicatorForm.getBinding().bind(this.indicator);
		pivotPanel.load(databaseId, this.indicator);

		show();
	}

	public void show(int databaseId, IndicatorDTO indicator, boolean getForm, boolean getPivot) {

		this.indicator = new IndicatorDTO();
		this.indicator.setProperties(indicator.getProperties());

		setHeading(indicator.getName() == null ? I18N.CONSTANTS.newIndicator() : indicator.getName());

		if (getForm)
			indicatorForm.getBinding().bind(this.indicator);
		if (getPivot)
			pivotPanel.load(databaseId, this.indicator);

		show();
	}

	@Override
	protected void onButtonPressed(Button button) {
		if (button.getItemId().equals(OK)) {
			save();
		} else {
			hide();
		}
	}

	private void save() {
		BatchCommand save = new BatchCommand();
		save.getCommands().add(new UpdateEntity(indicator, indicator.getProperties()));
		save.getCommands().addAll(pivotPanel.composeSaveCommand().getCommands());

		dispatcher.execute(save, new MaskingAsyncMonitor(this, I18N.CONSTANTS.saving()),
		                new AsyncCallback<BatchResult>() {

			                @Override
			                public void onFailure(Throwable caught) {
				                // handled by monitor
			                }

			                @Override
			                public void onSuccess(BatchResult result) {
				                IndicatorEvent event = new IndicatorEvent(IndicatorEvent.CHANGED, IndicatorDialog.this);
				                event.setEntityId(indicator.getId());
				                event.setChangeType(ChangeType.UPDATED);
				                event.setChanges(indicator.getProperties());
				                eventBus.fireEvent(event);
				                hide();
			                }
		                });
	}

	private void onValuesTabSelected() {

		// GWT DialogBox / PopupPanel cannot be displayed on top of
		// IndicatorDialog. Keep GXT components until the application
		// is migrated to pure GWT ui.

		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Database, indicator.getDatabaseId());

		GetSites cmd = new GetSites();
		cmd.setFilter(filter);

		dispatcher.execute(cmd, null, new AsyncCallback<SiteResult>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(SiteResult result) {
				if (((ArrayList<SiteDTO>) result.getData()).size() == 0) {
					MessageBox box = new MessageBox();
					box.setButtons(MessageBox.OK);
					box.setIcon(MessageBox.INFO);
					box.addCallback(new Listener<MessageBoxEvent>() {
						public void handleEvent(MessageBoxEvent ce) {
							ce.getButtonClicked();
						}
					});
					box.setMessage(I18N.CONSTANTS.projectWithNoSitesWarning());
					box.show();
				}
			}
		});
	}

	public void removeDef() {
		tabPanel.remove(defTab);
	}

	public void removeVal() {
		tabPanel.remove(valuesTab);
	}
}

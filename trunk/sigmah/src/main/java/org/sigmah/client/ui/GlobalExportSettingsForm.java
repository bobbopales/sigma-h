/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.common.element.ElementTypeEnum;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.GetGlobalExportSettings;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.dto.ExportUtils;
import org.sigmah.shared.dto.GlobalExportSettingsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.UpdateGlobalExportSettings;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

/*
 * Form to configure global export settings
 * 
 * @author sherzod
 */
public class GlobalExportSettingsForm {

	static class SimpleComboBoxData extends BaseModelData {

		private static final long serialVersionUID = 3241619100800404244L;

		public SimpleComboBoxData(Integer value, String label) {
			setValue(value);
			setLabel(label);
		}

		public Integer getValue() {
			return get("value");
		}

		public void setValue(Integer value) {
			set("value", value);
		}

		public String getLabel() {
			return get("label");
		}

		public void setLabel(String label) {
			set("label", label);
		}
	}

	private final ListStore<FlexibleElementDTO> fieldsStore;
	private final ListStore<ProjectModelDTO> modelsStore;
	private final Integer organizationId;
	private final Map<Integer, Boolean> fieldsMap;
	private final List<SimpleComboBoxData> autoExportSchedules;
	private final List<SimpleComboBoxData> autoDeleteSchedules;
	private final List<SimpleComboBoxData> autoExportMonthlySchedules;
	private final List<SimpleComboBoxData> autoExportWeeklySchedules;
	
	private final static Map<Integer, SimpleComboBoxData> exportScheduleMap;
	private final static Map<Integer, SimpleComboBoxData> deleteScheduleMap;
	private final static Map<Integer, SimpleComboBoxData> monthScheduleMap;
	private final static Map<Integer, SimpleComboBoxData> weekScheduleMap;
	
	static {
		exportScheduleMap = new HashMap<Integer, SimpleComboBoxData>();
		exportScheduleMap.put(0, new SimpleComboBoxData(0,I18N.CONSTANTS.notScheduled()));
		exportScheduleMap.put(1, new SimpleComboBoxData(1, I18N.CONSTANTS.daily()));
		exportScheduleMap.put(3, new SimpleComboBoxData(3, I18N.MESSAGES.everyXDays("3")));
		exportScheduleMap.put(9, new SimpleComboBoxData(9, I18N.MESSAGES.everyXDays("9")));
		exportScheduleMap.put(15, new SimpleComboBoxData(15, I18N.MESSAGES.everyXDays("15")));
		exportScheduleMap.put(31, new SimpleComboBoxData(31, I18N.CONSTANTS.everyMonth())); //In this case take value from monthMap
		exportScheduleMap.put(61, new SimpleComboBoxData(61, I18N.CONSTANTS.everyWeek())); //In this case take value from weekMap

		
		// The number space of 31-58 is reserved for month dates in monthyExport settings. 
		// 31 represents the first day of the month, 32 the second day and so on
		// Only 28 days as considered as they are common in every month of the year
		monthScheduleMap = new HashMap<Integer, SimpleComboBoxData>();
		monthScheduleMap.put(31, new SimpleComboBoxData(31, I18N.CONSTANTS.number_1()));
		monthScheduleMap.put(32, new SimpleComboBoxData(32, I18N.CONSTANTS.number_2()));
		monthScheduleMap.put(33, new SimpleComboBoxData(33, I18N.CONSTANTS.number_3()));
		monthScheduleMap.put(34, new SimpleComboBoxData(34, I18N.CONSTANTS.number_4()));
		monthScheduleMap.put(35, new SimpleComboBoxData(35, I18N.CONSTANTS.number_5()));
		monthScheduleMap.put(36, new SimpleComboBoxData(36, I18N.CONSTANTS.number_6()));
		monthScheduleMap.put(37, new SimpleComboBoxData(37, I18N.CONSTANTS.number_7()));
		monthScheduleMap.put(38, new SimpleComboBoxData(38, I18N.CONSTANTS.number_8()));
		monthScheduleMap.put(39, new SimpleComboBoxData(39, I18N.CONSTANTS.number_9()));
		monthScheduleMap.put(40, new SimpleComboBoxData(40, I18N.CONSTANTS.number_10()));
		monthScheduleMap.put(41, new SimpleComboBoxData(41, I18N.CONSTANTS.number_11()));
		monthScheduleMap.put(42, new SimpleComboBoxData(42, I18N.CONSTANTS.number_12()));
		monthScheduleMap.put(43, new SimpleComboBoxData(43, I18N.CONSTANTS.number_13()));
		monthScheduleMap.put(44, new SimpleComboBoxData(44, I18N.CONSTANTS.number_14()));
		monthScheduleMap.put(45, new SimpleComboBoxData(45, I18N.CONSTANTS.number_15()));
		monthScheduleMap.put(46, new SimpleComboBoxData(46, I18N.CONSTANTS.number_16()));
		monthScheduleMap.put(47, new SimpleComboBoxData(47, I18N.CONSTANTS.number_17()));
		monthScheduleMap.put(48, new SimpleComboBoxData(48, I18N.CONSTANTS.number_18()));
		monthScheduleMap.put(49, new SimpleComboBoxData(49, I18N.CONSTANTS.number_19()));
		monthScheduleMap.put(50, new SimpleComboBoxData(50, I18N.CONSTANTS.number_20()));
		monthScheduleMap.put(51, new SimpleComboBoxData(51, I18N.CONSTANTS.number_21()));
		monthScheduleMap.put(52, new SimpleComboBoxData(52, I18N.CONSTANTS.number_22()));
		monthScheduleMap.put(53, new SimpleComboBoxData(53, I18N.CONSTANTS.number_23()));
		monthScheduleMap.put(54, new SimpleComboBoxData(54, I18N.CONSTANTS.number_24()));
		monthScheduleMap.put(55, new SimpleComboBoxData(55, I18N.CONSTANTS.number_25()));
		monthScheduleMap.put(56, new SimpleComboBoxData(56, I18N.CONSTANTS.number_26()));
		monthScheduleMap.put(57, new SimpleComboBoxData(57, I18N.CONSTANTS.number_27()));
		monthScheduleMap.put(58, new SimpleComboBoxData(58, I18N.CONSTANTS.number_28()));
		
		
		//The number space 61-67 is reserved to denote weekly export schedule 
		// 61 represents sunday, 62 monday and so on
		weekScheduleMap = new HashMap<Integer, SimpleComboBoxData>();
		weekScheduleMap.put(61, new SimpleComboBoxData(61, I18N.CONSTANTS.dayName_1()));
		weekScheduleMap.put(62, new SimpleComboBoxData(62, I18N.CONSTANTS.dayName_2()));
		weekScheduleMap.put(63, new SimpleComboBoxData(63, I18N.CONSTANTS.dayName_3()));
		weekScheduleMap.put(64, new SimpleComboBoxData(64, I18N.CONSTANTS.dayName_4()));
		weekScheduleMap.put(65, new SimpleComboBoxData(65, I18N.CONSTANTS.dayName_5()));
		weekScheduleMap.put(66, new SimpleComboBoxData(66, I18N.CONSTANTS.dayName_6()));
		weekScheduleMap.put(67, new SimpleComboBoxData(67, I18N.CONSTANTS.dayName_7()));
		
		
		

		deleteScheduleMap = new HashMap<Integer, SimpleComboBoxData>();
		deleteScheduleMap.put(0, new SimpleComboBoxData(0, I18N.CONSTANTS.notScheduled()));
		deleteScheduleMap.put(1, new SimpleComboBoxData(1, I18N.MESSAGES.olderThanXMonths("1")));
		deleteScheduleMap.put(3, new SimpleComboBoxData(3, I18N.MESSAGES.olderThanXMonths("3")));
		deleteScheduleMap.put(6, new SimpleComboBoxData(6, I18N.MESSAGES.olderThanXMonths("6")));
		deleteScheduleMap.put(12, new SimpleComboBoxData(12, I18N.MESSAGES.olderThanXMonths("12")));
	}

	public GlobalExportSettingsForm(final Integer organizationId, final Dispatcher dispatcher) {

		modelsStore = new ListStore<ProjectModelDTO>();
		fieldsStore = new ListStore<FlexibleElementDTO>();
		this.organizationId = organizationId;
		fieldsMap = new HashMap<Integer, Boolean>();

		autoExportSchedules = new ArrayList<SimpleComboBoxData>();
		for (Integer value : exportScheduleMap.keySet()) {
			autoExportSchedules.add(exportScheduleMap.get(value));
		}
		autoDeleteSchedules = new ArrayList<SimpleComboBoxData>();
		for (Integer value : deleteScheduleMap.keySet()) {
			autoDeleteSchedules.add(deleteScheduleMap.get(value));
		}
		
		autoExportMonthlySchedules = new ArrayList<SimpleComboBoxData>();
		for (Integer value : monthScheduleMap.keySet()) {
			autoExportMonthlySchedules.add(monthScheduleMap.get(value));
		}
		autoExportWeeklySchedules = new ArrayList<SimpleComboBoxData>();
		for (Integer value : weekScheduleMap.keySet()) {
			autoExportWeeklySchedules.add(weekScheduleMap.get(value));
		}
		
		

		final Window w = new Window();
		w.setPlain(true);
		w.setModal(true);
		w.setBlinkModal(true);
		w.setLayout(new FitLayout());
		w.setSize(900, 420);
		w.setHeading(I18N.CONSTANTS.globalExportConfiguration());

		final FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(150);

		panel.setLayout(layout);
		// file format
		final Radio calcChoice = new Radio();
		calcChoice.setBoxLabel(I18N.CONSTANTS.openDocumentSpreadsheet());
		calcChoice.setValue(true);
		calcChoice.setName("type");

		final Radio excelChoice = new Radio();
		excelChoice.setBoxLabel(I18N.CONSTANTS.msExcel());
		excelChoice.setName("type");

		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setOrientation(Orientation.VERTICAL);
		radioGroup.setFieldLabel(I18N.CONSTANTS.chooseFileType());
		radioGroup.add(calcChoice);
		radioGroup.add(excelChoice);
		panel.add(radioGroup);

		// configure fields
		// project model
		final Grid<ProjectModelDTO> pModelsGrid = getModelsGrid();
		// flex elements
		final Grid<FlexibleElementDTO> elementsGrid = getElementsGrid();

		final FlexTable fieldsTable = new FlexTable();
		fieldsTable.setCellPadding(0);
		fieldsTable.setCellSpacing(0);
		fieldsTable.setWidget(0, 0, new LabelField(I18N.CONSTANTS.configureFieldsToExport() + ":"));
		fieldsTable.getFlexCellFormatter().setWidth(0, 0, "170px");
		fieldsTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		fieldsTable.setWidget(0, 1, pModelsGrid);
		fieldsTable.setWidget(0, 2, elementsGrid);
		panel.add(fieldsTable);

		// auto export schedule
		final ListStore<SimpleComboBoxData> exportScheduleStore = new ListStore<SimpleComboBoxData>();
		final ComboBox<SimpleComboBoxData> exportSchedulesBox = new ComboBox<SimpleComboBoxData>();
		exportSchedulesBox.setFieldLabel(I18N.CONSTANTS.autoExportSchedule());
		exportSchedulesBox.setStore(exportScheduleStore);
		exportSchedulesBox.setDisplayField("label");
		exportSchedulesBox.setValueField("value");
		exportSchedulesBox.setEditable(false);
		exportSchedulesBox.setTriggerAction(TriggerAction.ALL);
		exportScheduleStore.add(autoExportSchedules);
		exportScheduleStore.commitChanges();
		exportSchedulesBox.setValue(exportScheduleMap.get(0));
		panel.add(exportSchedulesBox);
		
		// auto export monthly schedule
		final ListStore<SimpleComboBoxData> exportMonthlyScheduleStore = new ListStore<SimpleComboBoxData>();
		final ComboBox<SimpleComboBoxData> exportMonthlySchedulesBox = new ComboBox<SimpleComboBoxData>();
		exportMonthlySchedulesBox.setFieldLabel(I18N.CONSTANTS.dayInMonth());
		exportMonthlySchedulesBox.setStore(exportMonthlyScheduleStore);
		exportMonthlySchedulesBox.setDisplayField("label");
		exportMonthlySchedulesBox.setValueField("value");
		exportMonthlySchedulesBox.setEditable(false);
		exportMonthlySchedulesBox.setTriggerAction(TriggerAction.ALL);
		exportMonthlyScheduleStore.add(autoExportMonthlySchedules);
		exportMonthlyScheduleStore.commitChanges();
		exportMonthlySchedulesBox.setValue(monthScheduleMap.get(31));
		exportMonthlySchedulesBox.hide();
		panel.add(exportMonthlySchedulesBox);
		
		// auto export Weekly schedule
		final ListStore<SimpleComboBoxData> exportWeeklyScheduleStore = new ListStore<SimpleComboBoxData>();
		final ComboBox<SimpleComboBoxData> exportWeeklySchedulesBox = new ComboBox<SimpleComboBoxData>();
		exportWeeklySchedulesBox.setFieldLabel(I18N.CONSTANTS.dayInWeek());
		exportWeeklySchedulesBox.setStore(exportWeeklyScheduleStore);
		exportWeeklySchedulesBox.setDisplayField("label");
		exportWeeklySchedulesBox.setValueField("value");
		exportWeeklySchedulesBox.setEditable(false);
		exportWeeklySchedulesBox.setTriggerAction(TriggerAction.ALL);
		exportWeeklyScheduleStore.add(autoExportWeeklySchedules);
		exportWeeklyScheduleStore.commitChanges();
		exportWeeklySchedulesBox.setValue(weekScheduleMap.get(61));
		exportWeeklySchedulesBox.hide();
		panel.add(exportWeeklySchedulesBox);
		

		// auto delete schedule
		final ListStore<SimpleComboBoxData> deleteScheduleStore = new ListStore<SimpleComboBoxData>();
		final ComboBox<SimpleComboBoxData> deleteSchedulesBox = new ComboBox<SimpleComboBoxData>();
		deleteSchedulesBox.setFieldLabel(I18N.CONSTANTS.autoCleanupSchedule());
		deleteSchedulesBox.setStore(deleteScheduleStore);
		deleteSchedulesBox.setDisplayField("label");
		deleteSchedulesBox.setValueField("value");
		deleteSchedulesBox.setEditable(false);
		deleteSchedulesBox.setTriggerAction(TriggerAction.ALL);
		deleteScheduleStore.add(autoDeleteSchedules);
		deleteScheduleStore.commitChanges();
		deleteSchedulesBox.setValue(deleteScheduleMap.get(0));
		panel.add(deleteSchedulesBox);
		
		
		
		//Add onChange handler for exportSchedulesBox
		exportSchedulesBox.addListener(Events.Select, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				exportMonthlySchedulesBox.hide();
				exportWeeklySchedulesBox.hide();

				if(exportSchedulesBox.getValue().getValue()==31){ //Case of Monthly Schedule
					exportMonthlySchedulesBox.show();
				}else if(exportSchedulesBox.getValue().getValue()==61){ //Case of Weekly Schedule
					exportWeeklySchedulesBox.show();
				}else{		
					//Regular case of every N days
				}
			}
		});
		//onChange handler for exportSchedulesBox handled
		

		// button
		final Button saveButton = new Button(I18N.CONSTANTS.saveExportConfiguration());
		panel.getButtonBar().add(saveButton);
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				UpdateGlobalExportSettings settings = new UpdateGlobalExportSettings(fieldsMap);
				if (excelChoice.getValue()) {
					settings.setExportFormat(ExportUtils.ExportFormat.XLS);
				} else {
					settings.setExportFormat(ExportUtils.ExportFormat.ODS);
				}
				if (exportSchedulesBox.getValue() == null || exportSchedulesBox.getValue().getValue() == 0) {
					settings.setAutoExportFrequency(null);
				} else {
					
					if(exportSchedulesBox.getValue().getValue()==31){ //Case of Monthly Schedule
						// Push the value selected in the Monthly export schedule box
						// Value between 31-38 (both inclusive)
						settings.setAutoExportFrequency(exportMonthlySchedulesBox.getValue().getValue());
					}else if(exportSchedulesBox.getValue().getValue()==61){ //Case of Weekly Schedule
						// Value between 61-67 (both inclusive)
						// Push the value selected in the Weekly export schedule box
						settings.setAutoExportFrequency(exportWeeklySchedulesBox.getValue().getValue());
					}else{
						//Regular case of every N days
						settings.setAutoExportFrequency(exportSchedulesBox.getValue().getValue());
					}
					
				}
				if (deleteSchedulesBox.getValue() == null || deleteSchedulesBox.getValue().getValue() == 0) {
					settings.setAutoDeleteFrequency(null);
				} else {
					settings.setAutoDeleteFrequency(deleteSchedulesBox.getValue().getValue());
				}

				settings.setOrganizationId(organizationId);
				dispatcher.execute(settings, null, new AsyncCallback<VoidResult>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageBox.alert(I18N.CONSTANTS.saveExportConfiguration(), I18N.MESSAGES
						                .adminStandardCreationFailure(I18N.CONSTANTS.globalExportConfiguration()), null);
					}

					@Override
					public void onSuccess(VoidResult result) {
						w.hide();
						Notification.show(I18N.CONSTANTS.saveExportConfiguration(), I18N.MESSAGES
						                .adminStandardUpdateSuccess(I18N.CONSTANTS.globalExportConfiguration()));
					}
				});
			}
		});

		w.add(panel);
		w.show();

		// action
		final GetGlobalExportSettings settingsCommand = new GetGlobalExportSettings(organizationId);
		dispatcher.execute(settingsCommand, new MaskingAsyncMonitor(panel, I18N.CONSTANTS.loading()),
		                new AsyncCallback<GlobalExportSettingsDTO>() {

			                @Override
			                public void onFailure(Throwable caught) {
				                MessageBox.alert(I18N.CONSTANTS.globalExportConfiguration(),
				                                I18N.CONSTANTS.serverError(), null);
			                }

			                @Override
			                public void onSuccess(GlobalExportSettingsDTO result) {
				                // set export format
				                if (result.getExportFormat() != null) {
					                switch (result.getExportFormat()) {
									case XLS:
										excelChoice.setValue(true);
										break;

									case ODS:
										calcChoice.setValue(true);
										break;
									}

								}

								// set pmodels
								modelsStore.add(result.getProjectModelsDTO());
								modelsStore.commitChanges();

								// auto export schdule
								if (exportScheduleMap.get(result.getAutoExportFrequency()) != null || monthScheduleMap.get(result.getAutoExportFrequency()) != null  ||  weekScheduleMap.get(result.getAutoExportFrequency()) != null){
								
									if(result.getAutoExportFrequency()>=31 && result.getAutoExportFrequency() <=58 ){//Case of Monthly Update
										exportSchedulesBox.setValue(exportScheduleMap.get(31));
										exportMonthlySchedulesBox.show();	
										
										exportMonthlySchedulesBox.setValue(monthScheduleMap.get(result.getAutoExportFrequency()));	
									}else if(result.getAutoExportFrequency()>=61 && result.getAutoExportFrequency()<=67){//Case of Weekly Update
										exportSchedulesBox.setValue(exportScheduleMap.get(61));
										exportWeeklySchedulesBox.show();
										
										exportWeeklySchedulesBox.setValue(weekScheduleMap.get(result.getAutoExportFrequency()));				
									}else{
										//Regular case of every N-days
										exportSchedulesBox.setValue(exportScheduleMap.get(result.getAutoExportFrequency()));
									}
									
								}
								
								// auto delete schedule
								if (deleteScheduleMap.get(result.getAutoDeleteFrequency()) != null) {
									deleteSchedulesBox.setValue(deleteScheduleMap.get(result.getAutoDeleteFrequency()));
								}
							}
		                });

	}

	private Grid<ProjectModelDTO> getModelsGrid() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("name", I18N.CONSTANTS.adminProjectModelsName(), 150);
		column.setRenderer(new GridCellRenderer<ProjectModelDTO>() {

			@Override
			public Object render(final ProjectModelDTO model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ProjectModelDTO> store, Grid<ProjectModelDTO> grid) {

				final ToggleAnchor anchor = new ToggleAnchor(model.getName());
				anchor.setAnchorMode(true);

				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						fieldsStore.removeAll();
						fieldsStore.add(model.getGlobalExportElements());
						fieldsStore.commitChanges();
					}

				});
				return anchor;
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<ProjectModelDTO> grid = new Grid<ProjectModelDTO>(modelsStore, cm);
		grid.setStyleName("global-export-fields-table");
		grid.setSize(160, 200);
		grid.getView().setForceFit(true);
		return grid;
	}

	private Grid<FlexibleElementDTO> getElementsGrid() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// checkbox
		ColumnConfig column = new ColumnConfig("globallyExportable", I18N.CONSTANTS.export() + "?", 70);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(final FlexibleElementDTO model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {
				final CheckBox gExportable = AdminUtil.createCheckBox("globallyExportable", null);
				gExportable.setValue(model.getGloballyExportable());
				gExportable.addListener(Events.OnClick, new Listener<FieldEvent>() {

					@Override
					public void handleEvent(FieldEvent be) {
						fieldsMap.put(model.getId(), gExportable.getValue());
					}
				});
				return gExportable;
			}
		});
		configs.add(column);

		// name
		column = new ColumnConfig("label", I18N.CONSTANTS.adminFlexibleName(), 180);
		column.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(FlexibleElementDTO model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) {
				String title = null;
				if (ElementTypeEnum.DEFAULT.equals(model.getElementType())){                         
					title = DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO) model).getType());                    
				} else {
					title = model.getLabel();
				}
				final Text text = AdminUtil.createGridText(title);
				text.setTitle(title);
				return text;
			}

		});
		configs.add(column);
		
		column = new ColumnConfig("container",I18N.CONSTANTS.adminFlexibleContainer(), 120);   
        column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

              @Override
              public Object render(FlexibleElementDTO model, String property,
                        ColumnData config, int rowIndex, int colIndex,
                        ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) { 
                   
                   /**
                   if (ElementTypeEnum.DEFAULT.equals(model.getElementType())){
                        
                        //System.out.println("Property ::: "+property);
                        //System.out.println("Model Properties :::: "+model.getLabel());
                        //System.out.println("Model ::: "+model);
                        //System.out.println("Model Properties :::: "+model.getContainerModel().getProperties().toString());
                        
                        return AdminUtil.createGridText("DEFAULT_CASE");
                   }else{
                   **/
                        BaseModelData container = model.getContainerModel();
                        return AdminUtil.createGridText((String)container.get("name"));
                        
                   //}
              
              
              }
         
        }); 
        configs.add(column);
        
        column = new ColumnConfig("group",I18N.CONSTANTS.adminFlexibleGroup(), 200);
        column.setRenderer(new GridCellRenderer<FlexibleElementDTO>(){

        	@Override
            public Object render(final FlexibleElementDTO model, String property,
            		ColumnData config, int rowIndex, int colIndex,
                    ListStore<FlexibleElementDTO> store, Grid<FlexibleElementDTO> grid) { 
                    
        		LayoutGroupDTO group = model.getGroup();
                return AdminUtil.createGridText((String)group.get("title"));
        	}
        }); 
        configs.add(column);
		
		
		ColumnModel cm = new ColumnModel(configs);
		Grid<FlexibleElementDTO> grid = new Grid<FlexibleElementDTO>(fieldsStore, cm);
		grid.setStyleName("global-export-fields-table");
		grid.setSize(500, 200);
		grid.getView().setForceFit(true);
		return grid;
	}
}

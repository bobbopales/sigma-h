/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.grid.AbstractEditorTreeGridView;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.common.widget.MappingComboBox;
import org.sigmah.shared.command.Month;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.IndicatorRowDTO;
import org.sigmah.shared.dto.SiteDTO;

import java.util.ArrayList;
import java.util.List;
/*
 * @author Alex Bertram
 */

public class HierSiteTree extends AbstractEditorTreeGridView<ModelData, HierSiteEditor>
    implements HierSiteEditor.View {

    private ActivityDTO activity;
    private EditorTreeGrid tree;
    private int firstMonthColumn;
    private MappingComboBox<Month> monthCombo;

    public HierSiteTree(ActivityDTO activity) {
        this.activity = activity;
        setLayout(new BorderLayout());
        setHeading(activity.getDatabase().getName() + " - " + activity.getName());
    }

    public void init(HierSiteEditor editor, TreeStore<ModelData> store) {
        super.init(editor, store);
    }

    @Override
    protected Grid<ModelData> createGridAndAddToContainer(Store store) {

        tree = new EditorTreeGrid((TreeStore) store, createColumnModel());
        tree.setStateful(true);
        tree.setStateId("hierSiteTree" + activity.getId());
        tree.setAutoExpandColumn("name");
        tree.setLoadMask(true);

        tree.setIconProvider(new ModelIconProvider() {
            public AbstractImagePrototype getIcon(ModelData model) {
                if(model instanceof IndicatorRowDTO) {
                    return IconImageBundle.ICONS.indicator();
                } else if(model instanceof SiteDTO) {
                    SiteDTO site = (SiteDTO)model;
                    if(site.hasCoords()) {
                        return IconImageBundle.ICONS.mapped();
                    } else {
                        return IconImageBundle.ICONS.unmapped();
                    }
                }
                return null;
            }
        });
        tree.addListener(Events.BeforeEdit, new Listener<GridEvent>() {
            public void handleEvent(GridEvent ge) {
                if(!presenter.beforeEdit(ge.getRecord(), ge.getProperty())) {
                    ge.setCancelled(true);
                }
            }
        } );

        add(tree, new BorderLayoutData(Style.LayoutRegion.CENTER));

        return tree;
    }

    private ColumnModel createColumnModel() {

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        ColumnConfig indicator = new ColumnConfig("name", I18N.CONSTANTS.indicators(), 150);
        indicator.setSortable(false);
        indicator.setMenuDisabled(true);
        indicator.setRenderer(new TreeGridCellRenderer());
        columns.add(indicator);

        if(activity.getDatabase().isViewAllAllowed()) {
            columns.add(new ColumnConfig("partner", I18N.CONSTANTS.partner(), 100));
        }

        firstMonthColumn = columns.size();
        
        NumberFormat indicatorFormat = NumberFormat.getFormat("0");

        for(int i = 0; i!=7; ++i) {

            NumberField indicatorField = new NumberField();
            indicatorField.getPropertyEditor().setFormat(indicatorFormat);

            ColumnConfig valueColumn = new ColumnConfig("month" + i, "", 75);
            valueColumn.setNumberFormat(indicatorFormat);
            valueColumn.setEditor(new CellEditor(indicatorField));
            valueColumn.setSortable(false);
            valueColumn.setMenuDisabled(true);

            columns.add(valueColumn);
        }

        return new ColumnModel(columns);

    }


    @Override
    protected void initToolBar() {
                            

        toolBar.addSaveSplitButton();
        toolBar.add(new SeparatorToolItem());

        toolBar.addButton(UIActions.add, I18N.MESSAGES.newSite(activity.getLocationType().getName()),
                IconImageBundle.ICONS.add());
        toolBar.addEditButton();
        toolBar.addDeleteButton();

        toolBar.add(new FillToolItem());

        toolBar.add(new LabelToolItem(I18N.CONSTANTS.month() + ": "));

        final MappingComboBox<Month> monthCombo = new MappingComboBox<Month>();
        this.monthCombo = monthCombo;
        this.monthCombo.setEditable(false);
        monthCombo.addListener(Events.Select, new Listener<FieldEvent>() {
            public void handleEvent(FieldEvent be) {
                presenter.onMonthSelected(monthCombo.getMappedValue());
            }
        });


        DateWrapper today = new DateWrapper();
        DateTimeFormat monthFormat = DateTimeFormat.getFormat("MMM yyyy");
        for(int year = today.getFullYear(); year != today.getFullYear()-3; --year) {

            for(int month = 12; month != 0; --month) {
                   
                DateWrapper d= new DateWrapper(year, month, 1);

                Month m = new Month(year, month);
                monthCombo.add(m, monthFormat.format(d.asDate()));
            }
        }
        toolBar.add(monthCombo);


    }

      public void setStartMonth(Month startMonth) {

        DateTimeFormat monthFormat = DateTimeFormat.getFormat("MMM yy");

        Month month = startMonth;
        for(int i=0; i!=7; ++i) {
            DateWrapper date = new DateWrapper(month.getYear(), month.getMonth(), 1);

            String label = monthFormat.format(date.asDate());
            tree.getColumnModel().setColumnHeader(i+firstMonthColumn, label);
            tree.getColumnModel().setDataIndex(i+1, IndicatorRowDTO.propertyName(month));

            month = month.next();
        }

        Month selectedMonth = new Month(startMonth.getYear(), startMonth.getMonth()+3);
        monthCombo.setMappedValue(selectedMonth);

    }


}
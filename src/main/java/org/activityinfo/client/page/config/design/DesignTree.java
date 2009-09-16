package org.activityinfo.client.page.config.design;

import org.activityinfo.client.Application;
import org.activityinfo.client.command.CommandService;
import org.activityinfo.client.common.action.UIActions;
import org.activityinfo.client.common.dialog.FormDialogCallback;
import org.activityinfo.client.common.dialog.FormDialogImpl;
import org.activityinfo.client.common.dialog.FormDialogTether;
import org.activityinfo.client.common.grid.AbstractEditorTreeGridView;
import org.activityinfo.client.common.grid.ImprovedCellTreeGridSelectionModel;
import org.activityinfo.client.page.config.design.ActivityForm;
import org.activityinfo.client.page.config.design.AttributeGroupForm;
import org.activityinfo.client.page.config.design.IndicatorForm;
import org.activityinfo.shared.dto.*;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.widget.grid.*;
import com.extjs.gxt.ui.client.widget.treegrid.*;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.dnd.*;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.event.*;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.Inject;


import java.util.List;
import java.util.ArrayList;
/*
 * @author Alex Bertram
 */

public class DesignTree extends AbstractEditorTreeGridView<ModelData, Designer>
        implements Designer.View {

    protected final CommandService service;

    protected EditorTreeGrid<ModelData> tree;
    protected ContentPanel formContainer;

    protected UserDatabaseDTO db;

    @Inject
    public DesignTree(CommandService service) {
        this.service = service;
    }

    @Override
    public void init(Designer presenter, UserDatabaseDTO db, TreeStore store) {

        this.db = db;

        setLayout(new BorderLayout());
        setHeading(Application.CONSTANTS.design() + " - " + db.getName());
        setIcon(Application.ICONS.design());

        super.init(presenter, store);

        createFormContainer();
    }



    @Override
    protected Grid<ModelData> createGridAndAddToContainer(Store store) {


        final TreeStore treeStore = (TreeStore) store;
        
        tree = new EditorTreeGrid<ModelData>(treeStore, createColumnModel());
        tree.setSelectionModel(new ImprovedCellTreeGridSelectionModel<ModelData>());
        tree.setClicksToEdit(EditorGrid.ClicksToEdit.TWO);
        tree.setAutoExpandColumn("name");
        tree.setHideHeaders(true);
        tree.setLoadMask(true);
        //   tree.setContextMenu(createContextMenu());

        tree.setIconProvider(new ModelIconProvider<ModelData>() {
            public AbstractImagePrototype getIcon(ModelData model) {
                if(model instanceof ActivityModel) {
                    return Application.ICONS.activity();
                } else if(model instanceof Folder) {
                    return GXT.IMAGES.tree_folder_closed();
                } else if(model instanceof AttributeGroupModel) {
                    return Application.ICONS.attributeGroup();
                } else if(model instanceof AttributeModel) {
                    return Application.ICONS.attribute();
                } else if(model instanceof IndicatorModel) {
                    return Application.ICONS.indicator();
                } else {
                    return null;
                }
            }
        });
        tree.addListener(Events.CellClick, new Listener<GridEvent>() {
            public void handleEvent(GridEvent ge) {
                showForm(tree.getStore().getAt(ge.getRowIndex()));
            }
        });

        add(tree, new BorderLayoutData(Style.LayoutRegion.CENTER));

        TreeGridDragSource source = new TreeGridDragSource(tree);
        source.addDNDListener(new DNDListener() {
            @Override
            public void dragStart(DNDEvent e) {

                ModelData sel = ((CellTreeGridSelectionModel) tree.getSelectionModel()).getSelectCell().model;
                if (!db.isDesignAllowed() || sel == null || sel instanceof Folder) {
                    e.setCancelled(true);
                    e.getStatus().setStatus(false);
                    return;
                }
                super.dragStart(e);
            }
        });

        TreeGridDropTarget target = new TreeGridDropTarget(tree);
        target.setAllowSelfAsSource(true);
        target.setFeedback(DND.Feedback.BOTH);
        target.setAutoExpand(false);
        target.addDNDListener(new DNDListener() {
            @Override
            public void dragMove(DNDEvent e) {
                List<TreeModel> sourceData = e.getData();
                ModelData source = sourceData.get(0).get("model");
                TreeGrid.TreeNode target = tree.findNode(e.getTarget());

                if(treeStore.getParent(target.getModel()) !=
                   treeStore.getParent(source)) {

                    e.setCancelled(true);
                    e.getStatus().setStatus(false);
                }
            }

            @Override
            public void dragDrop(DNDEvent e) {
                List<TreeModel> sourceData = e.getData();
                ModelData source = sourceData.get(0).get("model");
                presenter.onNodeDropped(source);
            }
        });
        return tree;
    }


    @Override
    protected void initToolBar() {

        toolBar.addSaveSplitButton();

        SelectionListener<MenuEvent> listener = new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {

                presenter.onNew(ce.getItem().getItemId());
            }
        };

        Menu newMenu = new Menu();
        initNewMenu(newMenu, listener);

        Button newButtonMenu = new Button(Application.CONSTANTS.newText(), Application.ICONS.add());
        newButtonMenu.setMenu(newMenu);
        newButtonMenu.setEnabled(db.isDesignAllowed());
        toolBar.add(newButtonMenu);

        toolBar.addDeleteButton();


    }

    protected void initNewMenu(Menu menu, SelectionListener<MenuEvent> listener) {

        MenuItem newActivity = new MenuItem(Application.CONSTANTS.newActivity(), Application.ICONS.activity(), listener);
        newActivity.setItemId("Activity");
        menu.add(newActivity);

        final MenuItem newAttributeGroup = new MenuItem(Application.CONSTANTS.newAttributeGroup(), Application.ICONS.attributeGroup(), listener);
        newAttributeGroup.setItemId("AttributeGroup");
        menu.add(newAttributeGroup);

        final MenuItem newAttribute = new MenuItem(Application.CONSTANTS.newAttribute(), Application.ICONS.attribute(), listener);
        newAttribute.setItemId("Attribute");
        menu.add(newAttribute);

        final MenuItem newIndicator = new MenuItem(Application.CONSTANTS.newIndicator(), Application.ICONS.indicator(), listener);
        newIndicator.setItemId("Indicator");
        menu.add(newIndicator);


        menu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {

                ModelData sel = getSelection();

                newAttributeGroup.setEnabled(sel != null);
                newAttribute.setEnabled(sel instanceof AttributeGroupModel || sel instanceof AttributeModel);
                newIndicator.setEnabled(sel != null);
            }
        });
    }

    protected void initRemoveMenu(Menu menu) {
        final MenuItem removeItem = new MenuItem(Application.CONSTANTS.delete(), Application.ICONS.delete());
        removeItem.setItemId(UIActions.delete);
        menu.add(removeItem);

    }

    protected Menu createContextMenu() {
        Menu menu = new Menu();

        initNewMenu(menu, null);
        menu.add(new SeparatorMenuItem());
        initRemoveMenu(menu);

        return menu;
    }


    private void createFormContainer() {
        formContainer = new ContentPanel();
        formContainer.setHeaderVisible(false);
        formContainer.setBorders(false);
        formContainer.setFrame(false);

        BorderLayoutData layout = new BorderLayoutData(Style.LayoutRegion.EAST);
        layout.setSplit(true);
        layout.setCollapsible(true);
        layout.setSize(375);
        layout.setMargins(new Margins(0, 0, 0, 5));

        add(formContainer, layout);
    }

    private ColumnModel createColumnModel() {

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

        TextField<String> nameField = new TextField<String>();
        nameField.setAllowBlank(false);

        ColumnConfig nameColumn = new ColumnConfig("name", Application.CONSTANTS.name(), 150);
        nameColumn.setEditor(new CellEditor(nameField));
        nameColumn.setRenderer(new TreeGridCellRenderer());

        columns.add(nameColumn);

        return new ColumnModel(columns);
    }

    protected Class formClassForSelection(ModelData sel) {

        if(sel instanceof ActivityModel) {
            return ActivityForm.class;
        } else if(sel instanceof AttributeGroupModel) {
            return AttributeGroupForm.class;
        } else if(sel instanceof IndicatorModel) {
            return IndicatorForm.class;
        }

        return null;

    }

    protected AbstractDesignForm createForm(ModelData sel) {
        if(sel instanceof ActivityModel) {
            return new ActivityForm(service, db);
        } else if(sel instanceof AttributeGroupModel) {
            return new AttributeGroupForm();
        } else if(sel instanceof AttributeModel) {
            return new AttributeForm();
        } else if(sel instanceof IndicatorModel) {
            return new IndicatorForm();
        }

        return null;
    }


    public void showForm(ModelData model) {

        // do we have the right form?
        Class formClass = formClassForSelection(model);

        AbstractDesignForm currentForm = null;
        if(formContainer.getItemCount() != 0) {
            currentForm = (AbstractDesignForm)formContainer.getItem(0);
        }

        if(formClass == null) {
            if(currentForm!=null) {
                currentForm.getBinding().unbind();
                formContainer.removeAll();
            }
            return;
        } else {

            if( currentForm == null ||
                    (currentForm != null && !formClass.equals(currentForm.getClass()))) {

                if(currentForm != null) {
                    formContainer.removeAll();
                    currentForm.getBinding().unbind();
                }

                currentForm = createForm(model);
                currentForm.setReadOnly(!db.isDesignAllowed());
                currentForm.setHeaderVisible(false);
                currentForm.setBorders(false);
                currentForm.setFrame(false);
                currentForm.getBinding().setStore(tree.getStore());
                formContainer.add(currentForm);
                formContainer.layout();
            }
        }
        currentForm.getBinding().bind(model);
    }

    public FormDialogTether showNewForm(EntityDTO entity, FormDialogCallback callback) {

        AbstractDesignForm form = createForm(entity);
        form.getBinding().bind(entity);

        for(FieldBinding field : form.getBinding().getBindings() ) {
            field.getField().clearInvalid();
        }

        FormDialogImpl dlg=  new FormDialogImpl(form);
        dlg.setWidth(form.getPreferredDialogWidth());
        dlg.setHeight(form.getPreferredDialogHeight());
        dlg.setScrollMode(Style.Scroll.AUTOY);
        
        if(entity instanceof ActivityModel) {
            dlg.setHeading(Application.CONSTANTS.newActivity());
        } else if(entity instanceof AttributeGroupModel) {
            dlg.setHeading(Application.CONSTANTS.newAttributeGroup());
        } else if(entity instanceof AttributeModel) {
            dlg.setHeading(Application.CONSTANTS.newAttribute());
        } else if(entity instanceof IndicatorModel) {
            dlg.setHeading(Application.CONSTANTS.newIndicator());
        }

        dlg.show(callback);

        return dlg;
    }
}

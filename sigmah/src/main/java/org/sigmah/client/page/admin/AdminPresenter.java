package org.sigmah.client.page.admin;

import org.sigmah.client.EventBus;
import org.sigmah.client.SigmahInjector;
import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.event.NavigationEvent;
import org.sigmah.client.event.NavigationEvent.NavigationError;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Frame;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.TabPage;
import org.sigmah.client.page.admin.category.AdminCategoryPresenter;
import org.sigmah.client.page.admin.importation.AdminImportPresenter;
import org.sigmah.client.page.admin.model.common.AdminOneModelPresenter;
import org.sigmah.client.page.admin.model.orgunit.AdminOrgUnitModelsPresenter;
import org.sigmah.client.page.admin.model.project.AdminProjectModelsPresenter;
import org.sigmah.client.page.admin.orgunit.AdminOrgUnitPresenter;
import org.sigmah.client.page.admin.report.AdminReportModelPresenter;
import org.sigmah.client.page.admin.users.AdminUsersPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.client.ui.ToggleAnchor;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.dto.profile.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Loads data for administration screen.
 * 
 * @author nrebiai
 */
public class AdminPresenter implements TabPage, Frame {

	private final static String[] MAIN_TABS = { I18N.CONSTANTS.adminUsers(), I18N.CONSTANTS.adminOrgUnit(),
	                I18N.CONSTANTS.adminProjectModels(), I18N.CONSTANTS.adminOrgUnitsModels(),
	                I18N.CONSTANTS.adminProjectModelReports(), I18N.CONSTANTS.adminCategories(),
	                I18N.CONSTANTS.adminImport(), I18N.CONSTANTS.adminManagementTitle() };
	public static final PageId PAGE_ID = new PageId(I18N.CONSTANTS.adminboard());

	private final View view;
	private final SigmahInjector injector;
	private final AdminSubPresenter[] presenters;
	private ToggleAnchor currentTab;
	private AdminPageState currentState;
	private AdminPageState oldState;
	private Page activePage;
	private final Authentication authentication;
	private AdminOneModelPresenter adminModelPresenter;
	private final String highlightStyle = "border:1px solid; border-radius:10px; "
			+ "border-color: #A8A8A8; width: 148px; heigh:30px;"
			+ "background-color: #A8A8A8; text-align:center;";

	@ImplementedBy(AdminView.class)
	public interface View {

		public void setMainPanel(Widget widget);

		public ContentPanel getTabPanel();

	}

	@Inject
	public AdminPresenter(SigmahInjector injector, final EventBus eventBus, final Dispatcher dispatcher,
	                final View view, final UserLocalCache cache, final Authentication authentication) {
		this.injector = injector;
		this.view = view;
		this.presenters = new AdminSubPresenter[] { new AdminUsersPresenter(dispatcher, cache, authentication),
		                new AdminOrgUnitPresenter(dispatcher, cache, authentication, eventBus),
		                new AdminProjectModelsPresenter(dispatcher, cache, authentication, eventBus, currentState),
		                new AdminOrgUnitModelsPresenter(dispatcher, cache, authentication, eventBus, currentState),
		                new AdminReportModelPresenter(dispatcher), new AdminCategoryPresenter(dispatcher),
		                new AdminImportPresenter(dispatcher), injector.getAdminManagementPresenter() };
		this.authentication = authentication;
		for (int i = 0; i < MAIN_TABS.length; i++) {
			final int index = i;

			String tabTitle = MAIN_TABS[i];

			final VBoxLayoutData layoutData = new VBoxLayoutData();
			layoutData.setMargins(new Margins(0, 0, 10, 0));

			final ToggleAnchor anchor = new ToggleAnchor(tabTitle);
			anchor.setAnchorMode(true);

			anchor.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					eventBus.fireEvent(new NavigationEvent(NavigationHandler.NavigationRequested, currentState
					                .deriveTo(index), null));
				}
			});

			this.view.getTabPanel().add(anchor, layoutData);
		}
	}

	@Override
	public void shutdown() {
	}

	@Override
	public PageId getPageId() {
		return PAGE_ID;
	}

	@Override
	public Object getWidget() {
		if (!ProfileUtils.isGranted(authentication, GlobalPermissionEnum.VIEW_ADMIN)) {
			ContentPanel insufficientView = new ContentPanel();
			final HTML insufficient = new HTML(I18N.CONSTANTS.permAdminInsufficient());
			insufficient.addStyleName("important-label");
			insufficientView.add(insufficient);
			return insufficientView;
		}
		return view;

	}

	@Override
	public void requestToNavigateAway(PageState place, final NavigationCallback callback) {
		NavigationError navigationError = NavigationError.NONE;
		for (SubPresenter subPresenter : presenters) {
			if (subPresenter.hasValueChanged()) {
				navigationError = NavigationError.WORK_NOT_SAVED;
			}
		}
		if (adminModelPresenter != null && adminModelPresenter.hasValueChanged()) {
			navigationError = NavigationError.WORK_NOT_SAVED;
		}

		Listener<MessageBoxEvent> listener = new Listener<MessageBoxEvent>() {

			@Override
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					for (SubPresenter subPresenter : presenters) {
						subPresenter.forgetAllChangedValues();
					}
					if (adminModelPresenter != null)
						adminModelPresenter.forgetAllChangedValues();
					callback.onDecided(NavigationError.NONE);
				}
			}
		};

		if (navigationError == NavigationError.WORK_NOT_SAVED) {
			MessageBox.confirm(I18N.CONSTANTS.unsavedDataTitle(), I18N.CONSTANTS.unsavedDataMessage(), listener);
		}

		callback.onDecided(navigationError);
	}

	@Override
	public String beforeWindowCloses() {
		return null;
	}

	@Override
	public void setActivePage(Page page) {
		this.activePage = page;

	}

	@Override
	public Page getActivePage() {
		return this.activePage;
	}

	@Override
	public AsyncMonitor showLoadingPlaceHolder(PageId pageId, PageState loadingPlace) {
		return null;
	}

	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.adminboard();
	}

	@Override
	public boolean navigate(PageState place) {
		
		// Update the old state
		this.oldState = currentState;
		
		// Update the current state
		final AdminPageState adminPageState = (AdminPageState) place;
		this.currentState = adminPageState;
		
		if(oldState != null) 
			Log.debug("AdminPresenter : navigate oldState " + oldState.getManualPageId());
		Log.debug("AdminPresenter : navigate requestedState " + currentState.getManualPageId());

		// Navigate to tab/subtab
		if (currentState.getModel() != null && currentState.getSubModel() != null) {	
			Log.debug("AdminPresenter : navigating to tab/subtab: " + currentState.serializeAsHistoryToken());
			
			adminModelPresenter = injector.getAdminModelPresenter();
			navigate(currentState, adminModelPresenter);
			
		// Navigate to tab
		} else {
			Log.debug("AdminPresenter : navigating to tab: " + currentState.serializeAsHistoryToken());

			setCurrentView();
			refreshNavigationPanel(false);
		}

		return true;
	}

	public boolean navigate(PageState place, AdminOneModelPresenter adminOneModelPresenter) {
		if (place != null)
			Log.debug("AdminPresenter : navigate special " + ((AdminPageState) place).serializeAsHistoryToken());
		
		// Navigating to tab/subtab => enable going back to tab
		refreshNavigationPanel(true);	
		adminOneModelPresenter.navigate(place, view);
		
		return true;
	}
	
	/*
	 * Refreshes the view of the left navigation panel.
	 * <p>
	 * This method should be independent of setCurrentView
	 * which manages the main display.
	 */
	private void refreshNavigationPanel(boolean enableBack) {
		
		// Updates the old anchor if any
		if (oldState != null) {
			final ToggleAnchor oldAnchor = (ToggleAnchor) view.getTabPanel().getWidget(oldState.getCurrentSection());
			oldAnchor.setAnchorMode(true);
		}
		
		// Sets the new anchor
		final ToggleAnchor anchor = (ToggleAnchor) view.getTabPanel().getWidget(currentState.getCurrentSection());
		
		if (enableBack) {
			anchor.setAnchorMode(true);
		} else {
			anchor.setAnchorMode(false);
		}
		anchor.setHTMLStyle(highlightStyle);
	}
	
	/*
	 * Sets the view on the main display
	 * <p>
	 * This method should be independent of refreshNavigationPanel
	 * which manages the navigationPanel.
	 */
	private void setCurrentView() {
		// Checks if view is up-to-date. Update only if necessary.	
		if(oldState != null) 
			Log.debug("AdminPresenter : navigate oldState " + oldState.getPageId());
		Log.debug("AdminPresenter : navigate requestedState " + currentState.getPageId());
		
		if (oldState == null || !oldState.getManualPageId().equals(currentState.getManualPageId())
				|| (oldState.getManualPageId().equals(currentState.getManualPageId())
						&& oldState.getModel() != null)) {
			
			int index = currentState.getCurrentSection();
			final ToggleAnchor anchor = (ToggleAnchor) view.getTabPanel().getWidget(index);
			currentTab = anchor;
			
			view.setMainPanel(presenters[index].getView());
			presenters[index].viewDidAppear();
			presenters[index].setCurrentState(currentState);
		}
	}
}

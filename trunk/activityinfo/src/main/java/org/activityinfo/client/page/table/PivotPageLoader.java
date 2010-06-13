package org.activityinfo.client.page.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import org.activityinfo.client.inject.AppInjector;
import org.activityinfo.client.page.*;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class PivotPageLoader implements PageLoader {

    private AppInjector injector;

    @Inject
    public PivotPageLoader(AppInjector injector, NavigationHandler pageManager, PageStateSerializer placeSerializer) {
        this.injector = injector;

        pageManager.registerPageLoader(PivotPresenter.Pivot, this);
        placeSerializer.registerParser(PivotPresenter.Pivot, new PivotPageState.Parser());
    }

    @Override
    public void load(PageId pageId, PageState pageState, final AsyncCallback<Page> callback) {

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess() {
                callback.onSuccess(injector.getPivotPresenter());
            }
        });

    }
}

package org.activityinfo.client.page.report;

import org.activityinfo.client.page.PageId;
import org.activityinfo.client.page.PageState;

import java.util.Arrays;
import java.util.List;

/**
 * @author Alex Bertram
 */
public class ReportHomePageState implements PageState {

    public PageId getPageId() {
        return ReportHomePresenter.ReportHome;
    }

    public String serializeAsHistoryToken() {
        return null;
    }

    public List<PageId> getEnclosingFrames() {
        return Arrays.asList(ReportHomePresenter.ReportHome);
    }
}

package org.activityinfo.shared.command.result;

import org.activityinfo.shared.dto.ReportSubscriptionDTO;

import java.util.List;

/**
 *
 * @author Alex Bertram
 */
public class InvitationList extends PagingResult<ReportSubscriptionDTO> {

    public InvitationList() {
    }

    public InvitationList(List<ReportSubscriptionDTO> data) {
        super(data);
    }
}

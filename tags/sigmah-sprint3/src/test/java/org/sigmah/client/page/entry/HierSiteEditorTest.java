/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import org.junit.Test;
import org.sigmah.client.AppEvents;
import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.mock.DispatcherStub;
import org.sigmah.client.mock.DummyData;
import org.sigmah.client.mock.MockEventBus;
import org.sigmah.client.mock.MockStateManager;
import org.sigmah.server.util.DateUtilCalendarImpl;
import org.sigmah.shared.command.GetAdminEntities;
import org.sigmah.shared.command.result.AdminEntityResult;
import org.sigmah.shared.dto.AdminEntityDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.SiteDTO;

import static org.easymock.EasyMock.createNiceMock;
/*
 * @author Alex Bertram
 */

public class HierSiteEditorTest {

    @Test
    public void testOnSiteCreated() {

        // Test Data
        SchemaDTO schema = DummyData.PEARPlus();

        // Collaborator:
        MockEventBus eventBus = new MockEventBus();

        // Collaborator
        DispatcherStub service = new DispatcherStub();
        service.setResult(new GetAdminEntities(1, null, 11), DummyData.PEARPlus_Provinces());
        service.setResult(new GetAdminEntities(2, null, 11), DummyData.PEARPlus_ZS());
        service.setResult(new GetAdminEntities(3, null, 11), new AdminEntityResult());

        // Collaborator:
        HierSiteEditor.View view = createNiceMock(HierSiteEditor.View.class);

        // CLASS Under test
        HierSiteEditor editor = new HierSiteEditor(eventBus, service, new MockStateManager(),
                new DateUtilCalendarImpl(), schema.getActivityById(11), view);

        editor.getTreeStore().getLoader().load();

        // VERIFY that when a site is created, it is added to the store
        SiteDTO newSite = new SiteDTO(3);
        newSite.setActivityId(11);
        newSite.setAdminEntity(1, new AdminEntityDTO(1, 1, "Ituri"));
        newSite.setAdminEntity(2, new AdminEntityDTO(2, 11, 1, "Banana"));
        newSite.setAdminEntity(3, new AdminEntityDTO(3, 113, 11, "Zengo"));

        eventBus.fireEvent(new SiteEvent(AppEvents.SiteCreated, this, newSite));

//        ModelData root = editor.getTreeStore().getRootItems().get(0);
//        Assert.assertEquals("root node is correct", "Ituri", root.get("name"));
//        Assert.assertEquals("ituri children are loaded", 2, editor.getTreeStore().getChildren(root).size());
//
    }

}
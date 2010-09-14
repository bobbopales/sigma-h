/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.report.generator;

import org.sigmah.server.dao.SiteProjectionBinder;
import org.sigmah.server.domain.SiteData;
import org.sigmah.shared.domain.AdminEntity;

public class SiteDataBinder implements SiteProjectionBinder<SiteData>{

	@Override
	public SiteData newInstance(String[] properties, Object[] values) {
		return new SiteData(values);
	}

	
	@Override
	public void addIndicatorValue(SiteData site, int indicatorId,
			int aggregationMethod, double value) {
		
		site.indicatorValues.put(indicatorId, value);
	}


	@Override
	public void setAdminEntity(SiteData site, AdminEntity entity) {
		site.adminNames.put(entity.getLevel().getId(), entity.getName());
        site.adminEntities.put(entity.getLevel().getId(), entity);
	}

	public void setAttributeValue(SiteData site, int attributeId, boolean value) {
        if(value) {
            site.attributes.put(attributeId, value);
        }
	}

}
package org.activityinfo.server.report.generator;

import com.google.inject.Inject;
import org.activityinfo.server.dao.hibernate.PivotDAO;
import org.activityinfo.server.report.util.DateRangeFormat;
import org.activityinfo.server.domain.User;
import org.activityinfo.shared.report.content.FilterDescription;
import org.activityinfo.shared.report.model.*;
import org.activityinfo.shared.date.DateRange;

import java.util.*;

/**
 * Implements functionality common to all generators.
 *
 * @param <T> The type of <code>ReportElement</code> generated by the subclass.
 */
public abstract class BaseGenerator<T extends ReportElement> implements ContentGenerator<T> {
	
	protected final PivotDAO pivotDAO;

	@Inject
	public BaseGenerator(PivotDAO pivotDAO) {
		this.pivotDAO = pivotDAO;
	}

	/**
	 * Resolves an element's filter into a the effective filter, taking into
	 * account inherited restrictions and the overall <code>DateRange</code> of the
     * report.
     *
     * Interaction between the report's date range <code>DateRange</code> and the
     * element's filter is specified in {@link org.activityinfo.shared.report.model.ReportElement#getFilter()}
	 * 
	 * @param element The report element for which to resolve the filter
	 * @param inheritedFilter The <code>Filter</code> that is inherited from the enclosing <code>Report</code> or other container
     * @param dateRange The overall <code>DateRange</code> of the report. This may be <code>null</code>, for example if generation is not
     *                  occuring in the context of an individual element.
     *
     * @return the effective <code>Filter</code>
	 */
	protected Filter resolveEffectiveFilter(T element, Filter inheritedFilter, DateRange dateRange) {

        Filter filter;
        if(inheritedFilter != null) {
			filter = new Filter(element.getFilter(), inheritedFilter);
		} else {
            filter = new Filter(element.getFilter());
        }
        return resolveElementFilter(element, dateRange);
	}

    protected Filter resolveElementFilter(T element, DateRange dateRange) {

        Filter filter = new Filter(element.getFilter());

        if(dateRange != null) {
            if(filter.getMinDate() == null)
                filter.setMinDate(dateRange.getMinDate());
            if(filter.getMaxDate() == null)
                filter.setMaxDate(dateRange.getMaxDate());
        }
        return filter;
    }

    //protected Filter resolveEffectiveFilter()

    protected List<FilterDescription> generateFilterDescriptions(Filter filter, Set<DimensionType> excludeDims, User user) {
	
		List<FilterDescription> list = new ArrayList<FilterDescription>();	
		
		Set<DimensionType> filterDims = filter.getRestrictedDimensions();
		filterDims.removeAll(excludeDims);
		
		for(DimensionType type : filterDims) {

            list.add( new FilterDescription(
				type,
				pivotDAO.getFilterLabels(type, filter.getRestrictions(type)) ) );
		}
		
		if(filter.getMinDate() != null || filter.getMaxDate() != null) {
			DateRangeFormat format = new DateRangeFormat(user.getLocaleObject());
			
			list.add( new FilterDescription(
				DimensionType.Date,
				format.format(filter.getMinDate(), filter.getMaxDate())));
		}
		
		return list;
		
	}

	
	/**
	 * Resolves a templated string using the supplied parameters. Parameters can be referenced
	 * within the template as ${DATE_RANGE}. Case sensitive.
     *
     * @see org.activityinfo.shared.report.model.Report#getFileName()
	 * 
	 * @param template The file name template
	 * @param range The date range of the report
     * @param user  The user for whom the report will be generated (required for the locale)
     * @return The resolved file name
	 */
	protected String resolveTemplate(String template, DateRange range, User user) {

        if(template.indexOf("${DATE_RANGE}") != -1) {
            DateRangeFormat format = new DateRangeFormat(user.getLocaleObject());
            String rangeText = format.format(range);

            return template.replace("${DATE_RANGE}", rangeText);
        } else {
            return template;
        }
	}

}

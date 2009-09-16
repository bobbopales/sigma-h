package org.activityinfo.server.report.renderer.html;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

import org.activityinfo.server.report.renderer.html.CssStyles;
import org.activityinfo.server.report.renderer.html.HtmlRenderer;
import org.activityinfo.server.report.renderer.html.HtmlReportUtil;
import org.activityinfo.server.report.util.HtmlTableCellTag;
import org.activityinfo.server.report.util.HtmlWriter;
import org.activityinfo.shared.report.content.TableData;
import org.activityinfo.shared.report.model.TableElement;
import org.activityinfo.shared.report.model.TableElement.Column;

public class HtmlTableRenderer implements HtmlRenderer<TableElement> {


    protected class CellRenderer {
		
		public String getStyleNames(Object value) {
			return null;
		}
		
		public String render(Object value) {
			if(value == null) {
				return "&nbsp;";
			} else {
				return value.toString();
			}
		}
	}
		
	@Override
	public void render(HtmlWriter html, ImageStorageProvider isp,
			TableElement element) throws IOException {



		/*
		 * Generate the title
		 */
		
		if(element.getTitle() != null) {
			html.header(2, element.getTitle());
		}
		
		/*
		 * Generate descriptions of the filter for dimensions that are
		 * filtered, but are not row/col headers
		 */
		
		HtmlReportUtil.generateFilterDescriptionHtml(html,
                element.getContent().getFilterDescriptions());

		
		/*
		 * Generate the html for the table
		 */

        TableData tableData = element.getContent().getData();

		
		if(tableData.isEmpty()) {
			html.div("Aucune données");
		} else {

			
			html.startTable();
			html.startTableHeader();
		
			int depth = element.getRootColumn().getDepth();
			
			for(int i = 1; i <= depth; ++i) {
			
				html.startTableRow();
				
				for(Column column : element.getRootColumn().getDescendantsAtDepth(i)) {
					
					HtmlTableCellTag cell = html.tableCell(column.getLabel());
					cell.colSpan( column.getLeaves().size() );
					
					if(i == 1) {
						cell.rowSpan( depth - column.getDepth() );
					}
				}
				
				html.endTableRow();
				
			}
			
			html.endTableHeader();
			
			html.startTableBody();
		
			List<Column> leaves = element.getRootColumn().getLeaves();
			CellRenderer[] renderers = createRenderers(element, tableData, leaves );
			int[] colIndexes = calcColIndexes(element, tableData, leaves);
			
			for(TableData.Row row : tableData.getRows()) {
				
				html.startTableRow();
				
				for(int i=0; i!=colIndexes.length; ++i) {
					
					Object value = row.values[colIndexes[i]];
									
					html.tableCell(renderers[i].render(value))
						.styleName(renderers[i].getStyleNames(value));					
				}
				
				html.endTableRow();
			}
						
			html.endTableBody();
			html.endTable();
		}
	}
	
	protected int[] calcColIndexes(TableElement element, TableData data, List<Column> leaves) {

		int[] colIndexes = new int[leaves.size()];
		
		for(int i=0; i!=leaves.size(); ++i) {

			colIndexes[i] = data.getColumnIndex(leaves.get(i));
		
		}
		
		return colIndexes;
	}
	
	protected CellRenderer[] createRenderers(TableElement element, TableData tableData, List<Column> leaves) {
		
		CellRenderer[] renderers = new CellRenderer[leaves.size()];
		
		NumberFormat indicatorFormat = NumberFormat.getInstance();
		indicatorFormat.setMaximumFractionDigits(0);
		indicatorFormat.setGroupingUsed(true);
		
		NumberFormat indicatorAvgFormat = NumberFormat.getInstance();
		indicatorAvgFormat.setMinimumFractionDigits(0);
		indicatorAvgFormat.setMaximumFractionDigits(0);
		
		for(int i=0; i!=leaves.size(); ++i) {
			
			if("indicator".equals(leaves.get(i).getProperty())) {


				//if(indicator.getAggregation() == AggregationMethod.Average.code()) {
				//	renderers[i] = new NumberCellRenderer(indicatorAvgFormat, "&nbsp;", "0");
				//} else {
					renderers[i] = new NumberCellRenderer(indicatorAvgFormat, "&nbsp;", "&nbsp;");
				//}
			} else { 
				
				renderers[i] = new CellRenderer();
			}
		}
		
		return renderers;
	}
	

	protected class NumberCellRenderer extends CellRenderer {
		
		private NumberFormat format;
		private String nullString;
		private String zeroString;
		
		public NumberCellRenderer(NumberFormat format, String nullString, String zeroString) {
			this.format = format;
			this.nullString = nullString;
			this.zeroString = zeroString;
		}
		
		@Override
		public String getStyleNames(Object value) {
			return CssStyles.VALUE_CELL;
		}
		
		@Override
		public String render(Object value) {
	
			if(value == null) {
				return nullString;
			} else {
				
				double dvalue = ((Number)value).doubleValue();
		
				if(dvalue == 0.0) {
					return zeroString;
				} else {
					return format.format(dvalue);	
				}
			}
		}
	}

}

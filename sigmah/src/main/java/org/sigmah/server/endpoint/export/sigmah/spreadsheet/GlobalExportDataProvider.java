/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.endpoint.export.sigmah.spreadsheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.quartz.SchedulerException;
import org.sigmah.server.Translator;
import org.sigmah.server.UIConstantsTranslator;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.hibernate.GlobalExportHibernateDAO;
import org.sigmah.server.endpoint.export.sigmah.ExporterUtil;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants.MultiItemText;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.data.LogFrameExportData;
import org.sigmah.server.endpoint.gwtrpc.handler.GetValueHandler;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.ValueResultUtils;
import org.sigmah.shared.domain.Country;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.PhaseModel;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.element.BudgetElement;
import org.sigmah.shared.domain.element.DefaultFlexibleElement;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.domain.element.QuestionChoiceElement;
import org.sigmah.shared.domain.element.QuestionElement;
import org.sigmah.shared.domain.element.TextAreaElement;
import org.sigmah.shared.domain.export.GlobalExport;
import org.sigmah.shared.domain.export.GlobalExportContent;
import org.sigmah.shared.domain.layout.Layout;
import org.sigmah.shared.domain.layout.LayoutConstraint;
import org.sigmah.shared.domain.layout.LayoutGroup;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.dto.value.TripletValueDTO;
import org.sigmah.shared.exception.CommandException;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Utility class Provides global export data
 * 
 * @author sherzod
 */
@Singleton
public class GlobalExportDataProvider {

	public static class ValueLabel {
		private String label;
		private Object value;
		private int lines = 1;

		public ValueLabel(String label, Object value) {
			this.label = label;
			this.value = value;
		}

		public ValueLabel(String label, Object value, int lines) {
			this.label = label;
			this.value = value;
			this.lines = lines;
		}

		public String getFormattedLabel() {
			return clearHtmlFormatting(label);
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		public int getLines() {
			return lines;
		}

	}

	private final Injector injector;
	private final CsvBuilder csvBuilder;
	private final CsvParser csvParser;

	@Inject
	public GlobalExportDataProvider(final Injector injector) {
		this.injector = injector;
		this.csvBuilder = new CsvBuilder();
		this.csvParser = new CsvParser();
	}

	public void persistGlobalExportDataAsCsv(final GlobalExport globalExport, EntityManager em,
	                Map<String, List<String[]>> exportData) throws SchedulerException {
		for (final String pModelName : exportData.keySet()) {
			final GlobalExportContent content = new GlobalExportContent();
			content.setGlobalExport(globalExport);
			content.setProjectModelName(pModelName);
			content.setCsvContent(csvBuilder.buildCsv(exportData.get(pModelName)));
			em.persist(content);
		}
	}

	public Map<String, List<String[]>> getBackedupGlobalExportData(EntityManager em, Integer gExportId) {
		final Map<String, List<String[]>> exportData = new TreeMap<String, List<String[]>>();
		final GlobalExport export = em.find(GlobalExport.class, new Long(gExportId.longValue()));
		final List<GlobalExportContent> contents = export.getContents();
		for (final GlobalExportContent content : contents) {
			final List<String[]> csvData = csvParser.parseCsv(content.getCsvContent());
			exportData.put(content.getProjectModelName(), csvData);
		}
		return exportData;
	}

	public Map<String, List<String[]>> generateGlobalExportData(final Integer organizationId,
	                EntityManager entityManager, final String localeString) throws SchedulerException {
		if (entityManager == null) {
			entityManager = injector.getInstance(EntityManager.class);
		}

		Locale locale = null;
		if (localeString != null)
			locale = new Locale(localeString);
		Translator translator = new UIConstantsTranslator(new Locale(""));

		final GlobalExportDAO exportDAO = new GlobalExportHibernateDAO(entityManager);
		final Organization organization = entityManager.find(Organization.class, organizationId);
		final List<ProjectModel> pModels = exportDAO.getProjectModelsByOrganization(organization);
		final List<Project> projects = exportDAO.getProjects(pModels);

		// project model and its projects
		final Map<String, List<Project>> pModelProjectsMap = new HashMap<String, List<Project>>();
		for (final Project project : projects) {
			if (project.getDateDeleted() == null) {
				final String pModelName = project.getProjectModel().getName();

				List<Project> pModelProjects = pModelProjectsMap.get(pModelName);
				if (pModelProjects == null) {
					pModelProjects = new ArrayList<Project>();
					pModelProjectsMap.put(pModelName, pModelProjects);
				}
				pModelProjects.add(project);
			}
		}

		// project model and its globally exportable fields
		final Map<String, List<FlexibleElement>> pModelElementsMap = new HashMap<String, List<FlexibleElement>>();
		for (final ProjectModel projectModel : pModels) {
			if (projectModel.getStatus() != ProjectModelStatus.DRAFT) {
				final String pModelName = projectModel.getName();

				final List<FlexibleElement> pModelElements = new ArrayList<FlexibleElement>();
				pModelElementsMap.put(pModelName, pModelElements);

				// detail elements
				fillElementList(pModelElements, projectModel.getProjectDetails().getLayout());

				// phase elements
				for (final PhaseModel phaseModel : projectModel.getPhases()) {
					fillElementList(pModelElements, phaseModel.getLayout());
				}
			}
		}

		final CommandHandler<GetValue> handler = new GetValueHandler(entityManager, injector.getInstance(Mapper.class));

		final Map<String, List<String[]>> pModelExportDataMap = new TreeMap<String, List<String[]>>();

		// collect export data
		for (final String pModelName : pModelElementsMap.keySet()) {

			// if no project for a given project model, skip even headers for
			// flexible elements
			if (pModelProjectsMap.get(pModelName) == null)
				continue;

			final List<FlexibleElement> elements = pModelElementsMap.get(pModelName);
			final List<String[]> exportData = new ArrayList<String[]>();
			pModelExportDataMap.put(pModelName, exportData);

			// field titles
			final String[] titles = new String[elements.size()];

			boolean isFirstLine = true;
			// projects
			for (final Project project : pModelProjectsMap.get(pModelName)) {

				final String[] values = new String[elements.size()];

				int titleIndex = 0;
				int valueIndex = 0;

				// fields
				for (final FlexibleElement element : elements) {

					// command to get element value
					final String elementName = "element." + element.getClass().getSimpleName();
					final GetValue command = new GetValue(project.getId(), element.getId(), elementName, null);
					try {
						final ValueResult valueResult = (ValueResult) handler.execute(command, null);

						// prepare value and label
						ValueLabel pair = null;
						/* DEF FLEXIBLE */
						if (elementName.equals("element.DefaultFlexibleElement")
						                || elementName.equals("element.BudgetElement")) {
							pair = getDefElementPair(valueResult, element, project, entityManager, locale, translator);

						} else /* CHECKBOX */if (elementName.equals("element.CheckboxElement")) {
							pair = getCheckboxElementPair(valueResult, element, locale, translator);
						} else /* TEXT AREA */if (elementName.equals("element.TextAreaElement")) {
							pair = getTextAreaElementPair(valueResult, element);

						}/* TRIPLET */
						if (elementName.equals("element.TripletsListElement")) {
							pair = getTripletPair(element, valueResult);

						}/* CHOICE */
						if (elementName.equals("element.QuestionElement")) {
							pair = getChoicePair(element, valueResult);
						}

						// titles

						if (isFirstLine) {
							titles[titleIndex++] = pair != null ? pair.getFormattedLabel() : null;
						}

						// values
						String valueStr = null;
						if (pair != null) {
							Object value = pair.getValue();
							if (value == null) {
								valueStr = null;
							} else if (value instanceof String) {
								valueStr = (String) value;
							} else if (value instanceof Double) {
								Double d = (Double) value;
								valueStr = LogFrameExportData.AGGR_AVG_FORMATTER.format(d.doubleValue());
							} else if (value instanceof Long) {
								Long l = (Long) value;
								valueStr = LogFrameExportData.AGGR_SUM_FORMATTER.format(l.longValue());
							} else { // date
								valueStr = ExportConstants.EXPORT_DATE_FORMAT.format((Date) value);
							}
						}

						values[valueIndex++] = valueStr;

					} catch (CommandException e) {
						Log.error("Failed to get element value" + e.getMessage());
					}

				}

				// add titles
				if (isFirstLine) {
					exportData.add(titles);
					isFirstLine = false;
				}

				// add values
				exportData.add(values);

			}// projects

		}

		return pModelExportDataMap;
	}

	public MultiItemText formatMultipleChoices(List<QuestionChoiceElement> list, String values) {
		final List<Long> selectedChoicesId = ValueResultUtils.splitValuesAsLong(values);
		final StringBuffer builder = new StringBuffer();
		int lines = 1;
		for (QuestionChoiceElement choice : list) {
			for (Long id : selectedChoicesId) {
				if (id.equals(choice.getId())) {
					builder.append(" - ");
					if (choice.getCategoryElement() != null) {
						builder.append(choice.getCategoryElement().getLabel());
					} else {
						builder.append(choice.getLabel());
					}
					builder.append("\n");
					lines++;
				}
			}
		}
		String value = null;
		if (lines > 1) {
			value = builder.substring(0, builder.length() - 1);
			lines--;
		}
		return new MultiItemText(value, lines);
	}

	public MultiItemText formatTripletValues(List<ListableValue> list) {
		int lines = list.size() + 1;
		final StringBuilder builder = new StringBuilder();
		for (ListableValue s : list) {
			final TripletValueDTO tripletValue = (TripletValueDTO) s;
			builder.append(" - ");
			builder.append(tripletValue.getCode());
			builder.append(" - ");
			builder.append(tripletValue.getName());
			builder.append(" : ");
			builder.append(tripletValue.getPeriod());
			builder.append("\n");
		}
		String value = null;
		if (lines > 1) {
			value = builder.substring(0, builder.length() - 2);
			lines--;
		}

		return new MultiItemText(value, lines);
	}

	public ValueLabel getTripletPair(final FlexibleElement element, final ValueResult valueResult) {
		String value = null;
		int lines = 1;

		if (valueResult != null && valueResult.isValueDefined()) {
			final MultiItemText item = formatTripletValues(valueResult.getValuesObject());
			value = item.text;
			lines = item.lineCount;
		}

		return new ValueLabel(element.getLabel(), value, lines);
	}

	public ValueLabel getChoicePair(final FlexibleElement element, final ValueResult valueResult) {
		String value = null;
		int lines = 1;

		if (valueResult != null && valueResult.isValueDefined()) {
			final QuestionElement questionElement = (QuestionElement) element;
			if (questionElement.getIsMultiple()) {
				final MultiItemText item = formatMultipleChoices(questionElement.getChoices(),
				                valueResult.getValueObject());
				value = item.text;
				lines = item.lineCount;

			} else {
				final String idChoice = (String) valueResult.getValueObject();
				for (QuestionChoiceElement choice : questionElement.getChoices()) {
					if (idChoice.equals(String.valueOf(choice.getId()))) {
						if (choice.getCategoryElement() != null) {
							value = choice.getCategoryElement().getLabel();
						} else {
							value = choice.getLabel();
						}
						break;
					}
				}
			}
		}

		return new ValueLabel(element.getLabel(), value, lines);
	}

	public ValueLabel getTextAreaElementPair(final ValueResult valueResult, final FlexibleElement element) {

		Object value = null;
		final TextAreaElement textAreaElement = (TextAreaElement) element;

		if (valueResult != null && valueResult.isValueDefined()) {
			String strValue = (String) valueResult.getValueObject();
			if (textAreaElement.getType() != null) {
				switch (textAreaElement.getType()) {
				// Number
				case 'N': {
					if (textAreaElement.getIsDecimal()) {
						value = Double.parseDouble(strValue);
					} else {
						value = Long.parseLong(strValue);

					}
				}
					break;
				case 'D': {
					value = new Date(Long.parseLong(strValue));
				}
					break;
				default: {
					value = strValue;
				}
					break;
				}
			} else {
				value = strValue;
			}

		}

		return new ValueLabel(element.getLabel(), value);
	}

	public ValueLabel getCheckboxElementPair(final ValueResult valueResult, final FlexibleElement element,
	                final Locale locale, final Translator translator) {
		String value = translator.translate("no", locale);
		if (valueResult != null && valueResult.getValueObject() != null) {
			if (((String) valueResult.getValueObject()).equalsIgnoreCase("true"))
				value = translator.translate("yes", locale);

		}
		return new ValueLabel(element.getLabel(), value);
	}

	private String getUserName(User u) {

		String name = "";
		if (u != null)
			name = u.getFirstName() != null ? u.getFirstName() + " " + u.getName() : u.getName();

		return name;
	}

	public ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element,
	                final Object object, final Class clazz, final EntityManager entityManager, final Locale locale,
	                final Translator translator) {
		if (clazz.equals(Project.class)) {
			return getDefElementPair(valueResult, element, (Project) object, entityManager, locale, translator);
		} else {
			return getDefElementPair(valueResult, element, (OrgUnit) object, entityManager, locale, translator);
		}
	}

	public ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element,
	                final Project project, final EntityManager entityManager, final Locale locale,
	                final Translator translator) {

		Object value = null;
		String label = ExporterUtil.getFlexibleElementLabel(element, translator, locale);

		final DefaultFlexibleElement defaultElement = (DefaultFlexibleElement) element;

		boolean hasValue = valueResult != null && valueResult.isValueDefined();

		switch (defaultElement.getType()) {
		case CODE: {
			if (hasValue) {
				value = valueResult.getValueObject();
			} else {
				value = project.getName();
			}
		}
			break;
		case TITLE: {
			if (hasValue) {
				value = valueResult.getValueObject();
			} else {
				value = project.getFullName();
			}
		}
			break;
		case START_DATE: {
			if (hasValue) {
				value = new Date(Long.parseLong(valueResult.getValueObject()));
			} else {
				value = project.getStartDate();
			}
		}
			break;
		case END_DATE: {
			if (hasValue) {
				value = new Date(Long.parseLong(valueResult.getValueObject()));
			} else {
				value = "";
				if (project.getEndDate() != null)
					value = project.getEndDate();
			}
		}
			break;
		case BUDGET: {
			BudgetElement budgetElement = (BudgetElement) element;

			Double pb = 0d;
			Double sb = 0d;
			if (hasValue) {
				final Map<Integer, String> values = ValueResultUtils.splitMapElements(valueResult.getValueObject());

				if (budgetElement.getRatioDividend() != null) {
					if (values.get(budgetElement.getRatioDividend().getId().intValue()) != null) {
						pb = Double.valueOf(values.get(budgetElement.getRatioDividend().getId().intValue()));

					}
				}

				if (budgetElement.getRatioDivisor() != null) {
					if (values.get(budgetElement.getRatioDivisor().getId().intValue()) != null) {
						sb = Double.valueOf(values.get(budgetElement.getRatioDivisor().getId().intValue()));

					}
				}
			}
			value = sb + " / " + pb;
		}
			break;
		case COUNTRY: {
			if (hasValue) {
				int countryId = Integer.parseInt(valueResult.getValueObject());
				value = entityManager.find(Country.class, countryId).getName();
			} else {
				value = project.getCountry().getName();
			}
		}
			break;
		case OWNER: {
			if (hasValue) {
				value = valueResult.getValueObject();
			} else {
				value = getUserName(project.getOwner());
			}
		}
			break;
		case MANAGER: {
			if (hasValue) {
				int userId = Integer.parseInt(valueResult.getValueObject());
				value = getUserName(entityManager.find(User.class, userId));
			} else {
				value = getUserName(project.getManager());
			}
		}
			break;
		case ORG_UNIT: {
			int orgUnitId = -1;
			if (hasValue) {
				orgUnitId = Integer.parseInt(valueResult.getValueObject());
			} else {
				for (final OrgUnit orgUnit : project.getPartners()) {
					orgUnitId = orgUnit.getId();
					break;
				}
			}
			OrgUnit orgUnit = entityManager.find(OrgUnit.class, orgUnitId);
			if (orgUnit != null)
				value = orgUnit.getName() + " - " + orgUnit.getFullName();

		}
			break;

		}
		return new ValueLabel(label, value);
	}

	public ValueLabel getDefElementPair(final ValueResult valueResult, final FlexibleElement element,
	                final OrgUnit orgUnit, final EntityManager entityManager, final Locale locale,
	                final Translator translator) {
		Object value = null;
		String label = ExporterUtil.getFlexibleElementLabel(element, translator, locale);

		final DefaultFlexibleElement defaultElement = (DefaultFlexibleElement) element;

		boolean hasValue = valueResult != null && valueResult.isValueDefined();

		switch (defaultElement.getType()) {
		case CODE: {
			if (hasValue) {
				value = valueResult.getValueObject();
			} else {
				value = orgUnit.getName();
			}
		}
			break;
		case TITLE: {
			if (hasValue) {
				value = valueResult.getValueObject();
			} else {
				value = orgUnit.getFullName();
			}
		}
			break;

		case BUDGET: {
			BudgetElement budgetElement = (BudgetElement) element;

			Double pb = 0d;
			Double sb = 0d;
			if (hasValue) {
				final Map<Integer, String> values = ValueResultUtils.splitMapElements(valueResult.getValueObject());

				if (budgetElement.getRatioDividend() != null) {
					if (values.get(budgetElement.getRatioDividend().getId()) != null) {
						pb = Double.valueOf(values.get(budgetElement.getRatioDividend().getId()));

					}
				}

				if (budgetElement.getRatioDivisor() != null) {
					if (values.get(budgetElement.getRatioDivisor().getId()) != null) {
						pb = Double.valueOf(values.get(budgetElement.getRatioDivisor().getId()));

					}
				}
			}
			value = sb + " / " + pb;
		}
			break;
		case COUNTRY: {
			if (hasValue) {
				int countryId = Integer.parseInt(valueResult.getValueObject());
				value = entityManager.find(Country.class, countryId).getName();
			} else {
				value = orgUnit.getOfficeLocationCountry().getName();
			}
		}
			break;
		case MANAGER: {
			if (hasValue) {
				int userId = Integer.parseInt(valueResult.getValueObject());
				value = getUserName(entityManager.find(User.class, userId));
			} else {
				value = "";
			}
		}
			break;
		case ORG_UNIT: {
			OrgUnit parentOrgUnit = orgUnit.getParent();
			if (parentOrgUnit == null)
				parentOrgUnit = orgUnit;
			value = parentOrgUnit.getName() + " - " + parentOrgUnit.getFullName();
		}
			break;
		}
		return new ValueLabel(label, value);
	}

	public static String clearHtmlFormatting(String text) {
		if (text != null && text.length() > 0) {
			text = text.replaceAll("<br>", " ");
			text = text.replaceAll("<[^>]+>|\\n", "");
			text = text.trim().replaceAll(" +", " ");
		}
		return text;
	}

	private void fillElementList(final List<FlexibleElement> elements, final Layout layout) {
		for (final LayoutGroup group : layout.getGroups()) {
			for (final LayoutConstraint constraint : group.getConstraints()) {
				final FlexibleElement element = constraint.getElement();
				if (element.isGloballyExportable())
					elements.add(element);
			}
		}
	}

}

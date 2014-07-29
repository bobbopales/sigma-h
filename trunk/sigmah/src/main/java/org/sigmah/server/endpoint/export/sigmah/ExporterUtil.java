package org.sigmah.server.endpoint.export.sigmah;

import java.util.Locale;

import org.sigmah.server.Translator;
import org.sigmah.shared.domain.element.DefaultFlexibleElement;
import org.sigmah.shared.domain.element.DefaultFlexibleElementType;
import org.sigmah.shared.domain.element.FlexibleElement;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

public class ExporterUtil {
	/**
	 * Gets the label of the flexible element
	 * 
	 * @param fleDTO
	 * @param translator
	 * @param locale
	 * @return
	 */
	public static String getFlexibleElementLabel(FlexibleElementDTO fleDTO, Translator translator, Locale locale) {
		String fleName = null;
		if (fleDTO.getLabel() != null) {
			fleName = fleDTO.getLabel();
		} else if (fleDTO instanceof DefaultFlexibleElementDTO) {
			fleName = getFlexibleElementLabel(((DefaultFlexibleElementDTO) fleDTO).getType(), translator, locale);
		}
		return fleName;
	}

	/**
	 * Gets the label of the flexible element
	 * 
	 * @param fle
	 * @param translator
	 * @param locale
	 * @return
	 */
	public static String getFlexibleElementLabel(FlexibleElement fle, Translator translator, Locale locale) {
		String fleName = null;
		if (fle.getLabel() != null) {
			fleName = fle.getLabel();
		} else if (fle instanceof DefaultFlexibleElement) {
			fleName = getFlexibleElementLabel(((DefaultFlexibleElement) fle).getType(), translator, locale);
		}
		return fleName;
	}

	/**
	 * Gets the label of the default flexible element type
	 * 
	 * @param type
	 * @param translator
	 * @param locale
	 * @return
	 */
	public static String getFlexibleElementLabel(DefaultFlexibleElementType type, Translator translator, Locale locale) {
		String fleName = null;
		switch (type) {
		case BUDGET:
			fleName = translator.translate("projectBudget", locale);
			break;
		case CODE:
			fleName = translator.translate("projectName", locale);
			break;
		case COUNTRY:
			fleName = translator.translate("projectCountry", locale);
			break;
		case END_DATE:
			fleName = translator.translate("projectEndDate", locale);
			break;
		case MANAGER:
			fleName = translator.translate("projectManager", locale);
			break;
		case ORG_UNIT:
			fleName = translator.translate("orgUnit", locale);
			break;
		case OWNER:
			fleName = translator.translate("projectOwner", locale);
			break;
		case START_DATE:
			fleName = translator.translate("projectStartDate", locale);
			break;
		case TITLE:
			fleName = translator.translate("projectFullName", locale);
			break;
		default:
			break;
		}
		return fleName;
	}
}

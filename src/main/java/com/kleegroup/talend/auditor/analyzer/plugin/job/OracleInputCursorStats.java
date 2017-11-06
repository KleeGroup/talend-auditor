package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.List;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.Util;

public class OracleInputCursorStats extends AbstractNodeJobAnalyzerPlugin {
	private int nbOracleInput = 0;
	private int nbOracleInputWCursor = 0;

	@Override
	public void analyzeNodeList(final List<Element> elemlist, final String jobName) {
		for (final Element node : elemlist) {
			if ("tOracleInput".equals(node.getAttribute("componentName"))) {
				doAnalyze(node);
			}
		}
	}

	private void doAnalyze(final Element elem) {
		nbOracleInput++;

		final Boolean useCursor = Boolean.valueOf(Util.getCompoParameterValue(elem, "CHECK", "USE_CURSOR"));
		if (useCursor) {
			nbOracleInputWCursor++;
		}
	}

	@Override
	public String getResults() {
		if (nbOracleInput == 0) {
			return "Pas de composant tOracleInput dans le projet";
		}
		return "Nombre de tOracleInput avec cursor : " + nbOracleInputWCursor + " (" + Util.divideRoundPercent(nbOracleInputWCursor, nbOracleInput) + ")";
	}

}

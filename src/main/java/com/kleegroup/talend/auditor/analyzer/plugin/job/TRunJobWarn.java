package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.List;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.Util;

public class TRunJobWarn extends AbstractNodeJobAnalyzerPlugin {
	private final StringBuilder result = new StringBuilder();

	@Override
	public void analyzeNodeList(final List<Element> elemlist, final String jobName) {
		for (final Element node : elemlist) {
			if ("tRunJob".equals(node.getAttribute("componentName"))) {
				doAnalyze(node, jobName);
			}
		}
	}

	private void doAnalyze(final Element elem, final String jobName) {
		final Boolean transmitContext = Boolean.valueOf(Util.getCompoParameterValue(elem, "CHECK", "TRANSMIT_WHOLE_CONTEXT"));

		if (!transmitContext) {
			final String compoName = Util.getCompoName(elem);
			result.append("\n\t" + jobName + " - " + compoName + " : Le tRunJob ne transmets pas le contexte !");
		}
	}

	@Override
	public String getResults() {
		return "Alertes concernant les tRunJob :" + result.toString();
	}

}

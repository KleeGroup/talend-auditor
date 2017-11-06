package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.Util;
import com.kleegroup.talend.auditor.talend.TalendItem;
import com.kleegroup.talend.auditor.talend.TalendItemListing;

public class JobHierarchyPlugin extends AbstractNodeJobAnalyzerPlugin {
	private final TalendItemListing til;

	private final Set<String> allRunJobName = new HashSet<>();

	public JobHierarchyPlugin(final TalendItemListing til) {
		this.til = til;
	}

	@Override
	public void analyzeNodeList(final List<Element> elemlist, final String jobName) {
		for (final Element node : elemlist) {
			if ("tRunJob".equals(node.getAttribute("componentName"))) {
				final String jobDest = Util.getCompoParameterValue(node, "PROCESS_TYPE", "PROCESS");
				allRunJobName.add(jobDest);
			}
		}
	}

	@Override
	public String getResults() {
		final List<String> allStartingJobName = til.getItemDistinctList().stream()
				.map(TalendItem::getName)
				.filter(e -> !allRunJobName.contains(e))
				.collect(Collectors.toList());

		final StringBuilder retour = new StringBuilder(52 + allStartingJobName.size() * 30);

		retour.append("Liste des jobs jamais appelés par un autre job (");
		retour.append(allStartingJobName.size());
		retour.append(") :");

		for (final String name : allStartingJobName) {
			retour.append("\n\t" + name);
		}

		return retour.toString();
	}

}

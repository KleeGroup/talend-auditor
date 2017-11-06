package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.List;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.talend.TalendItemListing;

public class JobStats extends AbstractNodeJobAnalyzerPlugin {
	private final TalendItemListing til;

	public JobStats(final TalendItemListing til) {
		this.til = til;
	}

	@Override
	public void analyzeNodeList(final List<Element> elemlist, final String jobName) {
		// RAS
	}

	@Override
	public String getResults() {
		return "Nombre total de jobs : " + til.getItemDistinctList().size() + "\n" +
				"Nombre d'ancienne version de jobs : " + (til.getItemList().size() - til.getItemDistinctList().size()) + "\n" +
				"Version maximale des job : " + til.getVersionMax();
	}

}

package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.List;

import org.w3c.dom.Element;

public interface IJobAnalyzerPlugin {

	public abstract void analyze(List<Element> elemlist, String jobName);

	public abstract String getResults();

}

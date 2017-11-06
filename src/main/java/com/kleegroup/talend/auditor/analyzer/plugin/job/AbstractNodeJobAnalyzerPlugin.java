package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

public abstract class AbstractNodeJobAnalyzerPlugin implements IJobAnalyzerPlugin {

	@Override
	public final void analyze(final List<Element> elemlist, final String jobName) {
		final List<Element> listNode = elemlist.stream()
				.filter(e -> "node".equals(e.getNodeName()))
				.collect(Collectors.toList());

		analyzeNodeList(listNode, jobName);
	}

	public abstract void analyzeNodeList(List<Element> elemlist, String jobName);

}

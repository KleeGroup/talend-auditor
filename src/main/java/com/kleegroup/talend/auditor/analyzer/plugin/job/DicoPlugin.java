package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

public class DicoPlugin extends AbstractNodeJobAnalyzerPlugin {

	private final Map<String, Integer> compDic = new HashMap<>();

	@Override
	public void analyzeNodeList(final List<Element> elemlist, final String jobName) {
		for (final Element comp : elemlist) {
			final String compName = comp.getAttribute("componentName");
			compDic.put(compName, compDic.getOrDefault(compName, 0) + 1);
		}
	}

	@Override
	public String getResults() {
		return "Dictionnaire de composants (" + compDic.size() + " types) :\n" +
				compDic.entrySet().stream()
						.sorted(Comparator.comparing(Entry::getValue, Comparator.reverseOrder()))
						.map(c -> "\t" + c.getKey() + " : " + c.getValue())
						.collect(Collectors.joining("\n"));
	}

}

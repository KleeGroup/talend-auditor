package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.Util;

public class LinkStats implements IJobAnalyzerPlugin {

	private int maxCompoIn = 0;
	private List<String> maxCompoInJobList = new ArrayList<>();
	private int maxCompoOut = 0;
	private List<String> maxCompoOutJobList = new ArrayList<>();
	private int nbCompoSup5In = 0;
	private int nbCompoSup5Out = 0;

	@Override
	public void analyze(final List<Element> elemlist, final String jobName) {
		final List<Element> listNode = elemlist.stream()
				.filter(e -> "connection".equals(e.getNodeName()))
				.collect(Collectors.toList());

		analyzeConnectionList(listNode, jobName);
	}

	private void analyzeConnectionList(final List<Element> connlist, final String jobName) {
		// construction des map<nom du composant, nombre de liens> pour les entrées et les sorties
		final Map<String, Long> mapSource = connlist.stream()
				.collect(Collectors.groupingBy(e -> e.getAttribute("source"), Collectors.counting()));

		final Map<String, Long> mapTarget = connlist.stream()
				.collect(Collectors.groupingBy(e -> e.getAttribute("target"), Collectors.counting()));

		// statistiques des compo avec + de 5 entrées/sorties
		nbCompoSup5Out += mapSource.entrySet().stream()
				.filter(e -> e.getValue() > 4)
				.count();
		nbCompoSup5In += mapTarget.entrySet().stream()
				.filter(e -> e.getValue() > 4)
				.count();

		// statistiques des compo avec les + d'entrées/sorties
		final int maxSource = mapSource.entrySet().stream()
				.map(Entry::getValue)
				.max(Comparator.naturalOrder()).orElse(-1L).intValue();

		final int maxTarget = mapTarget.entrySet().stream()
				.map(Entry::getValue)
				.max(Comparator.naturalOrder()).orElse(-1L).intValue();

		if (maxTarget >= maxCompoIn) {
			if (maxTarget > maxCompoIn) {
				maxCompoInJobList = new ArrayList<>();
				maxCompoIn = maxTarget;
			}
			maxCompoInJobList.add(jobName);
		}

		if (maxSource >= maxCompoOut) {
			if (maxSource > maxCompoOut) {
				maxCompoOutJobList = new ArrayList<>();
				maxCompoOut = maxSource;
			}
			maxCompoOutJobList.add(jobName);
		}

	}

	@Override
	public String getResults() {
		return "Nombre maximum d'entrées d'un composant : " + maxCompoIn + " (" + Util.listToString(maxCompoInJobList) + ")\n" +
				"Nombre maximum de sorties d'un composant : " + maxCompoOut + " (" + Util.listToString(maxCompoOutJobList) + ")\n" +
				"Nombre de composants avec plus de 5 entrées : " + nbCompoSup5In + "\n" +
				"Nombre de composants avec plus de 5 sorties : " + nbCompoSup5Out;
	}

}

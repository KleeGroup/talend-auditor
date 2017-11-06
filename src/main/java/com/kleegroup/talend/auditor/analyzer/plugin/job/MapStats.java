package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.Util;

public class MapStats extends AbstractNodeJobAnalyzerPlugin {
	private int nbMap = 0;

	private int maxTmapIn = 0;
	private List<String> maxTmapInJobList = new ArrayList<>();
	private int maxTmapOut = 0;
	private List<String> maxTmapOutJobList = new ArrayList<>();
	private int sumTmapIn = 0;
	private int sumTmapOut = 0;
	private int nbTmapSup5In = 0;
	private int nbTmapSup5Out = 0;

	@Override
	public void analyzeNodeList(final List<Element> elemlist, final String jobName) {
		for (final Element node : elemlist) {
			if ("tMap".equals(node.getAttribute("componentName"))) {
				nbMap++;

				final int nbInput = node.getElementsByTagName("inputTables").getLength();
				final int nbOutput = node.getElementsByTagName("outputTables").getLength();

				sumTmapIn += nbInput;
				sumTmapOut += nbOutput;

				if (nbInput > 5) {
					nbTmapSup5In++;
				}
				if (nbOutput > 5) {
					nbTmapSup5Out++;
				}

				if (nbInput >= maxTmapIn) {
					if (nbInput > maxTmapIn) {
						maxTmapInJobList = new ArrayList<>();
						maxTmapIn = nbInput;
					}
					maxTmapInJobList.add(jobName);
				}

				if (nbOutput >= maxTmapOut) {
					if (nbOutput > maxTmapOut) {
						maxTmapOutJobList = new ArrayList<>();
						maxTmapOut = nbOutput;
					}
					maxTmapOutJobList.add(jobName);
				}
			}
		}
	}

	@Override
	public String getResults() {
		return "Nombre maximum d'entrées d'une tMap : " + maxTmapIn + " (" + Util.listToString(maxTmapInJobList) + ")\n" +
				"Nombre maximum de sorties d'une tMap : " + maxTmapOut + " (" + Util.listToString(maxTmapOutJobList) + ")\n" +
				"Nombre moyen d'entrées d'une tMap : " + Util.divideRound(sumTmapIn, nbMap) + "\n" +
				"Nombre moyen de sorties d'une tMap : " + Util.divideRound(sumTmapOut, nbMap) + "\n" +
				"Nombre de tMap avec plus de 5 entrées : " + nbTmapSup5In + " (" + Util.divideRoundPercent(nbTmapSup5In, nbMap) + ")\n" +
				"Nombre de tMap avec plus de 5 sorties : " + nbTmapSup5Out + " (" + Util.divideRoundPercent(nbTmapSup5Out, nbMap) + ")";

	}

}

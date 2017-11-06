package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.Util;

public class GeneralStats extends AbstractNodeJobAnalyzerPlugin {

	private int sumComp = 0;
	private int sumCompHorsRunJob = 0;
	private int minComp = Integer.MAX_VALUE;
	private List<String> minCompJobList = new ArrayList<>();
	private int maxComp = 0;
	private List<String> maxCompJobList = new ArrayList<>();
	private int maxCompHorsRunJob = 0;
	private List<String> maxCompJobHorsRunJobList = new ArrayList<>();
	private int nbJob = 0;

	private int statInf10 = 0;
	private int stat10to19 = 0;
	private int stat20to29 = 0;
	private int stat30to50 = 0;
	private int statSup50 = 0;

	@Override
	public void analyzeNodeList(final List<Element> elemlist, final String jobName) {
		final int nbCompJob = elemlist.size();
		final int nbCompJobHorsRunJob = (int) elemlist.stream()
				.filter(e -> !"tRunJob".equals(e.getAttribute("componentName")))
				.count();

		nbJob++;
		sumComp += nbCompJob;
		sumCompHorsRunJob += nbCompJobHorsRunJob;

		if (nbCompJob <= minComp) {
			if (nbCompJob < minComp) {
				minCompJobList = new ArrayList<>();
				minComp = nbCompJob;
			}
			minCompJobList.add(jobName);
		}

		if (nbCompJob >= maxComp) {
			if (nbCompJob > maxComp) {
				maxCompJobList = new ArrayList<>();
				maxComp = nbCompJob;
			}
			maxCompJobList.add(jobName);
		}

		if (nbCompJobHorsRunJob >= maxCompHorsRunJob) {
			if (nbCompJobHorsRunJob > maxCompHorsRunJob) {
				maxCompJobHorsRunJobList = new ArrayList<>();
				maxCompHorsRunJob = nbCompJobHorsRunJob;
			}
			maxCompJobHorsRunJobList.add(jobName);
		}

		if (nbCompJob < 10) {
			statInf10++;
		} else if (nbCompJob < 20) {
			stat10to19++;
		} else if (nbCompJob < 30) {
			stat20to29++;
		} else if (nbCompJob < 51) {
			stat30to50++;
		} else {
			statSup50++;
		}
	}

	@Override
	public String getResults() {
		return "Nombre total de composants : " + sumComp + "\n" +
				"    -> hors tRunJob : " + sumCompHorsRunJob + "\n" +
				"Nombre maximum de composants présents dans un job : " + maxComp + " (" + Util.listToString(maxCompJobList) + ")\n" +
				"    -> hors tRunJob : " + maxCompHorsRunJob + " (" + Util.listToString(maxCompJobHorsRunJobList) + ")\n" +
				"Nombre minimum de composants présents dans un job : " + minComp + " (" + Util.listToString(minCompJobList) + ")\n" +
				"Nombre moyen de composants présents dans un job : " + Util.divideRound(sumComp, nbJob) + "\n" +
				"    -> hors tRunJob : " + Util.divideRound(sumCompHorsRunJob, nbJob) + "\n" +
				"Nombre de job ayant moins de 10 composants (très simple) : " + statInf10 + " (" + Util.divideRoundPercent(statInf10, nbJob) + ")\n" +
				"Nombre de job ayant entre 10 et 19 composants (simple) : " + stat10to19 + " (" + Util.divideRoundPercent(stat10to19, nbJob) + ")\n" +
				"Nombre de job ayant entre 20 et 29 composants (moyen) : " + stat20to29 + " (" + Util.divideRoundPercent(stat20to29, nbJob) + ")\n" +
				"Nombre de job ayant entre 30 et 50 composants (complexe) : " + stat30to50 + " (" + Util.divideRoundPercent(stat30to50, nbJob) + ")\n" +
				"Nombre de job ayant plus de 50 composants (très complexe) : " + statSup50 + " (" + Util.divideRoundPercent(statSup50, nbJob) + ")";

	}

}

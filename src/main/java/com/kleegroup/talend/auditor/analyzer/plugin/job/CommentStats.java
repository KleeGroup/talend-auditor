package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.List;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.Util;

public class CommentStats implements IJobAnalyzerPlugin {
	private int nbNote = 0;
	private int nbSubjob = 0;
	private int nbCommentSubjob = 0;
	private int nbCompoDesactive = 0;
	private int nbCompoCommentInactif = 0;
	private int nbCompoCommentActif = 0;

	@Override
	public void analyze(final List<Element> elemlist, final String jobName) {
		for (final Element elem : elemlist) {
			if ("note".equals(elem.getTagName())) {
				nbNote++;
			} else if ("subjob".equals(elem.getTagName())) {
				nbSubjob++;
				final String subjobTitle = Util.getCompoParameterValue(elem, "TEXT", "SUBJOB_TITLE");
				if (subjobTitle != null && !subjobTitle.trim().isEmpty()) {
					nbCommentSubjob++;
				}
			} else if ("node".equals(elem.getTagName())) {
				boolean siDesactive = false;
				final String elemParamActiv = Util.getCompoParameterValue(elem, "CHECK", "ACTIVATE");
				if (elemParamActiv != null && "false".equals(elemParamActiv)) {
					nbCompoDesactive++;
					siDesactive = true;
				}

				final String elemParam = Util.getCompoParameterValue(elem, "MEMO", "COMMENT");
				if (elemParam != null && !elemParam.trim().isEmpty()) {
					if (siDesactive) {
						nbCompoCommentInactif++;
					} else {
						nbCompoCommentActif++;
					}
				}

			}
		}
	}

	@Override
	public String getResults() {
		return "Nombre de notes : " + nbNote + "\n" +
				"Nombre de sous-job avec commentaire : " + nbCommentSubjob + " / " + nbSubjob + " (" + Util.divideRoundPercent(nbCommentSubjob, nbSubjob) + ")\n" +
				"Nombre de composants désactivés : " + nbCompoDesactive + "\n" +
				"Nombre de composants avec commentaire : " + (nbCompoCommentActif + nbCompoCommentInactif) + " (" + nbCompoCommentActif + " actif / " + nbCompoCommentInactif + " inactif)";

	}

}

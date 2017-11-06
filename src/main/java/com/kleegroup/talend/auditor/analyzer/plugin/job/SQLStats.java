package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.Util;

public class SQLStats extends AbstractNodeJobAnalyzerPlugin {
	private int nbSql = 0;
	private int nbCustom = 0;
	private int nbCustomComplexe = 0;
	private int nbPlSql = 0;

	private final Pattern patternSelect = Pattern.compile("(?s)(?i)(\\Wselect\\W)");
	private final Pattern patternMerge = Pattern.compile("(?s)(?i)(\\Wmerge\\W)");
	private final Pattern patternWhen = Pattern.compile("(?s)(?i)(\\Wwhen\\W)");
	private final Pattern patternJoin = Pattern.compile("(?s)(?i)(\\Wjoin\\W)");
	private final Pattern patternAndOr = Pattern.compile("(?s)(?i)(\\W(and|or)\\W)");

	@Override
	public void analyzeNodeList(final List<Element> elemlist, final String jobName) {
		for (final Element node : elemlist) {
			final Element attrib = Util.getCompoParameter(node, "MEMO_SQL", "QUERY");
			if (attrib != null) {
				final String sql = attrib.getAttribute("value");
				nbSql++;
				if (node.getAttribute("componentName").endsWith("Row") // un tXRow est toujours custom
						|| sql.matches("(?s)(?i).*\\W(case|sum|min|max|where|join|group by)\\W.*")) {
					// (?s) = .* prend aussi les retours chariot
					// (?i) = case insensitive
					nbCustom++;
				}

				if (sql.matches("(?s)(?i).*\\W(begin)\\W.*")) {
					// (?s) = .* prend aussi les retours chariot
					// (?i) = case insensitive
					nbPlSql++;
				}

				final int nbSelect = countMatch(patternSelect, sql);
				final int nbJoin = countMatch(patternJoin, sql);
				final int nbMerge = countMatch(patternMerge, sql);
				final int nbWhen = countMatch(patternWhen, sql);
				final int nbAndOr = countMatch(patternAndOr, sql);

				if (nbSelect > 1 || nbJoin > 2 || nbMerge > 0 || nbWhen > 4 || nbAndOr > 4) {
					nbCustomComplexe++;
				}
			}
		}

	}

	@Override
	public String getResults() {
		return "Nombre de requêtes SQL : " + nbSql + "\n" +
				"Nombre de requêtes SQL personnalisée (approx.) : " + nbCustom + " (" + Util.divideRoundPercent(nbCustom, nbSql) + ")\n" +
				"Nombre de requêtes SQL complexe (2+ select, 3+ join, 1+ merge, 5+ when, 5+ and/or) : " + nbCustomComplexe + " (" + Util.divideRoundPercent(nbCustomComplexe, nbSql) + ")\n" +
				"Nombre de requêtes PL/SQL (begin ...) : " + nbPlSql + " (" + Util.divideRoundPercent(nbPlSql, nbSql) + ")";
	}

	private int countMatch(final Pattern pattern, final String sql) {
		final Matcher matcher = pattern.matcher(sql);
		int count = 0;
		while (matcher.find()) {
			count++;
		}
		return count;
	}

}

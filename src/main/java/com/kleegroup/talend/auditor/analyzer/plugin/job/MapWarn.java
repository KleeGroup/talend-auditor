package com.kleegroup.talend.auditor.analyzer.plugin.job;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Element;

import com.kleegroup.talend.auditor.Util;

public class MapWarn extends AbstractNodeJobAnalyzerPlugin {
	private final StringBuilder result = new StringBuilder();
	private int nbWarn = 0;

	@Override
	public void analyzeNodeList(final List<Element> elemlist, final String jobName) {
		for (final Element node : elemlist) {
			if ("tMap".equals(node.getAttribute("componentName"))) {
				analyzeMap(node, jobName);
			}
		}
	}

	private void analyzeMap(final Element map, final String jobName) {
		final String compoName = Util.getCompoName(map);

		final List<Element> outputTables = Util.nodeListToElementList(map.getElementsByTagName("outputTables"));
		final List<Element> inputTables = Util.nodeListToElementList(map.getElementsByTagName("inputTables"));
		final List<Element> varTables = Util.nodeListToElementList(map.getElementsByTagName("varTables"));

		// on construit une string comportant le contenu de toutes les expressions utilisées dans la map (1 par ligne)
		final Stream<String> streamLineExpression = Stream.concat(outputTables.stream(), Stream.concat(inputTables.stream(), varTables.stream())) // on travaille sur les 3 listes combinées
				.flatMap(e -> Util.nodeListToElementList(e.getElementsByTagName("mapperTableEntries")).stream()) // on récupere tous les éléments mapperTableEntries dans un même flux
				.map(e -> e.getAttribute("expression")); // on ajoute les expression des lignes de sortie

		final Stream<String> streamExpressionFilter = outputTables.stream()
				.map(e -> e.getAttribute("expressionFilter")); // on ajoute les expression filter des output

		final String allExpressionString = Stream.concat(streamLineExpression, streamExpressionFilter)
				.collect(Collectors.joining("\n"));

		// on vérifie que pour chaques tables d'entrée, la table est utilisée dans une expression
		nbWarn += inputTables.stream()
				.filter(e -> !"true".equals(e.getAttribute("innerJoin"))) // on exclus les tables servant en inner join (peut être utilisé pour restreindre les lignes sans utiliser ses valeurs)
				.map(e -> e.getAttribute("name"))
				.filter(s -> !testIfPresent(s, allExpressionString))
				.peek(s -> result.append("\n\t" + jobName + " - " + compoName + " : L'entrée " + s + " n'est pas utilisée dans la map !"))
				.count();

		// on vérifie maintenant pour les variables intermédiaires
		nbWarn += varTables.stream()
				.flatMap(e -> Util.nodeListToElementList(e.getElementsByTagName("mapperTableEntries")).stream()) // on récupere tous les éléments mapperTableEntries
				.map(e -> e.getAttribute("name"))
				.filter(s -> !testIfPresent("Var." + s, allExpressionString))
				.peek(s -> result.append("\n\t" + jobName + " - " + compoName + " : La variable intermédiaire " + s + " n'est pas utilisée dans la map !"))
				.count();
	}

	private boolean testIfPresent(final String stringToCheck, final String stringToSearch) {
		return stringToSearch.matches("(?s)(?i).*\\b(" + stringToCheck + ")\\b.*");
	}

	@Override
	public String getResults() {
		return "Alertes concernant les tMap (" + nbWarn + ") :" + result.toString();
	}

}

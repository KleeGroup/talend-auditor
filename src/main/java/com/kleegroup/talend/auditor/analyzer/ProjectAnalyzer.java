package com.kleegroup.talend.auditor.analyzer;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ProjectAnalyzer extends AbstractAnalyzer {

	public ProjectAnalyzer(final String rootPath) {
		super(rootPath);
	}

	@Override
	public String analyze() throws ParserConfigurationException, SAXException, IOException {
		final File talendProjectFile = new File(rootPath + "/talend.project");
		if (!talendProjectFile.exists()) {
			throw new RuntimeException("Le répertoire fourni n'est pas un projet Talend valide.\nImpossible de trouver le fichier '" + talendProjectFile.getAbsolutePath() + "'");
		}

		final DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		final Document xmlTalendProjet = domBuilder.parse(talendProjectFile);
		final Element prop = (Element) xmlTalendProjet.getElementsByTagName("TalendProperties:Project").item(0);

		String result = "Nom du projet : " + prop.getAttribute("technicalLabel") + "\n";
		result += "Version de Talend utilisée : " + prop.getAttribute("productVersion");

		return result;
	}

}

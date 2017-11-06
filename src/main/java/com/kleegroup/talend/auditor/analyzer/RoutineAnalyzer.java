package com.kleegroup.talend.auditor.analyzer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.kleegroup.talend.auditor.talend.TalendItem;
import com.kleegroup.talend.auditor.talend.TalendItemListing;

public class RoutineAnalyzer extends AbstractAnalyzer {

	private static final String DIR_SEPARATOR_REGEX = "(\\\\|/)";

	private int totalLines = 0;

	public RoutineAnalyzer(final String rootPath) {
		super(rootPath);
	}

	@Override
	public String analyze() throws ParserConfigurationException, SAXException, IOException {
		final String userRoutinesPath = rootPath + "/code/routines";

		TalendItemListing til;
		try {
			til = TalendItemListing.getFromFolder(userRoutinesPath, ".*" + DIR_SEPARATOR_REGEX + "system" + DIR_SEPARATOR_REGEX + ".*");
		} catch (final IOException e) {
			throw new RuntimeException("Impossible de lister les fichiers.", e);
		}

		if (til.getItemList().isEmpty()) {
			return "Aucune routine utilisateur";
		}

		final StringBuilder result = new StringBuilder();
		result.append("Nombre total de routines : " + til.getItemDistinctList().size() + "\n");
		result.append("Nombre d'ancienne version de routines : " + (til.getItemList().size() - til.getItemDistinctList().size()) + "\n");
		result.append("Version maximale des routines : " + til.getVersionMax() + "\n\n");

		result.append("Liste des routines : \n");
		// on analyse que les dernières versions des routines
		for (final TalendItem job : til.getItemDistinctList()) {
			result.append(doAnalyze(job));
		}
		result.append("Nombre total de lignes : " + totalLines);

		return result.toString();
	}

	private String doAnalyze(final TalendItem job) {
		final int nbLines = countLines(job.getPath().toFile());
		totalLines += nbLines;
		return "\tRoutine " + job.getName() + " : " + nbLines + " lignes\n";
	}

	private int countLines(final File file) {
		int nbLines = 0;
		try (FileReader fr = new FileReader(file);
				final LineNumberReader lnr = new LineNumberReader(fr);) {
			lnr.skip(Long.MAX_VALUE);
			nbLines = lnr.getLineNumber() + 1; //Add 1 because line index starts at 0
			// Finally, the LineNumberReader object should be closed to prevent resource leak
		} catch (final IOException e) {
			throw new RuntimeException("Impossible de lire le fichier.", e);
		}

		return nbLines;
	}

}

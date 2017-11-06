package com.kleegroup.talend.auditor;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.kleegroup.talend.auditor.analyzer.AbstractAnalyzer;
import com.kleegroup.talend.auditor.analyzer.JobAnalyzer;
import com.kleegroup.talend.auditor.analyzer.ProjectAnalyzer;
import com.kleegroup.talend.auditor.analyzer.RoutineAnalyzer;

public class Main {

	public static void main(final String[] args) {
		final String path = args[0];

		System.out.println("Analyse de '" + path + "'\n");

		final long start = System.nanoTime();
		try {
			doProcessPath(path);
		} finally {
			final long end = System.nanoTime();
			final long elapsedMs = (end - start) / 1000000;
			System.out.println("Analyse effectuée en " + elapsedMs + " ms");
		}
	}

	private static void doProcessPath(final String path) {
		String pathToAnalyze = path;
		String tmpDir = null;

		try {
			if (new File(path).isFile()) {
				if (!path.endsWith(".zip")) {
					throw new RuntimeException("Seul le format zip est supporté");
				}
				tmpDir = Files.createTempDirectory("talendAuditor").toString();

				Util.unzip(path, tmpDir);
				final File[] fileList = new File(tmpDir).listFiles();
				if (fileList.length != 1 || !fileList[0].isDirectory()) {
					throw new RuntimeException("Fichier zip incorrect. Le zip doit comporter un et un seul répertoire à sa racine");
				}
				pathToAnalyze = tmpDir + "/" + fileList[0].getName();
			}

			doAnalyze(pathToAnalyze);
		} catch (final Exception e) {
			throw new RuntimeException("Erreur lors de l'analyse.", e);
		} finally {
			if (tmpDir != null) {
				FileUtils.deleteQuietly(new File(tmpDir));
			}
		}
	}

	private static void doAnalyze(final String path) throws Exception {
		final List<AbstractAnalyzer> analyzerList = new ArrayList<>();
		analyzerList.add(new ProjectAnalyzer(path));
		analyzerList.add(new JobAnalyzer(path));
		analyzerList.add(new RoutineAnalyzer(path));

		for (final AbstractAnalyzer analyzer : analyzerList) {
			System.out.println(analyzer.analyze() + "\n\n");
		}
	}

}

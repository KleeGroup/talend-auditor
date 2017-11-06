package com.kleegroup.talend.auditor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Util {

	private Util() {
		// utilitaire
	}

	public static String divideRound(final int sum, final int divisor) {
		final double sum10 = sum * 10 / (double) divisor;
		return String.valueOf(Math.round(sum10) / 10d);
	}

	public static String divideRoundPercent(final int sum, final int divisor) {
		return divideRound(sum * 100, divisor) + "%";
	}

	public static String listToString(final List<String> list) {
		final List<String> listAffichage;
		if (list.size() > 6) {
			listAffichage = list.subList(0, 4);
			listAffichage.add("...");
		} else {
			listAffichage = list;
		}

		return listAffichage.stream()
				.map(j -> '"' + j + '"')
				.collect(Collectors.joining(", "));
	}

	public static Element getCompoParameter(final Element elem, final String field, final String name) {
		final NodeList elemParamList = elem.getElementsByTagName("elementParameter");
		for (int i = 0; i < elemParamList.getLength(); i++) {
			final Element elemParam = (Element) elemParamList.item(i);
			if (elemParam.getAttribute("field").equals(field)
					&& elemParam.getAttribute("name").equals(name)) {
				return elemParam;
			}
		}
		return null;
	}

	public static String getCompoParameterValue(final Element elem, final String field, final String name) {
		final Element compoParameter = getCompoParameter(elem, field, name);
		if (compoParameter == null) {
			return null;
		}

		return compoParameter.getAttribute("value");
	}

	public static List<Element> nodeListToElementList(final NodeList nodeList) {
		final List<Element> elemList = new ArrayList<>(nodeList.getLength());
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node item = nodeList.item(i);
			if (item instanceof Element) { // on ne s'intéresse pas aux noeuds de type text, juste aux balises
				elemList.add((Element) item); // on cast en Element pour avoir accès aux méthode spécifiques aux balises
			}
		}

		return elemList;
	}

	public static void unzip(final String file, final String outputDir) {
		try (ZipFile zipFile = new ZipFile(file);) {
			final Enumeration<? extends ZipEntry> entries = zipFile.entries();
			while (entries.hasMoreElements()) {
				final ZipEntry entry = entries.nextElement();
				final File entryDestination = new File(outputDir, entry.getName());
				if (entry.isDirectory()) {
					entryDestination.mkdirs();
				} else {
					entryDestination.getParentFile().mkdirs();
					final InputStream in = zipFile.getInputStream(entry);
					final OutputStream out = new FileOutputStream(entryDestination);
					IOUtils.copy(in, out);
					IOUtils.closeQuietly(in);
					out.close();
				}
			}
		} catch (final IOException e) {
			throw new RuntimeException("Impossible de décompresser le fichier", e);
		}
	}
}

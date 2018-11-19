package com.kleegroup.talend.auditor.analyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kleegroup.talend.auditor.Util;
import com.kleegroup.talend.auditor.analyzer.plugin.job.CommentStats;
import com.kleegroup.talend.auditor.analyzer.plugin.job.DicoPlugin;
import com.kleegroup.talend.auditor.analyzer.plugin.job.GeneralStats;
import com.kleegroup.talend.auditor.analyzer.plugin.job.IJobAnalyzerPlugin;
import com.kleegroup.talend.auditor.analyzer.plugin.job.JobHierarchyPlugin;
import com.kleegroup.talend.auditor.analyzer.plugin.job.JobStats;
import com.kleegroup.talend.auditor.analyzer.plugin.job.LinkStats;
import com.kleegroup.talend.auditor.analyzer.plugin.job.MapStats;
import com.kleegroup.talend.auditor.analyzer.plugin.job.MapWarn;
import com.kleegroup.talend.auditor.analyzer.plugin.job.OracleInputCursorStats;
import com.kleegroup.talend.auditor.analyzer.plugin.job.SQLStats;
import com.kleegroup.talend.auditor.analyzer.plugin.job.TRunJobWarn;
import com.kleegroup.talend.auditor.talend.TalendItem;
import com.kleegroup.talend.auditor.talend.TalendItemListing;

public class JobAnalyzer extends AbstractAnalyzer {

	private final List<IJobAnalyzerPlugin> pluginList = new ArrayList<>();

	public JobAnalyzer(final String rootPath) {
		super(rootPath);
	}

	private void initPlugins(final TalendItemListing til) {
		pluginList.add(new JobStats(til));
		pluginList.add(new GeneralStats());
		pluginList.add(new CommentStats());
		pluginList.add(new LinkStats());
		pluginList.add(new MapStats());
		pluginList.add(new MapWarn());
		pluginList.add(new SQLStats());
		pluginList.add(new OracleInputCursorStats());
		pluginList.add(new TRunJobWarn());
		pluginList.add(new DicoPlugin());
		pluginList.add(new JobHierarchyPlugin(til));
	}

	@Override
	public String analyze() throws ParserConfigurationException, SAXException, IOException {
		TalendItemListing til;

		try {
			til = TalendItemListing.getFromFolder(rootPath + "/process");
		} catch (final IOException e) {
			throw new RuntimeException("Impossible de lister les fichiers.", e);
		}

		initPlugins(til);

		// on analyse uniquement les dernières versions des jobs
		for (final TalendItem job : til.getItemDistinctList()) {
			doAnalyze(job);
		}

		return pluginList.stream()
				.map(IJobAnalyzerPlugin::getResults)
				.collect(Collectors.joining("\n\n"));
	}

	private void doAnalyze(final TalendItem job) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilder domBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		final Document xml = domBuilder.parse(job.getPath().toFile());

		final NodeList compListXml = xml.getFirstChild().getChildNodes();
		final List<Element> compList = Util.nodeListToElementList(compListXml);

		pluginList.stream()
				.forEach(p -> p.analyze(compList, job.getName()));
	}

}

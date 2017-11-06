package com.kleegroup.talend.auditor.analyzer;

public abstract class AbstractAnalyzer {

	protected final String rootPath;

	public AbstractAnalyzer(final String rootPath) {
		super();
		this.rootPath = rootPath;
	}

	public abstract String analyze() throws Exception;

}

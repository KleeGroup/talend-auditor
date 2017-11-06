package com.kleegroup.talend.auditor.talend;

import java.math.BigDecimal;
import java.nio.file.Path;

public class TalendItem {

	private final Path path;
	private final BigDecimal version;
	private final String name;

	public TalendItem(Path path) {
		this.path = path;
		String fileName = path.getFileName().toString();
		version = new BigDecimal(fileName.substring(fileName.lastIndexOf('_') + 1, fileName.lastIndexOf('.')));
		name = fileName.substring(0, fileName.lastIndexOf('_'));
	}

	/**
	 * @return the path
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @return the version
	 */
	public BigDecimal getVersion() {
		return version;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
}

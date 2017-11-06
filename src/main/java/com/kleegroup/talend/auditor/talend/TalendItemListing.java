package com.kleegroup.talend.auditor.talend;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TalendItemListing {

	private final List<TalendItem> itemList;
	private final List<TalendItem> itemDistinctList;
	private final BigDecimal versionMax;

	private TalendItemListing(final List<TalendItem> itemList, final List<TalendItem> itemDistinctList, final BigDecimal versionMax) {
		this.itemList = itemList;
		this.itemDistinctList = itemDistinctList;
		this.versionMax = versionMax;
	}

	/**
	 * @return the itemList
	 */
	public List<TalendItem> getItemList() {
		return itemList;
	}

	/**
	 * @return the itemDistinctList
	 */
	public List<TalendItem> getItemDistinctList() {
		return itemDistinctList;
	}

	/**
	 * @return the versionMax
	 */
	public BigDecimal getVersionMax() {
		return versionMax;
	}

	public static TalendItemListing getFromFolder(final String path) throws IOException {
		return getFromFolder(path, null);
	}

	public static TalendItemListing getFromFolder(final String path, final String excludePattern) throws IOException {
		// listing des jobs
		final List<TalendItem> itemList = Files.walk(Paths.get(path))
				.filter(p -> p.toString().endsWith(".item"))
				.filter(p -> testExcludePattern(excludePattern, p))
				.map(TalendItem::new)
				.collect(Collectors.toList());

		// filtrage sur la dernière version
		final Map<String, TalendItem> jobMap = new HashMap<>();
		for (final TalendItem job : itemList) {
			if (jobMap.get(job.getName()) == null || jobMap.get(job.getName()).getVersion().compareTo(job.getVersion()) < 0) {
				jobMap.put(job.getName(), job);
			}
		}

		final List<TalendItem> jobDistinctList = new ArrayList<>(jobMap.values());

		final BigDecimal versionMax = jobDistinctList.stream()
				.map(TalendItem::getVersion)
				.max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

		return new TalendItemListing(itemList, jobDistinctList, versionMax);
	}

	private static boolean testExcludePattern(final String excludePattern, final Path p) {
		return excludePattern == null ? true : !p.toString().matches(excludePattern);
	}
}

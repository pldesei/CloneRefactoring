package org.eclipse.emf.test.ecore.xcore.legacy_xpect_runner;

import java.io.File;
import java.util.List;

import org.eclipse.emf.common.util.URI;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * This class will be removed in the next release after 2.4.2
 * 
 * @author Moritz Eysholdt - Initial contribution and API
 */
public class ResourceURICollector {
	public static class FilePredicate implements Predicate<File> {
		private final String[] fileExtensions;

		public FilePredicate(String... fileExts) {
			this.fileExtensions = fileExts;
		}

		public boolean apply(File input) {
			for (String ext : fileExtensions)
				if (input.getName().endsWith("." + ext))
					return true;
			return false;
		}
	}

	protected void collectFiles(File dir, List<URI> result,
			Predicate<File> shouldCollect) {
		for (File child : dir.listFiles()) {
			if (shouldCollect.apply(child))
				result.add(createURI(child));
			if (child.isDirectory())
				collectFiles(child, result, shouldCollect);
		}
	}

	public List<URI> collectFiles(String directory,
			Predicate<File> shouldCollect) {
		File dir = new File(directory);
		if (!dir.isDirectory())
			throw new RuntimeException("Directory not found: " + directory);
		List<URI> result = Lists.newArrayList();
		collectFiles(new File(directory), result, shouldCollect);
		return result;
	}

	public List<URI> collectFiles(String directory, String... fileExtensions) {
		return collectFiles(directory, new FilePredicate(fileExtensions));
	}

	public List<URI> collectFiles(String... files) {
		List<URI> result = Lists.newArrayList();
		for (String file : files) {
			File f = new File(file);
			if (!f.exists())
				throw new RuntimeException("File not found: " + file);
			result.add(createURI(f));
		}
		return result;
	}

	protected URI createURI(File file) {
		return URI.createFileURI(file.getAbsolutePath());
	}
}

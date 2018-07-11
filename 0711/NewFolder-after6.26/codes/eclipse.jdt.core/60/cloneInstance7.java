public void testBug124624_HashMap_old() throws CoreException {
	workingCopies = new ICompilationUnit[1];
	workingCopies[0] = getWorkingCopy("/JavaSearchBugs/src/Test.java",
		"class HashMap {}\n" + 
		"class HtmlMapper {}\n" + 
		"class HashMapEntry {}\n" + 
		"class HatMappage {}\n"
	);
	search("HashMap", IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_CAMELCASE_MATCH);
	assertSearchResults(
		"src/Test.java HashMap [HashMap] EXACT_MATCH\n" + 
		"src/Test.java HashMapEntry [HashMapEntry] EXACT_MATCH"
	);
}

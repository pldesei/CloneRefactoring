public void testBug124624_HMap_old() throws CoreException {
	workingCopies = new ICompilationUnit[1];
	workingCopies[0] = getWorkingCopy("/JavaSearchBugs/src/Test.java",
		"class HashMap {}\n" + 
		"class HtmlMapper {}\n" + 
		"class HashMapEntry {}\n" + 
		"class HatMappage {}\n"
	);
	search("HMap", IJavaSearchConstants.TYPE, IJavaSearchConstants.DECLARATIONS, SearchPattern.R_CAMELCASE_MATCH);
	assertSearchResults(
		"src/Test.java HashMap [HashMap] EXACT_MATCH\n" + 
		"src/Test.java HtmlMapper [HtmlMapper] EXACT_MATCH\n" + 
		"src/Test.java HashMapEntry [HashMapEntry] EXACT_MATCH\n" + 
		"src/Test.java HatMappage [HatMappage] EXACT_MATCH"
	);
}

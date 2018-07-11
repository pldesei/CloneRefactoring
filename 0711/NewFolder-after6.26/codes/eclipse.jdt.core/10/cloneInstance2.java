public void searchDeclarationsOfSentMessages(IWorkspace workspace, IJavaElement enclosingElement, IJavaSearchResultCollector resultCollector) throws JavaModelException {
	SearchPattern pattern = new DeclarationOfReferencedMethodsPattern(enclosingElement);
	IJavaSearchScope scope = createJavaSearchScope(new IJavaElement[] {enclosingElement});
	IResource resource = this.getResource(enclosingElement);
	if (resource instanceof IFile) {
		if (VERBOSE) {
			System.out.println("Searching for " + pattern + " in " + resource.getFullPath()); //$NON-NLS-1$//$NON-NLS-2$
		}
		MatchLocator locator = new MatchLocator(
			pattern,
			resultCollector,
			scope,
			resultCollector.getProgressMonitor());
		locator.locateMatches(
			new String[] {resource.getFullPath().toString()}, 
			workspace,
			this.getWorkingCopies(enclosingElement));
	} else {
		search(workspace, pattern, scope, resultCollector);
	}
}

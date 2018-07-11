public void removeSourceFolderFromIndex(JavaProject javaProject, IPath sourceFolder, char[][] inclusionPatterns, char[][] exclusionPatterns) {
	IProject project = javaProject.getProject();
	if (this.jobEnd > this.jobStart) {
		// check if a job to index the project is not already in the queue
		IndexRequest request = new IndexAllProject(project, this);
		for (int i = this.jobEnd; i > this.jobStart; i--) // NB: don't check job at jobStart, as it may have already started (see http://bugs.eclipse.org/bugs/show_bug.cgi?id=32488)
			if (request.equals(this.awaitingJobs[i])) return;
	}

	this.request(new RemoveFolderFromIndex(sourceFolder, inclusionPatterns, exclusionPatterns, project, this));
}

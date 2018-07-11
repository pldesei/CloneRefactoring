private void noCycleDetection(final int numberOfParticipants, final boolean useForwardReferences) throws CoreException {
	
	final IJavaProject[] projects = new IJavaProject[numberOfParticipants];
	final int[] allProjectsInCycle = new int[numberOfParticipants];
	
	final long[] start = new long[1];
	final long[] time = new long[1];
	
	try {
		JavaCore.run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (int i = 0; i < numberOfParticipants; i++){
					projects[i] = createJavaProject("P"+i, new String[]{""}, "");
					allProjectsInCycle[i] = 0;
				}		
				for (int i = 0; i < numberOfParticipants; i++){
					IClasspathEntry[] extraEntries = new IClasspathEntry[useForwardReferences ? numberOfParticipants - i -1 : i];
					int index = 0;
					for (int j = useForwardReferences ? i+1 : 0; 
						useForwardReferences ? (j < numberOfParticipants) : (j < i); 
						j++){
						extraEntries[index++] = JavaCore.newProjectEntry(projects[j].getPath());
					}
					// append project references			
					IClasspathEntry[] oldClasspath = projects[i].getRawClasspath();
					IClasspathEntry[] newClasspath = new IClasspathEntry[oldClasspath.length+extraEntries.length];
					System.arraycopy(oldClasspath, 0 , newClasspath, 0, oldClasspath.length);
					for (int j = 0; j < extraEntries.length; j++){
						newClasspath[oldClasspath.length+j] = extraEntries[j];
					}			
					// set classpath
					long innerStart = System.currentTimeMillis(); // time spent in individual CP setting
					projects[i].setRawClasspath(newClasspath, null);
					time[0] += System.currentTimeMillis() - innerStart;
				}
				start[0] = System.currentTimeMillis(); // time spent in delta refresh
			}
		}, 
		null);
		time[0] += System.currentTimeMillis()-start[0];
		System.out.println("No cycle check ("+numberOfParticipants+" participants) : "+ time[0]+" ms, "+ (useForwardReferences ? "forward references" : "backward references"));
		
		for (int i = 0; i < numberOfParticipants; i++){
			// check cycle markers
			this.assertCycleMarkers(projects[i], projects, allProjectsInCycle);
		}
		
	} finally {
		if (projects != null){
			for (int i = 0; i < numberOfParticipants; i++){
				if (projects[i] != null)
					this.deleteProject(projects[i].getElementName());
			}
		}
	}
}

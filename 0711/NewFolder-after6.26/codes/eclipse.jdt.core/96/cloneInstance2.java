	public static void compileTree(JavaCompiler compiler, List<String> options, File targetFolder) {
		StandardJavaFileManager manager = compiler.getStandardFileManager(null, Locale.getDefault(), Charset.defaultCharset());

		// create new list containing inputfile
		List<File> files = new ArrayList<File>();
		findFilesUnder(targetFolder, files);
		Iterable<? extends JavaFileObject> units = manager.getJavaFileObjectsFromFiles(files);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);

		options.add("-d");
		options.add(_tmpBinFolderName);
		options.add("-s");
		options.add(_tmpGenFolderName);
		options.add("-cp");
		options.add(_tmpSrcFolderName + File.pathSeparator + _tmpGenFolderName + File.pathSeparator + _processorJarPath);
		options.add("-processorpath");
		options.add(_processorJarPath);
		options.add("-XprintRounds");
		CompilationTask task = compiler.getTask(printWriter, manager, null, options, null, units);
		Boolean result = task.call();

		if (!result.booleanValue()) {
			String errorOutput = stringWriter.getBuffer().toString();
			System.err.println("Compilation failed: " + errorOutput);
	 		junit.framework.TestCase.assertTrue("Compilation failed : " + errorOutput, false);
		}
	}

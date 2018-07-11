	if (true) {
		Class c = JavadocCompletionContextTests.class;
		Method[] methods = c.getMethods();
		for (int i = 0, max = methods.length; i < max; i++) {
			if (methods[i].getName().startsWith("test")) { //$NON-NLS-1$
				suite.addTest(new JavadocCompletionContextTests(methods[i].getName()));
			}
		}
		return suite;
	}

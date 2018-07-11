public static Test suite() {
	TestSuite suite = new Suite(JavadocCompletionContextTests.class.getName());		

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
	suite.addTest(new JavadocCompletionContextTests("test0050"));			
	return suite;
}

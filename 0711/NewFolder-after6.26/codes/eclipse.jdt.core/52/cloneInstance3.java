public static Test suite() {
	TestSuite suite = new Suite(CompletionTests_1_5.class.getName());		

	if (true) {
		Class c = CompletionTests_1_5.class;
		Method[] methods = c.getMethods();
		for (int i = 0, max = methods.length; i < max; i++) {
			if (methods[i].getName().startsWith("test")) { //$NON-NLS-1$
				suite.addTest(new CompletionTests_1_5(methods[i].getName()));
			}
		}
		return suite;
	}
	suite.addTest(new CompletionTests_1_5("test0214"));			
	return suite;
}

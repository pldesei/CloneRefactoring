    	for (int i = 0, max = contents.length(); i < max; i++) {
    		final int lineNumber = compilationUnit.lineNumber(i);
    		final int columnNumber = compilationUnit.columnNumber(i);
    		final int position = compilationUnit.getPosition(lineNumber, columnNumber);
    		if (position == 0) {
    			assertEquals("Only true for first character", 0, i);
    		}
			assertEquals("Wrong char", contents.charAt(i), contents.charAt(position));
    	}

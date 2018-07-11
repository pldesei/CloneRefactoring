		if (!inclusion.equals("")) { //$NON-NLS-1$ 
			char[][] patterns = CharOperation.splitOn('|', inclusion.toCharArray());
			int patternCount;
			if ((patternCount  = patterns.length) > 0) {
				inclusionPatterns = new IPath[patternCount];
				for (int j = 0; j < patterns.length; j++){
					inclusionPatterns[j] = new Path(new String(patterns[j]));
				}
			}
		}

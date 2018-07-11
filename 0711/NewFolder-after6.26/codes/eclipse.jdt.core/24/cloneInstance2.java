		if (!exclusion.equals("")) { //$NON-NLS-1$ 
			char[][] patterns = CharOperation.splitOn('|', exclusion.toCharArray());
			int patternCount;
			if ((patternCount  = patterns.length) > 0) {
				exclusionPatterns = new IPath[patternCount];
				for (int j = 0; j < patterns.length; j++){
					exclusionPatterns[j] = new Path(new String(patterns[j]));
				}
			}
		}

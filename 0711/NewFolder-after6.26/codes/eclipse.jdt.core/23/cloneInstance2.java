		if (this.exclusionPatterns != null && this.exclusionPatterns.length > 0) {
			StringBuffer rule = new StringBuffer(10);
			for (int i = 0, max = this.exclusionPatterns.length; i < max; i++){
				if (i > 0) rule.append('|');
				rule.append(this.exclusionPatterns[i]);
			}
			parameters.put(TAG_EXCLUDING, String.valueOf(rule));
		}

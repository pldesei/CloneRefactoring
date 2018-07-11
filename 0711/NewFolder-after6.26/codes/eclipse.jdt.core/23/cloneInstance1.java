		if (this.inclusionPatterns != null && this.inclusionPatterns.length > 0) {
			StringBuffer rule = new StringBuffer(10);
			for (int i = 0, max = this.inclusionPatterns.length; i < max; i++){
				if (i > 0) rule.append('|');
				rule.append(this.inclusionPatterns[i]);
			}
			parameters.put(TAG_INCLUDING, String.valueOf(rule));
		}

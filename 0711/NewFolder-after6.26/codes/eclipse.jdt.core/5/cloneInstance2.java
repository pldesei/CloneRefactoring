				if (length == 10 && warningOption.equals("-warn:none")) { //$NON-NLS-1$
					Object[] entries = options.entrySet().toArray();
					for (int i = 0, max = entries.length; i < max; i++) {
						Map.Entry entry = (Map.Entry) entries[i];
						if (!(entry.getKey() instanceof String))
							continue;
						if (!(entry.getValue() instanceof String))
							continue;
						if (((String) entry.getValue()).equals(CompilerOptions.WARNING)) {
							options.put((String) entry.getKey(), CompilerOptions.IGNORE);
						}
					}
					continue;
				}

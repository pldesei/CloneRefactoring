			if(optionID.equals(OPTION_ReportUnusedImport)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnusedImport;
					this.warningThreshold &= ~UnusedImport;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnusedImport;
					this.warningThreshold |= UnusedImport;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnusedImport;
					this.warningThreshold &= ~UnusedImport;
				}
				continue;
			} 

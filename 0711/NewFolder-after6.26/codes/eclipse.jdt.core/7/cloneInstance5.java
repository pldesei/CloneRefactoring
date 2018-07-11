			if(optionID.equals(OPTION_ReportDeprecation)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UsingDeprecatedAPI;
					this.warningThreshold &= ~UsingDeprecatedAPI;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UsingDeprecatedAPI;
					this.warningThreshold |= UsingDeprecatedAPI;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UsingDeprecatedAPI;
					this.warningThreshold &= ~UsingDeprecatedAPI;
				}
				continue;
			} 

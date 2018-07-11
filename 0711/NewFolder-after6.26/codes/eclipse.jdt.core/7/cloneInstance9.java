			if(optionID.equals(OPTION_ReportUnusedParameter)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnusedArgument;
					this.warningThreshold &= ~UnusedArgument;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnusedArgument;
					this.warningThreshold |= UnusedArgument;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnusedArgument;
					this.warningThreshold &= ~UnusedArgument;
				}
				continue;
			} 

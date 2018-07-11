			if(optionID.equals(OPTION_ReportUnreachableCode)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnreachableCode;
					this.warningThreshold &= ~UnreachableCode;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnreachableCode;
					this.warningThreshold |= UnreachableCode;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnreachableCode;
					this.warningThreshold &= ~UnreachableCode;
				}
				continue;
			} 

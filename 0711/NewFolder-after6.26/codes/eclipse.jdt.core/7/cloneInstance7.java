			if(optionID.equals(OPTION_ReportUnusedLocal)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnusedLocalVariable;
					this.warningThreshold &= ~UnusedLocalVariable;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnusedLocalVariable;
					this.warningThreshold |= UnusedLocalVariable;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnusedLocalVariable;
					this.warningThreshold &= ~UnusedLocalVariable;
				}
				continue;
			}

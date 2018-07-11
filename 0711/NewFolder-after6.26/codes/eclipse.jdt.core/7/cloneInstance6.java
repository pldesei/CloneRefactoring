			if(optionID.equals(OPTION_ReportHiddenCatchBlock)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= MaskedCatchBlock;
					this.warningThreshold &= ~MaskedCatchBlock;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~MaskedCatchBlock;
					this.warningThreshold |= MaskedCatchBlock;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~MaskedCatchBlock;
					this.warningThreshold &= ~MaskedCatchBlock;
				}
				continue;
			} 

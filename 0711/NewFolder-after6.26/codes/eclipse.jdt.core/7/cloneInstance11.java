			if(optionID.equals(OPTION_ReportSyntheticAccessEmulation)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= AccessEmulation;
					this.warningThreshold &= ~AccessEmulation;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~AccessEmulation;
					this.warningThreshold |= AccessEmulation;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~AccessEmulation;
					this.warningThreshold &= ~AccessEmulation;
				}
				continue;
			}

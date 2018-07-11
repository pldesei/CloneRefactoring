			if (optionID.equals(OPTION_ReportNoImplicitStringConversion)) {
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= NoImplicitStringConversion;
					this.warningThreshold &= ~NoImplicitStringConversion;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~NoImplicitStringConversion;
					this.warningThreshold |= NoImplicitStringConversion;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~NoImplicitStringConversion;
					this.warningThreshold &= ~NoImplicitStringConversion;
				}
				continue;
			}

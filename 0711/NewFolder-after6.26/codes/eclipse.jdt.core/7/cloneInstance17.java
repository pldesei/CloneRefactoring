			if(optionID.equals(OPTION_ReportAssertIdentifier)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= AssertUsedAsAnIdentifier;
					this.warningThreshold &= ~AssertUsedAsAnIdentifier;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~AssertUsedAsAnIdentifier;
					this.warningThreshold |= AssertUsedAsAnIdentifier;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~AssertUsedAsAnIdentifier;
					this.warningThreshold &= ~AssertUsedAsAnIdentifier;
				}
				continue;
			}

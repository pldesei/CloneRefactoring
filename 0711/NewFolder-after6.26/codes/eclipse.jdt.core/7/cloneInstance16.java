			if(optionID.equals(OPTION_ReportNonExternalizedStringLiteral)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= NonExternalizedString;
					this.warningThreshold &= ~NonExternalizedString;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~NonExternalizedString;
					this.warningThreshold |= NonExternalizedString;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~NonExternalizedString;
					this.warningThreshold &= ~NonExternalizedString;
				}
				continue;
			}

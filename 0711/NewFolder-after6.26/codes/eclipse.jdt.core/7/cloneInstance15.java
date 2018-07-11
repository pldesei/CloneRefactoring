			if(optionID.equals(OPTION_ReportSuperfluousSemicolon)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= SuperfluousSemicolon;
					this.warningThreshold &= ~SuperfluousSemicolon;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~SuperfluousSemicolon;
					this.warningThreshold |= SuperfluousSemicolon;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~SuperfluousSemicolon;
					this.warningThreshold &= ~SuperfluousSemicolon;
				}
				continue;
			}

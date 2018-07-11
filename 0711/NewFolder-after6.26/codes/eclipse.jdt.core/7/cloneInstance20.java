			if(optionID.equals(OPTION_ReportUnusedPrivateMember)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnusedPrivateMember;
					this.warningThreshold &= ~UnusedPrivateMember;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnusedPrivateMember;
					this.warningThreshold |= UnusedPrivateMember;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnusedPrivateMember;
					this.warningThreshold &= ~UnusedPrivateMember;
				}
				continue;
			} 

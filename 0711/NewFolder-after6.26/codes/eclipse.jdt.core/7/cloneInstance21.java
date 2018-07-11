			if(optionID.equals(OPTION_ReportNoEffectAssignment)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= NoEffectAssignment;
					this.warningThreshold &= ~NoEffectAssignment;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~NoEffectAssignment;
					this.warningThreshold |= NoEffectAssignment;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~NoEffectAssignment;
					this.warningThreshold &= ~NoEffectAssignment;
				}
				continue;
			}

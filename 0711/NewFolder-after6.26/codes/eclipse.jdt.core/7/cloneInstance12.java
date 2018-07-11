			if(optionID.equals(OPTION_ReportLocalVariableHiding)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= LocalVariableHiding;
					this.warningThreshold &= ~LocalVariableHiding;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~LocalVariableHiding;
					this.warningThreshold |= LocalVariableHiding;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~LocalVariableHiding;
					this.warningThreshold &= ~LocalVariableHiding;
				}
				continue;
			}

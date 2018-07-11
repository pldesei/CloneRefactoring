			if(optionID.equals(OPTION_ReportPossibleAccidentalBooleanAssignment)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= AccidentalBooleanAssign;
					this.warningThreshold &= ~AccidentalBooleanAssign;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~AccidentalBooleanAssign;
					this.warningThreshold |= AccidentalBooleanAssign;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~AccidentalBooleanAssign;
					this.warningThreshold &= ~AccidentalBooleanAssign;
				}
				continue;
			}

			if(optionID.equals(OPTION_ReportMethodWithConstructorName)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= MethodWithConstructorName;
					this.warningThreshold &= ~MethodWithConstructorName;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~MethodWithConstructorName;
					this.warningThreshold |= MethodWithConstructorName;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~MethodWithConstructorName;
					this.warningThreshold &= ~MethodWithConstructorName;
				}
				continue;
			} 

			if(optionID.equals(OPTION_ReportStaticAccessReceiver)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= StaticAccessReceiver;
					this.warningThreshold &= ~StaticAccessReceiver;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~StaticAccessReceiver;
					this.warningThreshold |= StaticAccessReceiver;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~StaticAccessReceiver;
					this.warningThreshold &= ~StaticAccessReceiver;
				}
				continue;
			} 

			if(optionID.equals(OPTION_ReportIncompatibleNonInheritedInterfaceMethod)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= IncompatibleNonInheritedInterfaceMethod;
					this.warningThreshold &= ~IncompatibleNonInheritedInterfaceMethod;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~IncompatibleNonInheritedInterfaceMethod;
					this.warningThreshold |= IncompatibleNonInheritedInterfaceMethod;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~IncompatibleNonInheritedInterfaceMethod;
					this.warningThreshold &= ~IncompatibleNonInheritedInterfaceMethod;
				}
				continue;
			} 

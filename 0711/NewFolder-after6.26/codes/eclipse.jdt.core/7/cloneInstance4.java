			if(optionID.equals(OPTION_ReportOverridingPackageDefaultMethod)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= OverriddenPackageDefaultMethod;
					this.warningThreshold &= ~OverriddenPackageDefaultMethod;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~OverriddenPackageDefaultMethod;
					this.warningThreshold |= OverriddenPackageDefaultMethod;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~OverriddenPackageDefaultMethod;
					this.warningThreshold &= ~OverriddenPackageDefaultMethod;
				}
				continue;
			} 

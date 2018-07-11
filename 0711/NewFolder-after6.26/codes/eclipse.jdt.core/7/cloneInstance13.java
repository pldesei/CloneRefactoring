			if(optionID.equals(OPTION_ReportFieldHiding)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= FieldHiding;
					this.warningThreshold &= ~FieldHiding;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~FieldHiding;
					this.warningThreshold |= FieldHiding;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~FieldHiding;
					this.warningThreshold &= ~FieldHiding;
				}
				continue;
			}

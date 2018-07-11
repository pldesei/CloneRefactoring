			if(optionID.equals(OPTION_ReportInvalidImport)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= ImportProblem;
					this.warningThreshold &= ~ImportProblem;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~ImportProblem;
					this.warningThreshold |= ImportProblem;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~ImportProblem;
					this.warningThreshold &= ~ImportProblem;
				}
				continue;
			} 

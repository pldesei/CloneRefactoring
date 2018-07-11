		if (this.catchArguments != null) {
			int catchCount;
			this.catchExits = new boolean[catchCount = this.catchBlocks.length];
			this.catchExitInitStateIndexes = new int[catchCount];
			for (int i = 0; i < catchCount; i++) {
				// keep track of the inits that could potentially have led to this exception handler (for final assignments diagnosis)
				FlowInfo catchInfo;
				if (isUncheckedCatchBlock(i)) {
					catchInfo =
						flowInfo.unconditionalCopy().
							addPotentialInitializationsFrom(
								handlingContext.initsOnException(i)).
							addPotentialInitializationsFrom(tryInfo).
							addPotentialInitializationsFrom(
								handlingContext.initsOnReturn).
						addNullInfoFrom(handlingContext.initsOnFinally);
				} else {
					FlowInfo initsOnException = handlingContext.initsOnException(i);
					catchInfo =
						flowInfo.nullInfoLessUnconditionalCopy()
							.addPotentialInitializationsFrom(initsOnException)
							.addNullInfoFrom(initsOnException)	// null info only from here, this is the only way to enter the catch block
							.addPotentialInitializationsFrom(
									tryInfo.nullInfoLessUnconditionalCopy())
							.addPotentialInitializationsFrom(
									handlingContext.initsOnReturn.nullInfoLessUnconditionalCopy());
				}

				// catch var is always set
				LocalVariableBinding catchArg = this.catchArguments[i].binding;
				catchInfo.markAsDefinitelyAssigned(catchArg);
				catchInfo.markAsDefinitelyNonNull(catchArg);
				/*
				"If we are about to consider an unchecked exception handler, potential inits may have occured inside
				the try block that need to be detected , e.g.
				try { x = 1; throwSomething();} catch(Exception e){ x = 2} "
				"(uncheckedExceptionTypes notNil and: [uncheckedExceptionTypes at: index])
				ifTrue: [catchInits addPotentialInitializationsFrom: tryInits]."
				*/
				if (this.tryBlock.statements == null && this.resources == NO_RESOURCES) { // https://bugs.eclipse.org/bugs/show_bug.cgi?id=350579
					catchInfo.setReachMode(FlowInfo.UNREACHABLE_OR_DEAD);
				}
				flowContext.conditionalLevel++;
				catchInfo =
					this.catchBlocks[i].analyseCode(
						currentScope,
						flowContext,
						catchInfo);
				flowContext.conditionalLevel--;
				this.catchExitInitStateIndexes[i] = currentScope.methodScope().recordInitializationStates(catchInfo);
				this.catchExits[i] =
					(catchInfo.tagBits & FlowInfo.UNREACHABLE_OR_DEAD) != 0;
				tryInfo = tryInfo.mergedWith(catchInfo.unconditionalInits());
			}
		}

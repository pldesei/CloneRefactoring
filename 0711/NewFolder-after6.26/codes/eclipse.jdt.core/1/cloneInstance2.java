	public void analyseCode(CompilationUnitScope unitScope) {

		if (ignoreFurtherInvestigation)
			return;
		try {
			FlowInfo flowInfo = FlowInfo.initial(maxFieldCount); // start fresh init info
			ReferenceBinding[] defaultHandledExceptions = new ReferenceBinding[] { scope.getJavaLangThrowable()}; // tolerate any kind of exception
			InitializationFlowContext initializerContext =
				new InitializationFlowContext(null, this, initializerScope);
			InitializationFlowContext staticInitializerContext =
				new InitializationFlowContext(null, this, staticInitializerScope);
			FlowInfo nonStaticFieldInfo = flowInfo.copy().unconditionalInits().discardFieldInitializations();
			FlowInfo staticFieldInfo = flowInfo.copy().unconditionalInits().discardFieldInitializations();
			if (fields != null) {
				for (int i = 0, count = fields.length; i < count; i++) {
					FieldDeclaration field = fields[i];
					if (field.isStatic()) {
						/*if (field.isField()){
							staticInitializerContext.handledExceptions = NoExceptions; // no exception is allowed jls8.3.2
						} else {*/
						staticInitializerContext.handledExceptions = defaultHandledExceptions; // tolerate them all, and record them
						/*}*/
						staticFieldInfo =
							field.analyseCode(
								staticInitializerScope,
								staticInitializerContext,
								staticFieldInfo);
						// in case the initializer is not reachable, use a reinitialized flowInfo and enter a fake reachable
						// branch, since the previous initializer already got the blame.
						if (staticFieldInfo == FlowInfo.DEAD_END) {
							staticInitializerScope.problemReporter().initializerMustCompleteNormally(field);
							staticFieldInfo = FlowInfo.initial(maxFieldCount).setReachMode(FlowInfo.UNREACHABLE);
						}
					} else {
						/*if (field.isField()){
							initializerContext.handledExceptions = NoExceptions; // no exception is allowed jls8.3.2
						} else {*/
							initializerContext.handledExceptions = defaultHandledExceptions; // tolerate them all, and record them
						/*}*/
						nonStaticFieldInfo =
							field.analyseCode(initializerScope, initializerContext, nonStaticFieldInfo);
						// in case the initializer is not reachable, use a reinitialized flowInfo and enter a fake reachable
						// branch, since the previous initializer already got the blame.
						if (nonStaticFieldInfo == FlowInfo.DEAD_END) {
							initializerScope.problemReporter().initializerMustCompleteNormally(field);
							nonStaticFieldInfo = FlowInfo.initial(maxFieldCount).setReachMode(FlowInfo.UNREACHABLE);
						}
					}
				}
			}
			if (memberTypes != null) {
				for (int i = 0, count = memberTypes.length; i < count; i++) {
					memberTypes[i].analyseCode(scope);
				}
			}
			if (methods != null) {
				UnconditionalFlowInfo outerInfo = flowInfo.copy().unconditionalInits().discardFieldInitializations();
				FlowInfo constructorInfo = nonStaticFieldInfo.unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo);
				for (int i = 0, count = methods.length; i < count; i++) {
					AbstractMethodDeclaration method = methods[i];
					if (method.ignoreFurtherInvestigation)
						continue;
					if (method.isInitializationMethod()) {
						if (method.isStatic()) { // <clinit>
							((Clinit)method).analyseCode(
								scope, 
								staticInitializerContext, 
								staticFieldInfo.unconditionalInits().discardNonFieldInitializations().addInitializationsFrom(outerInfo));
						} else { // constructor
							((ConstructorDeclaration)method).analyseCode(scope, initializerContext, constructorInfo.copy());
						}
					} else { // regular method
						method.analyseCode(scope, null, FlowInfo.initial(maxFieldCount));
					}
				}
			}
		} catch (AbortType e) {
			this.ignoreFurtherInvestigation = true;
		};
	}

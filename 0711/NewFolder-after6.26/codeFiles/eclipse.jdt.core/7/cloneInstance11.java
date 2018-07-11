/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jdt.internal.compiler.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.lookup.ProblemReasons;
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

public class CompilerOptions implements ProblemReasons, ProblemSeverities, ClassFileConstants {
	
	/**
	 * Option IDs
	 */
	public static final String OPTION_LocalVariableAttribute = "org.eclipse.jdt.core.compiler.debug.localVariable"; //$NON-NLS-1$
	public static final String OPTION_LineNumberAttribute = "org.eclipse.jdt.core.compiler.debug.lineNumber"; //$NON-NLS-1$
	public static final String OPTION_SourceFileAttribute = "org.eclipse.jdt.core.compiler.debug.sourceFile"; //$NON-NLS-1$
	public static final String OPTION_PreserveUnusedLocal = "org.eclipse.jdt.core.compiler.codegen.unusedLocal"; //$NON-NLS-1$
	public static final String OPTION_ReportUnreachableCode = "org.eclipse.jdt.core.compiler.problem.unreachableCode"; //$NON-NLS-1$
	public static final String OPTION_ReportInvalidImport = "org.eclipse.jdt.core.compiler.problem.invalidImport"; //$NON-NLS-1$
	public static final String OPTION_ReportMethodWithConstructorName = "org.eclipse.jdt.core.compiler.problem.methodWithConstructorName"; //$NON-NLS-1$
	public static final String OPTION_ReportOverridingPackageDefaultMethod = "org.eclipse.jdt.core.compiler.problem.overridingPackageDefaultMethod"; //$NON-NLS-1$
	public static final String OPTION_ReportDeprecation = "org.eclipse.jdt.core.compiler.problem.deprecation"; //$NON-NLS-1$
	public static final String OPTION_ReportDeprecationInDeprecatedCode = "org.eclipse.jdt.core.compiler.problem.deprecationInDeprecatedCode"; //$NON-NLS-1$
	public static final String OPTION_ReportHiddenCatchBlock = "org.eclipse.jdt.core.compiler.problem.hiddenCatchBlock"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedLocal = "org.eclipse.jdt.core.compiler.problem.unusedLocal"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedParameter = "org.eclipse.jdt.core.compiler.problem.unusedParameter"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedParameterWhenImplementingAbstract = "org.eclipse.jdt.core.compiler.problem.unusedParameterWhenImplementingAbstract"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedParameterWhenOverridingConcrete = "org.eclipse.jdt.core.compiler.problem.unusedParameterWhenOverridingConcrete"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedImport = "org.eclipse.jdt.core.compiler.problem.unusedImport"; //$NON-NLS-1$
	public static final String OPTION_ReportSyntheticAccessEmulation = "org.eclipse.jdt.core.compiler.problem.syntheticAccessEmulation"; //$NON-NLS-1$
	public static final String OPTION_ReportNoEffectAssignment = "org.eclipse.jdt.core.compiler.problem.noEffectAssignment"; //$NON-NLS-1$
	public static final String OPTION_ReportLocalVariableHiding = "org.eclipse.jdt.core.compiler.problem.localVariableHiding"; //$NON-NLS-1$
	public static final String OPTION_ReportSpecialParameterHidingField = "org.eclipse.jdt.core.compiler.problem.specialParameterHidingField"; //$NON-NLS-1$
	public static final String OPTION_ReportFieldHiding = "org.eclipse.jdt.core.compiler.problem.fieldHiding"; //$NON-NLS-1$
	public static final String OPTION_ReportPossibleAccidentalBooleanAssignment = "org.eclipse.jdt.core.compiler.problem.possibleAccidentalBooleanAssignment"; //$NON-NLS-1$
	public static final String OPTION_ReportNonExternalizedStringLiteral = "org.eclipse.jdt.core.compiler.problem.nonExternalizedStringLiteral"; //$NON-NLS-1$
	public static final String OPTION_ReportIncompatibleNonInheritedInterfaceMethod = "org.eclipse.jdt.core.compiler.problem.incompatibleNonInheritedInterfaceMethod"; //$NON-NLS-1$
	public static final String OPTION_ReportUnusedPrivateMember = "org.eclipse.jdt.core.compiler.problem.unusedPrivateMember"; //$NON-NLS-1$
	public static final String OPTION_ReportNoImplicitStringConversion = "org.eclipse.jdt.core.compiler.problem.noImplicitStringConversion"; //$NON-NLS-1$
	public static final String OPTION_Source = "org.eclipse.jdt.core.compiler.source"; //$NON-NLS-1$
	public static final String OPTION_TargetPlatform = "org.eclipse.jdt.core.compiler.codegen.targetPlatform"; //$NON-NLS-1$
	public static final String OPTION_ReportAssertIdentifier = "org.eclipse.jdt.core.compiler.problem.assertIdentifier"; //$NON-NLS-1$
	public static final String OPTION_Compliance = "org.eclipse.jdt.core.compiler.compliance"; //$NON-NLS-1$
	public static final String OPTION_Encoding = "org.eclipse.jdt.core.encoding"; //$NON-NLS-1$
	public static final String OPTION_MaxProblemPerUnit = "org.eclipse.jdt.core.compiler.maxProblemPerUnit"; //$NON-NLS-1$
	public static final String OPTION_ReportStaticAccessReceiver = "org.eclipse.jdt.core.compiler.problem.staticAccessReceiver"; //$NON-NLS-1$
	public static final String OPTION_TaskTags = "org.eclipse.jdt.core.compiler.taskTags"; //$NON-NLS-1$
	public static final String OPTION_TaskPriorities = "org.eclipse.jdt.core.compiler.taskPriorities"; //$NON-NLS-1$
	public static final String OPTION_ReportSuperfluousSemicolon = "org.eclipse.jdt.core.compiler.problem.superfluousSemicolon"; //$NON-NLS-1$

	/* should surface ??? */
	public static final String OPTION_PrivateConstructorAccess = "org.eclipse.jdt.core.compiler.codegen.constructorAccessEmulation"; //$NON-NLS-1$

	/**
	 * Possible values for configurable options
	 */
	public static final String GENERATE = "generate";//$NON-NLS-1$
	public static final String DO_NOT_GENERATE = "do not generate"; //$NON-NLS-1$
	public static final String PRESERVE = "preserve"; //$NON-NLS-1$
	public static final String OPTIMIZE_OUT = "optimize out"; //$NON-NLS-1$
	public static final String VERSION_1_1 = "1.1"; //$NON-NLS-1$
	public static final String VERSION_1_2 = "1.2"; //$NON-NLS-1$
	public static final String VERSION_1_3 = "1.3"; //$NON-NLS-1$
	public static final String VERSION_1_4 = "1.4"; //$NON-NLS-1$
	public static final String VERSION_1_5 = "1.5"; //$NON-NLS-1$
	public static final String ERROR = "error"; //$NON-NLS-1$
	public static final String WARNING = "warning"; //$NON-NLS-1$
	public static final String IGNORE = "ignore"; //$NON-NLS-1$
	public static final String ENABLED = "enabled"; //$NON-NLS-1$
	public static final String DISABLED = "disabled"; //$NON-NLS-1$
	
	/**
	 * Bit mask for configurable problems (error/warning threshold)
	 */
	public static final int UnreachableCode = 0x100;
	public static final int ImportProblem = 0x400;
	public static final int MethodWithConstructorName = 0x1000;
	public static final int OverriddenPackageDefaultMethod = 0x2000;
	public static final int UsingDeprecatedAPI = 0x4000;
	public static final int MaskedCatchBlock = 0x8000;
	public static final int UnusedLocalVariable = 0x10000;
	public static final int UnusedArgument = 0x20000;
	public static final int NoImplicitStringConversion = 0x40000;
	public static final int AccessEmulation = 0x80000;
	public static final int NonExternalizedString = 0x100000;
	public static final int AssertUsedAsAnIdentifier = 0x200000;
	public static final int UnusedImport = 0x400000;
	public static final int StaticAccessReceiver = 0x800000;
	public static final int Task = 0x1000000;
	public static final int NoEffectAssignment = 0x2000000;
	public static final int IncompatibleNonInheritedInterfaceMethod = 0x4000000;
	public static final int UnusedPrivateMember = 0x8000000;
	public static final int LocalVariableHiding = 0x10000000;
	public static final int FieldHiding = 0x20000000;
	public static final int AccidentalBooleanAssign = 0x40000000;
	public static final int SuperfluousSemicolon = 0x80000000;
	
	// Default severity level for handlers
	public int errorThreshold = 
		UnreachableCode 
		| ImportProblem;
		
	public int warningThreshold = 
		MethodWithConstructorName 
		| UsingDeprecatedAPI 
		| MaskedCatchBlock 
		| OverriddenPackageDefaultMethod
		| UnusedImport
		| StaticAccessReceiver
		| NoEffectAssignment
		| IncompatibleNonInheritedInterfaceMethod
		| NoImplicitStringConversion;

	// Debug attributes
	public static final int Source = 1; // SourceFileAttribute
	public static final int Lines = 2; // LineNumberAttribute
	public static final int Vars = 4; // LocalVariableTableAttribute

	// By default only lines and source attributes are generated.
	public int produceDebugAttributes = Lines | Source;

	public long targetJDK = JDK1_1; // default generates for JVM1.1
	public long complianceLevel = JDK1_3; // by default be compliant with 1.3

	// toggle private access emulation for 1.2 (constr. accessor has extra arg on constructor) or 1.3 (make private constructor default access when access needed)
	public boolean isPrivateConstructorAccessChangingVisibility = false; // by default, follows 1.2

	// 1.4 feature (assertions are available in source 1.4 mode only)
	public long sourceLevel = JDK1_3; //1.3 behavior by default
	
	// source encoding format
	public String defaultEncoding = null; // will use the platform default encoding
	
	// print what unit is being processed
	public boolean verbose = Compiler.DEBUG;

	// indicates if reference info is desired
	public boolean produceReferenceInfo = true;

	// indicates if unused/optimizable local variables need to be preserved (debugging purpose)
	public boolean preserveAllLocalVariables = false;

	// indicates whether literal expressions are inlined at parse-time or not
	public boolean parseLiteralExpressionsAsConstants = true;

	// max problems per compilation unit
	public int maxProblemsPerUnit = 100; // no more than 100 problems per default
	
	// tags used to recognize tasks in comments
	public char[][] taskTags = null;

	// priorities of tasks in comments
	public char[][] taskPriorites = null;

	// deprecation report
	public boolean reportDeprecationInsideDeprecatedCode = false;
	
	// unused parameters report
	public boolean reportUnusedParameterWhenImplementingAbstract = false;
	public boolean reportUnusedParameterWhenOverridingConcrete = false;

	// constructor/setter parameter hiding
	public boolean reportSpecialParameterHidingField = false;
	
	/** 
	 * Initializing the compiler options with defaults
	 */
	public CompilerOptions(){
	}

	/** 
	 * Initializing the compiler options with external settings
	 */
	public CompilerOptions(Map settings){

		if (settings == null) return;
		
		// filter options which are related to the compiler component
		Iterator entries = settings.entrySet().iterator();
		while (entries.hasNext()) {
			Map.Entry entry = (Map.Entry)entries.next();
			if (!(entry.getKey() instanceof String)) continue;
			if (!(entry.getValue() instanceof String)) continue;
			String optionID = (String) entry.getKey();
			String optionValue = (String) entry.getValue();
			
			// Local variable attribute
			if(optionID.equals(OPTION_LocalVariableAttribute)){
				if (optionValue.equals(GENERATE)) {
					this.produceDebugAttributes |= Vars;
				} else if (optionValue.equals(DO_NOT_GENERATE)){
					this.produceDebugAttributes &= ~Vars;
				}
				continue;
			}  
			// Line number attribute	
			if(optionID.equals(OPTION_LineNumberAttribute)) {
				if (optionValue.equals(GENERATE)) {
					this.produceDebugAttributes |= Lines;
				} else if (optionValue.equals(DO_NOT_GENERATE)) {
					this.produceDebugAttributes &= ~Lines;
				}
				continue;
			} 
			// Source file attribute	
			if(optionID.equals(OPTION_SourceFileAttribute)) {
				if (optionValue.equals(GENERATE)) {
					this.produceDebugAttributes |= Source;
				} else if (optionValue.equals(DO_NOT_GENERATE)) {
					this.produceDebugAttributes &= ~Source;
				}
				continue;
			} 
			// Preserve unused local	
			if(optionID.equals(OPTION_PreserveUnusedLocal)){
				if (optionValue.equals(PRESERVE)) {
					this.preserveAllLocalVariables = true;
				} else if (optionValue.equals(OPTIMIZE_OUT)) {
					this.preserveAllLocalVariables = false;
				}
				continue;
			} 
			// Report unreachable code				
			if(optionID.equals(OPTION_ReportUnreachableCode)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnreachableCode;
					this.warningThreshold &= ~UnreachableCode;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnreachableCode;
					this.warningThreshold |= UnreachableCode;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnreachableCode;
					this.warningThreshold &= ~UnreachableCode;
				}
				continue;
			} 
			// Report invalid import	
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
			// Define the target JDK tag for .classfiles
			if(optionID.equals(OPTION_TargetPlatform)){
				long level = versionToJdkLevel(optionValue);
				if (level != 0) this.targetJDK = level;
				continue;
			} 
			// Define the JDK compliance level
			if(optionID.equals(OPTION_Compliance)){
				long level = versionToJdkLevel(optionValue);
				if (level != 0) this.complianceLevel = level;
				continue;
			} 
			// Private constructor access emulation (extra arg vs. visibility change)
			if(optionID.equals(OPTION_PrivateConstructorAccess)){
				long level = versionToJdkLevel(optionValue);
				if (level >= JDK1_3) this.isPrivateConstructorAccessChangingVisibility = true;
				continue;
			} 
			// Report method with constructor name
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
			// Report overriding package default method
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
			// Report deprecation
			if(optionID.equals(OPTION_ReportDeprecation)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UsingDeprecatedAPI;
					this.warningThreshold &= ~UsingDeprecatedAPI;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UsingDeprecatedAPI;
					this.warningThreshold |= UsingDeprecatedAPI;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UsingDeprecatedAPI;
					this.warningThreshold &= ~UsingDeprecatedAPI;
				}
				continue;
			} 
			// Report deprecation inside deprecated code 
			if(optionID.equals(OPTION_ReportDeprecationInDeprecatedCode)){
				if (optionValue.equals(ENABLED)) {
					this.reportDeprecationInsideDeprecatedCode = true;
				} else if (optionValue.equals(DISABLED)) {
					this.reportDeprecationInsideDeprecatedCode = false;
				}
				continue;
			} 
			// Report hidden catch block
			if(optionID.equals(OPTION_ReportHiddenCatchBlock)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= MaskedCatchBlock;
					this.warningThreshold &= ~MaskedCatchBlock;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~MaskedCatchBlock;
					this.warningThreshold |= MaskedCatchBlock;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~MaskedCatchBlock;
					this.warningThreshold &= ~MaskedCatchBlock;
				}
				continue;
			} 
			// Report unused local variable
			if(optionID.equals(OPTION_ReportUnusedLocal)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnusedLocalVariable;
					this.warningThreshold &= ~UnusedLocalVariable;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnusedLocalVariable;
					this.warningThreshold |= UnusedLocalVariable;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnusedLocalVariable;
					this.warningThreshold &= ~UnusedLocalVariable;
				}
				continue;
			}
			// Report no implicit String conversion
			if (optionID.equals(OPTION_ReportNoImplicitStringConversion)) {
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= NoImplicitStringConversion;
					this.warningThreshold &= ~NoImplicitStringConversion;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~NoImplicitStringConversion;
					this.warningThreshold |= NoImplicitStringConversion;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~NoImplicitStringConversion;
					this.warningThreshold &= ~NoImplicitStringConversion;
				}
				continue;
			}
			// Report unused parameter
			if(optionID.equals(OPTION_ReportUnusedParameter)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnusedArgument;
					this.warningThreshold &= ~UnusedArgument;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnusedArgument;
					this.warningThreshold |= UnusedArgument;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnusedArgument;
					this.warningThreshold &= ~UnusedArgument;
				}
				continue;
			} 
			// Report unused parameter when implementing abstract method 
			if(optionID.equals(OPTION_ReportUnusedParameterWhenImplementingAbstract)){
				if (optionValue.equals(ENABLED)) {
					this.reportUnusedParameterWhenImplementingAbstract = true;
				} else if (optionValue.equals(DISABLED)) {
					this.reportUnusedParameterWhenImplementingAbstract = false;
				}
				continue;
			} 
			// Report unused parameter when implementing abstract method 
			if(optionID.equals(OPTION_ReportUnusedParameterWhenOverridingConcrete)){
				if (optionValue.equals(ENABLED)) {
					this.reportUnusedParameterWhenOverridingConcrete = true;
				} else if (optionValue.equals(DISABLED)) {
					this.reportUnusedParameterWhenOverridingConcrete = false;
				}
				continue;
			} 
			// Report unused import
			if(optionID.equals(OPTION_ReportUnusedImport)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnusedImport;
					this.warningThreshold &= ~UnusedImport;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnusedImport;
					this.warningThreshold |= UnusedImport;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnusedImport;
					this.warningThreshold &= ~UnusedImport;
				}
				continue;
			} 
			// Report synthetic access emulation
			if(optionID.equals(OPTION_ReportSyntheticAccessEmulation)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= AccessEmulation;
					this.warningThreshold &= ~AccessEmulation;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~AccessEmulation;
					this.warningThreshold |= AccessEmulation;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~AccessEmulation;
					this.warningThreshold &= ~AccessEmulation;
				}
				continue;
			}
			// Report local var hiding another variable
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
			// Report field hiding another variable
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
			// Report constructor/setter parameter hiding another field
			if(optionID.equals(OPTION_ReportSpecialParameterHidingField)){
				if (optionValue.equals(ENABLED)) {
					this.reportSpecialParameterHidingField = true;
				} else if (optionValue.equals(DISABLED)) {
					this.reportSpecialParameterHidingField = false;
				}
				continue;
			}			
			// Report possible accidental boolean assignment
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
			// Report possible accidental boolean assignment
			if(optionID.equals(OPTION_ReportSuperfluousSemicolon)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= SuperfluousSemicolon;
					this.warningThreshold &= ~SuperfluousSemicolon;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~SuperfluousSemicolon;
					this.warningThreshold |= SuperfluousSemicolon;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~SuperfluousSemicolon;
					this.warningThreshold &= ~SuperfluousSemicolon;
				}
				continue;
			}
			// Report non-externalized string literals
			if(optionID.equals(OPTION_ReportNonExternalizedStringLiteral)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= NonExternalizedString;
					this.warningThreshold &= ~NonExternalizedString;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~NonExternalizedString;
					this.warningThreshold |= NonExternalizedString;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~NonExternalizedString;
					this.warningThreshold &= ~NonExternalizedString;
				}
				continue;
			}
			// Report usage of 'assert' as an identifier
			if(optionID.equals(OPTION_ReportAssertIdentifier)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= AssertUsedAsAnIdentifier;
					this.warningThreshold &= ~AssertUsedAsAnIdentifier;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~AssertUsedAsAnIdentifier;
					this.warningThreshold |= AssertUsedAsAnIdentifier;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~AssertUsedAsAnIdentifier;
					this.warningThreshold &= ~AssertUsedAsAnIdentifier;
				}
				continue;
			}
			// Set the source compatibility mode (assertions)
			if(optionID.equals(OPTION_Source)){
				long level = versionToJdkLevel(optionValue);
				if (level != 0) this.sourceLevel = level;
				continue;
			}
			// Set the default encoding format
			if(optionID.equals(OPTION_Encoding)){
				if (optionValue.length() == 0){
					this.defaultEncoding = null;
				} else {
					try { // ignore unsupported encoding
						new InputStreamReader(new ByteArrayInputStream(new byte[0]), optionValue);
						this.defaultEncoding = optionValue;
					} catch(UnsupportedEncodingException e){
					}
				}
				continue;
			}
			// Set the threshold for problems per unit
			if(optionID.equals(OPTION_MaxProblemPerUnit)){
				try {
					int val = Integer.parseInt(optionValue);
					if (val >= 0) this.maxProblemsPerUnit = val;
				} catch(NumberFormatException e){
				}				
				continue;
			}
			// Report unnecessary receiver for static access
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
			// Report interface method incompatible with non-inherited Object method
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
			// Report unused private members
			if(optionID.equals(OPTION_ReportUnusedPrivateMember)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= UnusedPrivateMember;
					this.warningThreshold &= ~UnusedPrivateMember;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~UnusedPrivateMember;
					this.warningThreshold |= UnusedPrivateMember;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~UnusedPrivateMember;
					this.warningThreshold &= ~UnusedPrivateMember;
				}
				continue;
			} 
			// Report task
			if(optionID.equals(OPTION_TaskTags)){
				if (optionValue.length() == 0) {
					this.taskTags = null;
				} else {
					this.taskTags = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
				}
				continue;
			} 
			// Report no-op assignments
			if(optionID.equals(OPTION_ReportNoEffectAssignment)){
				if (optionValue.equals(ERROR)) {
					this.errorThreshold |= NoEffectAssignment;
					this.warningThreshold &= ~NoEffectAssignment;
				} else if (optionValue.equals(WARNING)) {
					this.errorThreshold &= ~NoEffectAssignment;
					this.warningThreshold |= NoEffectAssignment;
				} else if (optionValue.equals(IGNORE)) {
					this.errorThreshold &= ~NoEffectAssignment;
					this.warningThreshold &= ~NoEffectAssignment;
				}
				continue;
			}
			if(optionID.equals(OPTION_TaskPriorities)){
				if (optionValue.length() == 0) {
					this.taskPriorites = null;
				} else {
					this.taskPriorites = CharOperation.splitAndTrimOn(',', optionValue.toCharArray());
				}
				continue;
			} 
		}
	}
	
	public int getSeverity(int irritant) {
		if((warningThreshold & irritant) != 0)
			return Warning;
		if((errorThreshold & irritant) != 0)
			return Error;
		return Ignore;
	}

	public void produceReferenceInfo(boolean flag) {
		this.produceReferenceInfo = flag;
	}

	public void setVerboseMode(boolean flag) {
		this.verbose = flag;
	}

	public String toString() {
	

		StringBuffer buf = new StringBuffer("CompilerOptions:"); //$NON-NLS-1$
		if ((produceDebugAttributes & Vars) != 0){
			buf.append("\n-local variables debug attributes: ON"); //$NON-NLS-1$
		} else {
			buf.append("\n-local variables debug attributes: OFF"); //$NON-NLS-1$
		}
		if ((produceDebugAttributes & Lines) != 0){
			buf.append("\n-line number debug attributes: ON"); //$NON-NLS-1$
		} else {
			buf.append("\n-line number debug attributes: OFF"); //$NON-NLS-1$
		}
		if ((produceDebugAttributes & Source) != 0){
			buf.append("\n-source debug attributes: ON"); //$NON-NLS-1$
		} else {
			buf.append("\n-source debug attributes: OFF"); //$NON-NLS-1$
		}
		if (preserveAllLocalVariables){
			buf.append("\n-preserve all local variables: ON"); //$NON-NLS-1$
		} else {
			buf.append("\n-preserve all local variables: OFF"); //$NON-NLS-1$
		}
		if ((errorThreshold & UnreachableCode) != 0){
			buf.append("\n-unreachable code: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & UnreachableCode) != 0){
				buf.append("\n-unreachable code: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-unreachable code: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & ImportProblem) != 0){
			buf.append("\n-import problem: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & ImportProblem) != 0){
				buf.append("\n-import problem: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-import problem: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & MethodWithConstructorName) != 0){
			buf.append("\n-method with constructor name: ERROR");		 //$NON-NLS-1$
		} else {
			if ((warningThreshold & MethodWithConstructorName) != 0){
				buf.append("\n-method with constructor name: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-method with constructor name: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & OverriddenPackageDefaultMethod) != 0){
			buf.append("\n-overridden package default method: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & OverriddenPackageDefaultMethod) != 0){
				buf.append("\n-overridden package default method: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-overridden package default method: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & UsingDeprecatedAPI) != 0){
			buf.append("\n-deprecation: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & UsingDeprecatedAPI) != 0){
				buf.append("\n-deprecation: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-deprecation: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & MaskedCatchBlock) != 0){
			buf.append("\n-masked catch block: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & MaskedCatchBlock) != 0){
				buf.append("\n-masked catch block: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-masked catch block: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & UnusedLocalVariable) != 0){
			buf.append("\n-unused local variable: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & UnusedLocalVariable) != 0){
				buf.append("\n-unused local variable: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-unused local variable: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & UnusedArgument) != 0){
			buf.append("\n-unused parameter: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & UnusedArgument) != 0){
				buf.append("\n-unused parameter: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-unused parameter: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & UnusedImport) != 0){
			buf.append("\n-unused import: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & UnusedImport) != 0){
				buf.append("\n-unused import: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-unused import: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & AccessEmulation) != 0){
			buf.append("\n-synthetic access emulation: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & AccessEmulation) != 0){
				buf.append("\n-synthetic access emulation: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-synthetic access emulation: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & NoEffectAssignment) != 0){
			buf.append("\n-assignment with no effect: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & NoEffectAssignment) != 0){
				buf.append("\n-assignment with no effect: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-assignment with no effect: IGNORE"); //$NON-NLS-1$
			}
		}		if ((errorThreshold & NonExternalizedString) != 0){
			buf.append("\n-non externalized string: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & NonExternalizedString) != 0){
				buf.append("\n-non externalized string: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-non externalized string: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & StaticAccessReceiver) != 0){
			buf.append("\n-static access receiver: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & StaticAccessReceiver) != 0){
				buf.append("\n-static access receiver: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-static access receiver: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & IncompatibleNonInheritedInterfaceMethod) != 0){
			buf.append("\n-incompatible non inherited interface method: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & IncompatibleNonInheritedInterfaceMethod) != 0){
				buf.append("\n-incompatible non inherited interface method: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-incompatible non inherited interface method: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & UnusedPrivateMember) != 0){
			buf.append("\n-unused private member: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & UnusedPrivateMember) != 0){
				buf.append("\n-unused private member: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-unused private member: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & LocalVariableHiding) != 0){
			buf.append("\n-local variable hiding another variable: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & LocalVariableHiding) != 0){
				buf.append("\n-local variable hiding another variable: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-local variable hiding another variable: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & FieldHiding) != 0){
			buf.append("\n-field hiding another variable: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & FieldHiding) != 0){
				buf.append("\n-field hiding another variable: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-field hiding another variable: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & AccidentalBooleanAssign) != 0){
			buf.append("\n-possible accidental boolean assignment: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & AccidentalBooleanAssign) != 0){
				buf.append("\n-possible accidental boolean assignment: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-possible accidental boolean assignment: IGNORE"); //$NON-NLS-1$
			}
		}
		if ((errorThreshold & SuperfluousSemicolon) != 0){
			buf.append("\n-superfluous semicolon: ERROR"); //$NON-NLS-1$
		} else {
			if ((warningThreshold & SuperfluousSemicolon) != 0){
				buf.append("\n-superfluous semicolon: WARNING"); //$NON-NLS-1$
			} else {
				buf.append("\n-superfluous semicolon: IGNORE"); //$NON-NLS-1$
			}
		}
		buf.append("\n-JDK compliance level: "+ versionFromJdkLevel(complianceLevel)); //$NON-NLS-1$
		buf.append("\n-JDK source level: "+ versionFromJdkLevel(sourceLevel)); //$NON-NLS-1$
		buf.append("\n-JDK target level: "+ versionFromJdkLevel(targetJDK)); //$NON-NLS-1$
		if (isPrivateConstructorAccessChangingVisibility){
			buf.append("\n-private constructor access emulation: extra argument"); //$NON-NLS-1$
		} else {
			buf.append("\n-private constructor access emulation: make default access"); //$NON-NLS-1$
		}
		buf.append("\n-verbose : " + (verbose ? "ON" : "OFF")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n-produce reference info : " + (produceReferenceInfo ? "ON" : "OFF")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n-parse literal expressions as constants : " + (parseLiteralExpressionsAsConstants ? "ON" : "OFF")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n-encoding : " + (defaultEncoding == null ? "<default>" : defaultEncoding)); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\n-task tags: " + (this.taskTags == null ? "" : new String(CharOperation.concatWith(this.taskTags,','))));  //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\n-task priorities : " + (this.taskPriorites == null ? "" : new String(CharOperation.concatWith(this.taskPriorites,',')))); //$NON-NLS-1$ //$NON-NLS-2$
		buf.append("\n-report deprecation inside deprecated code : " + (reportDeprecationInsideDeprecatedCode ? "ENABLED" : "DISABLED")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n-report unused parameter when implementing abstract method : " + (reportUnusedParameterWhenImplementingAbstract ? "ENABLED" : "DISABLED")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n-report unused parameter when overriding concrete method : " + (reportUnusedParameterWhenOverridingConcrete ? "ENABLED" : "DISABLED")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		buf.append("\n-report constructor/setter parameter hiding existing field : " + (reportSpecialParameterHidingField ? "ENABLED" : "DISABLED")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		return buf.toString();
	}

	public static long versionToJdkLevel(String versionID) {
		if (versionID.equals(VERSION_1_1)) {
			return JDK1_1;
		} else if (versionID.equals(VERSION_1_2)) {
			return JDK1_2;
		} else if (versionID.equals(VERSION_1_3)) {
			return JDK1_3;
		} else if (versionID.equals(VERSION_1_4)) {
			return JDK1_4;
		} else if (versionID.equals(VERSION_1_5)) {
			return JDK1_5;
		}
		return 0; // unknown
	}

	public static String versionFromJdkLevel(long jdkLevel) {
		if (jdkLevel == JDK1_1) {
			return VERSION_1_1;
		} else if (jdkLevel == JDK1_2) {
			return VERSION_1_2;
		} else if (jdkLevel == JDK1_3) {
			return VERSION_1_3;
		} else if (jdkLevel == JDK1_4) {
			return VERSION_1_4;
		} else if (jdkLevel == JDK1_5) {
			return VERSION_1_5;
		}
		return ""; // unknown version //$NON-NLS-1$
	}
}
/**
 * Copyright (c) 2011-2012 Eclipse contributors and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.emf.ecore.xcore.validation;


import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreValidator;
import org.eclipse.emf.ecore.xcore.XClass;
import org.eclipse.emf.ecore.xcore.XGenericType;
import org.eclipse.emf.ecore.xcore.XOperation;
import org.eclipse.emf.ecore.xcore.XTypeParameter;
import org.eclipse.emf.ecore.xcore.XTypedElement;
import org.eclipse.emf.ecore.xcore.XcorePackage;
import org.eclipse.emf.ecore.xcore.mappings.XcoreMapper;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.validation.AbstractValidationDiagnostic;
import org.eclipse.xtext.validation.CheckType;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.validation.Issue.IssueImpl;
import org.eclipse.xtext.xtext.XtextDiagnosticConverter;

import com.google.inject.Inject;


public class XcoreDiagnosticConverter extends XtextDiagnosticConverter
{
  @Inject
  private XcoreMapper mapper;

  protected boolean isEcoreConstraint(Diagnostic diagnostic)
  {
    return EcoreValidator.DIAGNOSTIC_SOURCE.equals(diagnostic.getSource());
  }

  protected boolean isEObjectConstraint(Diagnostic diagnostic)
  {
    return EObjectValidator.DIAGNOSTIC_SOURCE.equals(diagnostic.getSource());
  }

  @Override
  public void convertValidatorDiagnostic(Diagnostic diagnostic, IAcceptor<Issue> acceptor)
  {
    if (diagnostic.getSeverity() != Diagnostic.OK)
    {
      if (isEObjectConstraint(diagnostic))
      {
        switch (diagnostic.getCode())
        {
          case EcoreValidator.EOBJECT__EVERY_MULTIPCITY_CONFORMS:
          {
            // Ignore the warning about the attribute/reference type not being set; there's always another root cause.
            // 
            List<?> data = diagnostic.getData();
            if (data.contains(EcorePackage.Literals.EATTRIBUTE__EATTRIBUTE_TYPE) || data.contains(EcorePackage.Literals.EREFERENCE__EREFERENCE_TYPE))
            {
              return;
            }
            break;
          }
        }
      }
      
      IssueImpl issue = new Issue.IssueImpl();
      switch (diagnostic.getSeverity()) 
      {
        case Diagnostic.INFO:
        {
          issue.setSeverity(Severity.INFO);
          break;
        }
        case Diagnostic.WARNING:
        {
          issue.setSeverity(Severity.WARNING);
          break;
        }
        default:
        {
          issue.setSeverity(Severity.ERROR);
          break;
        }
      }
      IssueLocation locationData = getLocationData(diagnostic);
      if (locationData != null) 
      {
        issue.setLineNumber(locationData.lineNumber);
        issue.setOffset(locationData.offset);
        issue.setLength(locationData.length);
      }
      EObject causer = getCauser(diagnostic);
      if (causer != null)
      {
        issue.setUriToProblem(EcoreUtil.getURI(causer));
      }
      
      if (diagnostic instanceof AbstractValidationDiagnostic) 
      {
        AbstractValidationDiagnostic abstractValidationDiagnostic = (AbstractValidationDiagnostic) diagnostic;
        issue.setType(abstractValidationDiagnostic.getCheckType());
        issue.setCode(abstractValidationDiagnostic.getIssueCode());
        issue.setData(abstractValidationDiagnostic.getIssueData());
      } 
      else 
      {
        issue.setType(CheckType.FAST);
        issue.setCode(diagnostic.getSource() + "." + diagnostic.getCode());
      }
      issue.setMessage(diagnostic.getMessage());
      acceptor.accept(issue);
    }
  }

  @Override
  protected IssueLocation getLocationData(Diagnostic diagnostic)
  {
    if (isEcoreConstraint(diagnostic))
    {
      switch (diagnostic.getCode())
      {
        case EcoreValidator.CONSISTENT_BOUNDS:
        case EcoreValidator.SINGLE_CONTAINER:
        {
          EObject causer = getCauser(diagnostic);
          return getLocationData(causer, XcorePackage.Literals.XTYPED_ELEMENT__MULTIPLICITY);
        }
        case EcoreValidator.CONSISTENT_OPPOSITE_BAD_TRANSIENT:
        case EcoreValidator.CONSISTENT_OPPOSITE_BOTH_CONTAINMENT:
        case EcoreValidator.CONSISTENT_OPPOSITE_NOT_MATCHING:
        {
          EObject causer = getCauser(diagnostic);
          return getLocationData(causer, XcorePackage.Literals.XREFERENCE__OPPOSITE);
        }
      }
    }
    return super.getLocationData(diagnostic);
  }

  protected EObject getCauser(EObject eObject)
  {
    if (eObject instanceof ENamedElement)
    {
      return mapper.getToXcoreMapping(eObject).getXcoreElement();
    }
    else if (eObject instanceof EGenericType)
    {
      EObject eContainer = eObject.eContainer();
      if (eContainer != null)
      {
        EObject causerContainer = getCauser(eContainer);
        EReference eContainmentFeature = eObject.eContainmentFeature();
        if (eContainmentFeature == EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE)
        {
          return ((XTypedElement)causerContainer).getType();
        }
        else if (eContainmentFeature == EcorePackage.Literals.EOPERATION__EGENERIC_EXCEPTIONS)
        {
          return ((XOperation)causerContainer).getExceptions().get(((EOperation)eContainer).getEGenericExceptions().indexOf(eObject));
        }
        else if (eContainmentFeature == EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES)
        {
          return ((XClass)causerContainer).getSuperTypes().get(((EClass)eContainer).getEGenericSuperTypes().indexOf(eObject));
        }
        else if (eContainmentFeature == EcorePackage.Literals.EGENERIC_TYPE__ETYPE_ARGUMENTS)
        {
          return ((XGenericType)causerContainer).getTypeArguments().get(((EGenericType)eContainer).getETypeArguments().indexOf(eObject));
        }
        else if (eContainmentFeature == EcorePackage.Literals.EGENERIC_TYPE__ELOWER_BOUND)
        {
          return ((XGenericType)causerContainer).getLowerBound();
        }
        else if (eContainmentFeature == EcorePackage.Literals.EGENERIC_TYPE__EUPPER_BOUND)
        {
          return ((XGenericType)causerContainer).getUpperBound();
        }
        else if (eContainmentFeature == EcorePackage.Literals.ETYPE_PARAMETER__EBOUNDS)
        {
          return ((XTypeParameter)causerContainer).getBounds().get(((ETypeParameter)eContainer).getEBounds().indexOf(eObject));
        }
      }
      return eObject;
    }
    else
    {
      return eObject;
    }
  }

  @Override
  protected EObject getCauser(Diagnostic diagnostic)
  {
    if (isEcoreConstraint(diagnostic))
    {
      switch (diagnostic.getCode())
      {
        case EcoreValidator.UNIQUE_CLASSIFIER_NAMES:
        case EcoreValidator.UNIQUE_ENUMERATOR_LITERALS:
        case EcoreValidator.UNIQUE_ENUMERATOR_NAMES:
        {
          return getCauser((EObject)diagnostic.getData().get(1));
        }
      }
    }
    EObject result = super.getCauser(diagnostic);
    if (result != null && result.eClass().getEPackage() == EcorePackage.eINSTANCE)
    {
      result = getCauser(result);
    }
    return result;
  }
}

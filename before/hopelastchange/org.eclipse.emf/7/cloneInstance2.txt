This clone instance is located in File: tests/org.eclipse.emf.test.common/src/org/eclipse/emf/test/models/switch3/impl/EClass5Impl.java
The line range of this clone instance is: 166-178
The content of this clone instance is as follows:
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case Switch3Package.ECLASS5__EATTRIBUTE10:
        setEAttribute10((String)newValue);
        return;
      case Switch3Package.ECLASS5__EATTRIBUTE11:
        setEAttribute11((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

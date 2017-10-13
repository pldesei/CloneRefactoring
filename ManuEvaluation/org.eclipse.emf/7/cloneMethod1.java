This clone method is located in File: tests/org.eclipse.emf.test.common/src/org/eclipse/emf/test/models/switch3/impl/EClass4Impl.java
The line range of this clone method is: 167-179
The content of this clone method is as follows:
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case Switch3Package.ECLASS4__EATTRIBUTE8:
        setEAttribute8((String)newValue);
        return;
      case Switch3Package.ECLASS4__EATTRIBUTE9:
        setEAttribute9((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

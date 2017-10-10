This clone instance is located in File: tests/org.eclipse.emf.test.edit/src/org/eclipse/emf/test/edit/command/ChangeCommandTest.java
The line range of this clone instance is: 116-178
The content of this clone instance is as follows:
  public void testResource()
  {
    final Resource r = new ResourceImpl(URI.createURI("r"));
    final E initialE = refFactory.createE();
    final E finalE = refFactory.createE();
    r.getContents().add(initialE);
    final E e = refFactory.createE();

    // State 0
    assertEquals(1, r.getContents().size());
    assertEquals(initialE, r.getContents().get(0));
    assertTrue(e.getIds().isEmpty());

    ChangeCommand changeCommand = new ChangeCommand(r)
    {
      @Override
      protected void doExecute()
      {
        r.getContents().set(0, finalE);
        r.getContents().add(e);

        EList<String> ids = e.getIds();
        ids.add("0");
        ids.add("1");
        ids.add("2");
      }
    };

    editingDomain.getCommandStack().execute(changeCommand);
    assertTrue(changeCommand.canExecute());
    assertTrue(changeCommand.canUndo());

    // State 1
    assertEquals(2, r.getContents().size());
    assertEquals(finalE, r.getContents().get(0));
    assertEquals(e, r.getContents().get(1));
    assertEquals(3, e.getIds().size());
    assertEquals("0", e.getIds().get(0));
    assertEquals("1", e.getIds().get(1));
    assertEquals("2", e.getIds().get(2));

    editingDomain.getCommandStack().undo();
    assertTrue(changeCommand.canExecute());
    assertTrue(changeCommand.canUndo());

    // State 0
    assertEquals(1, r.getContents().size());
    assertEquals(initialE, r.getContents().get(0));
    assertTrue(e.getIds().isEmpty());

    editingDomain.getCommandStack().redo();
    assertTrue(changeCommand.canExecute());
    assertTrue(changeCommand.canUndo());

    // State 1
    assertEquals(2, r.getContents().size());
    assertEquals(finalE, r.getContents().get(0));
    assertEquals(e, r.getContents().get(1));
    assertEquals(3, e.getIds().size());
    assertEquals("0", e.getIds().get(0));
    assertEquals("1", e.getIds().get(1));
    assertEquals("2", e.getIds().get(2));
  }

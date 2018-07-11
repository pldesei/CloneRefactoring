	} catch (ImageBuilderInternalException e) {
		Util.log(e.getThrowable(), "JavaBuilder handling ImageBuilderInternalException while building: " + currentProject.getName()); //$NON-NLS-1$
		IMarker marker = currentProject.createMarker(IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER);
		marker.setAttributes(
			new String[] {IMarker.MESSAGE, IMarker.SEVERITY, IJavaModelMarker.CATEGORY_ID, IMarker.SOURCE_ID},
			new Object[] {
				Messages.bind(Messages.build_inconsistentProject, e.getLocalizedMessage()),
				new Integer(IMarker.SEVERITY_ERROR),
				new Integer(CategorizedProblem.CAT_BUILDPATH),
				JavaBuilder.SOURCE_ID
			}
		);
	} catch (MissingSourceFileException e) {

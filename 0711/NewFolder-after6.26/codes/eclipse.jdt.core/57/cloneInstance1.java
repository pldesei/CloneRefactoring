	} catch (CoreException e) {
		Util.log(e, "JavaBuilder handling CoreException while building: " + currentProject.getName()); //$NON-NLS-1$
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
	} catch (ImageBuilderInternalException e) {

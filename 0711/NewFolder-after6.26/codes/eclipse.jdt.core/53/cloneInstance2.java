						if (mecs != null) {
							nextCandidate: for (int m = 0, max = mecs.length; m < max; m++) {
								TypeBinding mec = mecs[m];
								if (mec == null) continue nextCandidate;
								Set invalidInvocations = (Set)invocations.get(mec);
								int invalidSize = invalidInvocations.size();
								if (invalidSize > 1) {
									TypeBinding[] collisions;
									invalidInvocations.toArray(collisions = new TypeBinding[invalidSize]);
									problemReporter().superinterfacesCollide(collisions[0].erasure(), typeRef, collisions[0], collisions[1]);
									typeVariable.tagBits |= TagBits.HierarchyHasProblems;
									noProblems = false;
									continue nextVariable;
								}
							}					
						}

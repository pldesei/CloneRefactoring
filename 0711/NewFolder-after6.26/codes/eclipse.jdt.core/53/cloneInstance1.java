					if (typeVariable.firstBound == typeVariable.superclass) {
						types[1] = typeVariable.superclass;
						TypeBinding[] mecs = minimalErasedCandidates(types, invocations);
						if (mecs != null) {
							nextCandidate: for (int k = 0, max = mecs.length; k < max; k++) {
								TypeBinding mec = mecs[k];
								if (mec == null) continue nextCandidate;
								Set invalidInvocations = (Set)invocations.get(mec);
								int invalidSize = invalidInvocations.size();
								if (invalidSize > 1) {
									TypeBinding[] collisions;
									invalidInvocations.toArray(collisions = new TypeBinding[invalidSize]);
									problemReporter().superinterfacesCollide(collisions[0].erasure(), typeRef, collisions[1], collisions[0]); // swap collisions since mec types got swapped
									typeVariable.tagBits |= TagBits.HierarchyHasProblems;
									noProblems = false;
									continue nextVariable;
								}
							}			
						}
					}
